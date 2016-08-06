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
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.objects.Event;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.ui.views.AvatarListItem;
import ca.deca.decaontarioapp.utils.VisualUtils;

/**
 * Created by Reno on 15-08-19.
 */
public class ActivityEventDetail extends BaseActivity {

    @InjectView(R.id.event_description) AvatarListItem mDescription;
    @InjectView(R.id.event_members) AvatarListItem mMembers;
    @InjectView(R.id.event_guidelines) AvatarListItem mGuidelines;
    @InjectView(R.id.event_written) AvatarListItem mWritten;
    @InjectView(R.id.event_presentation) AvatarListItem mPresentation;
    @InjectView(R.id.event_preptime) AvatarListItem mPreptime;
    @InjectView(R.id.event_sample) AvatarListItem mSample;
    @InjectView(R.id.event_rubric) AvatarListItem mRubric;
    @InjectView(R.id.event_exam) AvatarListItem mExam;
    @InjectView(R.id.event_performance_indicators) AvatarListItem mPIs;
    @InjectView(R.id.event_extras) AvatarListItem mExtras;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("EVENT")) {
            event = (Event) bundle.get("EVENT");
        }

        initToolbar();

        // Initialize all of the information Views
        initDescription();
        initMembers();
        initGuidelines();
        initWritten();
        initPresentation();
        initPrepTime();
        initSample();
        initRubric();
        initExam();
        initPIs();
        initExtras();
    }

    /**
     * Initialize the toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (event != null)
                actionBar.setTitle(event.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }
    }

    /**
     * The description box displays the event description
     */
    private void initDescription() {
        String description = event.getDescription();
        if (!description.isEmpty()) {
            mDescription.setText(description);
            mDescription.setAvatarText(event.getCode());
            mDescription.setAvatarColor(VisualUtils.getColor(this, event.getClusterColor()));
            mDescription.setVisibility(View.VISIBLE);
            mDescription.setDividerVisibility(true);
        }
    }

    /**
     * Does some fancy work to show the number of participants
     */
    private void initMembers() {
        String members = event.getMembers();
        if (!members.isEmpty()) {
            mMembers.setText("Allowed Number of Participants");
            // Add an 's' to the end if we have more than one member
            mMembers.setSubText(members + " participant" + (members.trim().equals("1") ? "" : "s"));
            // Change the icon is we have more than one member
            if (!(members.trim().equals("1"))) {
                mMembers.setAvatar(getResources().getDrawable(R.drawable.ic_users));
            }
            mMembers.setVisibility(View.VISIBLE);
            mMembers.setDividerVisibility(true);
        }
    }

    /**
     * Provides a clickable link to the event guidelines
     */
    private void initGuidelines() {
        final String guidelinesURL = event.getGuidelines();
        if (!guidelinesURL.isEmpty()) {
            mGuidelines.setText("Event Guidelines");
            mGuidelines.setVisibility(View.VISIBLE);
            mGuidelines.setDividerVisibility(true);
            mGuidelines.setOnTouchListener(this);
            mGuidelines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink("Guidelines for " + event.getName(), guidelinesURL);
                }
            });
        }
    }

    /**
     * Number of pages allowed in a written entry
     */
    private void initWritten() {
        final String written = event.getWritten();
        if (!written.isEmpty()) {
            mWritten.setText("Written Entry Pages Allowed");
            mWritten.setSubText(written);
            mWritten.setVisibility(View.VISIBLE);
            mWritten.setDividerVisibility(true);
        }
    }

    /**
     * Time allowed for a presentation
     */
    private void initPresentation() {
        final String presentation = event.getPresentation();
        if (!presentation.isEmpty()) {
            mPresentation.setText("Presentation Time");
            mPresentation.setSubText(presentation);
            mPresentation.setVisibility(View.VISIBLE);
            mPresentation.setDividerVisibility(true);
        }
    }

    /**
     * Allocated preparation time
     */
    private void initPrepTime() {
        final String presentation = event.getPreptime();
        if (!presentation.isEmpty()) {
            mPreptime.setText("Preperation Time");
            mPreptime.setSubText(presentation);
            mPreptime.setVisibility(View.VISIBLE);
            mPreptime.setDividerVisibility(true);
        }
    }

    /**
     * Provides a link to the posted sample event
     */
    private void initSample() {
        final String sampleURL = event.getSample();
        if (!sampleURL.isEmpty()) {
            mSample.setText("Sample Event");
            mSample.setOnTouchListener(this);
            mSample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink("Sample Event for " + event.getName(), sampleURL);
                }
            });
            mSample.setVisibility(View.VISIBLE);
            mSample.setDividerVisibility(true);
        }
    }

    /**
     * Provides a link to the rubric
     */
    private void initRubric() {
        final String rubricURL = event.getRubric();
        if (!rubricURL.isEmpty()) {
            mRubric.setText("Ontario Rubric");
            mRubric.setOnTouchListener(this);
            mRubric.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink("Rubric for " + event.getName(), rubricURL);
                }
            });
            mRubric.setVisibility(View.VISIBLE);
            mRubric.setDividerVisibility(true);
        }
    }

    /**
     * Provides a link to the sample exam
     */
    private void initExam() {
        final String examName = event.getExamName();
        if (!examName.isEmpty()) {
            mExam.setText("Multiple Choice Exam");
            mExam.setSubText(examName);
            mExam.setOnTouchListener(this);
            mExam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink("Sample Exam for " + examName, event.getExamLink());
                }
            });
            mExam.setVisibility(View.VISIBLE);
            mExam.setDividerVisibility(true);
        }
        //TODO: Set icon to be that of the cluster
    }

    /**
     * Provides a link ot the Performance Indicators
     */
    private void initPIs() {
        final String PIsURL = event.getExamPIs();
        if (!PIsURL.isEmpty()) {
            mPIs.setText("Performance Indicators");
            mPIs.setOnTouchListener(this);
            mPIs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink("PIs for " + event.getName(), PIsURL);
                }
            });
            mPIs.setVisibility(View.VISIBLE);
            mPIs.setDividerVisibility(true);
        }
    }

    /**
     * Provides a link to extra information, if any
     */
    private void initExtras() {
        final String extras = event.getExtras();
        if (!extras.isEmpty()) {
            mExtras.setText("More Information");
            if (extras.startsWith("http")) {
                mExtras.setOnTouchListener(this);
                mExtras.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("More Information", extras);
                    }
                });
            } else {
                mExtras.setSubText(extras);
            }
            mExtras.setVisibility(View.VISIBLE);
            mExtras.setDividerVisibility(true);
        }
    }

    /**
     * Asks the user if they want to open a certain link, and then opens it in the browser
     * @param title {String} : the title of the link
     * @param url {String} : the URL
     */
    private void openLink(String title, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEventDetail.this);
        // Create an AlertDialog to allow the user to confirm opening the link
        builder.setMessage(url)
                .setTitle("Open " + title + "?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open up the link in our internet browser
                Uri uri = Uri.parse(url);
                Intent action = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(action);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ActivityEventDetail.this, "Could not open link.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
