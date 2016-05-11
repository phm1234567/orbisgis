/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.map.tools.generated;

import java.awt.Graphics;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.*;

/**
 * Draw a multipolygon
 */
public abstract class Multipolygon extends AbstractAutomaton {

    @Override
	public String[] getTransitionLabels() {
                switch(status){
                        case STANDBY:
                                return new String[]{i18n.tr("Cancel"),i18n.tr("Terminate multipolygon")};
                        case POINT :
                                return new String[]{i18n.tr("Cancel"),i18n.tr("Terminate polygon"),i18n.tr("Terminate multipolygon")};
                        default :
                                return new String[0];
                }
	}

        @Override
	public Code[] getTransitionCodes() {
                switch(status){
                        case STANDBY:
                                return new Code[]{Code.ESC, Code.TERMINATE};
                        case POINT :
                                return new Code[]{Code.ESC, Code.P, Code.TERMINATE};
                        default :
                                return new Code[]{};
                }
	}

        @Override
	public void init(MapContext ec, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		logger.info("status: " + status);
		this.mc = ec;
		this.tm = tm;
		status = Status.STANDBY;
		transitionTo_Standby(ec, tm);
		if (isFinished(status)) {
			throw new FinishedAutomatonException();
		}
	}

        @Override
	public void transition(Code code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
                if (Code.ESC == code) {
                    status = Status.CANCEL;
                    transitionTo_Cancel(mc, tm);
                    if (isFinished(status)) {
                        throw new FinishedAutomatonException();
                    }
                    return;
                }
                Status preStatus;
                switch(status){
                        case STANDBY:
                                if (Code.PRESS == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.POINT;
                                                transitionTo_Point(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                } else if (Code.TERMINATE == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.DONE;
                                                transitionTo_Done(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case POINT :
                                switch(code){
                                        case PRESS :
                                                preStatus = status;
                                                try {
                                                        status = Status.POINT;
                                                        transitionTo_Point(mc, tm);
                                                        if (isFinished(status)) {
                                                                throw new FinishedAutomatonException();
                                                        }
                                                } catch (TransitionException e) {
                                                        status = preStatus;
                                                        throw e;
                                                }
                                                break;
                                        case P :
                                                preStatus = status;
                                                try {
                                                        status = Status.LINE;
                                                        transitionTo_Line(mc, tm);
                                                        if (isFinished(status)) {
                                                                throw new FinishedAutomatonException();
                                                        }
                                                } catch (TransitionException e) {
                                                        status = preStatus;
                                                        throw e;
                                                }
                                                break;
                                        case TERMINATE:
                                                preStatus = status;
                                                try {
                                                        status = Status.DONE;
                                                        transitionTo_Done(mc, tm);
                                                        if (isFinished(status)) {
                                                                throw new FinishedAutomatonException();
                                                        }
                                                } catch (TransitionException e) {
                                                        status = preStatus;
                                                        throw e;
                                                }
                                                break;
                                }
                                break;
                        case LINE :
                                if (Code.INIT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.STANDBY;
                                                transitionTo_Standby(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case DONE :
                                if (Code.INIT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.STANDBY;
                                                transitionTo_Standby(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        default :
                                throw new NoSuchTransitionException(code.toString());
                }
	}

	public boolean isFinished(Status status) {
                switch (status) {
                        case STANDBY:
                        case POINT :
                        case LINE :
                        case DONE :
                                return false;
                        case CANCEL :
                                return true;
                        default:
                                throw new RuntimeException("Invalid status: " + status);
                }
	}

        @Override
	public void draw(Graphics g) throws DrawingException {
                switch(status){
                        case STANDBY:
                                drawIn_Standby(g, mc, tm);
                                break;
                        case POINT :
                                drawIn_Point(g, mc, tm);
                                break;
                        case LINE :
                                drawIn_Line(g, mc, tm);
                                break;
                        case DONE :
                                drawIn_Done(g, mc, tm);
                                break;
                        case CANCEL:
                                drawIn_Cancel(g, mc, tm);
                                break;
                }

	}

	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Standby(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_Point(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;

	public abstract void transitionTo_Line(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Line(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;

	public abstract void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Done(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;

	public abstract void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;


	public String getMessage() {
                switch(status){
                        case STANDBY:
                                return i18n.tr("Select the first point/terminate multipolygon");
                        case POINT :
                                return i18n.tr("Select the next point/terminate polygon/terminate multipolygon");
                        case LINE :
                        case DONE :
                        case CANCEL :
                                return "";
                        default :
                                throw new RuntimeException();
                }
	}


    @Override
	public String getTooltip() {
		return i18n.tr("Draw a multipolygon");
	}


    @Override
	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {
		if (Status.POINT == status) {
			transition(Code.TERMINATE);
		}
	}
}