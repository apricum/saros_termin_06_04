/*
 * Copyright (C) 2015 privateuser
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
package de.fu_berlin.inf.dpp.netbeans.ui.views;

import de.fu_berlin.inf.dpp.ISarosContext;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.account.XMPPAccount;
import de.fu_berlin.inf.dpp.account.XMPPAccountStore;
import de.fu_berlin.inf.dpp.communication.connection.ConnectionHandler;
import de.fu_berlin.inf.dpp.net.xmpp.XMPPConnectionService;
import de.fu_berlin.inf.dpp.net.xmpp.roster.AbstractRosterListener;
import de.fu_berlin.inf.dpp.net.xmpp.roster.IRosterListener;
import de.fu_berlin.inf.dpp.net.xmpp.roster.RosterTracker;
import de.fu_berlin.inf.dpp.netbeans.EditorManager;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
import org.apache.log4j.Logger;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
//import org.openide.util.NbBundle.Messages;
import de.fu_berlin.inf.dpp.netbeans.ui.actions.ConnectServerAction;
import de.fu_berlin.inf.dpp.netbeans.ui.actions.SendFileAction;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;
import de.fu_berlin.inf.dpp.preferences.Preferences;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.ISessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.NullSessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.SessionEndReason;
import java.awt.Composite;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
//import java.util.prefs.Preferences;
import javax.swing.Action;
import org.jivesoftware.smack.packet.Presence;
import org.openide.util.NbBundle.Messages;
import org.picocontainer.annotations.Inject;




/**
 * Top component which displays something.
 */
@ConvertAsProperties(
       dtd = "-//de.fu_berlin.inf.dpp.netbeans.ui.views//SarosView//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SarosView",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "de.fu_berlin.inf.dpp.netbeans.ui.views.SarosView")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SarosViewAction",
        preferredID = "SarosView")
@Messages({
    "CTL_SarosViewAction=SarosView",
    "CTL_SarosView=SarosView Window",
    "HINT_SarosView=This is a SarosView window"
})
public final class SarosView extends TopComponent {

    @Inject
    private IPreferenceStore preferenceStore;
    
     private static final Logger LOG = Logger.getLogger(SarosView.class);

    public static final String ID = "de.fu_berlin.inf.dpp.ui.views.SarosView";

    private static final boolean MDNS_MODE = Boolean
        .getBoolean("de.fu_berlin.inf.dpp.net.ENABLE_MDNS");

    private final IRosterListener rosterListener = new AbstractRosterListener() {
        /**
         * Stores the most recent presence for each user, so we can keep track
         * of away/available changes which should not update the RosterView.
         */
        private Map<String, Presence> lastPresenceMap = new HashMap<String, Presence>();

        @Override
        public void presenceChanged(Presence presence) {

//            final boolean playAvailableSound = preferenceStore
//                .getBoolean(NetbeansPreferenceConstants.SOUND_PLAY_EVENT_CONTACT_ONLINE);
//
//            final boolean playUnavailableSound = preferenceStore
//                .getBoolean(NetbeansPreferenceConstants.SOUND_PLAY_EVENT_CONTACT_OFFLINE);
//
//            Presence lastPresence = lastPresenceMap.put(presence.getFrom(),
//                presence);
//
//            
            
        }
    };

   
    private final PropertyChangeListener propertyListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals(
                NetbeansPreferenceConstants.ENABLE_BALLOON_NOTIFICATION)) {
                showBalloonNotifications = Boolean.valueOf(event.getNewValue()
                    .toString());
            }
        }
    };

    private final ISessionLifecycleListener sessionLifecycleListener = new NullSessionLifecycleListener() {
        @Override
        public void sessionEnded(ISarosSession session, SessionEndReason reason) {
            //showStopNotification(session.getHost(), reason);
        }
    };

    protected Composite leftComposite;

    //protected ViewerComposite<?> sessionDisplay;

    //protected ChatRoomsComposite chatRooms;
    @Inject
    private ISarosContext context;
    
    @Inject
    private ConnectionHandler connectionHandler;
    
    //@Inject
    //protected IPreferenceStore preferenceStore;

    @Inject
    protected ISarosSessionManager sarosSessionManager;

    /*
    @Inject
    protected EditorManager editorManager;
*/
    @Inject
    protected RosterTracker rosterTracker;

    @Inject
    protected XMPPConnectionService connectionService;

    private static volatile boolean showBalloonNotifications;


    @Inject 
   private Preferences preferences;
   
    private final ConnectServerAction connectAction;
   
    //private SarosContext sarosContext = new SarosContext();
    //XMPPAccount myAccount;
    
    //@Inject
    //private ConnectionHandler connectionHandler;
    
    @Inject
    private XMPPAccountStore accountStore;
   
    private Map<String, Action> registeredActions = new HashMap<String,Action>();
    
    public SarosView() {
        initComponents();        
        setName(Bundle.CTL_SarosView());
        setToolTipText(Bundle.HINT_SarosView());
        SarosPluginContext.initComponent(this);
        createNewAccount();
        connectAction = new ConnectServerAction();
        //------------------ here??
        //preferenceStore.
                //addPropertyChangeListener(propertyListener);
        sarosSessionManager
            .addSessionLifecycleListener(sessionLifecycleListener);
        showBalloonNotifications = preferenceStore
            .getBoolean(NetbeansPreferenceConstants.ENABLE_BALLOON_NOTIFICATION);
         createActions();
        //connectAction.executeWithUser("apricum");

        
        //ArrayList<ISarosContextFactory> factories = new ArrayList<ISarosContextFactory>();
        //factories.add(new SarosCoreContextFactory());
        
       
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(SarosView.class, "SarosView.jToggleButton1.text")); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(317, Short.MAX_VALUE)
                .addComponent(jToggleButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButton1)
                .addContainerGap(266, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    public static void showNotification(String UserStatusChangeHandler_user_left, String format) {
        LOG.info("InvitaTION Received !!!!!!!!!!!!!!");
    }
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
         if (jToggleButton1.isSelected()) {
               LOG.error("Test on");
                connectAction.executeWithUser("apricum@saros-con.imp.fu-berlin.de");
            } else {
               connectionService.disconnect();
            }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "13.12.6");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = "13.12.6";
        // TODO read your settings according to their version
    }
    
    protected XMPPAccount createNewAccount(){
        XMPPAccount tempAccount;
        
        if(accountStore.exists("apricum", "saros-con.imp.fu-berlin.de", "", 0)){
            tempAccount = accountStore.findAccount("apricum@saros-con.imp.fu-berlin.de");
            return tempAccount;
        }
        
        tempAccount = accountStore.createAccount("apricum", "dpwmvss15*","saros-con.imp.fu-berlin.de", "", 0, true, true);
        accountStore.setAccountActive(tempAccount);
        return tempAccount;
    }

    private void createActions() {
        SendFileAction newSendFileAction = new SendFileAction();
        //newSendFileAction.putValue("ACTION_ID", "SendFile");
        registerAction(newSendFileAction);
    }
     /**
     * Displays a notification next to the Saros View. If the view cannot be
     * found the notification is displayed next to the element that has the
     * current focus. The visibility time of the notification will vary,
     * depending on how much words the text contains. This method <b>SHOULD
     * NOT</b> be called directly from the business logic.
     *
     * @param title
     *            the title of the notification
     * @param text
     *            the text of the notification
     * @throws NullPointerException
     *             if title or text is <code>null</code>
     */
//    public static void showNotification(final String title, final String text) {
//        showNotification(title, text, null);
//    }
    /**
     * Display a notification next to the given control..
     *
     * @param title
     * @param text
     * @param control
     */
//    public static void showNotification(final String title, final String text,
//        final Control control) {
//        if (title == null)
//            throw new NullPointerException("title is null");
//
//        if (text == null)
//            throw new NullPointerException("text is null");
//
//        if (!showBalloonNotifications)
//            return;
//
//        SWTUtils.runSafeSWTAsync(LOG, new Runnable() {
//            @Override
//            public void run() {
//
//                if (control != null) {
//                    BalloonNotification.showNotification(control, title, text);
//                    return;
//                }
//
//                IViewPart sarosView = SWTUtils.findView(SarosView.ID);
//                /*
//                 * If no session view is open then show the balloon notification
//                 * in the control which has the keyboard focus
//                 */
//
//                Control sarosViewControl;
//
//                if (sarosView != null) {
//                    sarosViewControl = ((SarosView) sarosView).leftComposite;
//                } else {
//                    sarosViewControl = Display.getDefault().getFocusControl();
//
//                }
//
//                BalloonNotification.showNotification(sarosViewControl, title,
//                    text);
//            }
//        });
//    }
     private Action registerAction(Action action) {
        Action oldAction = registeredActions.put(action.getValue("ACTION_ID").toString(), action);
        LOG.info("action_id : " + action.getValue("ACTION_ID").toString());
        if (oldAction != null)
            throw new IllegalArgumentException(
                "tried to register action with id " + action.getValue("ACTION_ID").toString()
                    + " more than once");

        return action;
    }
}
