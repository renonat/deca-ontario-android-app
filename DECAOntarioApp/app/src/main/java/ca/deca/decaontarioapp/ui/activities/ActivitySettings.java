package ca.deca.decaontarioapp.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.ui.views.AvatarListItem;

/**
 * Created by Reno on 15-08-05.
 */
public class ActivitySettings extends BaseActivity {

    @InjectView(R.id.settings_creator) AvatarListItem mCreator;
    @InjectView(R.id.settings_email) AvatarListItem mEmail;
    @InjectView(R.id.settings_version) AvatarListItem mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initToolbar();
        initAboutButtons();
    }

    private void initToolbar() {
        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Settings");
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }
    }

    /**
     * Set up the content of the Email, Version, and Creator buttons
     * Set up the content of the Email, Version, and Creator buttons
     */
    private void initAboutButtons() {
        //TODO: We need a real support email
        final String email = "support@placeholder.com";
        mEmail.setOnTouchListener(this);
        mEmail.setSubText(email);
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click create an email to the given email address
                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:" + email));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DECA Ontario App Feedback v" +
                        getAppVersion());
                startActivity(Intent.createChooser(emailIntent, "Contact Us"));
            }
        });

        mVersion.setSubText("v. " + getAppVersion());

        mCreator.setSubText("Reno Natalizio");
    }

    /**
     * Gets the version number of the application
     *
     * @return {@link java.lang.String} : the version-name (number) of the application
     */
    private String getAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivitySettings.this, ActivityMainScreen.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
    }
}
