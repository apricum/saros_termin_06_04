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
package de.fu_berlin.inf.dpp.netbeans.preferences;

import de.fu_berlin.inf.dpp.netbeans.SarosPanel;
import de.fu_berlin.inf.dpp.preferences.IPreferenceChangeListener;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;


/**
 *
 * @author privateuser
 */
public class NetbeansPreferenceStore implements IPreferenceStore{

    /**
     * The default-default value for boolean preferences (<code>false</code>).
     */
    public static final boolean BOOLEAN_DEFAULT_DEFAULT = false;

    /**
     * The default-default value for double preferences (<code>0.0</code>).
     */
    public static final double DOUBLE_DEFAULT_DEFAULT = 0.0;

    /**
     * The default-default value for float preferences (<code>0.0f</code>).
     */
    public static final float FLOAT_DEFAULT_DEFAULT = 0.0f;

    /**
     * The default-default value for int preferences (<code>0</code>).
     */
    public static final int INT_DEFAULT_DEFAULT = 0;

    /**
     * The default-default value for long preferences (<code>0L</code>).
     */
    public static final long LONG_DEFAULT_DEFAULT = 0L;

    /**
     * The default-default value for String preferences (<code>""</code>).
     */
    public static final String STRING_DEFAULT_DEFAULT = ""; //$NON-NLS-1$

    /**
     * The string representation used for <code>true</code> (<code>"true"</code>).
     */
    public static final String TRUE = "true"; //$NON-NLS-1$

    /**
     * The string representation used for <code>false</code> (<code>"false"</code>).
     */
    public static final String FALSE = "false"; //$NON-NLS-1$

    private PropertyChangeSupport changes = new PropertyChangeSupport( this );

    private Preferences pref;
    private Preferences defaultPref;
    //Indicates whether a value as been changed by setToDefault or setValue; initially false.
    private boolean dirty = false;
    
    
            
    public NetbeansPreferenceStore(){
        pref = NbPreferences.root(); 
        defaultPref = NbPreferences.forModule(SarosPanel.class);
    }
    
    @Override
    public void addPreferenceChangeListener(IPreferenceChangeListener il) {
         pref.addPreferenceChangeListener((PreferenceChangeListener) il);
    }

    @Override
    public void removePreferenceChangeListener(IPreferenceChangeListener il) {
       pref.removePreferenceChangeListener((PreferenceChangeListener) il);
    }

    @Override
    public boolean getBoolean(String name) {
        boolean value = BOOLEAN_DEFAULT_DEFAULT;
        if(pref != null)
             value = pref.getBoolean(name,BOOLEAN_DEFAULT_DEFAULT);
	return value;
    }

    @Override
    public boolean getDefaultBoolean(String name) {
        boolean value = BOOLEAN_DEFAULT_DEFAULT;
        if(defaultPref != null)
             value = defaultPref.getBoolean(name,BOOLEAN_DEFAULT_DEFAULT);
	return value;
    }

    @Override
    public int getInt(String number) {
        
        int value = INT_DEFAULT_DEFAULT;
        if(pref != null)
            value = pref.getInt(number,INT_DEFAULT_DEFAULT);
        return value;    
    }

    @Override
    public int getDefaultInt(String number) {
         int value = INT_DEFAULT_DEFAULT;
        if(defaultPref != null)
            value = defaultPref.getInt(number,INT_DEFAULT_DEFAULT);
        return value;    
    }

    @Override
    public long getLong(String number) {
        
        long value = LONG_DEFAULT_DEFAULT;
        if(pref != null)
            value = pref.getLong(number,LONG_DEFAULT_DEFAULT);
        return value;
    }

    @Override
    public long getDefaultLong(String number) {
         long value = LONG_DEFAULT_DEFAULT;
        if(defaultPref != null)
            value = defaultPref.getLong(number,LONG_DEFAULT_DEFAULT);
        return value;
    }

    @Override
    public String getString(String string) {
        
        String value = STRING_DEFAULT_DEFAULT;
        if(pref != null)
            value = pref.get(string,STRING_DEFAULT_DEFAULT);
        return value;
    }

    @Override
    public String getDefaultString(String string) {
         String value = STRING_DEFAULT_DEFAULT;
        if(defaultPref != null)
            value = defaultPref.get(string,STRING_DEFAULT_DEFAULT);
        return value;
    }

    @Override
    public void setValue(String name, int newValue) {
        
        int oldValue = getInt(name);
        if(oldValue != newValue){
            if(pref != null)
                pref.putInt(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Integer(oldValue), new Integer(newValue));           
    }

    @Override
    public void setDefault(String name, int newValue) {
       int oldValue = getInt(name);
        if(oldValue != newValue){
            if(defaultPref != null)
                defaultPref.putInt(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Integer(oldValue), new Integer(newValue));       
    }

    @Override
    public void setValue(String name, long newValue) {
        long oldValue = getLong(name);
        if(oldValue != newValue){
            if(pref != null)
                pref.putLong(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Long(oldValue), new Long(newValue));      
    }

    @Override
    public void setDefault(String name, long newValue) {
        long oldValue = getLong(name);
        if(oldValue != newValue){
            if(defaultPref != null)
                defaultPref.putLong(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Long(oldValue), new Long(newValue));  
    }

    @Override
    public void setValue(String name, String newValue) {
        String oldValue = getString(name);
        if(oldValue != newValue){
            if(pref != null)
                pref.put(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, oldValue, newValue);  
    }

    @Override
    public void setDefault(String name, String newValue) {
       String oldValue = getString(name);
        if(oldValue != newValue){
            if(defaultPref != null)
                defaultPref.put(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, oldValue, newValue); 
    }

    @Override
    public void setValue(String name, boolean newValue) {
        boolean oldValue = getBoolean(name);
        if(oldValue != newValue){
            if(pref != null)
                pref.putBoolean(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Boolean(oldValue), new Boolean(newValue));  
    }

    @Override
    public void setDefault(String name, boolean newValue) {
        boolean oldValue = getBoolean(name);
        if(oldValue != newValue){
            if(defaultPref != null)
                defaultPref.putBoolean(name, newValue);
        }
        dirty = true;
        changes.firePropertyChange( name, new Boolean(oldValue), new Boolean(newValue));  
    }
    public void addPropertyChangeListener( PropertyChangeListener l )
    {
      changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener( PropertyChangeListener l )
    {
      changes.removePropertyChangeListener( l );
    }
    
}
