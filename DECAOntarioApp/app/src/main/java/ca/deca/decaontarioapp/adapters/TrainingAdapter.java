package ca.deca.decaontarioapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.objects.BaseListItem;
import ca.deca.decaontarioapp.objects.Event;
import ca.deca.decaontarioapp.objects.ListItemHeader;
import ca.deca.decaontarioapp.utils.VisualUtils;

/**
 * Created by Reno on 15-08-01.
 * An adaptor that provides views for each competitive event.
 * These views are used in a list of all events.
 * Views can either be header items or they can be events.
 * Views can be sorted using three different methods, and are displayed accordingly.
 */
public class TrainingAdapter extends ArrayAdapter<BaseListItem> {

    /**
     * ID Tags for the two types of views.
     */
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private final Activity context;

    /**
     * This list can contain either {Event}s or {ListItemHeader}s.
     */
    private final List<BaseListItem> items = new ArrayList<>();

    /**
     * The mode can be:
     * ALPHABETICAL, with no headers and events sorted by name.
     * CLUSTER, with headers and events sorted by event cluster.
     * FORMAT, with headers and events sorted by event format.
     */
    private final Event.SORT_MODE mode;
    private LayoutInflater mInflater;

    /**
     * Creates the social adaptor. Also creates the list of items and headers based
     * on the given SORT_MODE.
     * @param context {Context} : The context of the calling activity
     * @param eventObjects {List<Events>} : The list of all competitive Events
     * @param mode {Event.SORT_MODE} : One of three sorting modes
     */
    public TrainingAdapter(Activity context, List<Event> eventObjects, Event.SORT_MODE mode) {
        super(context, R.layout.listitem_avatar);
        this.context = context;
        this.mode = mode;
        this.mInflater = LayoutInflater.from(context);

        // Alphabetical mode has no headers, so create headers only for the other modes
        if (mode != Event.SORT_MODE.ALPHABETICAL) {
            List<String> headers = new ArrayList<>();
            // Go through every item and find it's header string based on SORT_MODE
            for (int i = 0; i < eventObjects.size(); i++) {
                // Find the headerString based on SORT_MODE
                // CLUSTER gets the title fo the cluster, and FORMAT gets the event format
                String headerString = null;
                if (mode == Event.SORT_MODE.CLUSTER) {
                    headerString = eventObjects.get(i).getClusterString();
                } else if (mode == Event.SORT_MODE.FORMAT) {
                    headerString = eventObjects.get(i).getFormat();
                }
                // If not already in the array, add a header object to the items array
                // We only want each header to appear once in the headers array
                if (headerString != null && !headers.contains(headerString)) {
                    headers.add(headerString);
                    items.add(new ListItemHeader(headerString));
                }
                // And finally add the original event to the items array
                items.add(eventObjects.get(i));
            }
        } else {
            // There are no headers, so simply add every event to the items array
            for (Event e : eventObjects)
                items.add(e);
        }

    }

    /**
     * Gets the number of different view types based on SORT_MODE.
     * ALPHABETICAL mode has no headers so one view type.
     * FORMAT and CLUSTER have headers so two view types.
     * @return {int} : view type count
     */
    @Override
    public int getViewTypeCount() {
        if (mode == Event.SORT_MODE.ALPHABETICAL)
            return 1;
        else
            return 2;
    }

    /**
     * Gets the number of items to display. This is always the size of items because the array
     * includes both event and header objects.
     * @return {int} : view count
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Gets the specific item (whether header or not) based on the position
     * @param position {int} : index
     * @return {BaseListItem} : the desired item
     */
    @Override
    public BaseListItem getItem(int position) {
        return items.get(position);
    }

    /**
     * Gets the type of the item at a given position.
     * This type can be either TYPE_ITEM or TYPE_HEADER.
     * If the position does not exist, -1 is returned.
     * @param position {int} : index
     * @return {int} : the type of {BaseListItem}
     */
    @Override
    public int getItemViewType(int position) {
        if (items != null && items.size() > position)
            return items.get(position).isHeader() ? TYPE_HEADER : TYPE_ITEM;
        else
            return -1;
    }

    /**
     * Returns the event/header view that will be displayed in the list at a specific position.
     * @param position {int} : the index of the desired item
     * @param convertView {View} : A previously used view that is to be updated and reused
     * @param parent {ViewGroup} : Not used in this implementation
     * @return {View} : The final view filled with event/header information
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View eventView = convertView;
        ViewHolder holder;
        // Get the type of the item
        int rowType = getItemViewType(position);

        // If our eventView has already been set up, use it
        if (eventView == null) {
            holder = new ViewHolder();
            // Inflate a different layout depending on the type of item
            // Configure view holder, and save to eventView tag
            if (rowType == TYPE_ITEM) {
                eventView = mInflater.inflate(R.layout.listitem_avatar, null);
                holder.mainTextView = (TextView)
                        eventView.findViewById(R.id.listitem_main_textview);
                holder.subTextView = (TextView)
                        eventView.findViewById(R.id.listitem_sub_textview);
                holder.avatarTextView = (TextView)
                        eventView.findViewById(R.id.listitem_avatar_textview);
                holder.avatarView = eventView.findViewById(R.id.listitem_circle_view);
            } else if (rowType == TYPE_HEADER) {
                eventView = mInflater.inflate(R.layout.listitem_header, null);
                holder.headerTextView = (TextView)
                        eventView.findViewById(R.id.listitem_header_textview);
            }
            eventView.setTag(holder);
        } else {
            // Recycle the views from the old convertView
            holder = (ViewHolder) eventView.getTag();
        }

        // Enter the data depending on the item type
        if (rowType == TYPE_ITEM) {
            Event mItem = (Event) getItem(position);
            holder.avatarTextView.setText(mItem.getCode());
            // Adjust the text size based on the length of the code
            // This text size must be dynamically assigned for it to fit in the circle view
            int length = mItem.getCode().length();
            holder.avatarTextView.setTextSize(length == 3 ? 13 : length == 4 ? 11 : 9);
            int color = VisualUtils.getColor(context, mItem.getClusterColor());
            holder.avatarView = VisualUtils.viewWithCircularBackground(holder.avatarView, color);
            holder.mainTextView.setText(mItem.getName());
            holder.subTextView.setText(mItem.getFormat());
        } else if (rowType == TYPE_HEADER) {
            ListItemHeader mItem = (ListItemHeader) getItem(position);
            holder.headerTextView.setText(mItem.getText());
        }
        return eventView;
    }

    /**
     * Returns the Event associated with a specific position, else null if not an Event
     * @param position {int} : index
     * @return {Event} : The associated {Event} or null
     */
    public Event getEvent(int position) {
        int rowType = getItemViewType(position);
        if (rowType == TYPE_ITEM) {
            return (Event) getItem(position);
        } else
            return null;
    }

    static class ViewHolder {
        public TextView headerTextView;
        public TextView mainTextView;
        public TextView subTextView;
        public TextView avatarTextView;
        public View avatarView;
    }
}
