package org.orbisgis.plugins.core.geocognition;

import org.orbisgis.plugins.core.edition.EditableBaseElement;

public interface GeocognitionCommonElement extends EditableBaseElement {

	/**
	 * Gets the JAXB generated object containing all meaningful information for
	 * the marshalling process
	 * 
	 * @return
	 */
	Object getJAXBObject();

	/**
	 * Gets the factory that created this element
	 * 
	 * @return
	 */
	GeocognitionElementFactory getFactory();

}
