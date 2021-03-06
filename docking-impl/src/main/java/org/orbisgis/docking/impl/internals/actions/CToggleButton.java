/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.docking.impl.internals.actions;

import bibliothek.gui.dock.common.action.CRadioButton;
import bibliothek.gui.dock.common.intern.action.CDecorateableAction;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;

/**
 * Implementation of listener on CRadioButton.
 */
public class CToggleButton extends CRadioButton implements CActionHolder {
    private Action action;
    private AtomicBoolean propagateChange = new AtomicBoolean(true);


    public CToggleButton(Action action) {
        this.action = action;
        // Read properties from the action
        CommonFunctions.onActionPropertyChangeDecorateable(this, action , new PropertyChangeEvent(action,null,null,null));
        Object val = action.getValue(Action.SELECTED_KEY);
        if(val != null) {
            try {
                propagateChange.set(false);
                setSelected((Boolean) val);
            } finally {
                propagateChange.set(true);
            }
        }
        // Listen to action property changes
        action.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this, "onActionPropertyChange", ""));
    }

    @Override
    protected void changed() {
        if(propagateChange.get()) {
            action.putValue(Action.SELECTED_KEY, isSelected());
            action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Action.SELECTED_KEY));
        }
    }

    /**
     * Used by PropertyChangeListener, update CRadioButton properties
     * @param propertyChangeEvent Property edition information
     */
    public void onActionPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        CommonFunctions.onActionPropertyChangeSelectable(this, action, propertyChangeEvent);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
