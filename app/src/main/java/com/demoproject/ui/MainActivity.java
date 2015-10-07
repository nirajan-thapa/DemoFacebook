package com.demoproject.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;


import com.demoproject.R;
import com.demoproject.model.Status;
import com.demoproject.ui.adapter.StatusAdapter;
import com.demoproject.util.Log;
import com.demoproject.util.PreferencesUtility;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    GraphRequest.Callback graphCallback;
    GraphResponse previousResponse;
    GraphRequest nextRequest;

    @Bind(R.id.status_recycler_view)
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private StatusAdapter statusAdapter;

    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault());
    private Date formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        statusAdapter = new StatusAdapter();
        mRecyclerView.setAdapter(statusAdapter);

        makeStatusAPICall();

        // Scroll listener to fire next API request when user scrolls to the end of the list
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    Log.i(TAG, "list end");
                    // Call the next api request for status
                    loading = true;
                    progressWheel.setVisibility(View.VISIBLE);
                    //get next batch of results of exists
                    nextRequest = previousResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                    if (nextRequest != null) {
                        nextRequest.setCallback(graphCallback);
                        nextRequest.executeAsync();
                    } else {
                        // hide the progress bar
                        progressWheel.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * API call to retrieve the statuses; 30 limit
     */
    private void makeStatusAPICall() {
        Bundle param = new Bundle();
        param.putString("filter", "app_2915120374");
        param.putInt("limit", 30);

        //setup a general callback for each graph request sent, this callback will launch the next request if exists.
        graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    progressWheel.setVisibility(View.GONE);
                    previousResponse = response;
                    JSONArray rawStatusData = response.getJSONObject().getJSONArray("data");
                    Log.d(TAG, "LENGTH: " + String.valueOf(rawStatusData.length()));
                    for (int j = 0; j < rawStatusData.length(); j++) {
                        //save whatever data you want from the result
                        JSONObject status = rawStatusData.getJSONObject(j);
                        Gson gson = new Gson();
                        Status myStatus = gson.fromJson(status.toString(), Status.class);
                        Log.d(TAG, "Status time: " + myStatus.getDate());
                        String formatted = formatDateTime(myStatus.getDate());
                        if (formatted != null && !TextUtils.isEmpty(formatted))
                            myStatus.setDate(formatted);

                        statusAdapter.addData(myStatus);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //send first request, the rest should be called by the callback
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me/feed",param, HttpMethod.GET, graphCallback).executeAsync();
    }

    /**
     * Helper method to parse the date and time
     * Initial Format: "yyyy-MM-dd'T'hh:mm:ssZ"
     */
    private String formatDateTime(String dateTime) {
        String result = null;
        try {
            formattedDate = dateFormat.parse(dateTime);
            Log.d(TAG, "Formatted Date: " + formattedDate);
            SimpleDateFormat localDateFormat = new SimpleDateFormat("MMM d 'at' h:mm aa", Locale.getDefault());
            result = localDateFormat.format(formattedDate);
            Log.d(TAG, "Localized Date: " + result );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
