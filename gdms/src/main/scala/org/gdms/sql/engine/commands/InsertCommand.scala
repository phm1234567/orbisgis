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
package org.gdms.sql.engine.commands

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.sql.engine.SemanticException
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.driver.memory.MemoryDataSetDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Main command for static multi-row insert of expressions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class InsertCommand(table: String, fields: Option[Seq[String]])
extends Command with OutputCommand {

  private var rightOrder: Array[(Int, Int)] = null
  
  var ds: DataSource = null
  
  var res: MemoryDataSetDriver = null
  // number of inserted rows
  var ro: Long = 0

  override def doPrepare = {
    ds = dsf.getDataSource(table, DataSourceFactory.EDITABLE)
    ds.open

    val m = ds.getMetadata
    
    fields match {
      case Some(f) => {
          val r = f map (s => (s, m.getFieldIndex(s)))
          r foreach {i => if (i._2 == -1) {
              throw new SemanticException("There is no field '" + i._1 + "' in table '" + table + "'.")
            }}
          
          rightOrder = r.map(_._2).zipWithIndex toArray
        }
      case _ =>
    }
    
    val types = (0 until m.getFieldCount) map (m.getFieldType(_).getTypeCode)
    val chm = children.head.getMetadata
    
    val expCount = if (rightOrder == null) {
      m.getFieldCount
    } else {
      rightOrder.length
    }
    if (chm.getFieldCount != expCount) {
      throw new SemanticException("There are " + chm.getFieldCount + " fields specified. Expected " + expCount + "fields to insert.")
    }
    
    val inTypes = if (rightOrder == null) {
      (0 until chm.getFieldCount) map (chm.getFieldType(_).getTypeCode)
    } else {
      rightOrder map(r => chm.getFieldType(r._1).getTypeCode) toSeq
    }
    (types zip inTypes) foreach { _ match {
        case (a, b) if !TypeFactory.canBeCastTo(b, a) =>{
            ds.close
            throw new IncompatibleTypesException("type " + TypeFactory.getTypeName(b) + " cannot be cast to "
                                                 + TypeFactory.getTypeName(a))
          }
        case _ =>
      }
    }
    
    res = new MemoryDataSetDriver(new DefaultMetadata(Array(TypeFactory.createType(Type.LONG)), Array("Inserted")))
  }
  
  private def order(a: Array[Value]): Array[Value] = {
    if (rightOrder == null) a else {
      val out = new Array[Value](rightOrder.length)
      rightOrder foreach (i => out(i._1) = a(i._2))
      out
    }
  }

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Inserting", 0))
    
    r.next foreach { e =>
      ro = ro + 1
      ds.insertFilledRow(order(e))
    }
    
    res.addValues(ValueFactory.createValue(ro))
    ro = 0
    
    pm.map(_.endTask)
    null
  }

  override def doCleanUp = {
    ds.commit
    ds.close
  }

  def getResult = res

  override lazy val getMetadata = SQLMetadata("",getResult.getMetadata)
}
