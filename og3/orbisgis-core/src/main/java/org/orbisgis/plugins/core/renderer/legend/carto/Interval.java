/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.plugins.core.renderer.legend.carto;

import org.gdms.data.values.Value;
import org.orbisgis.utils.FormatUtils;

public class Interval {

	private Value start;
	private Value end;
	private boolean minIncluded;
	private boolean maxIncluded;

	public Interval(Value start, boolean minIncluded, Value end,
			boolean maxIncluded) {
		this.start = start;
		if ((start != null) && start.isNull()) {
			start = null;
		}
		this.minIncluded = minIncluded;
		this.end = end;
		if ((end != null) && end.isNull()) {
			end = null;
		}
		this.maxIncluded = maxIncluded;
	}

	public String getIntervalString() {
		String startF = new Double(FormatUtils.round(start.getAsDouble(), 3))
				.toString();
		String endF = new Double(FormatUtils.round(end.getAsDouble(), 3))
				.toString();
		return startF + " - " + endF;
	}

	public Value getMinValue() {
		return start;
	}

	public Value getMaxValue() {
		return end;
	}

	public boolean contains(Value value) {
		boolean matchesLower = true;
		if (start != null && !start.isNull()) {
			if (minIncluded) {
				matchesLower = start.lessEqual(value).getAsBoolean();
			} else {
				matchesLower = start.less(value).getAsBoolean();
			}
		}

		boolean matchesUpper = true;
		if (end != null && !end.isNull()) {
			if (maxIncluded) {
				matchesUpper = end.greaterEqual(value).getAsBoolean();
			} else {
				matchesUpper = end.greater(value).getAsBoolean();
			}
		}
		return matchesLower && matchesUpper;
	}

	public boolean isMinIncluded() {
		return minIncluded;
	}

	public boolean isMaxIncluded() {
		return maxIncluded;
	}
}
