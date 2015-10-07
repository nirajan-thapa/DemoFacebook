package com.demoproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demoproject.R;
import com.demoproject.model.Friend;
import com.demoproject.util.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nirajan on 10/7/2015.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static final String TAG = "FriendsAdapter";

    /**
     * Interface that implements on click listener for {@link RecyclerView}
     */
    public interface onItemClickListener{
        public void onItemClick(View view, int position);
    }
    // Click listener
    onItemClickListener mItemClickListener;

    public void setOnItemClickListener(final onItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    private List<Friend> friendList;

    public FriendsAdapter() {
        friendList = new ArrayList<>();
    }

    public void addData(Friend friend) {
        friendList.add(friend);
        notifyDataSetChanged();
    }

    public void clear() {
        friendList.clear();
        notifyDataSetChanged();
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_friends_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        Friend item = friendList.get(position);
        String pictureURL = item.getPicture().getData().getUrl();
        String name = item.getName();
        Picasso.with(holder.friendPicture.getContext())
                .load(pictureURL)
                .into(holder.friendPicture);
        if (name != null && !TextUtils.isEmpty(name)) {
            holder.friendName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Friend getItem(int position) {
        return friendList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.friend_item_name)
        TextView friendName;
        @Bind(R.id.friend_item_image)
        ImageView friendPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "item on click");
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
