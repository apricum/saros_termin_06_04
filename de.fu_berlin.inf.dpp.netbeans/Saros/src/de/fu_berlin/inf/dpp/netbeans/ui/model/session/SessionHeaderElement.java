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

import de.fu_berlin.inf.dpp.netbeans.EditorManager;
import de.fu_berlin.inf.dpp.netbeans.awareness.AwarenessInformationCollector;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;


import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.session.internal.SarosSession;

import de.fu_berlin.inf.dpp.netbeans.ui.Messages;
import de.fu_berlin.inf.dpp.netbeans.ui.model.HeaderElement;

import java.awt.Font;

/**
 * Container {@link TreeElement} for a {@link SarosSession}
 * 
 * @author bkahlert
 */
public class SessionHeaderElement extends HeaderElement {
    private final SessionInput sessionInput;
    private final EditorManager editorManager;
    private final AwarenessInformationCollector collector;

    public SessionHeaderElement(
        final SessionInput sessionInput, final EditorManager editorManager,
        AwarenessInformationCollector collector) {

       
        this.sessionInput = sessionInput;
        this.editorManager = editorManager;
        this.collector = collector;
    }

   

    

    

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(sessionInput);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        SessionHeaderElement other = (SessionHeaderElement) obj;
        return ObjectUtils.equals(sessionInput, other.sessionInput);
    }
}
