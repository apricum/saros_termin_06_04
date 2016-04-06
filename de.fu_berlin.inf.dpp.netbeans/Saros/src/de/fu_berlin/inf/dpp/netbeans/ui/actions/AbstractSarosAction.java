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
package de.fu_berlin.inf.dpp.netbeans.ui.actions;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
//import de.fu_berlin.inf.dpp.core.Saros;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import org.apache.log4j.Logger;
import org.picocontainer.annotations.Inject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for all Saros actions
 */
public abstract class AbstractSarosAction {
    protected static final Logger LOG = Logger
        .getLogger(AbstractSarosAction.class);

   // @Inject
    //protected Saros saros;

    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

    protected AbstractSarosAction() {
        SarosPluginContext.initComponent(this);
    }

    protected void actionPerformed() {
        for (ActionListener actionListener : actionListeners) {
            actionListener
                .actionPerformed(new ActionEvent(this, 0, getActionName()));
        }
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public abstract String getActionName();

    public abstract void execute();
}
