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

import de.fu_berlin.inf.dpp.netbeans.concurrent.watchdog.ConsistencyWatchdogHandler;
import de.fu_berlin.inf.dpp.netbeans.feedback.DataTransferCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.ErrorLogManager;
import de.fu_berlin.inf.dpp.netbeans.feedback.FeedbackManager;
import de.fu_berlin.inf.dpp.netbeans.feedback.FollowModeCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.JumpFeatureUsageCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.ParticipantCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.PermissionChangeCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.ProjectCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.SelectionCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.StatisticManager;
import de.fu_berlin.inf.dpp.netbeans.feedback.SessionDataCollector;
import de.fu_berlin.inf.dpp.netbeans.feedback.TextEditCollector;
import de.fu_berlin.inf.dpp.netbeans.project.SharedResourcesManager;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.SarosCoreSessionContextFactory;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author privateuser
 */
public class SarosNetbeansSessionContextFactory extends SarosCoreSessionContextFactory {
   
    @Override
    public void createNonCoreComponents(ISarosSession session,
        MutablePicoContainer container) {

        // Consistency Watchdog
        container.addComponent(ConsistencyWatchdogHandler.class);
        //if (session.isHost())
        //    container.addComponent(ConsistencyWatchdogServer.class);

        // Statistic Collectors
        /*
         * If you add a new collector here, make sure to add it to the
         * StatisticCollectorTest as well.
         */
        container.addComponent(StatisticManager.class);
        container.addComponent(DataTransferCollector.class);
        container.addComponent(PermissionChangeCollector.class);
        container.addComponent(ParticipantCollector.class);
        container.addComponent(SessionDataCollector.class);
        container.addComponent(TextEditCollector.class);
        container.addComponent(JumpFeatureUsageCollector.class);
        container.addComponent(FollowModeCollector.class);
        container.addComponent(SelectionCollector.class);
        container.addComponent(ProjectCollector.class);

        // Feedback
        container.addComponent(ErrorLogManager.class);
        container.addComponent(FeedbackManager.class);

        // Other
        container.addComponent(FollowingActivitiesManager.class);
        container.addComponent(SharedResourcesManager.class);
    }
}
