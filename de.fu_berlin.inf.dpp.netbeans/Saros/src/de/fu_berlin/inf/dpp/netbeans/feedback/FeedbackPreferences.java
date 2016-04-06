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

import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;
import java.util.prefs.Preferences;





public class FeedbackPreferences {

    private static Preferences preferences;

    public static synchronized void setPreferences(Preferences preferences) {
        if (preferences == null)
            throw new NullPointerException("preferences is null");

        FeedbackPreferences.preferences = preferences;
    }

    /**
     * Returns the {@link Preferences preferences} that are currently used by
     * the Feedback component.
     * 
     * @throws IllegalStateException
     *             if no preferences instance is available
     */
    public static synchronized Preferences getPreferences() {
        if (FeedbackPreferences.preferences == null)
            throw new IllegalStateException("preferences are not initialized");

        return FeedbackPreferences.preferences;
    }

    public static void applyDefaults(IPreferenceStore defaultPreferences) {
        if (FeedbackPreferences.preferences == null)
            throw new IllegalStateException("preferences are not initialized");

        final String[] keys = { NetbeansPreferenceConstants.FEEDBACK_SURVEY_DISABLED,
            NetbeansPreferenceConstants.FEEDBACK_SURVEY_INTERVAL,
            NetbeansPreferenceConstants.STATISTIC_ALLOW_SUBMISSION,
            NetbeansPreferenceConstants.STATISTIC_ALLOW_PSEUDONYM,
            NetbeansPreferenceConstants.ERROR_LOG_ALLOW_SUBMISSION,
            NetbeansPreferenceConstants.ERROR_LOG_ALLOW_SUBMISSION_FULL };

        for (final String key : keys)
            if (preferences.get(key, null) == null)
                preferences.put(key, defaultPreferences.getDefaultString(key));
    }
}