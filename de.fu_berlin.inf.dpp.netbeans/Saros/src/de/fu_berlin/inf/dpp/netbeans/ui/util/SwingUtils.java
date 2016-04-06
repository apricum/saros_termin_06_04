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
package de.fu_berlin.inf.dpp.netbeans.ui.util;

import de.fu_berlin.inf.dpp.util.StackTrace;
import de.fu_berlin.inf.dpp.util.ThreadUtils;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class SwingUtils {
    private static final Logger LOG = Logger.getLogger(SwingUtils.class);
    
    public static void runSafeSWTAsync(final Logger log, final Runnable runnable) {
        try {
            SwingUtilities.invokeLater(ThreadUtils.wrapSafe(log, runnable));
            
        } catch (Throwable e) {
            

            LOG.warn("could not execute runnable " + runnable
                + ", UI thread is not available", new StackTrace());
        }
    }
    
}
