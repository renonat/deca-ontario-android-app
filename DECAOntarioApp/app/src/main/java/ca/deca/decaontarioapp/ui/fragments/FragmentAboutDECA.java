package ca.deca.decaontarioapp.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseFragment;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;
import de.greenrobot.event.EventBus;

/**
 * The Fragment that displays information about DECA, in a dynamically loaded text page.
 */
public class FragmentAboutDECA extends BaseFragment {

    @InjectView(R.id.about_textview)
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_about_deca,
                container, false);
        ButterKnife.inject(this, rootView);

        // Initialize the text of the view
        initText();

        return rootView;
    }

    private void initText() {
        List<HashMap<String, String>> data = DataSingleton.getInstance().getAboutData();

        SpannableStringBuilder finalSpan = new SpannableStringBuilder();

        // The data is an array of "paragraphs", each ith a potential header, title, and body section
        // Each section has a different styling, allowing for dynamic text display
        SpannableString temp;
        for (HashMap<String, String> map : data) {
            // Add the header in a Headline sized, text colored, bold font
            if (map.containsKey(DataSingleton.Keys.ABOUT_HEADER)) {
                String header = map.get(DataSingleton.Keys.ABOUT_HEADER);
                temp = new SpannableString(header + "\n\n");
                temp.setSpan(new AbsoluteSizeSpan(
                                (int) getResources().getDimension(R.dimen.text_size_headline)),
                        0, header.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                temp.setSpan(new StyleSpan(Typeface.BOLD),
                        0, header.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                finalSpan.append(temp);
            }

            // Add the title in a Title sized, secondary colored
            if (map.containsKey(DataSingleton.Keys.ABOUT_TITLE)) {
                String title = map.get(DataSingleton.Keys.ABOUT_TITLE);
                temp = new SpannableString(title + "\n");
                temp.setSpan(new AbsoluteSizeSpan(
                                (int) getResources().getDimension(R.dimen.text_size_title)),
                        0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                finalSpan.append(temp);
            }

            // Add the body text in a normal black font
            if (map.containsKey(DataSingleton.Keys.ABOUT_BODY)) {
                String body = map.get(DataSingleton.Keys.ABOUT_BODY);
                temp = new SpannableString(body + "\n\n");
                finalSpan.append(temp);
            }
        }

        textView.setText(finalSpan);
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
     * When a notification is received that data has been updated, re-initialize the text.
     * This function responds to the EventBus class.
     *
     * @param event {EVENTBUS_EVENT}
     */
    public void onEventMainThread(DataSingleton.EVENTBUS_EVENT event) {
        if (event == DataSingleton.EVENTBUS_EVENT.ABOUT) {
            initText();
        }
        if (event == DataSingleton.EVENTBUS_EVENT.CANCELLED) {
            DataSingleton.getInstance().verifyCache();
        }
    }

}
