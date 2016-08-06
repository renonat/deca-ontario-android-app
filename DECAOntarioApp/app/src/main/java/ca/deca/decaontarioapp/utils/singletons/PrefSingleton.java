package ca.deca.decaontarioapp.utils.singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Handles all shared-preference related methods.
 * <br>This is a Singleton, so the first call initializes the class, and
 * all others access the same instance.
 * <br>Includes pre-made methods for accessing and changing all shared-preferences.
 */
public class PrefSingleton {
    private static PrefSingleton mInstance;
    private Context mContext;

    private SharedPreferences mMyPreferences;

    /**
     * A private constructor for the class
     */
    private PrefSingleton() {
    }

    /**
     * Gets the current instance of the class, and creates it if it does not exist
     *
     * @return {@link PrefSingleton} : the instance
     * of PrefSingleton
     */
    public static PrefSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new PrefSingleton();
        }
        return mInstance;
    }

    /**
     * Initializes the variables from string resources, sets the global context,
     * and preferencemanager
     *
     * @param ctxt {@link Context} : The context of the parent activity
     */
    public void Initialize(Context ctxt) {
        mContext = ctxt;
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Gets the boolean state of whether the Provincials view is expanded or not.
     * @return {boolean} : true = expanded, false = compressed
     */
    public boolean getMainProvincialsState() {
        return mMyPreferences.getBoolean(Keys.MAIN_PROVINCIALS_STATE, false);
    }

    /**
     * Sets the boolean state of whether the Provincials view is expanded or not.
     * @param expanded {boolean} : true = expanded, false = compressed
     */
    public void setMainProvincialsState(boolean expanded) {
        saveBoolean(Keys.MAIN_PROVINCIALS_STATE, expanded);
    }

    /**
     * Gets the sort mode for the Training screen
     * @return {int} : Sort mode
     */
    public int getTrainingNavState() {
        return mMyPreferences.getInt(Keys.TRAINING_NAV_STATE, 0);
    }

    /**
     * Sets the sort mode for the Training screen
     * @param value {int} : value
     */
    public void setTrainingNavState(int value) {
        saveInt(Keys.TRAINING_NAV_STATE, value);
    }

    /**
     * Gets the region for the Regionals screen
     * @return {int} : region
     */
    public int getRegionalsNavState() {
        return mMyPreferences.getInt(Keys.REGIONALS_NAV_STATE, 0);
    }

    /**
     * Sets the region for the Regionals screen
     * @param value {int} : region
     */
    public void setRegionalsNavState(int value) {
        saveInt(Keys.REGIONALS_NAV_STATE, value);
    }

    /**
     * Saves a boolean value to a certain key in preferences.
     * For internal use only.
     * @param key {String} : the key
     * @param value {boolean} : the value
     */
    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor mEdit = mMyPreferences.edit();
        mEdit.putBoolean(key, value);
        mEdit.apply();
    }

    /**
     * Saves an integer value to a certain key in preferences.
     * For internal use only.
     * @param key {String} : the key
     * @param value {int} : the value
     */
    private void saveInt(String key, int value) {
        SharedPreferences.Editor mEdit = mMyPreferences.edit();
        mEdit.putInt(key, value);
        mEdit.apply();
    }

    private class Keys {
        public static final String MAIN_PROVINCIALS_STATE = "main_provincials_state";
        public static final String TRAINING_NAV_STATE = "training_nav_state";
        public static final String REGIONALS_NAV_STATE = "regionals_nav_state";
    }
}