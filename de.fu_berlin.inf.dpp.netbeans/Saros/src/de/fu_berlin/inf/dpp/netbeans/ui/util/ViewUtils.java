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

import de.fu_berlin.inf.dpp.netbeans.ui.views.SarosView;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class ViewUtils {
    private static final Logger LOG = Logger.getLogger(ViewUtils.class);

    public static void openSarosView() {
        createView(SarosView.ID);
    }

    public static void bringViewToFront(String id) {
       // showView(id, IWorkbenchPage.VIEW_VISIBLE);
    }

    public static void activateView(String id) {
        //showView(id, IWorkbenchPage.VIEW_ACTIVATE);
    }

    public static void createView(String sarosViewTopComponentID) {
        TopComponent tempTopComponent = findTopComponent(sarosViewTopComponentID);
        if(tempTopComponent == null){
             tempTopComponent = new SarosView();
        }
        showView((SarosView) tempTopComponent);
    }
    
    private static TopComponent findTopComponent(String searchedSarosTopViewComponent){
        Set openedTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        
        for(Object tc: openedTopComponents){
            if(((((TopComponent)tc).getLookup().lookup(SarosView.class)).ID.equals(searchedSarosTopViewComponent))){
                return (TopComponent)tc;
            }
        }
        return null;
    }

    /*
     * TODO What to do if no WorkbenchWindows are are active?
     */
    private static void showView(SarosView topComponentToOpen) {
        //final IWorkbench workbench = PlatformUI.getWorkbench();
        
           topComponentToOpen.open();
        
    }
}
