package ca.deca.decaontarioapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.objects.Tweet;

/**
 * Created by Reno on 15-07-27.
 * An adaptor that provides views for each tweet.
 * These views are used in a list of tweets.
 * Each view includes a content and date TextView.
 */
public class SocialAdapter extends ArrayAdapter<Tweet> {

    private final Activity context;
    private final List<Tweet> tweets;

    /**
     * Creates the social adaptor.
     * @param context {Context} : The context of the calling activity
     * @param tweets {List<Tweets>} : The list of Tweets to be displayed
     */
    public SocialAdapter(Activity context, List<Tweet> tweets) {
        super(context, R.layout.listitem_tweet, tweets);
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * Returns the tweet view that will be displayed in the list at a specific position.
     * @param position {int} : the index of the desired tweet
     * @param convertView {View} : A previously used view that is to be updated and reused
     * @param parent {ViewGroup} : Not used in this implementation
     * @return {View} : The final view filled with profile information
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tweetView = convertView;
        // If our tweetView has already been set up, use it
        if (tweetView == null) {
            // Else initialize inflate the content of this tweetView
            LayoutInflater inflater = context.getLayoutInflater();
            tweetView = inflater.inflate(R.layout.listitem_tweet, null);
            // Configure view holder, and save to tweetView tag
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.content = (TextView) tweetView.findViewById(R.id.social_tweet_content);
            viewHolder.date = (TextView) tweetView.findViewById(R.id.social_tweet_date);
            tweetView.setTag(viewHolder);
        }

        //  Fill the ViewHolder within the tag of tweetView
        ViewHolder holder = (ViewHolder) tweetView.getTag();
        Tweet p = tweets.get(position);
        holder.content.setText(p.getContent());
        holder.date.setText(p.getTime());

        return tweetView;
    }

    static class ViewHolder {
        public TextView content;
        public TextView date;
    }
} 
