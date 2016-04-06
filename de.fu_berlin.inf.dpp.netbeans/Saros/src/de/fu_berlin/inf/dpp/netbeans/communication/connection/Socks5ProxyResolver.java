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
package de.fu_berlin.inf.dpp.netbeans.communication.connection;

import de.fu_berlin.inf.dpp.communication.connection.IProxyResolver;
import de.fu_berlin.inf.dpp.netbeans.ListenerForDocumentSwap;
//import de.fu_berlin.inf.dpp.netbeans.Saros;
import org.jivesoftware.smack.proxy.ProxyInfo;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class Socks5ProxyResolver implements IProxyResolver{

    

    // TODO maybe inject the bundle context ?
    private final ListenerForDocumentSwap plugin;

    public Socks5ProxyResolver(final ListenerForDocumentSwap plugin) {
        this.plugin = plugin;
    }

    @Override
    public ProxyInfo resolve(final String host) {

        /*
         * FIXME currently disabled until an option is available to either use
         * Eclipse settings or not
         */
        if (true)
            return null;

        URI hostURI;

        try {
            hostURI = new URI(host);
        } catch (URISyntaxException e) {
            return null;
        }

//        final IProxyService proxyService = getProxyService();
//
//        if (proxyService == null || !proxyService.isProxiesEnabled())
//            return null;
//
//        for (IProxyData pd : proxyService.select(hostURI)) {
//            if (IProxyData.SOCKS_PROXY_TYPE.equals(pd.getType())) {
//                return ProxyInfo.forSocks5Proxy(pd.getHost(), pd.getPort(),
//                    pd.getUserId(), pd.getPassword());
//            }
//        }

        return null;
    }

//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    private IProxyService getProxyService() {
//        BundleContext bundleContext = plugin.getBundle().getBundleContext();
//        ServiceReference serviceReference = bundleContext
//            .getServiceReference(IProxyService.class.getName());
//        return (IProxyService) bundleContext.getService(serviceReference);
//    }
}
