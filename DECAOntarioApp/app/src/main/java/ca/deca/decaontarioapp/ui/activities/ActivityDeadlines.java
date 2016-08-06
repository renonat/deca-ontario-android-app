package ca.deca.decaontarioapp.ui.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.deca.decaontarioapp.R;
import ca.deca.decaontarioapp.ui.BaseActivity;

/**
 * Created by Reno on 15-07-30.
 */
public class ActivityDeadlines extends BaseActivity {

    @InjectView(R.id.calendar_webview) WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deadline);
        ButterKnife.inject(this);

        initToolbar();

        // Load the WebView with the calendar for DECA Ontario
        // Javascript must be enabled for the interactive google calendar
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        // This html file is simple generated code to load a google calendar
        webView.loadUrl("file:///android_asset/calendar.html");

    }

    private void initToolbar() {
        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_deadlines));
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(4);
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityDeadlines.this, ActivityMainScreen.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // The menu only consists of "Open in browser"
        inflater.inflate(R.menu.activity_deadlines, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_browser) {
            // On action open the current webView Url in the real browser
            Uri uri = Uri.parse(webView.getUrl());
            Intent link = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(link);
            } catch (ActivityNotFoundException e) {
                // We cannot open the link we got, so open the default link instead
                uri = Uri.parse("https://www.google.com/calendar/embed?title=DECA%20Ontario&amp;" +
                        "showPrint=0&amp;showCalendars=0&amp;showTz=0&amp;height=600&amp;" +
                        "wkst=1&amp;bgcolor=%23FFFFFF&amp;src=kbhoi9psftfga84194ljn0hu1g%40group" +
                        ".calendar.google.com&amp;color=%235229A3&amp;ctz=America%2FToronto");
                link = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(link);
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
