package com.rubabuddin.nytimessearch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rubabuddin.nytimessearch.adapters.ArticlesAdapter;
import com.rubabuddin.nytimessearch.helpers.EndlessRecyclerViewScrollListener;
import com.rubabuddin.nytimessearch.helpers.ItemClickSupport;
import com.rubabuddin.nytimessearch.models.Article;
import com.rubabuddin.nytimessearch.models.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private final String API_KEY = "1e6e43a0007345d2b3957763bb2e0c1b";
    private final String SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private final String TOP_STORIES_URL = "https://api.nytimes.com/svc/topstories/v2/home.json";

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<Article> articles = new ArrayList<Article>();
    private ArticlesAdapter adapter;
    private Query previousQuery;
    //private String userSubmittedQuery = "";
    private MenuItem filterItem;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View parentLayout = findViewById(R.id.content_search);

        if(!isOnline()){
            Snackbar.make(parentLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new ArticlesAdapter(this, articles);
        recyclerView.setAdapter(adapter);
        setupArticleClickListener();
        showTopStories();
    }

    //check network connectivity
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private void showTopStories() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        params.put("api-key", API_KEY);
        client.get(TOP_STORIES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONArray("results");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(SearchActivity.this, "Top Stories not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //load more pages
    private void searchArticle(int page){
        if(previousQuery != null) {
            Query query = new Query(previousQuery, page);
            searchArticle(query, false);
        }
    }

    private void searchArticle(String queryString){
        filterItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        filterItem.setVisible(true);

        Query query;

        if (previousQuery == null) {
            //create new search with queryString
            query = new Query(queryString);
        } else {
            //use previous query filters witn new query String
            query = new Query(previousQuery, queryString);
        }
        previousQuery = query;
        searchArticle(query, true);
    }

    private void searchArticle(Query query, boolean clearView){
        RequestParams params = query.getParams(API_KEY);
        AsyncHttpClient client = new AsyncHttpClient();

        if (clearView) { //new search view articles
            recyclerView.clearOnScrollListeners();
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadMoreDataFromApi(page);
                }
            });

            client.get(SEARCH_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJsonResults = null;
                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        articles.clear();
                        articles.addAll(Article.fromJSONArray(articleJsonResults));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else { //load more on page scroll
            client.get(SEARCH_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJsonResults = null;
                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        articles.addAll(Article.fromJSONArray(articleJsonResults));
                        int curSize = adapter.getItemCount();
                        adapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(SearchActivity.this, "Loading more articles failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    //implement endless pagination
    public void loadMoreDataFromApi(int page) {
        if (page > 99) {
            return; // limit to 100 pages
        }
        searchArticle(page);
    }

    //launch article view activity
    private void setupArticleClickListener() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //selected article
                Article article = articles.get(position);
                // pass parcelable article into intent
                i.putExtra("article", Parcels.wrap(article));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchArticle(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
