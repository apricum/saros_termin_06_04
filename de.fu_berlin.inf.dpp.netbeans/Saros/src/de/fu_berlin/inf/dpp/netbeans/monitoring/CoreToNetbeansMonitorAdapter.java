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
public class CoreToNetbeansMonitorAdapter 
    implements org.eclipse.core.runtime.IProgressMonitor,
    de.fu_berlin.inf.dpp.monitoring.IProgressMonitor {

    private final de.fu_berlin.inf.dpp.monitoring.IProgressMonitor monitor;
    private double collectedWork;

    /**
     * Creates an Eclipse wrapper around a Saros core {@link IProgressMonitor}.
     *
     * @param monitor
     *            Saros core progress monitor to wrap
     */
    public CoreToNetbeansMonitorAdapter(
        de.fu_berlin.inf.dpp.monitoring.IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void beginTask(String name, int totalWork) {
        monitor.beginTask(name, totalWork);
    }

    @Override
    public void done() {
        monitor.done();
    }

    /**
     * Implementation of
     * {@link org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)}
     * which delegates to {@link #worked(int)}, keeping track of passed
     * fractions until whole full units of work are reached. This compensates
     * for the lack of an <code>internalWorked()</code> method in the Saros core
     * version of {@link IProgressMonitor}.
     */
    @Override
    public void internalWorked(double work) {
        collectedWork += work;

        int floorOfCollectedWork = (int) Math.floor(collectedWork);
        if (floorOfCollectedWork > 0) {
            worked(floorOfCollectedWork);
        }

        collectedWork -= floorOfCollectedWork;
    }

    @Override
    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    @Override
    public void setCanceled(boolean value) {
        monitor.setCanceled(value);
    }

    @Override
    public void setTaskName(String name) {
        monitor.setTaskName(name);
    }

    @Override
    public void subTask(String name) {
        monitor.subTask(name);
    }

    @Override
    public void worked(int work) {
        monitor.worked(work);
    }
}
