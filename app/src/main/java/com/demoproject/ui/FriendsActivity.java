package com.demoproject.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.demoproject.R;
import com.demoproject.model.Friend;
import com.demoproject.ui.adapter.FriendsAdapter;
import com.demoproject.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

public class FriendsActivity extends BaseActivity {

    private static final String TAG = "FriendsActivity";

    GraphRequest.Callback graphCallback;
    GraphResponse previousResponse;
    GraphRequest nextRequest;

    @Bind(R.id.friends_recycler_view)
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private FriendsAdapter friendsAdapter;

    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        friendsAdapter = new FriendsAdapter();
        mRecyclerView.setAdapter(friendsAdapter);

        makeFriendsAPICall();

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
    private void makeFriendsAPICall() {
        Bundle param = new Bundle();
        param.putString("fields", "name,picture");
        param.putInt("limit", 15);

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
                        String responseString = status.toString();
                        Log.d(TAG, "Response: " + status.toString());
                        Gson gson = new Gson();
                        Friend friend = gson.fromJson(responseString, Friend.class);
                        friendsAdapter.addData(friend);
                        friendsAdapter.notifyItemInserted(friendsAdapter.getItemCount()-1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //send first request, the rest should be called by the callback
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me/friends",param, HttpMethod.GET, graphCallback).executeAsync();
    }

}
