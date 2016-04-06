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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author privateuser
 */
public class SessionStatistic {
    private static final String KEY_SESSION_ID = "session.id";

    /**
     * This is the {@link Properties} object to hold the statistical data.
     * Properties are supposed to store only strings for both keys and values,
     * otherwise it can't be written to disk using Properties.store().
     */
    private Properties data;

    public SessionStatistic() {
        data = new Properties();
    }

    /**
     * Adds the given boolean value to this statistic data using the provided
     * key. The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final boolean value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), String.valueOf(value));
    }

    /**
     * Adds the given integer value to this statistic data using the provided
     * key. The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final int value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), String.valueOf(value));
    }

    /**
     * Adds the given long value to this statistic data using the provided key.
     * The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final long value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), String.valueOf(value));
    }

    /**
     * Adds the given float value to this statistic data using the provided key.
     * The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final float value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), String.valueOf(value));
    }

    /**
     * Adds the given double value to this statistic data using the provided
     * key. The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final double value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), String.valueOf(value));
    }

    /**
     * Adds the given string value to this statistic data using the provided
     * key. The key can be extended by specifying additional suffixes. It is
     * recommended to use only integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final String value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), value);
    }

    /**
     * Adds the given date value to this statistic data using the provided key.
     * The date will be stored in UTC time using ISO8601. The key can be
     * extended by specifying additional suffixes. It is recommended to use only
     * integer or string values to extend the key.
     * 
     * @param key
     *            key the value is associated with, not <code>null</code>
     * @param value
     *            value to store, not <code>null</code>
     * @param keySuffixes
     *            additional suffixes to extend the original key with
     */
    public void put(final String key, final Date value,
        final Object... keySuffixes) {
        data.put(appendToKey(key, keySuffixes), toISO8601UTCTimeFormat(value));
    }

    /**
     * Adds the contents of the given SessionStatistic to this SessionStatistic.
     * 
     * @param statistic
     */
    public void addAll(SessionStatistic statistic) {
        data.putAll(statistic.data);
    }

    @Override
    public String toString() {
        StringWriter out = new StringWriter(512);

        try {
            data.store(out, "Saros session data");
        } catch (IOException e) {
            // cannot happen
        } finally {
            IOUtils.closeQuietly(out);
        }
        return out.toString();
    }

    /**
     * Writes the session data to a file.
     * 
     * @param file
     *            the file to save the current session statistic into
     */
    public void toFile(File file) throws IOException {

        FileOutputStream fos = null;

        // write the statistic to the file
        try {
            fos = new FileOutputStream(file);
            data.store(fos, "Saros session data");
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public void setSessionID(String sessionID) {
        data.setProperty(KEY_SESSION_ID, sessionID);
    }

    public String getSessionID() {
        return data.getProperty(KEY_SESSION_ID);
    }

    private String appendToKey(String key, Object... suffixes) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        for (Object suffix : suffixes) {
            sb.append(".").append(suffix);
        }
        return sb.toString();
    }

    // need to be Java 6 compatible !
    private static String toISO8601UTCTimeFormat(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }
}
