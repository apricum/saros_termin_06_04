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
package de.fu_berlin.inf.dpp.netbeans.project.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.picocontainer.Startable;

import de.fu_berlin.inf.dpp.activities.StartFollowingActivity;
import de.fu_berlin.inf.dpp.activities.StopFollowingActivity;
import de.fu_berlin.inf.dpp.annotations.Component;

import de.fu_berlin.inf.dpp.editor.AbstractSharedEditorListener;
import de.fu_berlin.inf.dpp.editor.IEditorManager;
import de.fu_berlin.inf.dpp.editor.ISharedEditorListener;
import de.fu_berlin.inf.dpp.netbeans.awareness.AwarenessInformationCollector;
import de.fu_berlin.inf.dpp.session.AbstractActivityConsumer;
import de.fu_berlin.inf.dpp.session.AbstractActivityProducer;
import de.fu_berlin.inf.dpp.session.AbstractSessionListener;
import de.fu_berlin.inf.dpp.session.IActivityConsumer;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISessionListener;
import de.fu_berlin.inf.dpp.session.User;

/**
 * This manager is responsible for distributing knowledge about changes in
 * follow modes between session participants. It both produces and consumes
 * activities.
 * 
 * @author Alexander Waldmann (contact@net-corps.de)
 */
@Component(module = "core")
public class FollowingActivitiesManager extends AbstractActivityProducer
    implements Startable {

    private static final Logger LOG = Logger
        .getLogger(FollowingActivitiesManager.class);

    private final List<IFollowModeChangesListener> listeners = new CopyOnWriteArrayList<IFollowModeChangesListener>();

    private final ISarosSession session;

    private final AwarenessInformationCollector collector;

    private final IEditorManager editor;

    private final ISharedEditorListener followModeListener = new AbstractSharedEditorListener() {
        @Override
        public void followModeChanged(User target, boolean isFollowed) {

            if (isFollowed) {
                fireActivity(new StartFollowingActivity(session.getLocalUser(),
                    target));
            } else {
                fireActivity(new StopFollowingActivity(session.getLocalUser()));
            }
        }
    };

    private final IActivityConsumer consumer = new AbstractActivityConsumer() {
        @Override
        public void receive(StartFollowingActivity activity) {
            final User source = activity.getSource();
            final User target = activity.getFollowedUser();

            if (LOG.isDebugEnabled())
                LOG.debug("received new follow mode from: " + source
                    + " , followed: " + target);

            collector.setUserFollowing(source, target);
            notifyListeners();
        }

        @Override
        public void receive(StopFollowingActivity activity) {
            User source = activity.getSource();

            if (LOG.isDebugEnabled())
                LOG.debug("user " + source + " stopped follow mode");

            collector.setUserFollowing(source, null);
            notifyListeners();
        }
    };

    private final ISessionListener sessionListener = new AbstractSessionListener() {
        @Override
        public void userLeft(final User user) {
            collector.setUserFollowing(user, null);
            notifyListeners();
        }
    };

    public FollowingActivitiesManager(final ISarosSession session,
        final AwarenessInformationCollector collector,
        final IEditorManager editor) {
        this.session = session;
        this.collector = collector;
        this.editor = editor;
    }

    @Override
    public void start() {
        collector.flushFollowModes();
        session.addActivityProducer(this);
        session.addActivityConsumer(consumer);
        session.addListener(sessionListener);
        editor.addSharedEditorListener(followModeListener);
    }

    @Override
    public void stop() {
        session.removeActivityProducer(this);
        session.removeActivityConsumer(consumer);
        session.removeListener(sessionListener);
        editor.removeSharedEditorListener(followModeListener);
        collector.flushFollowModes();
    }

    private void notifyListeners() {
        for (IFollowModeChangesListener listener : listeners)
            listener.followModeChanged();
    }

    public void addListener(IFollowModeChangesListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IFollowModeChangesListener listener) {
        listeners.remove(listener);
    }
}
