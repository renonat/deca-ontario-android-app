package ca.deca.decaontarioapp.objects;

import java.io.Serializable;

import ca.deca.decaontarioapp.R;

/**
 * Created by Reno on 15-08-01.
 * The Event class stores information about each DECA event. It can be used in the TrainingAdapter
 * so the data can be used for display in a listview alongside headers. Thsis class is also
 * Serializable so that it can be cached.
 */
public class Event implements BaseListItem, Serializable {

    private String name = "", code = "", format = "", members = "", guidelines = "",
            presentation = "", preptime = "", written = "", sample = "", rubric = "",
            description = "", extras = "";

    private String examName = "", examLink = "", examPIs = "";

    private CLUSTER cluster;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * The format value is used for sorting Events. This is the order that each Format should
     * appear in when displayed to the user while using SORT_MODE.FORMAT.
     * @return {int} : id value for the format
     */
    public int getFormatValue() {
        if ("Principles of Business Administration Events".equals(format))
            return 1;
        if ("Individual Series Events".equals(format))
            return 2;
        if ("Personal Financial Literacy".equals(format))
            return 3;
        if ("Team Decision Making Events".equals(format))
            return 4;
        if ("Business Operations Research Event".equals(format))
            return 5;
        if ("Chapter Team Events".equals(format))
            return 6;
        if ("Business Management and Entrepreneurship Events".equals(format))
            return 7;
        if ("Marketing Representative Events".equals(format))
            return 8;
        if ("Professional Selling Events".equals(format))
            return 9;
        if ("Online Events".equals(format))
            return 10;
        return 11;
    }

    public CLUSTER getCluster() {
        return cluster;
    }

    public void setCluster(CLUSTER cluster) {
        this.cluster = cluster;
    }

    /**
     * Clusters can be set using the short ID letter used in the JSON files.
     * @param cluster {String} : ID letter
     */
    public void setCluster(String cluster) {
        if ("F".equals(cluster))
            this.cluster = CLUSTER.FINANCE;
        else if ("M".equals(cluster))
            this.cluster = CLUSTER.MARKETING;
        else if ("B".equals(cluster))
            this.cluster = CLUSTER.BUSINESS_ADMINISTRATION;
        else if ("H".equals(cluster))
            this.cluster = CLUSTER.HOSPITALITY_TOURISM;
        else if ("E".equals(cluster))
            this.cluster = CLUSTER.ENTREPRENEURSHIP;
        else
            this.cluster = CLUSTER.MULTI;
    }

    /**
     * A string representing the cluster of the event, suitable for display.
     * @return {String} : cluster name
     */
    public String getClusterString() {
        switch (cluster) {
            case MARKETING:
                return "Marketing";
            case FINANCE:
                return "Finance";
            case BUSINESS_ADMINISTRATION:
                return "Business Management and Administration";
            case HOSPITALITY_TOURISM:
                return "Hospitality and Tourism";
            case ENTREPRENEURSHIP:
                return "Entrepreneurship";
            default:
                return "Multiple Clusters";
        }
    }

    /**
     * Gets the resource id for the specific color associated with the event's cluster
     * @return {int} : a color resource id
     */
    public int getClusterColor() {
        switch (cluster) {
            case MARKETING:
                return R.color.material_red_500;
            case FINANCE:
                return R.color.material_green_500;
            case BUSINESS_ADMINISTRATION:
                return R.color.material_amber_500;
            case HOSPITALITY_TOURISM:
                return R.color.material_indigo_500;
            case ENTREPRENEURSHIP:
                return R.color.material_grey_500;
            default:
                return R.color.material_purple_500;
        }
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPreptime() {
        return preptime;
    }

    public void setPreptime(String preptime) {
        this.preptime = preptime;
    }

    public String getWritten() {
        return written;
    }

    public void setWritten(String written) {
        this.written = written;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getRubric() {
        return rubric;
    }

    public void setRubric(String rubric) {
        this.rubric = rubric;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamLink() {
        return examLink;
    }

    public void setExamLink(String examLink) {
        this.examLink = examLink;
    }

    public String getExamPIs() {
        return examPIs;
    }

    public void setExamPIs(String examPIs) {
        this.examPIs = examPIs;
    }

    /**
     * This is not a header object, so returns false.
     * @return {boolean} : false
     */
    @Override
    public boolean isHeader() {
        return false;
    }

    /**
     * The cluster of the event (the business topic for the event).
     * Multi is used for the virtual business challenges to indicate that there are many
     * different clusters to choose from.
     */
    public enum CLUSTER {
        MARKETING, FINANCE, BUSINESS_ADMINISTRATION, HOSPITALITY_TOURISM, ENTREPRENEURSHIP, MULTI
    }

    /**
     * The sorting mode for the training activity.
     */
    public enum SORT_MODE {
        ALPHABETICAL, FORMAT, CLUSTER
    }

}
