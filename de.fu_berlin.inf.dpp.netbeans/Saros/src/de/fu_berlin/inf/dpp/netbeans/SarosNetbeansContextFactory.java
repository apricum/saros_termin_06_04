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

import de.fu_berlin.inf.dpp.AbstractSarosContextFactory;
import de.fu_berlin.inf.dpp.ISarosContextBindings;
import de.fu_berlin.inf.dpp.communication.connection.IProxyResolver;
import de.fu_berlin.inf.dpp.concurrent.watchdog.ConsistencyWatchdogClient;
import de.fu_berlin.inf.dpp.editor.IEditorManager;

import de.fu_berlin.inf.dpp.filesystem.ChecksumCacheImpl;
import de.fu_berlin.inf.dpp.filesystem.IChecksumCache;
import de.fu_berlin.inf.dpp.filesystem.IPathFactory;
import de.fu_berlin.inf.dpp.filesystem.IWorkspace;
import de.fu_berlin.inf.dpp.monitoring.remote.IRemoteProgressIndicatorFactory;
import de.fu_berlin.inf.dpp.netbeans.awareness.AwarenessInformationCollector;
import de.fu_berlin.inf.dpp.netbeans.communication.connection.Socks5ProxyResolver;
import de.fu_berlin.inf.dpp.netbeans.editor.internal.EditorAPI;
import de.fu_berlin.inf.dpp.netbeans.filesystem.FileContentNotifierBridge;
import de.fu_berlin.inf.dpp.netbeans.monitoring.remote.NetbeansRemoteProgressIndicatorFactoryImpl;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceStore;
import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferences;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferencesStoreAdapter;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferences;
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferencesStoreAdapter;
import de.fu_berlin.inf.dpp.netbeans.project.filesystem.PathFactory;
import de.fu_berlin.inf.dpp.netbeans.project.internal.FollowingActivitiesManager;
import de.fu_berlin.inf.dpp.netbeans.project.internal.SarosNetbeansSessionContextFactory;
import de.fu_berlin.inf.dpp.netbeans.synchronize.internal.SwingSynchronizer;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.JoinSessionRequestHandler;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.NegotiationHandler;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.ServerPreferenceHandler;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.SessionStatusRequestHandler;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.SessionViewOpener;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.UserStatusChangeHandler;
import de.fu_berlin.inf.dpp.netbeans.ui.eventhandler.XMPPAuthorizationHandler;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;

import de.fu_berlin.inf.dpp.session.ISarosSessionContextFactory;
import de.fu_berlin.inf.dpp.synchronize.UISynchronizer;
import de.fu_berlin.inf.dpp.vcs.NetbeansVCSProviderFactoryImpl;
import de.fu_berlin.inf.dpp.vcs.VCSProviderFactory;
import java.util.Arrays;
import java.util.prefs.Preferences;
//import java.util.prefs.Preferences;
import org.jivesoftware.smack.util.FileUtils;
import org.picocontainer.BindKey;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class SarosNetbeansContextFactory extends AbstractSarosContextFactory{

    //private ListenerForDocumentSwap saros;
    private final ListenerForDocumentSwap saros;
    
    private final Component[] components = new Component[] {
        Component.create(EditorAPI.class),
        Component.create(SessionViewOpener.class),
        Component.create(IEditorManager.class, EditorManager.class),
        //Component.create(IPreferenceStore.class, NetbeansPreferencesStoreAdapter.class),
        Component.create(de.fu_berlin.inf.dpp.preferences.Preferences.class, NetbeansPreferences.class),
        Component.create(ISarosSessionContextFactory.class, SarosNetbeansSessionContextFactory.class),
        Component.create(JoinSessionRequestHandler.class),
        Component.create(ServerPreferenceHandler.class),
        Component.create(UISynchronizer.class, SwingSynchronizer.class),
        //Component.create(NetbeansPreferenceStore.class),
         // Proxy Support for the XMPP server connection
        Component.create(IProxyResolver.class, Socks5ProxyResolver.class),
        Component.create(XMPPAuthorizationHandler.class),
       
        //Component.create(SessionViewOpener.class),
        Component.create(SessionStatusRequestHandler.class),
        Component.create(NegotiationHandler.class),
        Component.create(UserStatusChangeHandler.class),
        
         Component.create(IChecksumCache.class, new ChecksumCacheImpl(
            new FileContentNotifierBridge())),
         Component.create(VCSProviderFactory.class,
            NetbeansVCSProviderFactoryImpl.class),
        // Remote progress indication
        Component.create(IRemoteProgressIndicatorFactory.class,      
            NetbeansRemoteProgressIndicatorFactoryImpl.class),
         Component.create(AwarenessInformationCollector.class)
    };
    


public SarosNetbeansContextFactory(ListenerForDocumentSwap saros) {
        this.saros = saros;
    }


//        // UI handlers
//        Component.create(HostLeftAloneInSessionHandler.class),



//        Component.create(JoinSessionRejectedHandler.class),
    



//        Component.create(ConnectingFailureHandler.class),
//        // Cache support
//        /*
//         * TODO avoid direct creation as this will become tricky especially if
//         * we are the delegate and depends on components that are only available
//         * after we added all our context stuff or vice versa
//         */

//
//        Component.create(IWorkspace.class, new EclipseWorkspaceImpl(
//            ResourcesPlugin.getWorkspace())),
//
//        Component.create(IWorkspaceRoot.class, new EclipseWorkspaceRootImpl(
//            ResourcesPlugin.getWorkspace().getRoot())),
//
//        // Saros Core Path Support
//        Component.create(IPathFactory.class, EclipsePathFactory.class),
//
//        // SWT EDT support
//        Component.create(UISynchronizer.class, SWTSynchronizer.class),
//
//        // VCS (SVN only)
//        Component.create(VCSProviderFactory.class,
//            EclipseVCSProviderFactoryImpl.class),
//
//        // Proxy Support for the XMPP server connection
//        Component.create(IProxyResolver.class, Socks5ProxyResolver.class),
//
//        // Remote progress indication
//        Component.create(IRemoteProgressIndicatorFactory.class,
//            EclipseRemoteProgressIndicatorFactoryImpl.class)
    
    
    @Override
    public void createComponents(MutablePicoContainer container) {
        for (Component component : Arrays.asList(components))
            container.addComponent(component.getBindKey(),
                component.getImplementation());

        container.addComponent(saros);

        container.addComponent(IPathFactory.class, new PathFactory());
        container.addComponent(BindKey.bindKey(String.class,
            ISarosContextBindings.SarosVersion.class), "14.10.31");

        container.addComponent(BindKey.bindKey(String.class,
            ISarosContextBindings.PlatformVersion.class),
            "3.7.2");

        // for core logic and extended Eclipse session components
        container.addComponent(IPreferenceStore.class,
            new NetbeansPreferenceStore());

        // TODO remove
        // for plain Eclipse components like preference pages etc.
//        container.addComponent(
//            NetbeansPreferenceStore.class,
//            saros.getPreferenceStore());
        // container.addComponent(FollowingActivitiesManager.class);
        container.addComponent(Preferences.class, saros.getGlobalPreferences());
    }
    
//    old:
//    @Override
//    public void createComponents(MutablePicoContainer container) {
//        //IWorkspace workspace = saros.getWorkspace();
//        //FileUtils.workspace = workspace;
//
//        // Saros Core PathIntl Support
//        container.addComponent(IPathFactory.class, new PathFactory());
//
//        //container.addComponent(IWorkspace.class, workspace);
//
//        for (Component component : Arrays.asList(components)) {
//            container.addComponent(component.getBindKey(),
//                component.getImplementation());
//        }
//
//        container.addComponent(saros);
//
//        container.addComponent(BindKey.bindKey(String.class,
//            ISarosContextBindings.SarosVersion.class), "13.12.6"); // todo
//
//       
//
//        container.addComponent(BindKey.bindKey(String.class,
//            ISarosContextBindings.PlatformVersion.class), "3.7.2"); // todo
//        
//         container.addComponent(IPreferenceStore.class,
//            new NetbeansPreferencesStoreAdapter(new NetbeansPreferenceStore()));
//         
//        container.addComponent(Preferences.class, saros.getGlobalPreferences());
//    }
//
//    

   
    
}
