package com.example.newsappdemo.adapters;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsappdemo.R;
import com.example.newsappdemo.entity.NewsFeed;
import com.example.newsappdemo.handlers.ImageDownloaderTask;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private NewsFeed[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView)view.findViewById(R.id.title);
            mImageView = (ImageView) view.findViewById(R.id.image_title);
        }
    }

    public NewsAdapter(NewsFeed[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position].getTitle());
        new ImageDownloaderTask(holder.mImageView).execute(mDataset[position].getTitleImage());
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}