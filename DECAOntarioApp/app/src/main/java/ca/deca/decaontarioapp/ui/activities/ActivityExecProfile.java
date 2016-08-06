package ca.deca.decaontarioapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;

/**
 * Created by Reno on 15-07-28.
 */
public class ActivityExecProfile extends BaseActivity {

    @InjectView(R.id.exec_profile_bio)
    TextView mBio;
    @InjectView(R.id.exec_profile_image)
    ImageView mImage;

    HashMap<String, String> profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exec_profile);
        ButterKnife.inject(this);

        // Get the passed in exec profile HashMap
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("EXEC_PROFILE")) {
            profile = (HashMap<String, String>) bundle.get("EXEC_PROFILE");
        }

        initToolbar();
        initBody();
        initImage();
    }

    /**
     * Initialize the toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (profile != null)
                actionBar.setTitle(profile.get(DataSingleton.Keys.EXECS_NAME));
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
                actionBar.setElevation(4);
        }
    }

    /**
     * Set the body text of the executive bio
     */
    private void initBody() {
        if (profile != null) {
            mBio.setText(profile.get(DataSingleton.Keys.EXECS_BIO));
        }
    }

    /**
     * Set the side photo as the exec tall image
     */
    private void initImage() {
        if (profile != null) {
            Bitmap image = DataSingleton.getInstance().getExecTallImage(profile);
            if (image != null) {
                mImage.setImageBitmap(image);
                mImage.setVisibility(View.VISIBLE);
            } else
                mImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // Go to the 3rd fragment when we get back to the activity
        Intent intent = new Intent(ActivityExecProfile.this, ActivityAbout.class).
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra("GO_TO_FRAGMENT", 2); // Does not work sadly
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exec_profile, menu);
        if (profile != null) {
            // Hide the phone option if their number was not listed
            MenuItem item = menu.findItem(R.id.action_phone);
            if (profile.get(DataSingleton.Keys.EXECS_PHONE).isEmpty())
                item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_email) {
            // On click create an email to the Exec
            if (profile != null) {
                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:" + profile.get(DataSingleton.Keys.EXECS_EMAIL)));
                startActivity(Intent.createChooser(emailIntent, "Email " +
                        profile.get(DataSingleton.Keys.EXECS_NAME)));
            }
        } else if (id == R.id.action_phone) {
            if (profile != null) {
                // On click open the phone with the phone number pre-dialed
                String phone = profile.get(DataSingleton.Keys.EXECS_PHONE);
                if (!phone.isEmpty()) {
                    final Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + phone));
                    startActivity(Intent.createChooser(phoneIntent, "Phone " + profile.get
                            (DataSingleton.Keys.EXECS_NAME)));
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When a notification is received that image data has been updated, refresh the image view.
     * This function responds to the EventBus class.
     *
     * @param event {EVENTBUS_EVENT}
     */
    @Override
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.TALL_IMAGE) {
            initImage();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.EXECS) {
            initBody();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }

}
