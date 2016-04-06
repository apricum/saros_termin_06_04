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

import de.fu_berlin.inf.dpp.ISarosContextFactory;
import de.fu_berlin.inf.dpp.SarosContext;
import de.fu_berlin.inf.dpp.SarosCoreContextFactory;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.communication.connection.ConnectionHandler;
//import de.fu_berlin.inf.dpp.misc.pico.DotGraphMonitor;

import de.fu_berlin.inf.dpp.netbeans.feedback.FeedbackPreferences;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferencesStoreAdapter;


//import de.fu_berlin.inf.dpp.preferences.Preferences;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.versioning.VersionManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Random;
import java.util.prefs.Preferences;
//import java.util.prefs.Preferences;
//import java.util.prefs.Preferences;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbPreferences;
//import org.openide.util.NbPreferences;
import org.openide.util.Utilities;



/**
 * ListenerForDocumentSwap tracks file selections in the editor
 * After selecting a file, a documentlistener gets started on it to retrieve informations about changing filecontent
 * After deselecting a file, the listener on it gets removed
 */
public class ListenerForDocumentSwap {

    public static final String PLUGIN_ID = "de.fu_berlin.inf.dpp"; //$NON-NLS-1$
    protected static ListenerForDocumentSwap plugin;
    private Lookup.Result<DataObject> dataObjectOfSelectedDocument = Utilities.actionsGlobalContext().lookupResult(DataObject.class);
    private final LookupListener listenerForDocumentSwap;
    private Document oldDocument = null;
    private Document newDocument;
    private ListenerForOutgoingDocumentModification currentListenerForOutgoingDocumentModification;
    private EditorManager editorManager = EditorManager.getInstance();
    private Path relativePathToFile;
    private FileObject thisCurrentFileObject = null;
    private Project currentProject = null;
    private PropertyChangeListener initialPropertyChangeListener;
    private SarosContext sarosContext;
    private String sarosVersion;
    private String sarosFeatureID;
    private ConnectionHandler connectionHandler;
    private ISarosSessionManager sessionManager;
    protected static boolean isInitialized;
    private static final Logger LOG = Logger.getLogger(ListenerForDocumentSwap.class);
    private static final String VERSION_COMPATIBILITY_PROPERTY_FILE = "version.comp"; //$NON-NLS-1$
    private BufferedReader myBufferedReader = null;
    private de.fu_berlin.inf.dpp.preferences.Preferences preferences;
    protected Preferences configPrefs;

    /**
     * The secure preferences store, used to store sensitive data that may (at
     * the user's option) be stored encrypted.
     */
    protected ISecurePreferences securePrefs;
    /**
     * The global plug-in preferences, shared among all workspaces. Should only
     * be accessed over {@link #getGlobalPreferences()} from outside this class.
     */
    //protected Preferences configPrefs;
   // private final de.fu_berlin.inf.dpp.preferences.Preferences preferences;


    public ListenerForDocumentSwap() {

        sarosVersion = "13.12.6";
        ArrayList<ISarosContextFactory> factories = new ArrayList<ISarosContextFactory>();
        factories.add(new SarosNetbeansContextFactory(this));
        factories.add(new SarosCoreContextFactory());
        sarosContext = new SarosContext(factories, null);
        sarosContext.initialize();
        
        preferences = sarosContext.getComponent(de.fu_berlin.inf.dpp.preferences.Preferences.class);
        sarosFeatureID = PLUGIN_ID + "_" + sarosVersion; //$NON-NLS-1$
        //FeedbackPreferences.setPreferences(sarosContext
          //  .getComponent(Preferences.class));
        FeedbackPreferences.setPreferences(NbPreferences.forModule(FeedbackPanel.class));

        int favoriteColorID = new Random().nextInt(5);
        NbPreferences.forModule(SarosPanel.class).put("FAVORITE_COLOR_ID_HACK_CREATE_RANDOM_COLOR", Integer.toString(favoriteColorID));
        //NbPreferences.forModule(ListenerForDocumentSwap.class).put(PLUGIN_ID, "test");
        initVersionCompatibilityChart(VERSION_COMPATIBILITY_PROPERTY_FILE,
        sarosContext.getComponent(VersionManager.class));
        connectionHandler = sarosContext.getComponent(ConnectionHandler.class);
        sessionManager = sarosContext.getComponent(ISarosSessionManager.class);
        SarosPluginContext.setSarosContext(sarosContext);
        
        
       // sarosContext.getComponents(Object.class);
        
        isInitialized = true;
       // setDefault(this);
       

        /*
         * Sets up a Listener to get noticed when the first
         * file/document is selected
         * Listener then gets removed and the LookupListener takes over
         */
        initialPropertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();
                if (jtc != null) {
                    Document currentDocument = jtc.getDocument();
                    DataObject dataObjectForDocument = NbEditorUtilities.getDataObject(currentDocument);
                    addListenerToCurrentlySelectedDocument(dataObjectForDocument);
                }
            }
        };
        EditorRegistry.addPropertyChangeListener(initialPropertyChangeListener);

        listenerForDocumentSwap = new LookupListener() {
            /*
             * The selected file in the editor changed
             * Remove the listener from the old file and/or
             * Set a listener on the new one
             * Get the relative and absolut path of the current file
             * Get the absolute path of the filecontaining project
             */
            public void resultChanged(final LookupEvent evt) {

                processDocumentSwap(evt);
            }
        ;
        };

        dataObjectOfSelectedDocument.addLookupListener(listenerForDocumentSwap);
    }

    private void processDocumentSwap(LookupEvent evt) {

        if ((evt.getSource() == dataObjectOfSelectedDocument)) {

            @SuppressWarnings("unchecked")
            Collection<DataObject> myDataObjects = ((Lookup.Result) evt.getSource()).allInstances();
            for (DataObject a : myDataObjects) {
                addListenerToCurrentlySelectedDocument(a);
            }
        }
    }

    /*
     * Check if the document changed
     */
    private void addListenerToCurrentlySelectedDocument(DataObject a) {

        DataObject thisDataObj = a;
        EditorCookie thisCurrentEditorCookie = thisDataObj.getLookup().lookup(EditorCookie.class);

        if (thisCurrentEditorCookie == null) {

            return;

        }
        Document thisCDoc = thisCurrentEditorCookie.getDocument();

        if (thisCDoc == null) {
            return;
        }

        thisCurrentFileObject = thisDataObj.getPrimaryFile();
        String fileObjectLocation = thisCurrentFileObject.getPath();
        currentProject = FileOwnerQuery.getOwner(thisCurrentFileObject);

        newDocument = thisCDoc;

        /*
         * Remove the DocumentListener from the old file and/or
         * set a listener on the new one
         */
        if (oldDocument != null && !(oldDocument.equals(newDocument))) {

            oldDocument.removeDocumentListener(currentListenerForOutgoingDocumentModification);
            currentListenerForOutgoingDocumentModification = null;
            oldDocument = newDocument;
        } else {
            if (oldDocument == null) {

                oldDocument = newDocument;


            } else {

                removeInitialPropertyChangedListener();
                return;
            }
        }
        removeInitialPropertyChangedListener();
        addDocumentFilterAndListener();

        /*
         * Get the relative and absolut path of the current file
         * Get the absolute path of the filecontaining project
         */
        FileObject root = currentProject.getProjectDirectory();
        String pathOfBase = root.getPath();
        Path absolutePathOfFile = Paths.get(fileObjectLocation);
        Path pathBase = Paths.get(pathOfBase);
        relativePathToFile = pathBase.relativize(absolutePathOfFile);
        editorManager.setRelativePathOfCurrentFile(relativePathToFile);
        editorManager.setAbsolutePathForCurrentProject(pathBase);
        editorManager.setCurrentProject(currentProject);
    }

    private void addDocumentFilterAndListener() {

        currentListenerForOutgoingDocumentModification = new ListenerForOutgoingDocumentModification();
        oldDocument.addDocumentListener(currentListenerForOutgoingDocumentModification);
        editorManager.setCurrentDoc(oldDocument);
    }

    private void removeInitialPropertyChangedListener() {
        if (initialPropertyChangeListener != null) {
            EditorRegistry.removePropertyChangeListener(initialPropertyChangeListener);
            initialPropertyChangeListener = null;
        }
    }
    
    private void initVersionCompatibilityChart(final String filename,
        final VersionManager versionManager) {

        if (versionManager == null) {
            LOG.error("no version manager component available");
            return;
        }

        
        File loadedFile = InstalledFileLocator.getDefault().locate(filename, null, false);
            if (loadedFile == null) {
                LOG.error("Loading input file version.comp failed because the requested file doesn't exist");
                return;
            }

        InputStreamReader in = null;
        
        try {
            in = new InputStreamReader(new FileInputStream(loadedFile), "UTF-8");
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
           // myBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(loadedFile), "UTF-8"));
        
        //-----------------------
        //final InputStream in = ListenerForDocumentSwap.class.getClassLoader().getResourceAsStream(filename);

        final Properties chart = new Properties();

        if (in == null) {
            LOG.warn("could not find compatibility property file: " + filename);
            return;
        }

        try {
            chart.load(in);
        } catch (IOException e) {
            LOG.warn("could not read compatibility property file: " + filename,
                e);

            return;
        } finally {
            IOUtils.closeQuietly(in);
        }

        versionManager.setCompatibilityChart(chart);
    }

     private String getPlatformInfo() {

        String javaVersion = System.getProperty("java.version",
            "Unknown Java Version");
        String javaVendor = System.getProperty("java.vendor", "Unknown Vendor");
        String os = System.getProperty("os.name", "Unknown OS");
        String osVersion = System.getProperty("os.version", "Unknown Version");
        String hardware = System.getProperty("os.arch", "Unknown Architecture");

        StringBuilder sb = new StringBuilder();

        sb.append("  Java Version: " + javaVersion + "\n");
        sb.append("  Java Vendor: " + javaVendor + "\n");
       // sb.append("  Eclipse Runtime Version: "
        //    + Platform.getBundle("org.eclipse.core.runtime").getVersion()
         //       .toString() + "\n");
        sb.append("  Operating System: " + os + " (" + osVersion + ")\n");
        sb.append("  Hardware Architecture: " + hardware);

        return sb.toString();
    }

    
    public synchronized Preferences getGlobalPreferences() {
        // TODO Singleton-Pattern code smell: ConfigPrefs should be a @component
        if (configPrefs == null) {
            configPrefs = NbPreferences.forModule(SarosPanel.class); 
            //configPrefs = new ConfigurationScope().getNode(PLUGIN_ID);
        }
        return configPrefs;
    }
    
     /**
     * Saves the global preferences to disk. Should be called at least before
     * the bundle is stopped to prevent loss of data. Can be called whenever
     * found necessary.
     */
    public synchronized void saveGlobalPreferences() throws java.util.prefs.BackingStoreException {
        /*
         * Note: If multiple JVMs use the config preferences and the underlying
         * backing store, they might not always work with latest data, e.g. when
         * using multiple instances of the same eclipse installation.
         */
        if (configPrefs != null) {
            
                configPrefs.flush();
            
        }
    }
    
    public String getVersion() {
        return sarosVersion;
    }
    
    public static void setDefault(ListenerForDocumentSwap newPlugin) {
        ListenerForDocumentSwap.plugin = newPlugin;

    }
     private synchronized ISecurePreferences getSecurePrefs() {

        if (securePrefs == null) {
            try {
                //File storeFile = new File(getStateLocation().toFile(), "/.pref"); //$NON-NLS-1$
                File storeFile = new File((FileUtil.getConfigRoot()).toString(),"/.pref");
                //File storeFile = InstalledFileLocator.getDefault().locate("MyTextEditActivitiesInput.txt", null, false);
                URI workspaceURI = storeFile.toURI();

                /*
                 * The SecurePreferencesFactory does not accept percent-encoded
                 * URLs, so we must decode the URL before passing it.
                 */
                String prefLocation = URLDecoder.decode(
                    workspaceURI.toString(), "UTF-8"); //$NON-NLS-1$
                URL prefURL = new URL(prefLocation);

                securePrefs = SecurePreferencesFactory.open(prefURL, null);
            } catch (MalformedURLException e) {
                LOG.error("Problem with URL when attempting to access secure preferences: "
                    + e);
            } catch (IOException e) {
                LOG.error("I/O problem when attempting to access secure preferences: "
                    + e);
            } finally {
                if (securePrefs == null)
                    securePrefs = SecurePreferencesFactory.getDefault();
            }
        }

        return securePrefs;
    }

       private synchronized void saveSecurePrefs() {
        try {
            if (securePrefs != null) {
                securePrefs.flush();
            }
        } catch (IOException e) {
            LOG.error("Exception when trying to store secure preferences: " + e);
        }
    }

   

}
