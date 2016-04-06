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


import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import de.fu_berlin.inf.dpp.net.util.XMPPUtils;
import de.fu_berlin.inf.dpp.net.xmpp.JID;

/**
 * Wrapper for {@link RosterEntryElement RosterEntryElements} in use with
 * {@link Viewer Viewers}
 * 
 * @author bkahlert
 */
public class RosterEntryElement{

    private final Roster roster;
    private final JID jid;

    private final boolean hasSarosSupport;

    public RosterEntryElement(Roster roster, JID jid, boolean hasSarosSupport) {

        this.roster = roster;
        this.jid = jid;
        this.hasSarosSupport = hasSarosSupport;
    }

    protected RosterEntry getRosterEntry() {
        if (roster == null)
            return null;

        return roster.getEntry(jid.getBase());
    }

  
    

    public boolean isOnline() {
        if (roster == null)
            return false;

        return roster.getPresence(jid.getBase()).isAvailable();
    }

    public JID getJID() {
        return jid;
    }

    public boolean isSarosSupported() {
        return hasSarosSupport;
    }

   

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RosterEntryElement)) {
            return false;
        }

        RosterEntryElement rosterEntryElement = (RosterEntryElement) obj;
        return (jid == null ? rosterEntryElement.jid == null : jid
            .equals(rosterEntryElement.jid));
    }

    @Override
    public int hashCode() {
        return (jid != null) ? jid.hashCode() : 0;
    }
}
