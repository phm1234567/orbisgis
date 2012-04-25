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
package org.gdms.data.types;

import org.gdms.data.values.Value;

/**
 * This constraint counts the number of decimal digits of the input values, and
 * check it is lower than the int value given when building the constraint.
 *
 * @author fernando, alexis, antoine
 */
public class PrecisionConstraint extends AbstractIntConstraint {

        /**
         * Build a new PrecisionConstraint that will check that the number of
         * decimal digit is lower than {@code constraintValue}.
         * @param constraintValue
         */
        public PrecisionConstraint(final int constraintValue) {
                super(constraintValue);
        }

        PrecisionConstraint(byte[] constraintBytes) {
                super(constraintBytes);
        }

        @Override
        public int getConstraintCode() {
                return Constraint.PRECISION;
        }

        @Override
        public String check(Value value) {
                if (!value.isNull() && getDecimalDigitsCount(value.getAsDouble()) > constraintValue) {
                        return "Too many digits. Only " + constraintValue + " allowed";
                }
                return null;
        }

        private int getDecimalDigitsCount(double value) {
                String str = Double.toString(value);
                if (str.endsWith(".0")) {
                        return 0;
                }
                return str.length() - (str.indexOf('.') + 1);
        }
}
