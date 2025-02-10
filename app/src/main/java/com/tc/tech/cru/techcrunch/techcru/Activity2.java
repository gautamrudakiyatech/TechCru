package com.tc.tech.cru.techcrunch.techcru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @noinspection deprecation
 */
public class Activity2 extends AppCompatActivity {

    String fulldata, tag, image, titl, te2, te3, te4, imagecredit;
    WebView webView;
    String e, a, f;
    ProgressDialog progressDialog;
    ImageView imageView;
    TextView title, text2, text3, text;

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        // Set custom uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0); // This ensures white text on the status bar
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading data..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        Intent getdata = getIntent();
        a = getdata.getStringExtra("title");
        e = getdata.getStringExtra("postLink");
        f = getdata.getStringExtra("image");

        imageView = findViewById(R.id.imageView);
        text = findViewById(R.id.text);

        new Content().execute();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeActionContentDescription("Back");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            webView.setWebChromeClient(new WebChromeClient());

            // Check for images in the content and remove them
            fulldata = removeImagesFromHtml(fulldata);

            // Apply CSS styling and construct HTML content
            String htmlContent = "<html><head><style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; padding: 12px; background-color: #000000; color: #333; }" +
                    ".content { margin-bottom: 25px; background-color: #000; border-radius: 14px; padding: 20px; box-shadow: 2px 4px 8px rgba(0,0,0,0.1); }" +
                    ".entry-title { font-size: 32px; margin-bottom: 20px; color: #007bff; }" +
                    ".entry-meta { font-style: italic; margin-bottom: 10px; color: #666; }" +
                    ".entry-meta span { margin-right: 10px; }" +
                    ".entry-content { margin-top: 20px; color: #fff; font-size: 18px; }" +
                    ".meta-tags { margin-top: 20px; color: #fff;}" +
                    ".meta-tags span { display: inline-block; background-color: #007bff; color: #fff; padding: 5px 10px; border-radius: 10px; margin-right: 10px; margin-left: 10px; margin-bottom: 10px; }" +
                    "a { color: #ffffff; }" + // Added CSS rule for links
                    "</style></head><body>" +
                    "<div class=\"content\">" +
                    "<h6 class=\"entry-meta\">" + imagecredit + "</h6>" +
                    "<h1 class=\"entry-title\">" + titl + "</h1>" +
                    "<div class=\"entry-meta\">" +
                    "<span class=\"meta-date\">" + te2 + "</span>" +
                    "<span class=\"meta-categories\">" + te3 + " / " + te4 + "</span>" +
                    "</div>" +
                    "<div class=\"entry-content\">" + fulldata + "</div>" +
                    "<div class=\"meta-tags\">" + formatTags(tag) + "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
            webView.requestFocus();

            Picasso.get().load(f).into(imageView);

            title = findViewById(R.id.title);
            title.setText(a != null ? a : "----");

            text2 = findViewById(R.id.text2);
            text2.setText(te2 != null ? te2 : "----");
            text2.setSelected(true);

            text3 = findViewById(R.id.text3);
            text3.setText(te3 != null ? te3 : "----");
            text3.setSelected(true);

            text.setText(te3);

            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Initialize tag to an empty string
                tag = "";

                Document doc = Jsoup.connect(e).get();

                // Remove unwanted elements
                doc.select("div.related-posts").remove();

                Elements data = doc.select("div.wp-site-blocks");

                int i = 0;
                fulldata = data.select("div.wp-block-post-content").eq(i).html();

                Elements divs = doc.select("div.wp-block-post-terms").eq(i);
                StringBuilder tags = new StringBuilder();

                for (Element div : divs) {
                    // Select all anchor tags inside the div
                    Elements links = div.select("a[href]");
                    for (Element link : links) {
                        // Remove the href attribute
                        link.removeAttr("href");
                        tags.append("#").append(link).append(" ").append(" ");
                    }
                }

                // Assign the formatted tags to the tag variable
                tag = tags.toString().trim();

                Element figcaption = doc.select("figcaption.wp-block-post-featured-image__caption").first();

                if (figcaption != null) {
                    // Extract the strong element (Image Credits)
                    Element strongElement = figcaption.select("strong").first();
                    String imageCreditsLabel = strongElement != null ? strongElement.text() : "";

                    // Extract the text following the strong element
                    String imageCreditsText = figcaption.ownText().trim();

                    imagecredit = imageCreditsLabel + " " + imageCreditsText;
                }

                titl = data.select("h1.wp-block-post-title").eq(i).text();

                te3 = data.select("a.is-taxonomy-category").eq(i).text();
                te2 = data.select(".wp-block-post-date").eq(i).text();
                te4 = data.select(".wp-block-tc23-author-card-name").eq(i).text();

                image = data.select("figure.wp-block-post-featured-image")
                        .select("img")
                        .eq(i)
                        .attr("src");

                Log.d("items", "data: " + e);
            } catch (IOException e) {
                Log.e("ContentAsyncTask", "Error fetching data", e);
                progressDialog.dismiss();
                return null;
            }
            return null;
        }

        // Method to remove images from HTML content
        private String removeImagesFromHtml(String html) {
            Document doc = Jsoup.parse(html);
            // Remove all image tags
            doc.select("img").remove();
            doc.select("div.idblog-social-share").remove();
            // Return the modified HTML
            return doc.html();
        }

        // Method to format tags
        private String formatTags(String tags) {
            if (tags == null || tags.isEmpty()) {
                return ""; // Return an empty string if tags are null or empty
            }

            // Split tags by comma and format each tag
            String[] tagArray = tags.split(",");
            StringBuilder formattedTags = new StringBuilder();
            for (String tag : tagArray) {
                formattedTags.append("<span>").append(tag.trim()).append("</span>");
            }
            return formattedTags.toString();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), Activity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_1, R.anim.slide_in_left_1);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Activity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_1, R.anim.slide_in_left_1);
        finish();
    }

}
