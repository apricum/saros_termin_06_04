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

/**
 *
 * @author privateuser
 */
//import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;

import de.fu_berlin.inf.dpp.netbeans.preferences.NetbeansPreferenceConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



public class StatisticManagerConfiguration {

    private static final Random RANDOM = new Random();

    public static String getStatisticsPseudonymID() {
        return FeedbackPreferences.getPreferences().get(
            NetbeansPreferenceConstants.STATISTICS_PSEUDONYM_ID, "");
    }

    public static boolean isStatisticSubmissionAllowed() {
        return getStatisticSubmissionStatus() == AbstractFeedbackManager.ALLOW;
    }

    public static boolean hasStatisticAgreement() {
        return getStatisticSubmissionStatus() != AbstractFeedbackManager.UNKNOWN;
    }

    public static boolean isPseudonymSubmissionAllowed() {
        return FeedbackPreferences.getPreferences().getBoolean(
            NetbeansPreferenceConstants.STATISTIC_ALLOW_PSEUDONYM, false);
    }

    /**
     * Returns whether the submission of statistic is allowed, forbidden or
     * unknown.
     * 
     * @return 0 = unknown, 1 = allowed, 2 = forbidden
     */
    public static int getStatisticSubmissionStatus() {
        return FeedbackPreferences.getPreferences().getInt(
            NetbeansPreferenceConstants.STATISTIC_ALLOW_SUBMISSION,
            AbstractFeedbackManager.UNDEFINED);
    }

    public static void setPseudonymSubmissionAllowed(
        final boolean isPseudonymAllowed) {

        FeedbackPreferences.getPreferences().putBoolean(
            NetbeansPreferenceConstants.STATISTIC_ALLOW_PSEUDONYM, isPseudonymAllowed);
    }

    /**
     * Sets if the user wants to submit statistic data.
     */
    public static void setStatisticSubmissionAllowed(final boolean allow) {

        final int submission = allow ? AbstractFeedbackManager.ALLOW
            : AbstractFeedbackManager.FORBID;

        setStatisticSubmission(submission);
    }

    /**
     * Sets if the user wants to submit statistic data.
     * 
     * @param submission
     *            (see constants of {@link AbstractFeedbackManager})
     */
    protected static void setStatisticSubmission(final int submission) {
        FeedbackPreferences.getPreferences().putInt(
            NetbeansPreferenceConstants.STATISTIC_ALLOW_SUBMISSION, submission);
    }

    public static void setStatisticsPseudonymID(final String userID) {
        FeedbackPreferences.getPreferences().put(
            NetbeansPreferenceConstants.STATISTICS_PSEUDONYM_ID, userID);
    }

    /**
     * Returns the random user ID from the global preferences, if one was
     * created and saved yet. If none was found, a newly generated ID is stored
     * and returned.
     * 
     * @see StatisticManagerConfiguration#generateUserID()
     * 
     * @return the random user ID for this eclipse installation
     */
    public static String getUserID() {

        String userID = FeedbackPreferences.getPreferences().get(
            NetbeansPreferenceConstants.RANDOM_USER_ID, null);

        if (userID == null) {
            userID = generateUserID();
            // save ID in the global preferences
            FeedbackPreferences.getPreferences().put(
                NetbeansPreferenceConstants.RANDOM_USER_ID, userID);
        }

        if (isPseudonymSubmissionAllowed())
            userID += "-"
                + StatisticManagerConfiguration.getStatisticsPseudonymID();

        return userID;
    }

    /**
     * Generates a random user ID. The ID consists of the current date and time
     * plus a random positive Integer. Thus identical IDs for different users
     * should be very unlikely.
     * 
     * @return a random user ID e.g. 2009-06-11_14-53-59_1043704453
     */
    public static String generateUserID() {
        int randInt = RANDOM.nextInt(Integer.MAX_VALUE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss");
        String userID = dateFormat.format(new Date()) + "_" + randInt;

        return userID;
    }
}
