package ca.deca.decaontarioapp.ui.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.ui.views.AvatarListItem;
import ca.deca.decaontarioapp.utils.VisualUtils;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import de.greenrobot.event.EventBus;

/**
 * Created by Reno on 15-08-15.
 */
public class ActivityProvincialsAbout extends BaseActivity {

    final DateTimeFormatter inFormat = new DateTimeFormatterBuilder()
            .appendMonthOfYear(1).appendLiteral("/")
            .appendDayOfMonth(1).appendLiteral("/")
            .appendYear(4, 4).toFormatter();
    final DateTimeFormatter outFormat = new DateTimeFormatterBuilder()
            .appendMonthOfYearText().appendLiteral(" ")
            .appendDayOfMonth(1).appendLiteral(", ")
            .appendYear(4, 4).toFormatter();

    @InjectView(R.id.provincials_description) TextView mDescription;
    @InjectView(R.id.provincials_subdescription) TextView mSubDescription;
    @InjectView(R.id.provincials_cost) AvatarListItem mCost;
    @InjectView(R.id.provincials_date) AvatarListItem mDate;
    @InjectView(R.id.provincials_deadline) AvatarListItem mDeadline;
    @InjectView(R.id.provincials_location) AvatarListItem mLocation;
    @InjectView(R.id.provincials_layout) View mLayout;
    private HashMap<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provincials_about);
        ButterKnife.inject(this);


        initToolbar();

        initOnTouchListeners();
        refreshData();
    }

    private void initToolbar() {
        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_provincials_about));
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }
    }

    private void initOnTouchListeners() {
        mDate.setOnTouchListener(this);
        mDeadline.setOnTouchListener(this);
        mLocation.setOnTouchListener(this);
    }

    /**
     * Refresh all the data and the listeners for the ListItems
     */
    private void refreshData() {
        data = DataSingleton.getInstance().getProvincialsAbout();

        mDescription.setText(data.get(DataSingleton.Keys.PROVINCIALS_DESCRIPTION));
        mSubDescription.setText(data.get(DataSingleton.Keys.PROVINCIALS_SUBDESCRIPTION));

        mCost.setText(data.get(DataSingleton.Keys.PROVINCIALS_COST));
        mCost.setSubText(data.get(DataSingleton.Keys.PROVINCIALS_COSTSUB));

        mDeadline.setText("Registration Due");
        mDeadline.setSubText(outFormat.print(inFormat.parseDateTime(
                data.get(DataSingleton.Keys.PROVINCIALS_DEADLINE))));
        mDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DateTime date = inFormat.parseDateTime(
                        data.get(DataSingleton.Keys.PROVINCIALS_DEADLINE));
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProvincialsAbout
                        .this);
                // Create an AlertDialog to allow the user to confirm opening in maps
                builder.setMessage("Provincials Registration Due" + "\n" + outFormat.print(date))
                        .setTitle("Add to Calendar?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create a new calendar event for the day
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE,
                                "Provincials Registration Due");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                date.getMillis());
                        // Add 86400000 so the calendar registers the event as spanning one day
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                date.getMillis() + 86400000);
                        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mLocation.setText(data.get(DataSingleton.Keys.PROVINCIALS_LOCATION));
        mLocation.setSubText(data.get(DataSingleton.Keys.PROVINCIALS_ADDRESS));
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ActivityProvincialsAbout.this);
                // Create an AlertDialog to allow the user to confirm opening in maps
                builder.setMessage(data.get(DataSingleton.Keys.PROVINCIALS_LOCATION)
                        + "\n" + data.get(DataSingleton.Keys.PROVINCIALS_ADDRESS))
                        .setTitle("Open in Maps?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://www.google.ca/maps/place/" +
                                data.get(DataSingleton.Keys.PROVINCIALS_LOCATION)
                                        .replace(" ", "+") + "/";
                        Uri uri = Uri.parse(url);
                        Intent action = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(action);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(ActivityProvincialsAbout.this, "Could not open map.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mDate.setText("Provincials Date");
        mDate.setSubText(data.get(DataSingleton.Keys.PROVINCIALS_DATE));
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProvincialsAbout
                        .this);
                // Create an AlertDialog to allow the user to confirm opening in maps
                builder.setMessage("DECA Provincials" + "\n" +
                        data.get(DataSingleton.Keys.PROVINCIALS_DATE))
                        .setTitle("Add to Calendar?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create a new calendar event that spans multiple days
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE,
                                "DECA Provincials");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                inFormat.parseDateTime(data.get(DataSingleton.Keys
                                        .PROVINCIALS_STARTDATE)).getMillis());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                inFormat.parseDateTime(data.get(DataSingleton.Keys
                                        .PROVINCIALS_ENDDATE)).getMillis() + 86400000);
                        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        VisualUtils.expand(mLayout);
    }


    /**
     * When a notification is received that data has been updated, refresh the data.
     * This function responds to the EventBus class.
     * @param event {EVENTBUS_EVENT}
     */
    @Override
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.PROVINCIALS) {
            refreshData();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }
}
