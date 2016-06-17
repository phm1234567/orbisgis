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

package org.orbisgis.wpsservice.controller.parser;

import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsgroovyapi.attributes.RawDataAttribute;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;
import org.orbisgis.wpsservice.model.ObjectFactory;
import org.orbisgis.wpsservice.model.RawData;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Field;

/**
 * Parser dedicated to the RawData parsing.
 *
 * @author Sylvain PALOMINOS
 **/

public class RawDataParser implements Parser {

    @Override
    public InputDescriptionType parseInput(Field f, Object defaultValue, String processId) {
        //Instantiate the RawData
        Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
        RawData rawData = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(RawDataAttribute.class), format);

        InputDescriptionType input = new InputDescriptionType();
        JAXBElement<RawData> jaxbElement = new ObjectFactory().createRawData(rawData);
        input.setDataDescription(jaxbElement);

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        if(input.getIdentifier() == null){
            CodeType codeType = new CodeType();
            codeType.setValue(processId+":input:"+input.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_"));
            input.setIdentifier(codeType);
        }

        return input;
    }

    @Override
    public OutputDescriptionType parseOutput(Field f, String processId) {
        //Instantiate the RawData
        Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
        RawData rawData = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(RawDataAttribute.class), format);

        OutputDescriptionType output = new OutputDescriptionType();
        JAXBElement<RawData> jaxbElement = new ObjectFactory().createRawData(rawData);
        output.setDataDescription(jaxbElement);

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        if(output.getIdentifier() == null){
            CodeType codeType = new CodeType();
            codeType.setValue(processId+":output:"+output.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_"));
            output.setIdentifier(codeType);
        }

        return output;
    }

    @Override
    public Class getAnnotation() {
        return RawDataAttribute.class;
    }
}