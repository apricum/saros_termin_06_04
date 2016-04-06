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
package de.fu_berlin.inf.dpp.netbeans.ui.model.roster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosConstants;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.net.xmpp.discovery.DiscoveryManager;
import de.fu_berlin.inf.dpp.net.xmpp.discovery.DiscoveryManagerListener;
import de.fu_berlin.inf.dpp.netbeans.ui.model.TreeContentProvider;

/**
 * {@link IContentProvider} for use in conjunction with a {@link Roster} input.
 * <p>
 * Automatically keeps track of changes of contacts.
 * 
 * @author bkahlert
 */
public final class RosterContentProvider extends TreeContentProvider {

    
    private volatile Roster roster;

    @Inject
    private volatile DiscoveryManager discoveryManager;

    private final DiscoveryManagerListener discoveryManagerListener = new DiscoveryManagerListener() {
        @Override
        public void featureSupportUpdated(final JID jid, String feature,
            boolean isSupported) {

            // TODO maybe use display.timerExec to avoid massive refresh calls
            //ViewerUtils.refresh(viewer, true);
        }
    };

    private final RosterListener rosterListener = new RosterListener() {
        @Override
        public void presenceChanged(Presence presence) {
            //ViewerUtils.refresh(viewer, true);

            final String user = presence.getFrom();

            if (user != null)
                querySarosSupport(Collections.singletonList(user));
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            //ViewerUtils.refresh(viewer, true);
            querySarosSupport(addresses);
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            //ViewerUtils.refresh(viewer, true);
        }

        @Override
        public void entriesAdded(Collection<String> addresses) {
            //ViewerUtils.refresh(viewer, true);
            querySarosSupport(addresses);
        }
    };

    public RosterContentProvider() {
        SarosPluginContext.initComponent(this);

        discoveryManager.addDiscoveryManagerListener(discoveryManagerListener);
    }

    
    public void inputChanged() {
       
    }

    @Override
    public void dispose() {
        if (roster != null)
            roster.removeRosterListener(rosterListener);

        discoveryManager
            .removeDiscoveryManagerListener(discoveryManagerListener);

        roster = null;
        discoveryManager = null;
    }

    /**
     * Returns {@link RosterGroup}s followed by {@link RosterEntry}s which don't
     * belong to any {@link RosterGroup}.
     */
    @Override
    public Object[] getElements(Object inputElement) {

        if (!(inputElement instanceof Roster))
            return new Object[0];

        Roster roster = (Roster) inputElement;
        final List<Object> elements = new ArrayList<Object>();

        /*
         * always show contacts that support Saros regardless of any other
         * source, i.e the user is online with:
         * 
         * alice@foo/Saros (<- has Saros support)
         * 
         * and
         * 
         * alice@foo/Pidgen
         * 
         * so always display alice@foo/Saros
         */

        for (RosterGroup group : roster.getGroups()) {
            
        }

        elements
            .addAll(filterRosterEntryElements(createRosterEntryElements(roster
                .getUnfiledEntries())));

        return elements.toArray();
    }

    private List<RosterEntryElement> createRosterEntryElements(
        final Collection<RosterEntry> entries) {

        final List<RosterEntryElement> elements = new ArrayList<RosterEntryElement>();

        for (final RosterEntry entry : entries)
            elements.add(createRosterEntryElement(new JID(entry.getUser())));

        return elements;
    }

    private RosterEntryElement createRosterEntryElement(final JID jid) {
        final Boolean isSarosSupport = discoveryManager.isFeatureSupported(jid,
            SarosConstants.XMPP_FEATURE_NAMESPACE);

        return new RosterEntryElement(roster, jid,
            isSarosSupport == null ? false : isSarosSupport);
    }

    private void querySarosSupport(Collection<String> users) {

        final Roster currentRoster = roster;
        final DiscoveryManager currentDiscoveryManager = discoveryManager;

        if (currentRoster == null || currentDiscoveryManager == null)
            return;

        for (final String user : users) {
            final JID jid = new JID(user);

            if (!currentRoster.getPresence(jid.getBase()).isAvailable())
                continue;

            Boolean sarosSupported = discoveryManager.isFeatureSupported(jid,
                SarosConstants.XMPP_FEATURE_NAMESPACE);

            if (sarosSupported == null)
                discoveryManager.queryFeatureSupport(jid,
                    SarosConstants.XMPP_FEATURE_NAMESPACE, true);
        }
    }

    /**
     * Filters the given roster entry elements by removing entries which bare
     * JID are equal. Furthermore if two entries are equal the one with possible
     * Saros support will always be kept and the other one will be discarded.
     */
    private final List<RosterEntryElement> filterRosterEntryElements(
        final Collection<RosterEntryElement> elements) {

        final Map<JID, RosterEntryElement> filteredElements = new HashMap<JID, RosterEntryElement>(
            elements.size());

        for (final RosterEntryElement element : elements) {

            final JID bareJID = element.getJID().getBareJID();

            final RosterEntryElement filteredElement = filteredElements
                .get(bareJID);

            if (filteredElement != null && filteredElement.isSarosSupported()) {
                continue;
            } else if (filteredElement != null
                && !filteredElement.isSarosSupported()) {
                filteredElements.remove(bareJID);
            }

            filteredElements.put(bareJID, element);
        }

        return new ArrayList<RosterEntryElement>(filteredElements.values());
    }
}
