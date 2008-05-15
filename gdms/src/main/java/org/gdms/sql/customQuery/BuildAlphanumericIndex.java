/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;

public class BuildAlphanumericIndex implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		String sourceName = tables[0].getName();
		try {
			dsf.getIndexManager().buildIndex(sourceName, values[0].toString(),
					IndexManager.BTREE_ALPHANUMERIC_INDEX, pm);
		} catch (IndexException e) {
			throw new ExecutionException("Cannot create the index", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Source not found: " + sourceName, e);
		}

		return null;
	}

	public String getName() {
		return "BuildAlphaIndex";
	}

	public String getDescription() {
		return "Builds an alphanumeric index";
	}

	public String getSqlOrder() {
		return "select BuildAlphaIndex('fieldName') from sourceName;";
	}

	public Metadata getMetadata(Metadata[] tables) {
		return null;
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 1);
		switch (types[0].getTypeCode()) {
		case Type.BYTE:
		case Type.DATE:
		case Type.DOUBLE:
		case Type.FLOAT:
		case Type.INT:
		case Type.LONG:
		case Type.SHORT:
		case Type.STRING:
		case Type.TIME:
		case Type.TIMESTAMP:
			break;
		default:
			throw new IncompatibleTypesException(
					"Cannot create a alphanumeric index on the data type: "
							+ TypeFactory.getTypeName(types[0].getTypeCode()));
		}
	}

	public void validateTables(Metadata[] tables) throws SemanticException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
	}
}