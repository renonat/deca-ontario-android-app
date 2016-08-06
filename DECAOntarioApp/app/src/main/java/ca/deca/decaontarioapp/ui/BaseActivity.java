package ca.deca.decaontarioapp.ui;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import ca.deca.decaontarioapp.utils.singletons.PrefSingleton;
import de.greenrobot.event.EventBus;


/**
 * Created by Reno on 14-12-31.
 * A base Activity that is extended by every Activity for common theme and cache execution
 */
public class BaseActivity extends ActionBarActivity implements View.OnTouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize my singletons for use throughout the application
        PrefSingleton.getInstance().Initialize(this);
        DataSingleton.getInstance().Initialize(this);

        // Now do the usual activity methods
        super.onCreate(savedInstanceState);

        initTheme();
    }

    /**
     *  If on the correct build version, sets the status bar to be a slightly darker color
     *  than the Primary Color of the theme.
     */
    private void initTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            int color = typedValue.data;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Needed else the selector is laggy to update colors
        // This sends the ACTION_DOWN much quicker, making extremely fast response times
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                v.setPressed(true);
                break;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Needed to help ensure efficiency of returning back when back is pressed
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // We want to make sure our cache wasn't deleted when the app was Paused
        DataSingleton.getInstance().verifyCache();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register for EventBus notifications
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        // Stop the EventBus listener before we stop the Activity
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        // This is here so that a class that does not override will not crash when
        // an event comes through
    }
}
