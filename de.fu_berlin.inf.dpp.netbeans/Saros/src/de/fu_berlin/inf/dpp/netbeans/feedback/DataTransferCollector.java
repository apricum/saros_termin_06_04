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
package de.fu_berlin.inf.dpp.netbeans.feedback;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.net.ConnectionMode;
import de.fu_berlin.inf.dpp.net.IConnectionManager;
import de.fu_berlin.inf.dpp.net.ITransferListener;
import de.fu_berlin.inf.dpp.session.ISarosSession;

/**
 * Collects information about the amount of data transfered with the different
 * {@link ConnectionMode}s
 * 
 * @author Christopher Oezbek
 */
@Component(module = "feedback")
public class DataTransferCollector extends AbstractStatisticCollector {

    private static final String KEY_TRANSFER_STATS = "data_transfer";

    private static final String TRANSFER_STATS_EVENT_SUFFIX = "number_of_events";

    /** Total size in KB */
    private static final String TRANSFER_STATS_SIZE_SUFFIX = "total_size_kb";

    /** Total size for transfers in milliseconds */
    private static final String TRANSFER_STATS_TIME_SUFFIX = "total_time_ms";

    /** Convenience value of total_size / total_time in KB/s */
    private static final String TRANSFER_STATS_THROUGHPUT_SUFFIX = "average_throughput_kbs";

    // we currently do not distinguish between sent and received data
    private static class TransferStatisticHolder {
        private long bytesTransferred;
        private long transferTime; // ms
        private int count;
    }

    private final Map<ConnectionMode, TransferStatisticHolder> statistic = new EnumMap<ConnectionMode, TransferStatisticHolder>(
        ConnectionMode.class);

    private final IConnectionManager connectionManager;

    private final ITransferListener dataTransferlistener = new ITransferListener() {

        @Override
        public void sent(final ConnectionMode mode, final long sizeCompressed,
            final long sizeUncompressed, final long duration) {
            // see processGatheredData
            synchronized (DataTransferCollector.this) {
                TransferStatisticHolder holder = statistic.get(mode);

                if (holder == null) {
                    holder = new TransferStatisticHolder();
                    statistic.put(mode, holder);
                }

                holder.bytesTransferred += sizeCompressed;
                holder.transferTime += sizeUncompressed;
                holder.count++;

                // TODO how to handle overflow ?
            }
        }

        @Override
        public void received(final ConnectionMode mode,
            final long sizeCompressed, final long sizeUncompressed,
            final long duration) {
            // TODO differentiate the traffic
            sent(mode, sizeCompressed, sizeUncompressed, duration);
        }

    };

    public DataTransferCollector(StatisticManager statisticManager,
        ISarosSession session, IConnectionManager connectionManager) {
        super(statisticManager, session);
        this.connectionManager = connectionManager;
    }

    @Override
    protected synchronized void processGatheredData() {

        for (final Entry<ConnectionMode, TransferStatisticHolder> entry : statistic
            .entrySet()) {

            final ConnectionMode mode = entry.getKey();
            final TransferStatisticHolder holder = entry.getValue();

            storeTransferStatisticForMode(mode.toString(), holder.count,
                holder.bytesTransferred, holder.transferTime);

        }
    }

    @Override
    protected void doOnSessionStart(ISarosSession sarosSession) {
        connectionManager.addTransferListener(dataTransferlistener);
    }

    @Override
    protected void doOnSessionEnd(ISarosSession sarosSession) {
        connectionManager.removeTransferListener(dataTransferlistener);
    }

    private void storeTransferStatisticForMode(final String transferMode,
        final int transferEvents, final long totalSize,
        final long totalTransferTime) {

        data.put(KEY_TRANSFER_STATS, transferEvents, transferMode,
            TRANSFER_STATS_EVENT_SUFFIX);

        data.put(KEY_TRANSFER_STATS, totalSize / 1024, transferMode,
            TRANSFER_STATS_SIZE_SUFFIX);

        data.put(KEY_TRANSFER_STATS, totalTransferTime, transferMode,
            TRANSFER_STATS_TIME_SUFFIX);

        data.put(KEY_TRANSFER_STATS,
            totalSize * 1000.0 / 1024.0 / Math.max(1.0, totalTransferTime),
            transferMode, TRANSFER_STATS_THROUGHPUT_SUFFIX);
    }
}
