package org.orbisgis.plugins.core.geocognition.symbology;

import org.orbisgis.plugins.core.edition.EditableElementException;
import org.orbisgis.plugins.core.geocognition.AbstractExtensionElement;
import org.orbisgis.plugins.core.geocognition.GeocognitionElementContentListener;
import org.orbisgis.plugins.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.plugins.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.plugins.core.renderer.legend.Legend;
import org.orbisgis.progress.IProgressMonitor;

public class GeocognitionLegend extends AbstractExtensionElement implements
		GeocognitionExtensionElement {

	private Legend legend;
	private Object revertStatus;

	public GeocognitionLegend(Legend legend, GeocognitionElementFactory factory) {
		super(factory);
		this.legend = legend;
	}

	@Override
	public Object getJAXBObject() {
		return legend.getJAXBObject();
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return legend;
	}

	@Override
	public String getTypeId() {
		return legend.getLegendTypeId();
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		legend.setJAXBObject(revertStatus);
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		revertStatus = getJAXBObject();
	}

	@Override
	public void save() {
		revertStatus = getJAXBObject();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public Object getRevertJAXBObject() {
		return revertStatus;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		legend.setJAXBObject(jaxbObject);
	}

}
