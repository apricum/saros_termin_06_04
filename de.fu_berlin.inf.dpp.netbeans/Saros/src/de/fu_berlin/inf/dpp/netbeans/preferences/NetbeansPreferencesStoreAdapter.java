package de.fu_berlin.inf.dpp.netbeans.preferences;

import de.fu_berlin.inf.dpp.preferences.IPreferenceChangeListener;
import de.fu_berlin.inf.dpp.preferences.IPreferenceStore;
import de.fu_berlin.inf.dpp.preferences.PreferenceChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
//import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
//import java.util.prefs.Preferences;
//import org.openide.util.NbPreferences;

   
    
    
    
    
/**
 *
 * @author Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 */
public class NetbeansPreferencesStoreAdapter implements IPreferenceStore{

    private final NetbeansPreferenceStore delegate;
    
    private final List<IPreferenceChangeListener> listeners = new CopyOnWriteArrayList<IPreferenceChangeListener>();

    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
             final PreferenceChangeEvent eventToFire = new PreferenceChangeEvent(
                evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

            for (final IPreferenceChangeListener listener : listeners)
                listener.preferenceChange(eventToFire);

        }
    };

    /**
     * Constructs an EclipsePreferenceStoreAdapter with an
     * {@link IPreferenceStore}
     * 
     * @param delegate
     */
    public NetbeansPreferencesStoreAdapter(final NetbeansPreferenceStore delegate) {
        this.delegate = delegate;
        this.delegate.addPropertyChangeListener(propertyChangeListener);
    }

     @Override
    public void addPreferenceChangeListener(
        final IPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePreferenceChangeListener(
        final IPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean getBoolean(String name) {
        return delegate.getBoolean(name);
    }

    @Override
    public boolean getDefaultBoolean(String name) {
        return delegate.getDefaultBoolean(name);
    }

    @Override
    public int getInt(String name) {
        return delegate.getInt(name);
    }

    @Override
    public int getDefaultInt(String name) {
        return delegate.getDefaultInt(name);
    }

    @Override
    public long getLong(String name) {
        return delegate.getLong(name);
    }

    @Override
    public long getDefaultLong(String name) {
        return delegate.getDefaultLong(name);
    }

    @Override
    public String getString(String name) {
        return delegate.getString(name);
    }

    @Override
    public String getDefaultString(String name) {
        return delegate.getDefaultString(name);
    }

    @Override
    public void setValue(String name, int value) {
        delegate.setValue(name, value);
    }

    @Override
    public void setValue(String name, long value) {
        delegate.setValue(name, value);
    }

    @Override
    public void setValue(String name, String value) {
        delegate.setValue(name, value);
    }

    @Override
    public void setValue(String name, boolean value) {
        delegate.setValue(name, value);
    }

    @Override
    public void setDefault(String name, int value) {
        delegate.setDefault(name, value);
    }

    @Override
    public void setDefault(String name, long value) {
        delegate.setDefault(name, value);
    }

    @Override
    public void setDefault(String name, String value) {
        delegate.setDefault(name, value);
    }

    @Override
    public void setDefault(String name, boolean value) {
        delegate.setDefault(name, value);
    }

    /**
     * Returns the {@linkplain IPreferenceStore preference store} for this
     * adapter.
     * 
     * @return the preference store of this adapter
     */
    public IPreferenceStore getPreferenceStore() {
        return delegate;
    }
    
    
}
