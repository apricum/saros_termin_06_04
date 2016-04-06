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

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
//import org.eclipse.ui.progress.IProgressConstants;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import de.fu_berlin.inf.dpp.netbeans.ListenerForDocumentSwap;
import de.fu_berlin.inf.dpp.net.util.XMPPUtils;
import de.fu_berlin.inf.dpp.net.xmpp.JID;

/**
 * This job is intended to be used with a pending outgoing
 * {@linkplain FileTransferRequest XMPP file transfer}. It will start the file
 * transfer and monitor the status of the process.
 * <p>
 * This job supports cancellation.
 */
public final class OutgoingFileTransferJob extends FileTransferJob {

    private static final Logger LOG = Logger
        .getLogger(OutgoingFileTransferJob.class);

    private final OutgoingFileTransfer transfer;
    private final File file;

    public OutgoingFileTransferJob(OutgoingFileTransfer transfer, File file,
        JID jid) {
        super("File Transfer", jid);
        //setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
        this.transfer = transfer;
        this.file = file;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        String nickname = XMPPUtils.getNickname(null, jid, jid.getBase());

        try {
            transfer.sendFile(file, file.getName());

            monitor.beginTask("Waiting for " + nickname
                + " to accept the file transfer...", IProgressMonitor.UNKNOWN);

            while (!transfer.isDone()) {
                if (monitor.isCanceled())
                    break;

                boolean proceed = true;

                if (transfer.getStatus() == org.jivesoftware.smackx.filetransfer.FileTransfer.Status.negotiating_transfer)
                    proceed = false;

                if (transfer.getStatus() == org.jivesoftware.smackx.filetransfer.FileTransfer.Status.initial)
                    proceed = false;

                if (proceed)
                    break;

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    transfer.cancel();
                    break;
                }
            }

            monitor.done();
            monitor.beginTask("Sending file " + file.getName(), 100);

            return monitorTransfer(transfer, monitor);
        } catch (RuntimeException e) {
            LOG.error("internal error in file transfer", e);
            throw e;
        } catch (XMPPException e) {
            LOG.error("file transfer failed: " + jid, e);

            return new Status(IStatus.ERROR, ListenerForDocumentSwap.PLUGIN_ID,
                "file transfer failed", e);
        } finally {
            monitor.done();
        }
    }

}