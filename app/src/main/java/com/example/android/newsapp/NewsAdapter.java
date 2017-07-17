package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsItem> mNews;
    private Context mContext;
    private String mURL;

    public static final String LOG_TAG = NewsAdapter.class.getSimpleName();


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView newsTitle;
        public TextView newsSection;
        public ImageView coverImageView;
        private Context context;


        public ViewHolder(Context context, View itemView) {

            super(itemView);


            this.context = context;


            itemView.setOnClickListener(this);

            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsSection = (TextView) itemView.findViewById(R.id.details);
            coverImageView = (ImageView) itemView.findViewById(R.id.thumbnail);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            NewsItem newsItem = mNews.get(position);

            mURL = newsItem.getWebURL();

            Uri newsURI = Uri.parse(mURL);

            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsURI);

            context.startActivity(websiteIntent);
        }
    }

    public NewsAdapter(Context context, List<NewsItem> newsItems) {
        mContext = context;
        mNews = newsItems;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View listView = inflater.inflate(R.layout.list_item, parent, false);


        ViewHolder viewHolder = new ViewHolder(mContext, listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder viewHolder, int position) {

        NewsItem newsItem = mNews.get(position);
        String imgUrl = mNews.get(position).getCoverImagePath();

        TextView newsTitleTextView = viewHolder.newsTitle;
        TextView newsSectionTextView = viewHolder.newsSection;

        newsTitleTextView.setText(newsItem.getTitle());
        newsSectionTextView.setText(newsItem.getSectionName());
        //Render image using Picasso library
        Picasso.with(mContext)
                .load(imgUrl)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.coverImageView);

    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public void addAll(List<NewsItem> newsItemList) {
        mNews.clear();
        mNews.addAll(newsItemList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mNews.clear();
        notifyDataSetChanged();
    }
}