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
import de.fu_berlin.inf.dpp.editor.IEditorManager;
import de.fu_berlin.inf.dpp.editor.ISharedEditorListener;
import de.fu_berlin.inf.dpp.filesystem.IProject;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.netbeans.editor.RemoteEditorManager;
import de.fu_berlin.inf.dpp.netbeans.editor.internal.EditorAPI;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;
import de.fu_berlin.inf.dpp.session.AbstractActivityConsumer;
import de.fu_berlin.inf.dpp.session.AbstractActivityProducer;
import de.fu_berlin.inf.dpp.session.AbstractSessionListener;
import de.fu_berlin.inf.dpp.session.IActivityConsumer;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.ISessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.ISessionListener;
import de.fu_berlin.inf.dpp.session.NullSessionLifecycleListener;
import de.fu_berlin.inf.dpp.session.User;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.text.Document;
import org.apache.log4j.Logger;
import org.netbeans.api.project.Project;
import org.openide.LifecycleManager;

/**
 * EditorManager manages the incoming and outgoing textchanges
 * TextEditActivities are created and passed to the processing
 * classes and methods
 */
public class EditorManager extends AbstractActivityProducer implements
    IEditorManager {

    private ISarosSession session;
    private static final EditorManager uniqueEditorManagerInstance = new EditorManager();
    private static final Logger LOG = Logger.getLogger(EditorManager.class);
    private Document currentDoc;
    private Path relativePathOfFile;
    private Path absolutePathOfProject;
    private Project currentProject;
    private List<IListenerForOutgoingTextEditActivity> listenersForOutgoingModifications = null;
    private List<IListenerForIncomingTextEditActivity> listenersForIncomingModifications = null;
    private ArrayList<String[]> currentChangesList;
    private int counter = 0;
    boolean hasWriteAccess;
    
    
    private final IActivityConsumer consumer = new AbstractActivityConsumer() {
        /**
         * @JTourBusStop 12, Activity sending, More complex example of a second
         *               dispatch:
         * 
         *               The exec() method below is a more complex example of
         *               the second dispatch: Before letting the activity
         *               perform the third dispatch (done in super.exec()), this
         *               specific implementation dispatches the activity to two
         *               other consumers.
         */

        /***/
        
    };
   private  IPreferenceStore preferenceStore = null;
   private final ISessionLifecycleListener sessionLifecycleListener = new NullSessionLifecycleListener();

  private final ISessionListener sessionListener = new AbstractSessionListener() {

        @Override
        public void permissionChanged(final User user) {

            // Make sure we have the up-to-date facts about ourself
            hasWriteAccess = session.hasWriteAccess();

            // Lock / unlock editors
            if (user.isLocal()) {
                
            }

            // TODO [PERF] 1 Make this lazy triggered on activating a part?
            refreshAnnotations();
        }

        @Override
        public void userFinishedProjectNegotiation(User user) {

        
           
        }

        @Override
        public void userColorChanged(User user) {
           
        }

        @Override
        public void userLeft(final User user) {

            
        }

        private void refreshAnnotations() {
            
        }
    };
     
        


    private EditorManager() {
        listenersForOutgoingModifications = new ArrayList<>();
        listenersForIncomingModifications = new ArrayList<>();
        ListenerForOutgoingTextEditActivity currentListenerForOutgoingTextEditActivities = new ListenerForOutgoingTextEditActivity();
        addListenerForOutgoingModification(currentListenerForOutgoingTextEditActivities);
        ListenerForIncomingTextEditActivity currentListenerForIncomingTextEditActivities = new ListenerForIncomingTextEditActivity();
        addListenerForIncomingModification(currentListenerForIncomingTextEditActivities);
    }
    
    public EditorManager(ISarosSessionManager sessionManager, EditorAPI editorAPI, IPreferenceStore preferenceStore) {
        
        this.preferenceStore = preferenceStore;
        
        sessionManager
            .addSessionLifecycleListener(this.sessionLifecycleListener);
        //addSharedEditorListener(sharedEditorListener);
    }

    /**
     * Makes sure that only one instance of EditorManagerNebeans is created
     */
    public static EditorManager getInstance() {
        return uniqueEditorManagerInstance;
    }

    private void addListenerForOutgoingModification(IListenerForOutgoingTextEditActivity currentListenerForOutgoingModifications) {
        listenersForOutgoingModifications.add(currentListenerForOutgoingModifications);
    }

    private void addListenerForIncomingModification(IListenerForIncomingTextEditActivity currentListenerForIncomingModifications) {
        listenersForIncomingModifications.add(currentListenerForIncomingModifications);
    }
    /**
     * A DocumentEvent occured
     * The relativePathToFile to the file,to the project and the textchanges are
     * passed as a TextEditActivity to the ListenerForOutgoingTextEditActivity
     */
    public void processOutgoingDocumentModification(User currentUser, int offset, String text, String replacedText, Path relativePathToFile) {
        //relative Path to File
        NetBeansPathImpl currentNBPath = new NetBeansPathImpl();
        currentNBPath.setRelativePath(relativePathToFile);
        //current Project
        NetbeansProjectImpl currentNBProject = new NetbeansProjectImpl(currentProject);
        SPath currentSPath = new SPath(currentNBProject, currentNBPath);
        TextEditActivity currentOutgoingTextEditActivity = new TextEditActivity(currentUser, offset, text, replacedText, currentSPath);
        for (IListenerForOutgoingTextEditActivity myCurrentListener : listenersForOutgoingModifications) {
            myCurrentListener.processOutgoingDocumentModification(currentOutgoingTextEditActivity);
        }
    }

    /**
     * Incoming changes from stream are processed,
     * a timertask is startet to insert a new change into the
     * target file every 50 milliseconds
     */
    public void startTimerForProcessingIncomingDocumentModification(ArrayList<String[]> changesList) {
        currentChangesList = changesList;
        Timer timer = new Timer();
        TaskForProcessingIncomingDocumentModification thisTimerTask = new TaskForProcessingIncomingDocumentModification();
        timer.schedule(thisTimerTask, 0, 50);
    }
     private void initialize(final ISarosSession newSession) {
        //checkThreadAccess();

       // assert session == null;
        //assert editorPool.getAllEditors().size() == 0 : "EditorPool was not correctly reset!";

        session = newSession;

        //session.getStopManager().addBlockable(stopManagerListener);

        hasWriteAccess = session.hasWriteAccess();
        session.addListener(sessionListener);
        session.addActivityProducer(this);
        session.addActivityConsumer(consumer);

        /*annotationModelHelper = new AnnotationModelHelper();
        locationAnnotationManager = new LocationAnnotationManager(
            preferenceStore);

        contributionAnnotationManager = new ContributionAnnotationManager(
            session, preferenceStore);
            */

        //remoteEditorManager = new RemoteEditorManager(session);
       
        /*
         * remoteWriteAccessManager = new RemoteWriteAccessManager(session,
            editorAPI);

        preferenceStore.addPropertyChangeListener(annotationPreferenceListener);

        /*
         
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();

        if (window != null)
            window.getPartService().addPartListener(partListener);
        */
    }


    public Document getCurrentDoc() {
        return currentDoc;
    }

    public void setCurrentDoc(Document currentDoc) {
        this.currentDoc = currentDoc;
    }

    public Path getRelativePathOfCurrentFile() {
        return relativePathOfFile;
    }

    public void setRelativePathOfCurrentFile(Path currentPath) {
        relativePathOfFile = currentPath;
    }

    public void setCurrentProject(Project tempProject) {
        currentProject = tempProject;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setAbsolutePathForCurrentProject(Path tempAbsolutePath) {
        absolutePathOfProject = tempAbsolutePath;
    }

    public Path getAbsolutePathForCurrentProject() {
        return absolutePathOfProject;
    }

    @Override
    public Set<SPath> getLocallyOpenEditors() {
        return null;
    }

    @Override
    public Set<SPath> getRemotelyOpenEditors() {
        return null;
    }

    @Override
    public String getContent(SPath spath) {
       return null;
    }

    @Override
    public void saveEditors(IProject ip) {
        
    }

    @Override
    public void addSharedEditorListener(ISharedEditorListener il) {
    }

    @Override
    public void removeSharedEditorListener(ISharedEditorListener il) {
       
    }

    public RemoteEditorManager getRemoteEditorManager() {
       return null;
    }

    /*
     * Parameter of an incoming textchange are passed to Variables and
     * passed on as a TextEditActivity to ListenerForIncomingTextEditActivity
     * every 50 milliseconds - This is just for testing purposes.
     */
    private class TaskForProcessingIncomingDocumentModification extends TimerTask {

        private Iterator<String[]> it = currentChangesList.iterator();

        @Override
        public void run() {


            if (it.hasNext()) {
                String[] x = it.next();
                JID currentJID = new JID(x[0]);
                User currentUser = new User(currentJID, false, true, 1, 1);
                int currentOffset = Integer.parseInt(x[1]);
                String currentText = x[2];
                String currentReplacedText = x[3];
                Path relativePathToFile = Paths.get(x[4]);
                NetBeansPathImpl currentNBPath = new NetBeansPathImpl();
                currentNBPath.setRelativePath(relativePathToFile);
                NetbeansProjectImpl currentNBProject = new NetbeansProjectImpl();
                Path absolutePathToProject = Paths.get(x[8]);
                currentNBProject.setAbsolutePathOfProject(absolutePathToProject);
                SPath currentSPath = new SPath(currentNBProject, currentNBPath);

                counter++;

                TextEditActivity incomingTextEditActivity = new TextEditActivity(currentUser, currentOffset, currentText, currentReplacedText, currentSPath);
                LOG.trace("INCOMING CHANGES: Timer - line " + String.valueOf(counter) + ", method: " + x[5] + ", text: " + currentText + ", replacedText: " + currentReplacedText);

                for (IListenerForIncomingTextEditActivity currentListener : listenersForIncomingModifications) {
                    currentListener.processIncomingDocumentModification(incomingTextEditActivity);
                }

                LifecycleManager.getDefault().saveAll();
            }
        }
    }
}
