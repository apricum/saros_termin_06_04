/*
 * Copyright (C) 2015 Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.fu_berlin.inf.dpp.netbeans.monitoring;



/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class ProgressMonitorAdapterFactory {
    /**
     * Convert an Eclipse ProgressMonitor to Saros Core ProgressMonitor
     * 
     * @param monitor
     *            an Eclipse ProgressMonitor
     * @return converted ProgressMonitor
     */
    public static de.fu_berlin.inf.dpp.monitoring.IProgressMonitor convert(
        org.eclipse.core.runtime.IProgressMonitor monitor) {

        if (monitor == null)
            return null;

        if (monitor instanceof de.fu_berlin.inf.dpp.monitoring.IProgressMonitor)
            return (de.fu_berlin.inf.dpp.monitoring.IProgressMonitor) monitor;

        return new NetbeansToCoreMonitorAdapter(monitor);
    }

    /**
     * Converts a Saros Core ProgressMonitor to a Eclipse ProgressMonitor
     * 
     * @param monitor
     *            a Saros Core ProgressMonitor
     * @return the corresponding Eclipse
     *         {@linkplain org.eclipse.core.runtime.IProgressMonitor}
     */
    public static org.eclipse.core.runtime.IProgressMonitor convert(
        de.fu_berlin.inf.dpp.monitoring.IProgressMonitor monitor) {

        if (monitor == null)
            return null;

        if (monitor instanceof org.eclipse.core.runtime.IProgressMonitor)
            return (org.eclipse.core.runtime.IProgressMonitor) monitor;

        return new CoreToNetbeansMonitorAdapter(monitor);
    }
}
