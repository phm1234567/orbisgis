package org.orbisgis.plugins.core.ui.views;

import java.awt.Component;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.PersistenceException;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionView;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class GeocognitionViewPlugIn extends ViewPlugIn {

	private GeocognitionView panel;
	private JMenuItem menuItem;

	public GeocognitionView getPanel() {
		return panel;
	}

	public void delete() {
		panel.delete();
	}

	public Component getComponent() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new GeocognitionView();
		panel.initialize();
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.GEOCOGNITION, true,
				getIcon(Names.GEOCOGNITION_ICON), null, panel,
				null, null, context.getWorkbenchContext());
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void loadStatus() throws PersistenceException {
		panel.loadStatus();
	}

	public void saveStatus() throws PersistenceException {
		panel.saveStatus();
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}
}
