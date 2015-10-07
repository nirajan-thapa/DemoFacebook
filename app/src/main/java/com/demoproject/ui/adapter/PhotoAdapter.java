package com.demoproject.ui.adapter;

import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.demoproject.R;
import com.demoproject.model.Photo;
import com.demoproject.util.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nirajan on 10/5/2015.
 */
public class PhotoAdapter  extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{

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

    private List<Photo> mItems;

    public PhotoAdapter() {
        mItems = new ArrayList<>();
    }

    public void addData(Photo photo) {
        mItems.add(photo);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_photo_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        Photo item = mItems.get(position);
        Log.d(TAG, "onBindView " + item.getPhotoLink());
        Picasso.with(holder.itemImage.getContext())
                .load(item.getPhotoLink())
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Photo getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.card_item_image)
        ImageView itemImage;


        @VisibleForTesting
        public ImageView getItemImage() {
            return itemImage;
        }


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
