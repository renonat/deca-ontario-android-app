package ca.deca.decaontarioapp.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;
import ca.deca.decaontarioapp.ui.fragments.FragmentAboutDECA;
import ca.deca.decaontarioapp.ui.fragments.FragmentConnect;
import ca.deca.decaontarioapp.ui.fragments.FragmentMeetTheTeam;

public class ActivityAbout extends BaseActivity {

    /**
     * The number of pages (wizard steps) to show.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Create the toolbar which displays the name of the app at the top
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("About");
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.about_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });

        // Create the tablayout with our tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.about_tablayout);

        tabLayout.addTab(tabLayout.newTab().setText("DECA Ontario"));
        tabLayout.addTab(tabLayout.newTab().setText("Connect"));
        tabLayout.addTab(tabLayout.newTab().setText("Meet the Officers"));

        // Update tablayout when we swipe the viewpager
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Update viewpager when we tap the tablayout
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));

        // Set the background color of the tablayout to our primary color
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int colorPrimary = typedValue.data;
        tabLayout.setBackgroundColor(colorPrimary);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // A fix as the activity is already running when we send the intent from ExecsActivity
        // Scroll the tabs to the passed in tab
        int GO_TO_FRAGMENT = intent.getIntExtra("GO_TO_FRAGMENT", -1);

        //TODO: THIS NEVER GETS CALLED THE EXTRAS DON'T GET PASSED
        if (GO_TO_FRAGMENT > -1)
            mPager.setCurrentItem(GO_TO_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityAbout.this, ActivityMainScreen.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
    }

    /**
     * A simple pager adapter that represents 2 Fragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new FragmentConnect();
                case 2:
                    return new FragmentMeetTheTeam();
                default:
                    return new FragmentAboutDECA();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
