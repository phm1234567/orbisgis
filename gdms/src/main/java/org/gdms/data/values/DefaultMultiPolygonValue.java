/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.types.Type;

/**
 *
 * @author Antoine Gourlay
 */
public class DefaultMultiPolygonValue extends DefaultGeometryCollectionValue implements MultiPolygonValue {

        public DefaultMultiPolygonValue(MultiPolygon g,  CoordinateReferenceSystem crs) {
                super(g, crs);
        }

        @Override
        public void setValue(MultiPolygon value) {
                super.setValue(value);
        }

        @Override
        public void setValue(GeometryCollection value) {
                super.setValue((Geometry)value);
        }

        @Override
        public void setValue(Geometry value) {
                if (value instanceof MultiPolygon) {
                        setValue((MultiPolygon)value);
                } else {
                        throw new IllegalArgumentException("Cannot set this MultiPoliygonValue to a Geometry of type: "
                                + value.getGeometryType());
                }
        }

        @Override
        public int getType() {
                return Type.MULTIPOLYGON;
        }
        
}
