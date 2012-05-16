/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan Bocher, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.orbisgis.collections.twoqueue;

import java.util.HashMap;
import java.util.Map;

/**
 * Special FIFO Linked Queue for use by {@link TwoQueueBuffer}.
 * 
 * @param <I> the key
 * @param <B> the value
 * @author Antoine Gourlay
 */
final class TwoQueueA1out<I, B> {

        private Map<I, SingleQueueValue<I>> map;
        private SingleQueueValue<I> newest;
        private int maxSize;

        TwoQueueA1out(int maxSize) {
                this.maxSize = maxSize;
                map = new HashMap<I, SingleQueueValue<I>>(maxSize);
        }

        int size() {
                return map.size();
        }

        boolean isEmpty() {
                return map.isEmpty();
        }

        boolean containsKey(I key) {
                return map.containsKey(key);
        }

        boolean retrieve(I key) {
                final SingleQueueValue<I> get = map.remove(key);
                return remove(get);
        }

        void put(I key) {
                final SingleQueueValue<I> q = new SingleQueueValue<I>(key);
                map.put(q.key, q);
                insert(q);
        }

        private boolean remove(SingleQueueValue<I> v) {
                if (v != null) {
                        if (map.isEmpty()) {
                                newest = null;
                        } else {
                                newest = v.next;
                                newest.previous = v.previous;
                                newest.previous.next = newest;
                        }

                        return true;
                } else {
                        return false;
                }
        }

        private void insert(SingleQueueValue<I> v) {
                trimOldest();
                if (newest != null) {
                        v.previous = newest.previous;
                        v.next = newest;
                        v.previous.next = v;
                        newest.previous = v;
                        newest = v;
                } else {
                        newest = v;
                        newest.next = newest;
                        newest.previous = newest;
                }
        }

        private void trimOldest() {
                if (map.size() == maxSize) {
                        map.remove(newest.previous.key);
                        newest.previous = newest.previous.previous;
                        newest.previous.next = newest;
                }
        }

        void clear() {
                map.clear();
                newest = null;
        }

        /**
         * @return the maxSize
         */
        int getMaxSize() {
                return maxSize;
        }

        /**
         * @param maxSize the maximum size to set
         */
        void setMaxSize(int maxSize) {
                this.maxSize = maxSize;
        }

        private static class SingleQueueValue<I> {

                I key;
                SingleQueueValue<I> next;
                SingleQueueValue<I> previous;

                SingleQueueValue(I key) {
                        this.key = key;
                }
        }
}
