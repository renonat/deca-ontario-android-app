package ca.deca.decaontarioapp.objects;

/**
 * Created by Reno on 15-08-05.
 * A base interface for the TrainingAdaptor.
 * Allows both Events and ListItemHeaders to be displayed, which both implement this interface.
 */
public interface BaseListItem {

    /**
     * Is the item in question a header object?
     * true : is a header object
     * false : is a event object
     * @return {boolean}
     */
    boolean isHeader();

}
