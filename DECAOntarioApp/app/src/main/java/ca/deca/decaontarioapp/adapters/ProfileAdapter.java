package ca.deca.decaontarioapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.utils.singletons.DataSingleton;

/**
 * Created by Reno on 15-07-27.
 * An adaptor that provides views for each executive profile.
 * These views are used in a list of executives.
 * Each view includes a profile picture, and main/sub text boxes.
 */
public class ProfileAdapter extends ArrayAdapter<HashMap<String, String>> {

    private final Activity context;
    private final List<HashMap<String, String>> profiles;

    /**
     * Creates the profile adaptor.
     * @param context {Context} : the context of the calling activity
     * @param profiles {List<HashMap<String, String>>} : A list of profiles to be displayed,
     *                 with each profile being a HashMap of information. These profiles must
     *                 respond to the following: Keys.EXECS_*
     */
    public ProfileAdapter(Activity context, List<HashMap<String, String>> profiles) {
        super(context, R.layout.listitem_profile, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    /**
     * Returns the profile view that will be displayed in the list at a specific position.
     * @param position {int} : the index of the desired profile
     * @param convertView {View} : A previously used view that is to be updated and reused
     * @param parent {ViewGroup} : Not used in this implementation
     * @return {View} : The final view filled with profile information
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View profileView = convertView;
        // If our profileView has already been set up, use it
        if (profileView == null) {
            // Else initialize inflate the content of this profileView
            LayoutInflater inflater = context.getLayoutInflater();
            profileView = inflater.inflate(R.layout.listitem_profile, null);
            // Configure view holder, and save to profileView tag
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.main = (TextView) profileView.findViewById(R.id.profile_listitem_main);
            viewHolder.sub = (TextView) profileView.findViewById(R.id.profile_listitem_sub);
            viewHolder.avatar = (ImageView) profileView.findViewById(R.id.profile_listitem_avatar);
            profileView.setTag(viewHolder);
        }

        //  Fill the ViewHolder within the tag of profileView
        ViewHolder holder = (ViewHolder) profileView.getTag();
        HashMap<String, String> p = profiles.get(position);
        holder.main.setText(p.get(DataSingleton.Keys.EXECS_NAME));
        holder.sub.setText(p.get(DataSingleton.Keys.EXECS_POSITION));

        // The profile image is separately retrieved using the profile information
        holder.avatar.setImageBitmap(DataSingleton.getInstance().getExecProfileImage(p));

        return profileView;
    }

    static class ViewHolder {
        public TextView main;
        public TextView sub;
        public ImageView avatar;
    }
}
