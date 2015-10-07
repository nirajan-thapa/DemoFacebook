package com.demoproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demoproject.R;
import com.demoproject.model.UserData;
import com.demoproject.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nirajan on 10/5/2015.
 */
public class ProfileDetailAdapter extends RecyclerView.Adapter<ProfileDetailAdapter.ViewHolder> {

    private static final String TAG = "PhotoAdapter";

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

    private List<UserData> userDatas;

    public ProfileDetailAdapter() {
        userDatas = new ArrayList<>();
    }

    public void addData(UserData data) {
        userDatas.add(data);
        notifyDataSetChanged();
    }

    public void clear() {
        userDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public ProfileDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_profile_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ProfileDetailAdapter.ViewHolder holder, int position) {
        UserData item = userDatas.get(position);
        holder.itemTitle.setText(item.getTitle());
        holder.itemDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return userDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public UserData getItem(int position) {
        return userDatas.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.profile_item_title)
        TextView itemTitle;
        @Bind(R.id.profile_item_description)
        TextView itemDescription;

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
