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
package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

public class GigaCustomQuery extends AbstractTableFunction {

	@Override
	public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
		try {
			Metadata metadata = getMetadata(null);
			DiskBufferDriver dbd = new DiskBufferDriver(dsf, metadata);
			for (int i = 0; i < 1000000; i++) {
				dbd.addValues(new Value[] { ValueFactory
						.createValue("this is row " + i) });
			}
                        dbd.writingFinished();
			dbd.open();
			return dbd.getTable("main");
		} catch (DriverException e) {
			throw new FunctionException("Cannot generate the gigasource", e);
		}
	}

	@Override
	public String getDescription() {
		return "Returns a super big source";
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.STRING) }, new String[] { "string" });
		return metadata;
	}

	@Override
	public String getName() {
		return "gigaquery";
	}

	@Override
	public String getSqlOrder() {
		return "select gigaquery()";
	}

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                        new TableFunctionSignature(TableDefinition.ANY)
                };
        }

}
