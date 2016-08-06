package ca.deca.decaontarioapp.ui.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.javatuples.Pair;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.adapters.SocialAdapter;
import ca.deca.decaontarioapp.objects.Tweet;
import ca.deca.decaontarioapp.ui.BaseFragment;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import de.greenrobot.event.EventBus;

public class FragmentConnect extends BaseFragment implements View.OnTouchListener {
    @InjectViews({R.id.connect_button_one, R.id.connect_button_two,
            R.id.connect_button_three})
    Button[] mbuttons;
    @InjectView(R.id.connect_list)
    ListView mSocialList;

    List<Tweet> socialTweets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_connect, container, false);
        ButterKnife.inject(this, rootView);

        initButtons();

        refreshTweets();

        return rootView;
    }

    private void initButtons() {
        //TODO: Button cuts off part of the title text, it is not properly resizing
        final Pair[] accounts = new Pair[]{
                new Pair<>("Facebook", "https://www.facebook.com/DECAOntario"),
                new Pair<>("Twitter", "https://twitter.com/decaontario"),
                new Pair<>("Instagram", "https://instagram.com/decaontario/")
        };

        // For each link that we have, set a button up for it
        for (int i = 0; i < accounts.length; i++) {
            // Set the text and color for the buttons
            final Pair<String, String> account = accounts[i];
            mbuttons[i].setText(account.getValue0());
            mbuttons[i].setTextColor(Color.WHITE);
            mbuttons[i].setOnTouchListener(this);
            // On click, the button will navigate to its specific link
            mbuttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(account.getValue1());
                    Intent goToWebsite = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        getActivity().startActivity(goToWebsite);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Couldn't go to " + account.getValue1(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    /**
     * Refresh the ListView of tweet items using DataSingleton data.
     */
    private void refreshTweets() {
        List<Tweet> tweets = DataSingleton.getInstance().getTweets();
        if (tweets != null && getActivity() != null) {
            socialTweets = tweets;
            mSocialList.setAdapter(new SocialAdapter(getActivity(), socialTweets));
            mSocialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position,
                                        long id) {
                    // When a tweet is clicked on
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    // Create an AlertDialog to allow the user to confirm opening the tweet
                    builder.setMessage(socialTweets.get(position).getContent())
                            .setTitle("Open in Twitter?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open up the tweet in our internet browser
                            Uri uri = Uri.parse(socialTweets.get(position).getLink());
                            Intent action = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                startActivity(action);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getActivity(), "Could not open tweet.",
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
     * When a notification is received that data has been updated, refresh tweets.
     * This function responds to the EventBus class.
     *
     * @param event {EVENTBUS_EVENT}
     */
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.TWITTER) {
            refreshTweets();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }
}
