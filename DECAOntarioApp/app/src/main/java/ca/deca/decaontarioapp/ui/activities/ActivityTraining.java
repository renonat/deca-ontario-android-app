package ca.deca.decaontarioapp.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.adapters.ToolbarSpinnerAdapter;
import ca.deca.decaontarioapp.adapters.TrainingAdapter;
import ca.deca.decaontarioapp.objects.Event;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import ca.deca.decaontarioapp.utils.singletons.PrefSingleton;
import de.greenrobot.event.EventBus;

public class ActivityTraining extends BaseActivity {

    @InjectView(R.id.training_list) ListView rootList;

    // The data for the listview
    List<Event> events;

    Event.SORT_MODE mode = Event.SORT_MODE.ALPHABETICAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.inject(this);

        initToolbar();

        events = DataSingleton.getInstance().getEventsData();
        refreshList();
    }

    /**
     * Refresh the list view. First the events are sorted based on the sorting mode.
     * Next a new adapter is created to handel the newly sorted list of events.
     */
    private void refreshList() {
        if (events != null) {
            sortEvents();
            final TrainingAdapter adapter = new TrainingAdapter(this, events, mode);
            rootList.setAdapter(adapter);
            rootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // On item click, get the event item and then start the detail activity
                    // The event must be passed to the activity as an Extra
                    Event e = adapter.getEvent(position);
                    if (e != null)
                        startActivity(new Intent(ActivityTraining.this, ActivityEventDetail.class)
                                .putExtra("EVENT", e));
                }
            });
        }
    }

    /**
     * Sort the list of events based on the three list sort modes.
     */
    private void sortEvents() {
        if (mode == Event.SORT_MODE.ALPHABETICAL) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    // Sort events simply by name.
                    return e1.getName().compareTo(e2.getName());
                }
            });
        } else if (mode == Event.SORT_MODE.FORMAT) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    // Sort events by format rank.
                    int n1 = e1.getFormatValue();
                    int n2 = e2.getFormatValue();
                    if (n1 > n2)
                        return 1;
                    else if (n2 > n1)
                        return -1;
                    else {
                        // If events have the same rank, then compare by name
                        return e1.getName().compareTo(e2.getName());
                    }

                }
            });
        } else if (mode == Event.SORT_MODE.CLUSTER) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    // Compare by cluster, then format, then name
                    int cluster = e1.getCluster().compareTo(e2.getCluster());
                    if (cluster == 0) {
                        int format = e1.getFormat().compareTo(e2.getFormat());
                        if (format == 0) {
                            return e1.getName().compareTo(e2.getName());
                        } else
                            return format;
                    } else
                        return cluster;
                }
            });
        }
    }

    /**
     * Initialize the toolbar, which contains a dropdown spinner allowing the user to select
     * the sort mode for the data.
     */
    private void initToolbar() {
        // Create the actual toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // The the toolbar for the Material Design paradigm
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }

            // Set up the spinner to choose the sort mode
            ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter(this);
            spinnerAdapter.addItem("Alphabetical");
            spinnerAdapter.addItem("Event Format");
            spinnerAdapter.addItem("Cluster");

            Spinner spinner = (Spinner) findViewById(R.id.toolbar_spinner);
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(PrefSingleton.getInstance().getTrainingNavState());

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long
                        id) {
                    // Update the sort mode, and then update it in shared preferences
                    mode = Event.SORT_MODE.values()[position];
                    PrefSingleton.getInstance().setTrainingNavState(position);
                    // Reload the list based on the new data
                    refreshList();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // Go back to the main screen and clear off the stack
        Intent intent = new Intent(ActivityTraining.this, ActivityMainScreen.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    /**
     * When a notification is received that data has been updated, refresh the list of events.
     * This function responds to the EventBus class.
     * @param event {EVENTBUS_EVENT}
     */
    @Override
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.EVENTS) {
            refreshList();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }

}
