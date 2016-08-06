package ca.deca.decaontarioapp.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by Reno on 14-08-31.
 */
public class VisualUtils {

    /**
     * Starts an animation of the view expanding downwards into visibility.
     *
     * @source http://stackoverflow.com/a/13381228/3399351
     * @param v {View} : the source view
     */
    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Animate at the speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics()
                .density));
        v.startAnimation(a);
        v.refreshDrawableState();
    }

    /**
     * Starts an animation of the view collapsing upwards to the point of invisibility
     *
     * @source http://stackoverflow.com/a/13381228/3399351
     * @param v {View} : the source view
     */
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight *
                            interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Animate at the speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics()
                .density));
        v.startAnimation(a);
    }

    /**
     * Takes an ImageView and alternately rotates it clockwise and counterclockwise
     * Based on the previous rotation. This change is applied directly to the ImageView
     * This animation lasts for half a second
     *
     * @param v {ImageView} : the source view
     */
    public static void rotateImage(final ImageView v) {
        // Initialize the tag on the imageview
        // Signals if we rotate clockwise or counterclockwise
        if (v.getTag() == null)
            v.setTag(false);
        final boolean tag = (boolean) v.getTag();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Matrix matrix = new Matrix();
                v.setScaleType(ImageView.ScaleType.MATRIX);
                // Rotates counterclockwise if the tag is true
                float rotation = interpolatedTime * 180f;
                if (tag)
                    rotation = 180f - rotation;
                matrix.postRotate(rotation, v.getDrawable().getBounds().width() / 2,
                        v.getDrawable().getBounds().height() / 2);
                v.setImageMatrix(matrix);
                // Inverse the tag, so next run it will rotate the inverse direction
                v.setTag(!tag);
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };
        a.setDuration(500);
        v.startAnimation(a);
    }

    /**
     * Converts a measurement form dp to pixels based on the screen density
     *
     * @param c  {Context}
     * @param dp {int} : the measurement in density pixels (dp)
     * @return {int} : the measurement in pixels
     */
    public static int dpToPx(Context c, int dp) {
        DisplayMetrics displayMetrics = c.getResources()
                .getDisplayMetrics();
        return Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Code from http://stackoverflow.com/a/10471479/3399351
     * <p>
     * Cycles through an array of images in one ImageView, sequentially
     * fading them out and fading the new photo in.
     *
     * @param imageView  ImageView : The View which displays the images
     * @param images     int[] : Holds R references to the images to display
     * @param imageIndex int : index of the first image to show in images[]
     * @param forever    boolean : If equals true than it loops back to the first image
     */
    public static void animate(final ImageView imageView, final int images[], final int imageIndex,
                               final boolean forever) {
        //TODO: Smooth fade between images instead of fade through white
        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 5000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.VISIBLE);
        //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        // Initialize the fade in animation
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInDuration);

        // Initialize the fade out animation
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        // Apply the animation set
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        // Overriding this function causes the animations to loop
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever);
                    // Calls itself until it gets to the end of the array
                    if (forever) {
                        animate(imageView, images, 0, forever);
                        // Calls itself to start the animation
                        // all over again in a loop, if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    /**
     * Paints the background of a view as a circle with a solid colour fill
     *
     * @param v     {View} : the source view
     * @param color {int} : the
     * @return {View} : the view with added background
     */
    public static View viewWithCircularBackground(View v, int color) {
        // Create a circular background with the color of choice
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]
                        {color, color});
        gradient.setShape(GradientDrawable.OVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(gradient);
        } else {
            v.setBackgroundDrawable(gradient);
        }
        return v;
    }

    /**
     * Gets a color from resources based on the resource id.
     *
     * @param context {Context}
     * @param id      {int} : the color resource id
     * @return {int} : the color as an integer
     */
    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

}
