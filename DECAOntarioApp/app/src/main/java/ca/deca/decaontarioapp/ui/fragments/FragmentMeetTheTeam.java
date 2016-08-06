package ca.deca.decaontarioapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.adapters.ProfileAdapter;
import ca.deca.decaontarioapp.ui.BaseFragment;
import ca.deca.decaontarioapp.ui.activities.ActivityExecProfile;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import de.greenrobot.event.EventBus;

public class FragmentMeetTheTeam extends BaseFragment implements View.OnTouchListener {

    ListView rootView;

    ProfileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = (ListView) inflater.inflate(
                R.layout.fragment_meet_the_team, container, false);
        ButterKnife.inject(this, rootView);

        // Loads the list adaptor with profiles taken from the DataSingleton
        final List<HashMap<String, String>> profiles = DataSingleton.getInstance().getExecsData();
        if (profiles != null) {
            adapter = new ProfileAdapter(getActivity(), profiles);
            rootView.setAdapter(adapter);
            rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Go to the 3rd fragment when we get back to the activity
                    //TODO: Is not working currently
                    Intent intent = new Intent(getActivity(), ActivityExecProfile.class).
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    // Send the Profile as an array through the intent
                    intent.putExtra("EXEC_PROFILE", profiles.get(position));
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * When a notification is received that data has been updated, update the list data set.
     * This function responds to the EventBus class.
     *
     * @param event {EVENTBUS_EVENT}
     */
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.PROFILE_IMAGE) {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }
}
