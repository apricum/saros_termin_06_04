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

import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.activities.TextEditActivity;
import de.fu_berlin.inf.dpp.concurrent.jupiter.Operation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.internal.text.InsertOperation;
import de.fu_berlin.inf.dpp.session.User;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import org.apache.log4j.Logger;

/**
 * ListenerForOutgoingTextEditActivity creates a BufferedWriter, the
 * parameters of a given TexteditActivity for outgoing changes of the modified file
 * are passed to variables and are written into a textfile via the BuffferedWriter
 */
public class ListenerForOutgoingTextEditActivity implements IListenerForOutgoingTextEditActivity {

    private BufferedWriter outgoingStream = null;
    private String replacedTextWithoutNewLine;
    private String textWithoutNewLine;
    private static final Logger LOG = Logger.getLogger(ListenerForOutgoingTextEditActivity.class);

    public ListenerForOutgoingTextEditActivity() {
        try {
            outgoingStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("MyTextEditActivitiesOutput.txt"), "UTF-8"));
        } catch (FileNotFoundException ex) {
            LOG.error("FileOutputStream couldn't be created: ", ex);
        } catch (IOException ex) {
            LOG.error("BufferedWriter couldn't be created: ", ex);
        }
    }

    /**
     * parameters of a TexteditActivity for a changed file are passed to variables and
     * are written into a textfile via a BuffferedWriter
     */
    @Override
    public void processOutgoingDocumentModification(TextEditActivity outgoingTextEditActivity) {

        int offset = outgoingTextEditActivity.getOffset();
        SPath currentSPath = outgoingTextEditActivity.getPath();
        NetBeansPathImpl currentNBPath = (NetBeansPathImpl) currentSPath.getProjectRelativePath();
        NetbeansProjectImpl currentNBProject = (NetbeansProjectImpl) currentSPath.getProject();
        Path absolutePathToProject = currentNBProject.returnAbsolutePathOfProject();
        String relativePathToFile = currentNBPath.getRelativePath();
        User currentUser = outgoingTextEditActivity.getSource();
        String text = outgoingTextEditActivity.getText();
        String replacedText = outgoingTextEditActivity.getReplacedText();
        Operation currentOperation = outgoingTextEditActivity.toOperation();
        String currentModification;

        /*
         * remove all newline characters
         */
        if (currentOperation instanceof InsertOperation) {
            currentModification = "insert";
            textWithoutNewLine = text.replaceAll("(\\r|\\n)", "#1#");
            replacedTextWithoutNewLine = "";
        } else {
            currentModification = "remove";
            replacedTextWithoutNewLine = replacedText.replaceAll("(\\r|\\n)", "#1#");
            textWithoutNewLine = "";

        }

        try {
            outgoingStream.write(currentUser.toString() + "#2#" + offset + "#2#" + textWithoutNewLine + "#2#" + replacedTextWithoutNewLine + "#2#" + relativePathToFile + "#2#" + currentModification + "#2#" + text.length() + "#2#" + replacedText.length() + "#2#" + absolutePathToProject.toString() + "\r\n");
            outgoingStream.flush();
        } catch (IOException ex) {
            LOG.error("Couldn't write to output stream: ", ex);
        }
    }
}
