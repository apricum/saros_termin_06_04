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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.log4j.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import static org.netbeans.api.project.ui.OpenProjects.getDefault;
import org.netbeans.modules.java.j2seproject.api.J2SEProjectBuilder;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * ListenerForIncomingTextEditActivity modifies the target file in order to apply the incoming changes,
 * the filecontaining project and the files are created if they don't exits
 */
public class ListenerForIncomingTextEditActivity implements IListenerForIncomingTextEditActivity {

    private TextEditActivity currentIncomingTextEditActivity;
    private static final Logger LOG = Logger.getLogger(ListenerForIncomingTextEditActivity.class);

    public ListenerForIncomingTextEditActivity() {
    }

    /**
     * modifies the target file in order to apply the incoming changes,
     * the filecontaining project and the files are created if they don't exits
     */
    @Override
    public void processIncomingDocumentModification(TextEditActivity incomingTextEditActivity) {

        currentIncomingTextEditActivity = incomingTextEditActivity;
        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /*
                     * path and classpath of the filecontaining project
                     * are passed to variables
                     */
                    SPath currentSpath = currentIncomingTextEditActivity.getPath();

                    String relativePathOfFile = getRelativePathOfFile(currentSpath);

                    Path absolutePathOfFileContainingProject = getAbsolutePathOfFileContainingProject(currentSpath);

                    File currentProject = new File(absolutePathOfFileContainingProject.toString());

                    openTheFileContainingProject(currentProject, absolutePathOfFileContainingProject.toString());

                    Document currentDocument = createAndOpenTheFile(absolutePathOfFileContainingProject, relativePathOfFile, currentProject);

                    modifyTheFileAccordingToTheIncomingChanges(currentIncomingTextEditActivity, currentDocument);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            LOG.error("Message couldn't be processed: ", ex);
        }
    }

    private void createJavaProject(File tempProjectFile, String thisTempProjectString) {
        J2SEProjectBuilder thisBuilder = new J2SEProjectBuilder(tempProjectFile, thisTempProjectString);

        try {
            JavaPlatform platform = JavaPlatformManager.getDefault().getDefaultPlatform();
            thisBuilder = thisBuilder.addDefaultSourceRoots();
            thisBuilder = thisBuilder.setJavaPlatform(platform);
            thisBuilder = thisBuilder.setManifest(thisTempProjectString);
            thisBuilder = thisBuilder.setBuildXmlName("build");
            thisBuilder.build();

        } catch (IOException ex) {
            LOG.error("Project couldn't be created: ", ex);
        }
    }

    private void openTheFileContainingProject(File currentProject, String absolutePathOfFileContainingProject) {

        FileObject fileObjectForFileContainingProject = FileUtil.toFileObject(currentProject);

        /*
         * creates a new j2se project because
         * the project that contains the modified file doesn't exist
         * part of the plugin prototype for testing purposes
         */
        if (fileObjectForFileContainingProject == null) {

            createJavaProject(currentProject, absolutePathOfFileContainingProject);
            fileObjectForFileContainingProject = FileUtil.toFileObject(currentProject);
        }

        Project projectToOpen = null;

        /*
         * opens the filecontaining project in netbeans
         */
        try {

            projectToOpen = ProjectManager.getDefault().findProject(fileObjectForFileContainingProject);
        } catch (IOException | IllegalArgumentException ex) {
            LOG.error("Project couldn't be found: ", ex);
        }

        Project[] listOfFileConatiningProjects = {projectToOpen};
        getDefault().open(listOfFileConatiningProjects, true);
    }

    private Document openTheFile(FileObject fileObjectForCurrentFile) {
        /*
         * opens the java document in the editor
         */
        DataObject dataObjectForCurrentFile = null;
        try {
            dataObjectForCurrentFile = DataObject.find(fileObjectForCurrentFile);
        } catch (DataObjectNotFoundException ex) {
            LOG.error("File couldn't be found: ", ex);
        }

        EditorCookie currentEditorCookie = dataObjectForCurrentFile.getLookup().lookup(EditorCookie.class);
        Document currentDocument = currentEditorCookie.getDocument();

        if (currentDocument == null) {
            try {
                currentDocument = currentEditorCookie.openDocument();
            } catch (IOException ex) {
                LOG.error("File couldn't be opened: ", ex);

            }
        }
        return currentDocument;

    }

    private void modifyTheFileAccordingToTheIncomingChanges(TextEditActivity currentTextEditActivity, Document currentDocument) {


        if (currentDocument == null) {
            return;
        }

        /*
         * modifies the file according to the incoming changes
         */
        int offset = currentTextEditActivity.getOffset();
        String text = currentTextEditActivity.getText();
        String replacedText = currentTextEditActivity.getReplacedText();
        int textLength = text.length();
        int replacedTextLength = replacedText.length();

        if (textLength > replacedTextLength) {
            try {
                while (currentDocument.getLength() <= offset) {
                    currentDocument.insertString(currentDocument.getLength(), "\r\n", null);

                }
                currentDocument.insertString(offset, text, null);
            } catch (BadLocationException ex) {
                LOG.error("Text couldn't be inserted: ", ex);

            }
        } else {
            try {
                while (currentDocument.getLength() <= offset) {
                    currentDocument.insertString(currentDocument.getLength(), "\r\n", null);

                }
                currentDocument.remove(offset, replacedTextLength);
            } catch (BadLocationException ex) {
                LOG.error("Text couldn't be removed: ", ex);
            }
        }
    }

    private FileObject createFile(File currentFile, File currentProject) {

        FileObject fileObjectForCurrentFile = null;
        try {
            fileObjectForCurrentFile = FileUtil.createData(currentFile);
            ClasspathInfo currentClasspathInfo = ClasspathInfo.create(currentProject);
            JavaSource.create(currentClasspathInfo, fileObjectForCurrentFile);


        } catch (IOException ex) {
            LOG.error("Java File couldn't be created: ", ex);
        }
        return fileObjectForCurrentFile;
    }

    private Document createAndOpenTheFile(Path absolutePathOfFileContainingProject, String relativePathOfFile, File currentProject) {
        /*
         * computes the absolute path of the file and
         * creates a fileobject that represents the existing file
         */
        String absolutePathOfFile = absolutePathOfFileContainingProject.toString() + "\\" + relativePathOfFile;
        File currentFile = new File(absolutePathOfFile);
        FileObject fileObjectForCurrentFile = FileUtil.toFileObject(currentFile);

        /*
         * the file didn't exist and is going to be created
         * as a javasource file
         * just for testing purposes
         */
        if (fileObjectForCurrentFile == null) {
            fileObjectForCurrentFile = createFile(currentFile, currentProject);
        }

        Document currentDocument = openTheFile(fileObjectForCurrentFile);
        return currentDocument;
    }

    private String getRelativePathOfFile(SPath currentSpath) {
        NetBeansPathImpl currentNBPath = (NetBeansPathImpl) currentSpath.getProjectRelativePath();
        String relativePathOfFile = currentNBPath.getRelativePath();
        return relativePathOfFile;
    }

    private Path getAbsolutePathOfFileContainingProject(SPath currentSpath) {
        NetbeansProjectImpl currentNBProject = (NetbeansProjectImpl) currentSpath.getProject();
        Path absolutePathOfFileContainingProject = currentNBProject.returnAbsolutePathOfProjectPassed();
        return absolutePathOfFileContainingProject;
    }
}
