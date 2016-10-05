/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsgroovyapi.output

import groovy.transform.AnnotationCollector
import groovy.transform.Field
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute
import org.orbisgis.wpsgroovyapi.attributes.DataFieldAttribute
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute

/**
 * DataField output annotation.
 * The DataField is a complex data that represents a DataSource field (i.e. a column of a table).
 * As an output, this annotation should be placed just before the variable.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String
 *       Title of the output. Normally available for display to a human.
 *  - variableReference : String
 *      Name of the variable of the DataStore.
 *
 * The following fields can be defined (optional) :
 *  - traducedTitles : LanguageString[]
 *      List of LanguageString containing the traduced titles.
 *  - resume : String
 *      Brief narrative description of the output. Normally available for display to a human.
 *  - traducedResumes : LanguageString[]
 *      List of LanguageString containing the traduced description.
 *  - keywords : String
 *      Array of keywords that characterize the output.
 *  - traducedKeywords : Keyword[]
 *      List of Keyword containing the keywords translations.
 *  - identifier : String
 *      Unambiguous identifier of the output. It should be a valid URI.
 *  - metadata : MetaData[]
 *      Reference to additional metadata about this item.
 *  - fieldTypes : String[]
 *      Array of the types allowed. If no types are specified, accepts all.
 *  - excludedTypes : String[]
 *      Array of the type forbidden. If no types are specified, accept all.
 *  - multiSelection : boolean
 *      Enable or not the user to select more than one field. Disabled by default.
 *
 * Usage example can be found at https://github.com/orbisgis/orbisgis/wiki/
 *
 * @author Sylvain PALOMINOS
 */
@AnnotationCollector([Field, DataFieldAttribute, OutputAttribute, DescriptionTypeAttribute])
@interface DataFieldOutput {}