package ca.deca.decaontarioapp.objects;

import org.apache.commons.lang3.StringEscapeUtils;

import ca.deca.decaontarioapp.utils.SocialUtils;

/**
 * An object which contains the necessary elements of a tweet
 */
public class Tweet {

    private String content;
    private String time;
    private String link;

    /**
     * Blank constructor
     */
    public Tweet() {}

    /**
     * Get the content of the post
     *
     * @return {String} : content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content of the post
     *
     * @param content {String}
     */
    public void setContent(String content) {
        this.content = StringEscapeUtils.unescapeHtml4(content);
    }

    /**
     * Get the offset from the time of the tweet
     *
     * @return {String} : Time of the post
     */
    public String getTime() {
        return SocialUtils.getOffsetFromRSS(time);
    }

    /**
     * Set the time of the post in RSS format
     *
     * @param time {String} : Time of the post
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Get the url to the specific tweet
     * @return {String} : url
     */
    public String getLink() {return link;}

    /**
     * Generates and sets the link to the tweet based on the username and tweet id.
     * @param screenname {String} : twitter handle of the tweeter
     * @param id {String} : the ID of the tweet
     */
    public void setLink(String screenname, String id) {
        this.link = "https://twitter.com/" + screenname + "/status/" + id;
    }

}
