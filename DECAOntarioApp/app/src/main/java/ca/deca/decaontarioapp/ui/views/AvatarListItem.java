package ca.deca.decaontarioapp.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.utils.VisualUtils;

/**
 * A reusable Layout that contains:
 *      an avatar view {a colored circle with either a short text string or an image within}
 *      A main TextView of bold text
 *      A sub TextView of smaller, lighter text
 *
 * This layout can be customized with values for each component before being inflated.
 * This layout can also be used directly in an xml layout.
 */
public class AvatarListItem extends RelativeLayout {

    @InjectView(R.id.listitem_circle_view) View mAvatarView;
    @InjectView(R.id.iv_listitem_circle) ImageView mAvatarImageView;
    @InjectView(R.id.listitem_avatar_textview) TextView mAvatarTextView;
    @InjectView(R.id.listitem_main_textview) TextView mMainTextView;
    @InjectView(R.id.listitem_sub_textview) TextView mSubTextView;

    @InjectView(R.id.listitem_divider) View mDivider;

    public AvatarListItem(Context context) {
        this(context, null);
    }

    public AvatarListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Get the variables from the xml layout
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.AvatarListItem, 0, 0);

        // Initialize the content holding variables for each View
        String mTextMain = "", mTextSub = "", mAvatarText = "";
        int mAvatarColor = VisualUtils.getColor(context, R.color.material_blue_grey_500);
        Drawable mAvatar = null;

        try {
            // If passed in attribute values from the xml file, use them as default values.
            mAvatar = a.getDrawable(R.styleable.AvatarListItem_avatarSrc);
            mAvatarColor = a.getColor(R.styleable.AvatarListItem_avatarColor,
                    VisualUtils.getColor(context, R.color.material_blue_grey_500));
            mAvatarText = a.getString(R.styleable.AvatarListItem_avatarText);
            mTextMain = a.getString(R.styleable.AvatarListItem_textMain);
            mTextSub = a.getString(R.styleable.AvatarListItem_textSub);
        } finally {
            a.recycle();
        }

        // Make sure our layout is inflated and added to self as a View.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.listitem_avatar, null, false);
        addView(mView);

        ButterKnife.inject(this, mView);

        // Set the Views with the default values if they exist
        setAvatar(mAvatar);
        setAvatarColor(mAvatarColor);
        setAvatarText(mAvatarText);
        setSubText(mTextSub);
        setText(mTextMain);
    }

    /**
     * Set the Avatar image of the AvatarView.
     * @param avatar {Drawable} : desired image
     */
    public void setAvatar(Drawable avatar) {
        if (avatar != null)
            mAvatarImageView.setImageDrawable(avatar);
    }

    /**
     * Set the background color of the AvatarView.
     * @param color {int} : the color as an integer
     */
    public void setAvatarColor(int color) {
        // Create a circular background with the color of choice
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]
                        {color, color});
        gradient.setShape(GradientDrawable.OVAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mAvatarView.setBackground(gradient);
        } else {
            mAvatarView.setBackgroundDrawable(gradient);
        }
    }

    /**
     * Set the text content of the main TextView.
     * @param text {String} : content
     */
    public void setText(String text) {
        if (text != null)
            mMainTextView.setText(text);
    }

    /**
     * Set the text content of the sub TextView.
     * @param text {String} : content
     */
    public void setSubText(String text) {
        if (text == null || text.isEmpty()) {
            mSubTextView.setVisibility(GONE);
        } else {
            mSubTextView.setVisibility(VISIBLE);
            mSubTextView.setText(text);
        }
    }

    /**
     * Set the text content of the AvatarView. Text of 3-4 chars in length works best.
     * @param text {String} : content
     */
    public void setAvatarText(String text) {
        if (text != null) {
            // Adjust the text size based on the length of the text
            // 3 letters or less are 13pt font, 4 letters is 11pt font, all greater are 9pt font.
            int length = text.length();
            mAvatarTextView.setTextSize(length <= 3 ? 13 : length == 4 ? 11 : 9);
            mAvatarTextView.setText(text);
        }
    }

    /**
     * Adjust whether the bottom divider is visible or not
     * @param visible {boolean} : visibility of the divider
     */
    public void setDividerVisibility(boolean visible) {
        if (visible)
            mDivider.setVisibility(VISIBLE);
        else
            mDivider.setVisibility(GONE);
    }

}
