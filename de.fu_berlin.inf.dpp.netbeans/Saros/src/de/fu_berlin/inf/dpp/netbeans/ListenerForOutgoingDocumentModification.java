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
package de.fu_berlin.inf.dpp.netbeans;

import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.session.User;
import java.nio.file.Path;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
import static org.netbeans.lib.editor.util.swing.DocumentUtilities.getModificationText;


/**
 * ListenerForOutgoingDocumentModification reacts to DocumentEvents
 * that are caused when the content of the selected file was changed
 */
public class ListenerForOutgoingDocumentModification implements DocumentListener {

    private enum ModificationMethod {

        INSERT, REMOVE, UPDATE
    }
    private final EditorManager editorManager;
    private static final Logger LOG = Logger.getLogger(ListenerForOutgoingDocumentModification.class);

    public ListenerForOutgoingDocumentModification() {
        this.editorManager = EditorManager.getInstance();
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        forwardOutgoingDocumentModification(de, ModificationMethod.INSERT);
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        forwardOutgoingDocumentModification(de, ModificationMethod.REMOVE);
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        forwardOutgoingDocumentModification(de, ModificationMethod.UPDATE);
    }

    /*
     * Get the textchanges that were made to the selected file in the editor
     * and passes them to the EditorManager
     */
    private void forwardOutgoingDocumentModification(DocumentEvent modificationEvent, ModificationMethod modificationMethod) {

        String modifiedText = getModificationText(modificationEvent);
        modifiedText = modifiedText == null ? "" : modifiedText;
        ModificationMethod thisCurrentModificationMethod = modificationMethod;
        String replacedText;
        String text;
        Path pathOfCurrentProject = editorManager.getAbsolutePathForCurrentProject();

        if (thisCurrentModificationMethod.equals(ModificationMethod.INSERT)) {
            replacedText = "";
            text = modifiedText;
        } else {
            replacedText = modifiedText;
            text = "";
        }
        JID thisJID = new JID("Apricum_Test_JID");
        User thisUser = new User(thisJID, false, true, 1, 1);
        LOG.trace("OUTGOING CHANGES: user: " + thisUser.toString() + ", offset: " + String.valueOf(modificationEvent.getOffset()) + ", text: " + text + ", replacedText: " + replacedText + ", path: " + (editorManager.getRelativePathOfCurrentFile()).toString() + ", length: " + String.valueOf(modificationEvent.getLength()) + ", method: " + thisCurrentModificationMethod.toString() + ", pathOfProject: " + pathOfCurrentProject.toString());
        editorManager.processOutgoingDocumentModification(thisUser, modificationEvent.getOffset(), text, replacedText, editorManager.getRelativePathOfCurrentFile());
    }
}
