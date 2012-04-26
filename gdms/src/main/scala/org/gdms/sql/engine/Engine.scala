/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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

package org.gdms.sql.engine

import java.util.Properties
import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.step.filters.FiltersStep
import org.gdms.sql.engine.step.logicalJoin.LogicalJoinOptimStep
import org.gdms.sql.engine.step.parsing.ParsingStep
import org.gdms.sql.engine.step.treeparsing.TreeParsingStep
import org.gdms.sql.engine.step.validate.ValidationStep

object Engine {
  
  @throws(classOf[ParseException])
  def parse(sql: String): Array[SQLStatement] = parse(sql, DataSourceFactory.getDefaultProperties) toArray
  
  @throws(classOf[ParseException])
  def parse(sql: String, p: Properties) = {
    implicit val pp = p
    
    {
      sql          >=: // original string
      ParsingStep  >=: // parsing into AST
      TreeParsingStep  // parsing into Seq[Operation]
    } map {
      _                    >=:
      LogicalJoinOptimStep >=: // joins
      FiltersStep          >=: // filters
      ValidationStep           // validation
    } map (new SQLStatement(_)) toArray
  }
  
  @throws(classOf[ParseException])
  def execute(sql: String, dsf: DataSourceFactory, p: Properties) {
    parse(sql, p) foreach { ss =>
      ss.setDataSourceFactory(dsf)
      ss.prepare
      ss.execute
      ss.cleanUp
    }
  }
  
  @throws(classOf[ParseException])
  def execute(sql: String, dsf: DataSourceFactory) {
    execute(sql, dsf, dsf.getProperties)
  }
}