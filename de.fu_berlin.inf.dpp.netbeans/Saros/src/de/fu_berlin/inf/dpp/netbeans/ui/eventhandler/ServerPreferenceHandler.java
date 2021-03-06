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


import org.jivesoftware.smack.Connection;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

import de.fu_berlin.inf.dpp.SarosConstants;
import de.fu_berlin.inf.dpp.net.ConnectionState;
import de.fu_berlin.inf.dpp.net.xmpp.IConnectionListener;
import de.fu_berlin.inf.dpp.net.xmpp.XMPPConnectionService;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;

public class ServerPreferenceHandler {

    private IPreferenceStore preferenceStore;

    private IConnectionListener connectionListener = new IConnectionListener() {

        @Override
        public void connectionStateChanged(Connection connection,
            ConnectionState newState) {

            // Adding the feature while state is CONNECTING would be much
            // better, yet it's not possible since the ServiceDiscoveryManager
            // is not available at that point
            if (ConnectionState.CONNECTED.equals(newState)) {
                if (Boolean.getBoolean("de.fu_berlin.inf.dpp.server.SUPPORTED")) {
                    if (preferenceStore
                        .getBoolean(NetbeansPreferenceConstants.SERVER_ACTIVATED)) {
                        addServerFeature(connection);
                    } else {
                        removeServerFeature(connection);
                    }
                }
            }
        }
    };

    public ServerPreferenceHandler(XMPPConnectionService connectionService,
        IPreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;

        connectionService.addListener(connectionListener);
    }

    private void addServerFeature(Connection connection) {
        if (connection == null)
            return;

        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager
            .getInstanceFor(connection);

        if (discoveryManager == null)
            return;

        discoveryManager.addFeature(SarosConstants.NAMESPACE_SERVER);
    }

    private void removeServerFeature(Connection connection) {
        if (connection == null)
            return;

        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager
            .getInstanceFor(connection);

        if (discoveryManager == null)
            return;

        discoveryManager.removeFeature(SarosConstants.NAMESPACE_SERVER);
    }
}
