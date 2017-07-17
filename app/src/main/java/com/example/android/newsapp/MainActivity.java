package com.example.android.newsapp;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {


    private static final int BOOK_LOADER_ID = 1;
    private static LoaderManager loaderManager;
    private static final String API_INITIAL_QUERY = "http://content.guardianapis.com/search?";
    private static NewsAdapter newsAdapter;

    TextView messageTextView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    NetworkInfo networkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_screen_title));

        messageTextView = (TextView) findViewById(R.id.message_txt);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeRefreshLayout.setColorSchemeColors(getColor(R.color.colorAccent));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {

            messageTextView.setText(getString(R.string.message_fetching));


            initializeLoaderAndAdapter();

        } else {

            progressBar.setVisibility(View.GONE);


            messageTextView.setText(getString(R.string.message_no_internet));
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        String searchQuery = sharedPreferences.getString(getString(R.string
                .settings_search_query_key), getString(R.string.settings_search_query_default));


        String orderBy = sharedPreferences.getString(getString(R.string
                .settings_order_by_list_key), getString(R.string.settings_order_by_list_default));


        Uri baseIri = Uri.parse(API_INITIAL_QUERY);
        Uri.Builder uriBuilder = baseIri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        Log.v("MainActivity", "Uri: " + uriBuilder);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        // If there is a valid list of {@link BookItem}s, then add them to the adapter's
        if (newsItems != null && !newsItems.isEmpty()) {
            newsAdapter.addAll(newsItems);
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Hide message text
            messageTextView.setText("");

        } else {
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Set message text to display "No articles found!"
            messageTextView.setText(getString(R.string.message_no_articles));
        }
        Log.v("MainActivity", "Loader completed operation!");
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        newsAdapter.clearAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshRecyclerView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeLoaderAndAdapter() {

        loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        newsAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        recyclerView.setAdapter(newsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void refreshRecyclerView() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connMgr.getActiveNetworkInfo();
        Log.v("MainActivity", "networkInfo: " + networkInfo);

        if (networkInfo != null && networkInfo.isConnected()) {

            messageTextView.setText(getString(R.string.message_refreshing));

            progressBar.setVisibility(View.VISIBLE);

            if (newsAdapter != null) {

                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                initializeLoaderAndAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {

            progressBar.setVisibility(View.GONE);

            if (newsAdapter != null) {
                // Clear the adapter
                newsAdapter.clearAll();
            }

            messageTextView.setText(getString(R.string.message_no_internet));
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}