package com.tc.tech.cru.techcrunch.techcru;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @noinspection deprecation
 */
class DataFetch extends AppCompatActivity {


    ParseAdapter adapter;
    final ArrayList<ParseItem> parseItems = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmer;
    String urls;
    int currentPage = 1; // Variable to keep track of current page
    TextView tenext, teprev;
    SearchView searchView;
    Activity activity;
    int[] colorBackgrounds;


    void urlsWeb(String urls) {
        this.urls = urls;
    }

    void dataFet() {
        new Content().execute(urls);
    }

    void recy(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    void shim(ShimmerFrameLayout shimmer) {
        this.shimmer = shimmer;
    }

    void textPre(TextView teprev) {
        this.teprev = teprev;
    }

    void textnext(TextView tenext) {
        this.tenext = tenext;
    }

    void acti(Activity activity) {
        this.activity = activity;
    }

    void sear(SearchView searchView) {
        this.searchView = searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We handle filtering in real-time as the user types
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void loadContent() {
        shimmer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tenext.setVisibility(View.GONE);
        teprev.setVisibility(View.GONE);
        int drawableBackgroundResId2 = colorBackgrounds[1 % colorBackgrounds.length];
        Drawable drawable2 = ContextCompat.getDrawable(activity, drawableBackgroundResId2);
        tenext.setBackground(drawable2);
        teprev.setBackground(drawable2);
        shimmer.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
        String url = currentPage > 1 ? urls + "/page/" + currentPage + "/" : urls;
        new Content().execute(url);
    }


    void layoutSetRecyc() {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new ParseAdapter(parseItems, activity, activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
    }

    void swipe() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadContent();
            shuffleTextBoxColor();
            int drawableBackgroundResId1 = colorBackgrounds[1 % colorBackgrounds.length];
            Drawable drawable1 = ContextCompat.getDrawable(activity, drawableBackgroundResId1);
            tenext.setBackground(drawable1);
            teprev.setBackground(drawable1);
        });
    }

    void goneText() {
        tenext.setVisibility(View.GONE);
        teprev.setVisibility(View.GONE);
    }


    public void nextCard(MaterialCardView next) {
        next.setOnClickListener(v -> {
            currentPage++; // Increment page number
            loadContent();
        });
    }

    public void peviousCard(MaterialCardView previous) {
        previous.setOnClickListener(v -> {
            if (currentPage > 1) { // Ensure currentPage doesn't go below 1
                currentPage--; // Decrement page number
                loadContent();
            }
        });
    }


    void callBG() {
        int drawableBackgroundResId = colorBackgrounds[1 % colorBackgrounds.length];
        Drawable drawable = ContextCompat.getDrawable(activity, drawableBackgroundResId);
        tenext.setBackground(drawable);
        teprev.setBackground(drawable);
    }


    void shuffleTextBoxColor() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.drawable.cardview_10);
        colorList.add(R.drawable.cardview_3);
        colorList.add(R.drawable.cardview_9);
        colorList.add(R.drawable.cardview_5);
        colorList.add(R.drawable.cardview_7);
        colorList.add(R.drawable.cardview_6);
        colorList.add(R.drawable.cardview_13);

        // Shuffle the list
        Collections.shuffle(colorList);

        // Convert the list back to an array
        colorBackgrounds = new int[colorList.size()];
        for (int i = 0; i < colorList.size(); i++) {
            colorBackgrounds[i] = colorList.get(i);
        }
    }


    /**
     * @noinspection CallToPrintStackTrace
     */
    @SuppressLint("StaticFieldLeak")
    class Content extends AsyncTask<String, Void, ArrayList<ParseItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            shimmer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tenext.setVisibility(View.GONE);
            teprev.setVisibility(View.GONE);
            shimmer.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(ArrayList<ParseItem> result) {
            super.onPostExecute(result);
            shimmer.setVisibility(View.GONE);
            shimmer.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
            recyclerView.setVisibility(View.VISIBLE);
            tenext.setVisibility(View.VISIBLE);
            teprev.setVisibility(View.VISIBLE);

            // Clear the existing data and add the new items
            parseItems.clear();
            if (result != null) {
                parseItems.addAll(result);
            }

            adapter.notifyDataSetChanged();

            // Hide the refresh icon
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected ArrayList<ParseItem> doInBackground(String... urls) {
            ArrayList<ParseItem> newItems = new ArrayList<>();

            // Check if urls array is not empty
            if (urls != null && urls.length > 0) {
                try {
                    Document doc = Jsoup.connect(urls[0]).get();
                    Elements data = doc.select("div.wp-site-blocks");

                    for (int i = 0; i < data.size() + 29; i++) {
                        String imgUrl = data.select("figure.wp-block-post-featured-image")
                                .select("img")
                                .eq(i)
                                .attr("src");

                        String title = data.select("h2.wp-block-post-title a").eq(i).text();

                        String desc = data.select("p.wp-block-post-excerpt__excerpt").eq(i).text();
                        String postLink = data.select("h2.wp-block-post-title a").eq(i).attr("href");

                        String cate = data.select("a.is-taxonomy-category").eq(i).text();
                        String date = data.select(".wp-block-tc23-post-time-ago").eq(i).text();
                        String author = data.select(".wp-block-tc23-author-card-name a").eq(i).text();

                        newItems.add(new ParseItem(imgUrl, title, desc, date, cate, postLink, author));
                        Log.d("items", "img: " + imgUrl + " , pl: " + postLink);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Show error message if there's an IOException
                    showErrorToast();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    // Show error message if there's an IndexOutOfBoundsException
                    showErrorToast();
                }
            }

            return newItems;
        }

        // Helper method to show error toast
        private void showErrorToast() {
            if (activity != null) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Failed to load content. Please try again later.", Toast.LENGTH_SHORT).show());
            }
        }

    }


    public void filter(String text) {
        ArrayList<ParseItem> filteredList = new ArrayList<>();

        for (ParseItem item : parseItems) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDate().toLowerCase().contains(text.toLowerCase()) ||
                    item.getAuthor().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDesc().toLowerCase().contains(text.toLowerCase()) ||
                    item.getCategories().toLowerCase().contains(text.toLowerCase()) ||
                    item.getTitle().toUpperCase().contains(text.toUpperCase()) ||
                    item.getDate().toUpperCase().contains(text.toUpperCase()) ||
                    item.getAuthor().toUpperCase().contains(text.toUpperCase()) ||
                    item.getDesc().toUpperCase().contains(text.toUpperCase()) ||
                    item.getCategories().toUpperCase().contains(text.toUpperCase())

            ) {
                filteredList.add(item);
            }
        }

        adapter.updateList(filteredList);
    }


}
