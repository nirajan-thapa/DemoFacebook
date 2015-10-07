package com.demoproject.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.demoproject.R;
import com.demoproject.util.Log;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

public class FullScreenActivity extends BaseActivity {

    private static final String TAG = "FullScreenActivity";

    public static final String KEY_PHOTO_URL = "key_photo_url";

    private String photoURL;

    @Bind(R.id.full_screen)
    ImageView fullScreen;

    private boolean toHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                photoURL = extras.getString(KEY_PHOTO_URL);
            }
        } else {
            photoURL = savedInstanceState.getString(KEY_PHOTO_URL);
        }
        Picasso.with(this)
                .load(photoURL)
                .into(fullScreen);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PHOTO_URL, photoURL);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoURL = savedInstanceState.getString(KEY_PHOTO_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
