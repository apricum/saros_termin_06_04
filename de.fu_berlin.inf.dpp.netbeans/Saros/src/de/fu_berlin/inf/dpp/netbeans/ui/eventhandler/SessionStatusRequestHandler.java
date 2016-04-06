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

import java.util.Set;

import org.apache.log4j.Logger;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import de.fu_berlin.inf.dpp.communication.extensions.SessionStatusRequestExtension;
import de.fu_berlin.inf.dpp.communication.extensions.SessionStatusResponseExtension;
import de.fu_berlin.inf.dpp.filesystem.IProject;
import de.fu_berlin.inf.dpp.net.IReceiver;
import de.fu_berlin.inf.dpp.net.ITransmitter;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;

import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.netbeans.ui.util.SWTUtils;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;

public final class SessionStatusRequestHandler {

    private static final Logger LOG = Logger
        .getLogger(SessionStatusRequestHandler.class);

    private final ISarosSessionManager sessionManager;

    private final IReceiver receiver;

    private final ITransmitter transmitter;

    private final IPreferenceStore preferenceStore;

    private final PacketListener statusRequestListener = new PacketListener() {

        @Override
        public void processPacket(final Packet packet) {
            SWTUtils.runSafeSWTAsync(LOG, new Runnable() {

                @Override
                public void run() {
                    handleStatusRequest(new JID(packet.getFrom()));
                }
            });
        }
    };

    public SessionStatusRequestHandler(ISarosSessionManager sessionManager,
        ITransmitter transmitter, IReceiver receiver,
        IPreferenceStore preferenceStore) {
        this.sessionManager = sessionManager;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.preferenceStore = preferenceStore;

        if (Boolean.getBoolean("de.fu_berlin.inf.dpp.server.SUPPORTED")) {
            this.receiver.addPacketListener(statusRequestListener,
                SessionStatusRequestExtension.PROVIDER.getPacketFilter());
        }
    }

    private void handleStatusRequest(JID from) {
        if (!preferenceStore.getBoolean(NetbeansPreferenceConstants.SERVER_ACTIVATED))
            return;

        ISarosSession session = sessionManager.getSarosSession();
        SessionStatusResponseExtension response;

        if (session == null) {
            response = new SessionStatusResponseExtension();
        } else {
            // Don't count the server
            int participants = session.getUsers().size() - 1;

            response = new SessionStatusResponseExtension(participants,
                getSessionDescription(session));
        }

        transmitter.sendPacketExtension(from,
            SessionStatusResponseExtension.PROVIDER.create(response));
    }

    private String getSessionDescription(ISarosSession session) {
        String description = "Projects: ";

        Set<IProject> projects = session.getProjects();
        int i = 0;
        int numOfProjects = projects.size();

        for (IProject project : projects) {
            description += project.getName();

            if (!session.isCompletelyShared(project))
                description += " (partial)";

            if (i < numOfProjects - 1)
                description += ", ";

            i++;
        }

        if (numOfProjects == 0) {
            description += "none";
        }

        return description;
    }
}
