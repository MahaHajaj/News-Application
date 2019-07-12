package com.example.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.news.model.newsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class newsAdapter extends RecyclerView.Adapter<newsAdapter.NewsViewHolder> {

    private ArrayList<Boolean> bookmark;
    private AdapterOnClickHandler mClickHandler;
    private AdapterOnItemClickHandler onItemClickHandler;
    private ArrayList<newsModel> news;
    private Context context;

    newsAdapter(ArrayList<newsModel> news, Context context, AdapterOnClickHandler mClickHandler, AdapterOnItemClickHandler onItemClickHandler, ArrayList<Boolean> bookmark) {
        this.news = news;
        this.context = context;
        this.mClickHandler = mClickHandler;
        this.onItemClickHandler = onItemClickHandler;
        this.bookmark = bookmark;

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutId = R.layout.news_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, viewGroup, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        newsModel newModel = news.get(i);
        Picasso.with(context).load(newModel.getUrlToImage()).into(newsViewHolder.newsImage);
        newsViewHolder.title.setText(newModel.getTitle());
        newsViewHolder.auther.setText(newModel.getAuthor());
        if (bookmark.get(i)) {
            newsViewHolder.bookMark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);
        } else {
            newsViewHolder.bookMark.setBackgroundResource(R.drawable.ic_bookmark_border_black_48dp);
        }
    }

    @Override
    public int getItemCount() {
        if (null == news) return 0;
        return news.size();
    }

    public void setNewsData(ArrayList<newsModel> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    public interface AdapterOnClickHandler {
        void onClick(int adapterPosition);
    }

    public interface AdapterOnItemClickHandler {
        void onItemClick(int adapterPosition);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.new_image)
        ImageView newsImage;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.auther)
        TextView auther;
        @BindView(R.id.bookmark)
        AppCompatImageView bookMark;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            bookMark.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onItemClickHandler.onItemClick(getAdapterPosition());
                    bookMark.setBackgroundResource(R.drawable.ic_bookmark_black_48dp);

                }
            });
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}

