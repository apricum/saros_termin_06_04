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
package de.fu_berlin.inf.dpp.netbeans.ui.eventhandler;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.jface.preference.IPreferenceStore;
//import org.openide.util.NbPreferences;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import de.fu_berlin.inf.dpp.communication.extensions.JoinSessionRejectedExtension;
import de.fu_berlin.inf.dpp.communication.extensions.JoinSessionRequestExtension;
import de.fu_berlin.inf.dpp.filesystem.IResource;
import de.fu_berlin.inf.dpp.net.IReceiver;
import de.fu_berlin.inf.dpp.net.ITransmitter;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceStore;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferencesStoreAdapter;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferencesStoreAdapter;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.netbeans.ui.util.CollaborationUtils;
import de.fu_berlin.inf.dpp.netbeans.ui.util.SWTUtils;


public final class JoinSessionRequestHandler {

    private static final Logger LOG = Logger
        .getLogger(JoinSessionRequestHandler.class);

    private final ISarosSessionManager sessionManager;

    private final ITransmitter transmitter;

    private final IReceiver receiver;

    private final NetbeansPreferenceStore preferenceStore;

    private final PacketListener joinSessionRequestListener = new PacketListener() {

        @Override
        public void processPacket(final Packet packet) {
            SWTUtils.runSafeSWTAsync(LOG, new Runnable() {

                @Override
                public void run() {
                    LOG.info("Session Invitation in run in join SessionRequestHandler.");
                    handleInvitationRequest(new JID(packet.getFrom()),
                        JoinSessionRequestExtension.PROVIDER.getPayload(packet));
                }
            });
        }
    };

    public JoinSessionRequestHandler(ISarosSessionManager sessionManager,
        ITransmitter transmitter, IReceiver receiver,
        NetbeansPreferenceStore preferenceStore) {
        this.sessionManager = sessionManager;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.preferenceStore = preferenceStore;

        if (Boolean.getBoolean("de.fu_berlin.inf.dpp.server.SUPPORTED")) {
            this.receiver.addPacketListener(joinSessionRequestListener,
                JoinSessionRequestExtension.PROVIDER.getPacketFilter());
        }
    }

    private void handleInvitationRequest(JID from,
        JoinSessionRequestExtension extension) {

        ISarosSession session = sessionManager.getSarosSession();
       
        //if (session != null && !session.isHost())
          //  return;
/*
       if (!preferenceStore.getBoolean(NetbeansPreferenceConstants.SERVER_ACTIVATED)
            || (session != null && extension.isNewSessionRequested())
            || (session == null && !extension.isNewSessionRequested())) {
            sendRejection(from);
            return;
        }
*/
        List<JID> list = Collections.singletonList(from);

        // TODO remove calls to CollaborationUtils
       // if (extension.isNewSessionRequested()) {
            CollaborationUtils.startSession(new ArrayList<IResource>(), list);
        //} else {
          //  CollaborationUtils.addContactsToSession(list);
        //}
    }

    private void sendRejection(JID to) {
        transmitter.sendPacketExtension(to, JoinSessionRejectedExtension.PROVIDER
            .create(new JoinSessionRejectedExtension()));
    }
}

