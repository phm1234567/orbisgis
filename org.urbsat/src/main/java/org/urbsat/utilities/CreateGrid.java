/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.urbsat.utilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/*
 * select register('../../datas2tests/cir/face_unitaire.cir','face_unitaire');
 * select creategrid(0.2,0.2) from face_unitaire; select
 * sum(area(intersection(a.the_geom,b.the_geom))) from face_unitaire as a,
 * grid_face_unitaire as b where intersects(a.the_geom,b.the_geom); select
 * area(intersection(a.the_geom,b.the_geom)),index from face_unitaire as a,
 * grid_face_unitaire as b where intersects(a.the_geom,b.the_geom) order by
 * index;
 * 
 * select creategrid(0.2,0.2,45) from face_unitaire;
 */

public class CreateGrid implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private boolean isAnOrientedGrid;

	private double deltaX;
	private double deltaY;
	private double angle;
	private double cosAngle;
	private double sinAngle;
	private double cosInvAngle;
	private double sinInvAngle;
	private double llcX;
	private double llcY;

	private SpatialDataSourceDecorator inSds;
	private ObjectMemoryDriver driver;
	private String outDsName;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException(
					"CreateGrid only operates on one table");
		}
		if ((2 != values.length) && (3 != values.length)) {
			throw new ExecutionException(
					"CreateGrid only operates with two or three values (width, height[, angle])");
		}

		try {
			deltaX = values[0].getAsDouble();
			deltaY = values[1].getAsDouble();
			inSds = new SpatialDataSourceDecorator(tables[0]);
			inSds.open();

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(
					new String[] { "the_geom", "index" }, new Type[] {
							TypeFactory.createType(Type.GEOMETRY),
							TypeFactory.createType(Type.INT) });
			outDsName = dsf.getSourceManager().nameAndRegister(driver);

			if (3 == values.length) {
				isAnOrientedGrid = true;
				angle = (values[2].getAsDouble() * Math.PI) / 180;
				createGrid(prepareOrientedGrid());
			} else {
				isAnOrientedGrid = false;
				createGrid(inSds.getFullExtent());
			}
			inSds.cancel();
			return dsf.getDataSource(outDsName);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "CREATEGRID";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select creategrid(4000,1000[,15]) from myTable;";
	}

	private void createGrid(final Envelope env) throws DriverException {
		final int nbX = new Double(Math.ceil((env.getMaxX() - env.getMinX())
				/ deltaX)).intValue();
		final int nbY = new Double(Math.ceil((env.getMaxY() - env.getMinY())
				/ deltaY)).intValue();
		int gridCellIndex = 0;
		double x = env.centre().x - (deltaX * nbX) / 2;
		for (int i = 0; i < nbX; i++, x += deltaX) {
			double y = env.centre().y - (deltaY * nbY) / 2;
			for (int j = 0; j < nbY; j++, y += deltaY) {
				gridCellIndex++;
				final Coordinate[] summits = new Coordinate[5];
				summits[0] = invTranslateAndRotate(x, y);
				summits[1] = invTranslateAndRotate(x + deltaX, y);
				summits[2] = invTranslateAndRotate(x + deltaX, y + deltaY);
				summits[3] = invTranslateAndRotate(x, y + deltaY);
				summits[4] = invTranslateAndRotate(x, y);
				createGridCell(summits, gridCellIndex);
			}
		}
	}

	private Envelope prepareOrientedGrid() throws DriverException {
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;

		cosAngle = Math.cos(angle);
		sinAngle = Math.sin(angle);
		cosInvAngle = Math.cos(-angle);
		sinInvAngle = Math.sin(-angle);
		final Envelope env = inSds.getFullExtent();
		llcX = env.getMinX();
		llcY = env.getMinY();

		final int rowCount = (int) inSds.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry g = inSds.getGeometry(rowIndex);
			final Coordinate[] allCoordinates = g.getCoordinates();
			for (Coordinate inCoordinate : allCoordinates) {
				final Coordinate outCoordinate = translateAndRotate(inCoordinate);
				if (outCoordinate.x < xMin) {
					xMin = outCoordinate.x;
				}
				if (outCoordinate.x > xMax) {
					xMax = outCoordinate.x;
				}
				if (outCoordinate.y < yMin) {
					yMin = outCoordinate.y;
				}
				if (outCoordinate.y > yMax) {
					yMax = outCoordinate.y;
				}
			}
		}
		return new Envelope(xMin, xMax, yMin, yMax);
	}

	private final Coordinate translateAndRotate(final Coordinate inCoordinate) {
		// do the rotation after the translation in the local coordinates system
		final double x = inCoordinate.x - llcX;
		final double y = inCoordinate.y - llcY;
		return new Coordinate(cosAngle * x - sinAngle * y, sinAngle * x
				+ cosAngle * y, inCoordinate.z);
	}

	private final Coordinate invTranslateAndRotate(final double x,
			final double y) {
		if (isAnOrientedGrid) {
			// do the (reverse) translation after the (reverse) rotation
			final double localX = cosInvAngle * x - sinInvAngle * y;
			final double localY = sinInvAngle * x + cosInvAngle * y;
			return new Coordinate(localX + llcX, localY + llcY);
		} else {
			return new Coordinate(x, y);
		}
	}

	private void createGridCell(final Coordinate[] summits,
			final int gridCellIndex) {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
		driver.addValues(new Value[] { ValueFactory.createValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}
}