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


import de.fu_berlin.inf.dpp.session.AbstractSessionListener;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.ISessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.ISessionListener;
import de.fu_berlin.inf.dpp.session.NullSessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.SessionEndReason;
import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.netbeans.ui.Messages;
import de.fu_berlin.inf.dpp.netbeans.ui.util.ModelFormatUtils;
import de.fu_berlin.inf.dpp.netbeans.ui.views.SarosView;

/**
 * Simple handler that informs the local user of the status changes for users in
 * the current session.
 * 
 * @author srossbach
 */
public class UserStatusChangeHandler {

    private final ISessionLifecycleListener sessionLifecycleListener = new NullSessionLifecycleListener() {
        @Override
        public void sessionStarting(ISarosSession session) {
            session.addListener(sessionListener);
        }

        @Override
        public void sessionEnded(ISarosSession session, SessionEndReason reason) {
            session.removeListener(sessionListener);
        }

    };

    private ISessionListener sessionListener = new AbstractSessionListener() {

        /*
         * save to call SarosView.showNotification because it uses asyncExec
         * calls
         */

        @Override
        public void permissionChanged(User user) {

            if (user.isLocal()) {
                SarosView
                    .showNotification(
                        Messages.UserStatusChangeHandler_permission_changed,
                        ModelFormatUtils
                            .format(
                                Messages.UserStatusChangeHandler_you_have_now_access,
                                user,
                                user.hasWriteAccess() ? Messages.UserStatusChangeHandler_write
                                    : Messages.UserStatusChangeHandler_read_only));
            } else {
                SarosView
                    .showNotification(
                        Messages.UserStatusChangeHandler_permission_changed,
                        ModelFormatUtils.format(
                            Messages.UserStatusChangeHandler_he_has_now_access,
                            user,
                            user.hasWriteAccess() ? Messages.UserStatusChangeHandler_write
                                : Messages.UserStatusChangeHandler_read_only));

            }
        }

        @Override
        public void userJoined(User user) {

            SarosView.showNotification(
                Messages.UserStatusChangeHandler_user_joined, ModelFormatUtils
                    .format(Messages.UserStatusChangeHandler_user_joined_text,
                        user));
        }

        @Override
        public void userLeft(User user) {
            SarosView.showNotification(
                Messages.UserStatusChangeHandler_user_left, ModelFormatUtils
                    .format(Messages.UserStatusChangeHandler_user_left_text,
                        user));
        }
    };

    public UserStatusChangeHandler(ISarosSessionManager sessionManager) {
        sessionManager.addSessionLifecycleListener(sessionLifecycleListener);
    }
}
