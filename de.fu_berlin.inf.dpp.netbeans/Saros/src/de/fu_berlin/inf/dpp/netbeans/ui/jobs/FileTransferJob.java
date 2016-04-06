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
package de.fu_berlin.inf.dpp.netbeans.ui.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Error;

import de.fu_berlin.inf.dpp.netbeans.ListenerForDocumentSwap;
import de.fu_berlin.inf.dpp.monitoring.MonitorableFileTransfer;
import de.fu_berlin.inf.dpp.monitoring.MonitorableFileTransfer.TransferStatus;
import de.fu_berlin.inf.dpp.netbeans.monitoring.ProgressMonitorAdapterFactory;
import de.fu_berlin.inf.dpp.net.xmpp.JID;

abstract class FileTransferJob extends Job {

    private static final Logger LOG = Logger.getLogger(FileTransferJob.class);
    final JID jid;

    FileTransferJob(String name, JID jid) {
        super(name);
        this.jid = jid;
    }

    IStatus monitorTransfer(FileTransfer transfer, IProgressMonitor monitor) {
        MonitorableFileTransfer mtf = new MonitorableFileTransfer(transfer,
            ProgressMonitorAdapterFactory.convert(monitor));
        TransferStatus result = mtf.monitorTransfer();

        switch (result) {
        case ERROR:
            Error error = transfer.getError();

            /*
             * there is currently no chance to determine on the sender side if
             * the receiving side has canceled the transfer
             */
            String errMsg = error == null ? "File transfer failed. Maybe the remote side canceled the transfer."
                : error.getMessage();
            Status status = new Status(IStatus.ERROR, ListenerForDocumentSwap.PLUGIN_ID, errMsg,
                transfer.getException());

            LOG.error("file transfer from " + jid + " failed: " + errMsg,
                transfer.getException());

            return status;
        case CANCEL:
            return Status.CANCEL_STATUS;

        case OK:
            // fall through
        default:
            return Status.OK_STATUS;
        }
    }

}