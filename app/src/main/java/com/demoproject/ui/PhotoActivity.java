package com.demoproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.demoproject.R;
import com.demoproject.model.Photo;
import com.demoproject.ui.adapter.PhotoAdapter;
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

import butterknife.Bind;

public class PhotoActivity extends BaseActivity implements PhotoAdapter.onItemClickListener{

    private static final String TAG = "PhotoActivity";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    PhotoAdapter mAdapter;

    GraphRequest.Callback graphCallback;
    GraphResponse previousResponse;
    GraphRequest nextRequest;

    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        PreferencesUtility.setCurrentDrawerActivityId(this, R.id.nav_photos);

        mRecyclerView.setHasFixedSize(true);
        // The number of columns
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PhotoAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        makePhotoAPICall();

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
     * Initiate the API calls to retrieve user phots
     */
    private void makePhotoAPICall() {
        //setup a general callback for each graph request sent, this callback will launch the next request if exists.
        graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    progressWheel.setVisibility(View.GONE);
                    previousResponse = response;
                    JSONArray rawPhotosData = response.getJSONObject().getJSONArray("data");
                    for (int j = 0; j < rawPhotosData.length(); j++) {
                        //save whatever data you want from the result
                        JSONObject photo = rawPhotosData.getJSONObject(j);
                        String responseString = photo.toString();
                        Gson gson = new Gson();
                        Photo photoItem = gson.fromJson(responseString, Photo.class);
                        Log.d(TAG, photoItem.toString());
                        mAdapter.addData(photoItem);
                        mAdapter.notifyItemInserted(mAdapter.getItemCount()-1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Bundle param = new Bundle();
        param.putString("fields", "id,source");
        param.putInt("limit", 10);
        //send first request, the rest should be called by the callback
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me/photos",param, HttpMethod.GET, graphCallback).executeAsync();
    }

    @Override
    public void onItemClick(View view, int position) {
        Photo clickedPhoto = mAdapter.getItem(position);
        Log.d(TAG, "Clicked photo link: " + clickedPhoto.getPhotoLink());
        startFullScreenActivity(clickedPhoto.getPhotoLink());
    }

    /**
     * Start the Full screen view for the photo
     */
    private void startFullScreenActivity(String photoURL) {
        if (photoURL != null && !photoURL.isEmpty()){
            Intent intent = new Intent(this, FullScreenActivity.class);
            intent.putExtra(FullScreenActivity.KEY_PHOTO_URL, photoURL);
            startActivity(intent);
        }
    }
}
