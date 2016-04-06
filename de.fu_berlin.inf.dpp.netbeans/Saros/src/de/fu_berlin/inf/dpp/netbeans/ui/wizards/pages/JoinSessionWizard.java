/*
 * Copyright (C) 2016 privateuser
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
package de.fu_berlin.inf.dpp.netbeans.ui.wizards.pages;

import de.fu_berlin.inf.dpp.monitoring.IProgressMonitor;
import de.fu_berlin.inf.dpp.negotiation.IncomingSessionNegotiation;
import de.fu_berlin.inf.dpp.negotiation.NegotiationTools.CancelLocation;
import de.fu_berlin.inf.dpp.negotiation.SessionNegotiation;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.netbeans.monitoring.NullProgressMonitor;
import de.fu_berlin.inf.dpp.netbeans.monitoring.ProgressMonitorAdapterFactory;
import de.fu_berlin.inf.dpp.netbeans.ui.Messages;
import de.fu_berlin.inf.dpp.netbeans.ui.util.JobWithStatus;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="de.fu_berlin.inf.dpp.netbeans.ui.wizards.pages.JoinSessionWizard")
// @ActionRegistration(displayName="Open JoinSessionWizard Wizard")
// @ActionReference(path="Menu/Tools", position=...)
public final class JoinSessionWizard {

    private final IncomingSessionNegotiation isn;
    private boolean isNegotiationRunning = false;
    private SessionNegotiation.Status status;
    
    public JoinSessionWizard(IncomingSessionNegotiation iSN){
         

        isn = iSN;
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new JoinSessionWizardWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Messages.JoinSessionWizard_title);
        //setWindowTitle(Messages.JoinSessionWizard_title);
        //setHelpAvailable(false);
        
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            

       
        isNegotiationRunning = true;

        try {
            

            status = isn.accept(new NullProgressMonitor());
//            getContainer().run(true, false, new IRunnableWithProgress() {
//                @Override
//                public void run(IProgressMonitor monitor)
//                    throws InvocationTargetException, InterruptedException {
//                    try {
//                        status = isn.accept(new NullProgressMonitor());
//                    } catch (Exception e) {
//                        throw new InvocationTargetException(e);
//                    }
//                }
//            });
        } catch (Exception e) {
            Throwable cause = e.getCause();

            if (cause == null)
                cause = e;

           

            // give up, close the wizard as we cannot do anything here !
           
        }

        }
    }
}

      
        

        
                
                        
//                            status = isn.accept(new IProgressMonitor() {
//
//                                @Override
//                                public void done() {
//                                   
//                                         
//                                }
//
//                                @Override
//                                public void subTask(String string) {
//                                    
//                                }
//
//                                @Override
//                                public void setTaskName(String string) {
//                                   
//                                }
//
//                                @Override
//                                public void worked(int i) {
//                                    
//                                }
//
//                                @Override
//                                public void setCanceled(boolean bln) {
//                                    
//                                }
//
//                                @Override
//                                public boolean isCanceled() {
//                                    return false;
//                                }
//
//                                @Override
//                                public void beginTask(String string, int i) {
//                                    
//                                }
//                           
//        
//                            });
 
    
     

