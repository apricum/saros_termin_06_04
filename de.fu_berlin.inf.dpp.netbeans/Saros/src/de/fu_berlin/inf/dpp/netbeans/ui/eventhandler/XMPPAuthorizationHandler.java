/*
 * Copyright (C) 2015 Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
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

import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.net.xmpp.subscription.SubscriptionHandler;
import de.fu_berlin.inf.dpp.net.xmpp.subscription.SubscriptionListener;
import de.fu_berlin.inf.dpp.netbeans.ui.util.SwingUtils;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class XMPPAuthorizationHandler {
    
    private static final Logger LOG = Logger
        .getLogger(XMPPAuthorizationHandler.class);

    private final SubscriptionHandler subscriptionHandler;

    private final SubscriptionListener subscriptionListener = new SubscriptionListener() {

            @Override
            public void subscriptionRequestReceived(final JID jid) {

                SwingUtils.runSafeSWTAsync(LOG, new Runnable() {
                    @Override
                    public void run() {
                        handleAuthorizationRequest(jid);
                    }
                });
            }
        };

        public XMPPAuthorizationHandler(
               final SubscriptionHandler subscriptionHandler) {
               this.subscriptionHandler = subscriptionHandler;
               this.subscriptionHandler
                   .addSubscriptionListener(subscriptionListener);
           }
        private void handleAuthorizationRequest(final JID jid) {

                int accept = JOptionPane.showConfirmDialog(null, "subscription request", null, JOptionPane.YES_NO_OPTION);
                /*
               boolean accept = MessageDialog
                   .openConfirm(
                       SWTUtils.getShell(),
                       Messages.SubscriptionManager_incoming_subscription_request_title,
                       MessageFormat
                           .format(
                               Messages.SubscriptionManager_incoming_subscription_request_message,
                               jid.getBareJID()));
                               */

               if (accept == 0)
                   subscriptionHandler.addSubscription(jid, true);
               else
                   subscriptionHandler.removeSubscription(jid);
           }

}
