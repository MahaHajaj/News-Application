package com.example.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.news.model.newsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkActivity extends AppCompatActivity implements newsAdapter.AdapterOnClickHandler, newsAdapter.AdapterOnItemClickHandler {

    @BindView(R.id.news_list)
    RecyclerView newsList;
    ArrayList<newsModel> bookmarkList;
    FirebaseDatabase database;
    DatabaseReference myRef;
    newsAdapter mainArticleAdapter;
    DatabaseReference bookmarks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bookmarkList = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecor = new DividerItemDecoration(newsList.getContext(), linearLayoutManager.getOrientation());

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookmarkList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    bookmarkList.add(snapshot.child("bookmark").getValue(newsModel.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getData();

        newsList.setLayoutManager(linearLayoutManager);
        newsList.addItemDecoration(itemDecor);
    }

    public void getData() {
        final ArrayList<Boolean> bookmark = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference Ref = myRef.child("bookmark");

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookmarkList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    bookmarkList.add(snapshot.getValue(newsModel.class));
                    bookmark.add(Boolean.TRUE);
                }

                mainArticleAdapter =
                        new newsAdapter(bookmarkList, getBaseContext(), BookmarkActivity.this, BookmarkActivity.this, bookmark);
                newsList.setAdapter(mainArticleAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(int adapterPosition) {
        newsModel newsModel = bookmarkList.get(adapterPosition);
        String url = newsModel.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onItemClick(int adapterPosition) {
        final newsModel newsModel = bookmarkList.get(adapterPosition);
        myRef.child("bookmark").child(newsModel.getTitle());
        bookmarks = myRef.child("bookmark").child(newsModel.getTitle());
        bookmarks.child("title").removeValue();
        bookmarks.child("author").removeValue();
        bookmarks.child("url").removeValue();
        bookmarks.child("urlToImage").removeValue();

    }
}
