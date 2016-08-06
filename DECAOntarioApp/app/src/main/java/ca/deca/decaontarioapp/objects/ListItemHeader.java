package ca.deca.decaontarioapp.objects;

/**
 * Created by Reno on 15-08-05.
 * Extends the BaseListItem class, allowing the item to be used alongside Event items in a list view.
 * This header is very simple and just contains a line of text.
 */
public class ListItemHeader implements BaseListItem {

    private String text;

    /**
     * This is the header for lists
     * @param text {String}: The text of the header
     */
    public ListItemHeader(String text) {
        this.text = text;
    }

    /**
     * Yes this class is a header
     * @return {boolean} : true
     */
    @Override
    public boolean isHeader() {
        return true;
    }

    /**
     * The saved text of the header
     * @return {String}
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of the header
     * @param text {String}
     */
    public void setText(String text) {
        this.text = text;
    }
}
