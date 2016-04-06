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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle.Messages;

/* The following annotations are declarative registrations
 * of an always enabled Action,
 * the action LoadFileWithIncomingTextModifications with the label "Load File"
 * is assigned to the Menu "File" from where it can be invoked
 */
@ActionID(
        category = "File",
        id = "de.fu_berlin.inf.dpp.LoadFileWithIncomingTextModifications")
@ActionRegistration(
        displayName = "#CTL_LoadFileWithIncomingTextModifications")
@ActionReference(path = "Menu/File", position = 0)
@Messages("CTL_LoadFileWithIncomingTextModifications=Load File")
/**
 * LoadFileWithIncomingTextModifications loads a textfile with changes that have to be applied to
 * files,
 * the changes are read line by line and passed on to the EditorManager
 * for further processing
 * this class exists just for testing purposes because the
 * future input is going to be a stream
 */
public final class LoadFileWithIncomingTextModifications implements ActionListener {

    private BufferedReader myBufferedReader = null;
    private static final Logger LOG = Logger.getLogger(LoadFileWithIncomingTextModifications.class);
    private List<String[]> tempList = null;
    private final EditorManager editorManager = EditorManager.getInstance();

    public LoadFileWithIncomingTextModifications() {
    }

    /**
     * load textfile with incoming changes
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            File loadedFile = InstalledFileLocator.getDefault().locate("MyTextEditActivitiesInput.txt", null, false);
            if (loadedFile == null) {
                LOG.error("Class LoadFileForIncomingTextChanges.java - Loading input file failed because the requested file doesn't exist");
                return;
            }

            myBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(loadedFile), "UTF-8"));
            String line;
            String[] result;

            tempList = new ArrayList<>();

            while ((line = myBufferedReader.readLine()) != null) {
                result = line.split("#2#");
                String tempText = result[2].replaceAll("#1#", "\r\n");
                result[2] = tempText;
                tempText = result[3].replaceAll("#1#", "\r\n");
                result[3] = tempText;
                tempList.add(result);
            }
            myBufferedReader.close();
            editorManager.startTimerForProcessingIncomingDocumentModification((ArrayList<String[]>) tempList);
            tempList = null;
        } catch (IOException ex) {

            LOG.error("Couldn't read input file: ", ex);
        }
    }
}
