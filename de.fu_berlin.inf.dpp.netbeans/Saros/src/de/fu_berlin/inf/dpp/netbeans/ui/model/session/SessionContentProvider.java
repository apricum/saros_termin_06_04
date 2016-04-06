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
package de.fu_berlin.inf.dpp.netbeans.ui.model.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.activities.SPath;
//import de.fu_berlin.inf.dpp.awareness.AwarenessInformationCollector;
import de.fu_berlin.inf.dpp.editor.AbstractSharedEditorListener;

import de.fu_berlin.inf.dpp.editor.ISharedEditorListener;
import de.fu_berlin.inf.dpp.net.mdns.MDNSService;
import de.fu_berlin.inf.dpp.net.xmpp.roster.AbstractRosterListener;
import de.fu_berlin.inf.dpp.netbeans.EditorManager;
import de.fu_berlin.inf.dpp.netbeans.awareness.AwarenessInformationCollector;
import de.fu_berlin.inf.dpp.netbeans.project.internal.FollowingActivitiesManager;
import de.fu_berlin.inf.dpp.netbeans.project.internal.IFollowModeChangesListener;
import de.fu_berlin.inf.dpp.session.AbstractSessionListener;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISessionListener;
import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.netbeans.ui.model.HeaderElement;
//import de.fu_berlin.inf.dpp.netbeans.ui.model.mdns.MDNSContentProvider;
//import de.fu_berlin.inf.dpp.netbeans.ui.model.mdns.MDNSHeaderElement;
//import de.fu_berlin.inf.dpp.netbeans.ui.model.roster.RosterContentProvider;
//import de.fu_berlin.inf.dpp.netbeans.ui.model.roster.RosterHeaderElement;
import de.fu_berlin.inf.dpp.netbeans.ui.util.SWTUtils;
import java.awt.Font;


public class SessionContentProvider{

  

   

    private HeaderElement sessionHeaderElement;
    private HeaderElement contentHeaderElement;

    private Roster currentRoster;
    private ISarosSession currentSession;

    private FollowingActivitiesManager followingTracker;

    @Inject
    private EditorManager editorManager;

    @Inject
    private AwarenessInformationCollector collector;

    public SessionContentProvider() {
        SarosPluginContext.initComponent(this);

       

        editorManager.addSharedEditorListener(sharedEditorListener);
    }

    private final IFollowModeChangesListener followModeChangesListener = new IFollowModeChangesListener() {

        @Override
        public void followModeChanged() {
            
        }
    };

    private final ISharedEditorListener sharedEditorListener = new AbstractSharedEditorListener() {
        

        
    };

    // TODO call update and not refresh
    private final RosterListener rosterListener = new AbstractRosterListener() {
        // update nicknames
        @Override
        public void entriesUpdated(Collection<String> addresses) {
            
        }

        // update away icons
        @Override
        public void presenceChanged(Presence presence) {
            
        }
    };

    /*
     * as we have a filter installed that will hide contacts from the contact
     * list that are currently part of the session we must currently do a full
     * refresh otherwise the viewer is not correctly updated
     */
    private final ISessionListener sessionListener = new AbstractSessionListener() {
        @Override
        public void userLeft(User user) {
            
        }

        @Override
        public void userJoined(User user) {
            // UserElement userElement = getUserElement(currentRoster, user);
            // if (userElement != null)
            // ViewerUtils.add(viewer, sessionHeaderElement, userElement);

           
        }

        @Override
        public void permissionChanged(User user) {
           
        }

        @Override
        public void userColorChanged(User user) {

            // does not force a redraw
            // ViewerUtils.refresh(viewer, true);

            SWTUtils.runSafeSWTAsync(null, new Runnable() {
                @Override
                public void run() {
                  
                }
            });
        }
    };

    

    private void disposeHeaderElements() {
        if (sessionHeaderElement != null)
            sessionHeaderElement.dispose();

        if (contentHeaderElement != null)
            contentHeaderElement.dispose();

        sessionHeaderElement = null;
        contentHeaderElement = null;
    }

    // TODO abstract !
    private void createHeaders(SessionInput input) {
        
        sessionHeaderElement = new SessionHeaderElement(input, editorManager, collector);

       
    }

    
    public void dispose() {
        if (currentSession != null)
            currentSession.removeListener(sessionListener);

        if (currentRoster != null)
            currentRoster.removeRosterListener(rosterListener);

        editorManager.removeSharedEditorListener(sharedEditorListener);

        if (followingTracker != null)
            followingTracker.removeListener(followModeChangesListener);

       
        disposeHeaderElements();

        /* ENSURE GC */
        currentSession = null;
        currentRoster = null;
        editorManager = null;
        
        followingTracker = null;
    }

    /**
     * Returns {@link RosterGroup}s followed by {@link RosterEntry}s which don't
     * belong to any {@link RosterGroup}.
     */
   
    public Object[] getElements(Object inputElement) {

        if (!(inputElement instanceof SessionInput))
            return new Object[0];

        List<Object> elements = new ArrayList<Object>();

        if (sessionHeaderElement != null)
            elements.add(sessionHeaderElement);

        if (contentHeaderElement != null)
            elements.add(contentHeaderElement);

        return elements.toArray();
    }

    private ISarosSession getSession(Object input) {

        if (!(input instanceof SessionInput))
            return null;

        return ((SessionInput) input).getSession();
    }

    private Roster getRoster(Object input) {
        if (!(input instanceof SessionInput))
            return null;

        Object roster = ((SessionInput) input).getCustomContent();

        if (roster instanceof Roster)
            return (Roster) roster;

        return null;
    }

    private Object getContent(Object input) {
        if (!(input instanceof SessionInput))
            return null;

        return ((SessionInput) input).getCustomContent();
    }
}
