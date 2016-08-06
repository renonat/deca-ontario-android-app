package ca.deca.decaontarioapp.ui.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.utils.VisualUtils;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import ca.deca.decaontarioapp.utils.singletons.PrefSingleton;

public class ActivityMainScreen extends BaseActivity {

    // View Injections

    @InjectView(R.id.main_hero)
    ImageView mHeroImage;

    // Rectangle of four buttons

    @InjectView(R.id.main_b_about)
    Button mAbout;
    @InjectView(R.id.main_b_deadlines)
    Button mDeadlines;
    @InjectView(R.id.main_b_training)
    Button mTraining;
    @InjectView(R.id.main_b_controlcenter)
    Button mControlCenter;

    // Vertical buttons

    @InjectView(R.id.main_b_regionals)
    Button mRegionals;

    // Provincials grouping includes an expanding box setup

    @InjectView(R.id.main_tv_provincials)
    View mProvincials;
    @InjectView(R.id.main_iv_provincials)
    ImageView mProvincialsArrow;
    @InjectView(R.id.main_ll_provincials_links)
    LinearLayout mProvincialsLinks;

    // Information used in building the views

    List<View> provLinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        ButterKnife.inject(this);

        initToolbar();

        initHeroImage();
        initButtons();
        refreshProvincialsLinks();
    }

    /**
     * Creates the ActionBar/Toolbar that is displayed at the top of the screen
     * This toolbar is set to comply to Material Design guidelines on all supported devices.
     */
    private void initToolbar() {
        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Display the DECA Diamond image to the left of the title
            actionBar.setTitle("  DECA ONTARIO");
            actionBar.setLogo(R.drawable.ic_decadiamond);
            // The floating design option is only available on Lollipop or higher
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }
    }

    /**
     * Initializes the animation on the large hero ImageView.
     * The images fade between each other in a loop, but the fade has a white in-between step
     */
    private void initHeroImage() {
        // Animates the ImageView through a looping array of images
        int imagesToShow[] = {R.drawable.heroimagefriends, R.drawable.heroimageawards,
                R.drawable.heroimagewinners, R.drawable.heroimageexams};
        VisualUtils.animate(mHeroImage, imagesToShow, 0, true);
    }

    /**
     * For each button, set their click listener to open to the desired screen or website
     */
    private void initButtons() {
        mAbout.setOnTouchListener(this);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMainScreen.this, ActivityAbout.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        mDeadlines.setOnTouchListener(this);
        mDeadlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMainScreen.this, ActivityDeadlines.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        mTraining.setOnTouchListener(this);
        mTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMainScreen.this, ActivityTraining.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mControlCenter.setOnTouchListener(this);
        mControlCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("Login to DECA Ontario?", "https://register.deca.ca/login");
            }
        });

        mRegionals.setOnTouchListener(this);
        mRegionals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMainScreen.this, ActivityRegionals.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mProvincials.setOnTouchListener(this);
        mProvincials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand and contract the links when the textview is clicked
                if (mProvincialsLinks.getVisibility() == View.GONE) {
                    VisualUtils.expand(mProvincialsLinks);
                    VisualUtils.rotateImage(mProvincialsArrow);
                    PrefSingleton.getInstance().setMainProvincialsState(true);
                } else { // It is currently visible
                    VisualUtils.collapse(mProvincialsLinks);
                    VisualUtils.rotateImage(mProvincialsArrow);
                    PrefSingleton.getInstance().setMainProvincialsState(false);
                }
            }
        });

        // Restore the state of the expanded view
        if (PrefSingleton.getInstance().getMainProvincialsState()) {
            VisualUtils.expand(mProvincialsLinks);
            VisualUtils.rotateImage(mProvincialsArrow);
        }
    }

    /**
     * Replace all old Provincials linsk with new links.
     */
    private void refreshProvincialsLinks() {
        // Trash all the old listeners
        for (View v : provLinks) {
            v.setOnClickListener(null);
            v.setOnTouchListener(null);
        }

        // Remove all existing children from the layout
        if (mProvincialsLinks.getChildCount() > 0)
            mProvincialsLinks.removeAllViews();

        // Add an about textview that opens the Provs About Activity
        mProvincialsLinks.addView(createDivider());
        TextView textView = createTextView();
        textView.setText("About");
        textView.setOnTouchListener(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMainScreen.this, ActivityProvincialsAbout.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        provLinks.add(textView);
        mProvincialsLinks.addView(textView);

        // Add all the links from the data file as TextViews
        List<Pair<String, String>> data = DataSingleton.getInstance().getProvincialsLinks();
        for (int i = 0; i < data.size(); i++) {
            final Pair<String, String> item = data.get(i);

            // First add a divider line
            mProvincialsLinks.addView(createDivider());

            textView = createTextView();
            textView.setText(item.getValue0());

            // Open the link on click
            textView.setOnTouchListener(this);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // The first value is the link title, the second is the URL
                    openLink("Open " + item.getValue0() + "?", item.getValue1());
                }
            });

            provLinks.add(textView);
            mProvincialsLinks.addView(textView);
        }

    }

    /**
     * Create a 1 pixel thick horizontal divider
     * @return {View} : the divider
     */
    private View createDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(VisualUtils.getColor(this, R.color.divider));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(VisualUtils.dpToPx(this, 72), 0, 0, 0);
        divider.setLayoutParams(params);
        return divider;
    }

    /**
     * Create a styled TextView styled like a button
     * @return {TextView} : the textview
     */
    private TextView createTextView() {
        // Add a textview with the title as the text, styled like a button
        TextView textView = new TextView(this);
        textView.setTextSize(16.0f);
        textView.setTextColor(VisualUtils.getColor(this, R.color.white));
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.flat_button_blue));
        int margin = VisualUtils.dpToPx(this, 16);
        textView.setPadding(VisualUtils.dpToPx(this, 72), margin, margin, margin);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * Asks the user if they want to open a certain link, and then opens it in the browser
     * @param title {String} : the title of the link
     * @param url {String} : the URL
     */
    private void openLink(String title, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainScreen.this);
        // Create an AlertDialog to allow the user to confirm opening the link
        builder.setMessage(url)
                .setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open up the link in our internet browser
                Uri uri = Uri.parse(url);
                Intent action = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(action);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ActivityMainScreen.this, "Could not open link.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(ActivityMainScreen.this, ActivitySettings.class).
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When a notification is received that data has been updated, refresh the provincials links section.
     * This function responds to the EventBus class.
     * @param event {EVENTBUS_EVENT}
     */
    @Override
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.LINKS) {
            refreshProvincialsLinks();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }


}
