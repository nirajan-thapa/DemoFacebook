package com.demoproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demoproject.R;
import com.demoproject.model.Status;
import com.demoproject.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nirajan on 10/6/2015.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
    private static final String TAG = "StatusAdapter";

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

    private List<Status> statusList;

    public StatusAdapter() {
        statusList = new ArrayList<>();
    }

    public void addData(Status status) {
        statusList.add(status);
        notifyDataSetChanged();
    }

    public void clear() {
        statusList.clear();
        notifyDataSetChanged();
    }

    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main_status, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(StatusAdapter.ViewHolder holder, int position) {
        Status item = statusList.get(position);
        String story = item.getStory();
        String message = item.getMessage();
        String date = item.getDate();
        holder.statusStory.setText(story);
        holder.statusMessage.setText(message);
        holder.statusDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Status getItem(int position) {
        return statusList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.status_item_story)
        TextView statusStory;
        @Bind(R.id.status_item_message)
        TextView statusMessage;
        @Bind(R.id.status_item_date)
        TextView statusDate;

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
