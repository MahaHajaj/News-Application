package com.example.news;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.news.googleApi.APIClient;
import com.example.news.googleApi.APIInterface;
import com.example.news.login.LoginActivity;
import com.example.news.model.newsModel;
import com.example.news.model.responseModel;
import com.example.news.widget.CollectionAppWidgetProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements newsAdapter.AdapterOnClickHandler, newsAdapter.AdapterOnItemClickHandler {

    private static final String API_KEY = "e8c37a599373412bb26fd2fd468d4f17";
    @BindView(R.id.news_list)
    RecyclerView newsList;
    ArrayList<newsModel> articleList;
    ArrayList<newsModel> bookmarkList;
    DatabaseReference myRef;
    DatabaseReference bookmarks;
    newsAdapter mainArticleAdapter;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bookmarkList = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecor = new DividerItemDecoration(newsList.getContext(), linearLayoutManager.getOrientation());

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
            myRef.child("bookmark").addValueEventListener(new ValueEventListener() {
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

            new InternetCheck().execute();

            newsList.setLayoutManager(linearLayoutManager);
            newsList.addItemDecoration(itemDecor);

        }


    public void news() {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            String orderBy = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_published_value));

            String sourceName = sharedPrefs.getString(
                    getString(R.string.settings_source_name),
                    getString(R.string.settings_source_google_news_value)
            );
            final APIInterface apiService = APIClient.getClient().create(APIInterface.class);
            Call<responseModel> call = apiService.getLatestNews(sourceName, orderBy, API_KEY);

            call.enqueue(new Callback<responseModel>() {
                @Override
                public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                    if (response.body().getStatus().equals("ok")) {
                        articleList = response.body().getArticles();
                        Log.v("in", String.valueOf(response.raw().request().url()));

                        setTo(articleList);
                    }
                }

                @Override
                public void onFailure(Call<responseModel> call, Throwable t) {
                    Log.e("out", t.toString());
                }
            });


    }

    public void setTo(final ArrayList<newsModel> articleList) {
        final ArrayList<Boolean> bookmark = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

            DatabaseReference Ref = myRef.child("bookmark");
            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookmark.clear();
                    for (int t = 0; t < articleList.size(); t++) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if ((articleList.get(t).getTitle()).equals(snapshot.child("title").getValue())) {
                                bookmark.add(t, Boolean.TRUE);
                            } else {
                                bookmark.add(t, Boolean.FALSE);
                            }
                        }
                    }

                    if (articleList.size() > 0) {
                        mainArticleAdapter =
                                new newsAdapter(articleList, getBaseContext(), MainActivity.this, MainActivity.this, bookmark);
                        newsList.setAdapter(mainArticleAdapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            menu.findItem(R.id.sign).setTitle(R.string.sign_out);
        } else {
            menu.findItem(R.id.sign).setTitle(R.string.login_or_signup);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                item.setTitle(R.string.login_or_signup);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }

            return true;
        }
        if (id == R.id.bookmark) {
            Intent intent = new Intent(this, BookmarkActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int adapterPosition) {
        newsModel newsModel = articleList.get(adapterPosition);
        String url = newsModel.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onItemClick(final int adapterPosition) {
        final newsModel newsModel = articleList.get(adapterPosition);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference Ref = myRef.child("bookmark");
        myRef.child("bookmark").child(newsModel.getTitle());
        bookmarks = myRef.child("bookmark").child(newsModel.getTitle());


        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   if (dataSnapshot.hasChild(newsModel.getTitle())) {
                                                       bookmarks.child("title").removeValue();
                                                       bookmarks.child("author").removeValue();
                                                       bookmarks.child("url").removeValue();
                                                       bookmarks.child("urlToImage").removeValue();
                                                   } else {
                                                       bookmarks.child("title").setValue(newsModel.getTitle());
                                                       bookmarks.child("author").setValue(newsModel.getAuthor());
                                                       bookmarks.child("url").setValue(newsModel.getUrl());
                                                       bookmarks.child("urlToImage").setValue(newsModel.getUrlToImage());
                                                   }
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           }

        );


        Intent intent = new Intent(this, CollectionAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(
                getApplication()).getAppWidgetIds(new ComponentName(getApplication(), CollectionAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    class InternetCheck extends AsyncTask<Void, Void, Boolean> {

        boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isNetworkAvailable(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            news();
        }
    }
}
