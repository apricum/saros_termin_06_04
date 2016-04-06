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
package de.fu_berlin.inf.dpp.netbeans.ui.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.negotiation.ProjectNegotiation;
import de.fu_berlin.inf.dpp.net.ConnectionState;
import de.fu_berlin.inf.dpp.net.util.XMPPUtils;
import de.fu_berlin.inf.dpp.net.xmpp.IConnectionListener;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.net.xmpp.XMPPConnectionService;
import de.fu_berlin.inf.dpp.netbeans.ui.Messages;
import de.fu_berlin.inf.dpp.netbeans.ui.jobs.OutgoingFileTransferJob;
import de.fu_berlin.inf.dpp.netbeans.ui.util.SWTUtils;
import de.fu_berlin.inf.dpp.session.User;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 * Action for sending and receiving a file over XMPP.
 * 
 * @author srossbach
 */
/*
 * TODO the receiving and file transfer creation part is misplaced here ... wrap
 * those calls and put them in the dpp.net package e.g XMPPFileTransfer class
 * hiding the need for tracking the XMPPConnection status etc.
 */

/*
 * FIXME as the roster currently does not support multiple resources it can be
 * random which presence will receive the file
 */
public class SendFileAction implements Action,Disposable {

    public static final String ACTION_ID = SendFileAction.class.getName();

    private static final Logger LOG = Logger.getLogger(SendFileAction.class);

    // static smack ****
    static {
        OutgoingFileTransfer.setResponseTimeout(5 * 60 * 1000);
    }

    private FileTransferListener fileTransferListener = new FileTransferListener() {

        @Override
        public void fileTransferRequest(final FileTransferRequest request) {

            final String description = request.getDescription();

            if (description != null
                && description
                    .startsWith(ProjectNegotiation.ARCHIVE_TRANSFER_ID))
                return;

            SWTUtils.runSafeSWTAsync(LOG, new Runnable() {
                @Override
                public void run() {
                    handleIncomingFileTransferRequest(request);
                }
            });
        }

    };

   

    private IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void connectionStateChanged(final Connection connection,
            final ConnectionState state) {
            SWTUtils.runSafeSWTAsync(LOG, new Runnable() {

                @Override
                public void run() {

                    switch (state) {
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        updateFileTransferManager(connection);
                        break;
                    case DISCONNECTING:
                    case ERROR:
                    case NOT_CONNECTED:
                        updateFileTransferManager(null);
                        break;
                    }

                    updateEnablement();
                }
            });
        }
    };

    @Inject
    private XMPPConnectionService connectionService;

    private FileTransferManager fileTransferManager;

    private Connection connection;

    public SendFileAction() {
        //super(Messages.SendFileAction_title);
        SarosPluginContext.initComponent(this);

        SarosPluginContext.initComponent(this);

        
        connectionService.addListener(connectionListener);
        updateFileTransferManager(connectionService.getConnection());

        updateEnablement();
    }

   
    public void run() {

        if (!canRun())
            return;

        final JID jid = new JID("tester25", "saros-con.imp.fu-berlin.de"); 

        //final FileDialog fd = new FileDialog();
        //fd.setText(Messages.SendFileAction_filedialog_text);

        final String filename = "test";

        if (filename == null)
            return;

        final File file = new File(filename);

       

        // connection changes are executed while the dialog is open !
        if (fileTransferManager == null)
            return;

        final OutgoingFileTransfer transfer = fileTransferManager
            .createOutgoingFileTransfer(jid.getRAW());

        Job job = new OutgoingFileTransferJob(transfer, file, jid);
        job.setUser(true);
        job.schedule();
    }

    @Override
    public void dispose() {
        connectionService.removeListener(connectionListener);

        
    }

    private void updateEnablement() {
        setEnabled(canRun());
    }

    private boolean canRun() {
        return true;
    }

//    private JID getSelectedJID() {
//        List<User> sessionUsers = SelectionRetrieverFactory
//            .getSelectionRetriever(User.class).getSelection();
//
//        List<JID> contacts = SelectionRetrieverFactory.getSelectionRetriever(
//            JID.class).getSelection();
//
//        // currently only one transfer per click (maybe improved later)
//        if (contacts.size() + sessionUsers.size() != 1)
//            return null;
//
//        if (sessionUsers.size() == 1 && sessionUsers.get(0).isLocal())
//            return null;
//
//        if (contacts.size() == 1 && !isOnline(contacts.get(0)))
//            return null;
//
//        if (sessionUsers.size() == 1)
//            return sessionUsers.get(0).getJID();
//
//        // FIXME see TODO at class level ... this currently does not work well
//        // if (contacts.size() == 1 && !isOnline(contacts.get(0)))
//        // return null;
//        // return contacts.get(0);
//
//        // workaround
//        if (connection == null)
//            return null;
//
//        Presence presence = connection.getRoster().getPresence(
//            contacts.get(0).getBase());
//
//        if (!presence.isAvailable() || presence.getFrom() == null)
//            return null;
//
//        return new JID(presence.getFrom());
//    }

    private void updateFileTransferManager(Connection connection) {
        if (connection == null) {
            if (fileTransferManager != null)
                fileTransferManager
                    .removeFileTransferListener(fileTransferListener);

            fileTransferManager = null;
        } else {
            fileTransferManager = new FileTransferManager(connection);
            fileTransferManager.addFileTransferListener(fileTransferListener);
        }

        this.connection = connection;
    }

    private boolean isOnline(JID jid) {
        if (connection == null)
            return false;

        return connection.getRoster().getPresenceResource(jid.getRAW())
            .isAvailable();
    }

    // TODO popping up dialogs can create a very bad UX but we have currently no
    // other awareness methods
    private void handleIncomingFileTransferRequest(
        final FileTransferRequest request) {

        final String filename = request.getFileName();
        final long fileSize = request.getFileSize();
        final JID jid = new JID(request.getRequestor());

        String nickname = XMPPUtils.getNickname(null, jid,
            new JID(request.getRequestor()).getBase());

       
        String msg = "File Transfer Request" +  nickname + " wants to send a file. \n Name: " + filename  + "\n Accept the file?";
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
        Object result = DialogDisplayer.getDefault().notify(nd);
        if (!(NotifyDescriptor.OK_OPTION == result)) {
            request.reject();
            return;
        }     
                     
       

        
        
    }

    private static void showFileInOSGui(File file) {
        String osName = System.getProperty("os.name");
        if (osName == null || !osName.toLowerCase().contains("windows"))
            return;

        try {
            new ProcessBuilder("explorer.exe", "/select,"
                + file.getAbsolutePath()).start();
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public Object getValue(String key) {
       if(key.equals(ACTION_ID)){
           return ACTION_ID;
       }
       return ACTION_ID;
    }

    @Override
    public void putValue(String key, Object value) {
        if(key.equals(ACTION_ID)){
           
       }
    }

    @Override
    public void setEnabled(boolean b) {
       
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    }
}