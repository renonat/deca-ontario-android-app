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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.adapters.ToolbarSpinnerAdapter;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.ui.views.AvatarListItem;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import ca.deca.decaontarioapp.utils.singletons.PrefSingleton;
import de.greenrobot.event.EventBus;

/**
 * Created by Reno on 15-08-06.
 */
public class ActivityRegionals extends BaseActivity {

    final DateTimeFormatter inFormat = new DateTimeFormatterBuilder()
            .appendMonthOfYear(1).appendLiteral("/")
            .appendDayOfMonth(1).appendLiteral("/")
            .appendYear(4, 4).toFormatter();
    final DateTimeFormatter outFormat = new DateTimeFormatterBuilder()
            .appendMonthOfYearText().appendLiteral(" ")
            .appendDayOfMonth(1).appendLiteral(", ")
            .appendYear(4, 4).toFormatter();

    @InjectView(R.id.regionals_about) TextView mAbout;
    @InjectView(R.id.regionals_cost) AvatarListItem mCost;
    @InjectView(R.id.regionals_date) AvatarListItem mDate;
    @InjectView(R.id.regionals_deadline) AvatarListItem mDeadline;
    @InjectView(R.id.regionals_location) AvatarListItem mLocation;
    @InjectView(R.id.regionals_multiplechoice) AvatarListItem mMultipleChoice;
    @InjectView(R.id.regionals_layout) View mLayout;
    private List<HashMap<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regionals);
        ButterKnife.inject(this);

        initOnTouchListeners();

        initToolbar();
    }

    private void initToolbar() {
        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }

            refreshData();
        }
    }

    /**
     * Firstly refresh the data from storage.
     * Next set up the spinner adaptor to have all the regional events as options.
     */
    private void refreshData() {
        data = DataSingleton.getInstance().getRegionalsData();
        // Set up the spinner to choose the sort mode
        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter(this);
        for (HashMap<String, String> map : data) {
            spinnerAdapter.addItem(map.get(DataSingleton.Keys.REGIONALS_NAME));
        }

        Spinner spinner = (Spinner) findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long
                    id) {
                // Refresh the displayed information based on the selected regional event
                PrefSingleton.getInstance().setRegionalsNavState(position);
                refreshList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(PrefSingleton.getInstance().getRegionalsNavState());
    }

    private void initOnTouchListeners() {
        mDate.setOnTouchListener(this);
        mLocation.setOnTouchListener(this);
        mMultipleChoice.setOnTouchListener(this);
        mDeadline.setOnTouchListener(this);
    }

    /**
     * Set the data for each ListItem and set the ClickListeners
     * @param position {int} : Regional id
     */
    private void refreshList(int position) {
        final HashMap<String, String> map = data.get(position);

        final String titleRegionals = "Regionals Date",
                titleMultipleChoice = "Multiple Choice Testing",
                titleDeadline = "Registration Deadline";

        mAbout.setText(map.get(DataSingleton.Keys.REGIONALS_ABOUT));

        mDate.setText(titleRegionals);
        mDate.setSubText(outFormat.print(
                inFormat.parseDateTime(map.get(DataSingleton.Keys.REGIONALS_DATE))));

        mLocation.setText(map.get(DataSingleton.Keys.REGIONALS_LOCATION));
        mLocation.setSubText(map.get(DataSingleton.Keys.REGIONALS_ADDRESS));

        mCost.setText(map.get(DataSingleton.Keys.REGIONALS_COST));

        mMultipleChoice.setText(titleMultipleChoice);
        mMultipleChoice.setSubText(outFormat.print(
                inFormat.parseDateTime(map.get(DataSingleton.Keys.REGIONALS_MULTIPLECHOICE))));

        mDeadline.setText(titleDeadline);
        mDeadline.setSubText(outFormat.print(
                inFormat.parseDateTime(map.get(DataSingleton.Keys.REGIONALS_DEADLINE))));

        // Only show the layout after all the data has been loaded
        mLayout.setVisibility(View.VISIBLE);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(map.get(DataSingleton.Keys.REGIONALS_NAME),
                        map.get(DataSingleton.Keys.REGIONALS_DATE));
            }
        });

        mMultipleChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(titleMultipleChoice, map.get(DataSingleton.Keys
                        .REGIONALS_MULTIPLECHOICE));
            }
        });

        mDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(titleDeadline, map.get(DataSingleton.Keys.REGIONALS_DEADLINE));
            }
        });

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the selected location in google maps, based on the location stored in data
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegionals.this);
                // Create an AlertDialog to allow the user to confirm opening in maps
                builder.setMessage(map.get(DataSingleton.Keys.REGIONALS_LOCATION)
                        + "\n" + map.get(DataSingleton.Keys.REGIONALS_ADDRESS))
                        .setTitle("Open in Maps?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://www.google.ca/maps/place/" +
                                map.get(DataSingleton.Keys.REGIONALS_LOCATION)
                                        .replace(" ", "+") + "/";
                        Uri uri = Uri.parse(url);
                        Intent action = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(action);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(ActivityRegionals.this, "Could not open map.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * Create a calendar event with a specific title on a specific date and time.
     * @param title {String} : The title of the event
     * @param dateString {String} : The date as a string in MM/DD/YYYY format
     */
    private void openCalendar(final String title, String dateString) {
        final DateTime date = inFormat.parseDateTime(dateString);
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegionals.this);
        // Create an AlertDialog to allow the user to confirm opening in calendar
        builder.setMessage(title + "\n" + outFormat.print(date))
                .setTitle("Add to Calendar?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create a new calendar event
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, title);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        date.getMillis());
                // Add 86400000 milliseconds so the calendar registers the event as spanning one day
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

    /**
     * When a notification is received that data has been updated, refresh the Regionals data.
     * This function responds to the EventBus class.
     * @param event {EVENTBUS_EVENT}
     */
    @Override
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.REGIONALS) {
            refreshData();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }
}
