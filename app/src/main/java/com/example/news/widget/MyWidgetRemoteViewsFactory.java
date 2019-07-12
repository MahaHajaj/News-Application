package com.example.news.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.news.R;
import com.example.news.model.newsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<newsModel> articleList = new ArrayList<>();
    private ArrayList<String> articleTitle = new ArrayList<>();
    private Context mContext;

    MyWidgetRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference Ref = myRef.child("bookmark");

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articleList.clear();
                articleTitle.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    articleList.add(snapshot.getValue(newsModel.class));
                }
                for (newsModel s : articleList) {
                    articleTitle.add(s.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDataSetChanged() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference Ref = myRef.child("bookmark");

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articleList.clear();
                articleTitle.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    articleList.add(snapshot.getValue(newsModel.class));
                }
                for (newsModel s : articleList) {
                    articleTitle.add(s.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (articleTitle == null) return 0;
        return articleTitle.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.collection_widget_list_item);
        System.out.println(articleTitle);
        views.setTextViewText(R.id.widgetItemTaskNameLabel, articleTitle.get(i));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
