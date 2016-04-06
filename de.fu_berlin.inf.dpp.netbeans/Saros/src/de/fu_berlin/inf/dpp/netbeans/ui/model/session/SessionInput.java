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

import de.fu_berlin.inf.dpp.session.ISarosSession;
//import de.fu_berlin.inf.dpp.netbeans.ui.widgets.viewer.session.XMPPSessionDisplayComposite;

/**
 * Instances of this class bundle a custom content and an {@link ISarosSession}
 * instance for use with {@link XMPPSessionDisplayComposite}.
 */
public class SessionInput {

    private final Object customContent;
    private final ISarosSession session;

    public SessionInput(ISarosSession session, Object additionalContent) {
        this.session = session;
        this.customContent = additionalContent;
    }

    public Object getCustomContent() {
        return customContent;
    }

    public ISarosSession getSession() {
        return session;
    }
}
