package com.tc.tech.cru.techcrunch.techcru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/** @noinspection deprecation*/
public class Activity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager viewPager;
    TabLayout tab;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    NavigationView navigationView;
    ImageView navigation_icon,newsforge_icon;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_1);

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0); // This ensures white text on the status bar
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading..");
        progressDialog.setCancelable(false);

        navigation_icon = findViewById(R.id.navigation_icon);
        newsforge_icon = findViewById(R.id.newsforge_icon);

        newsforge_icon.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Activity3.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_1, R.anim.slide_in_left_1);
        });

        viewPager = findViewById(R.id.viewPager);

        tab = findViewById(R.id.tab);

        viewPager.setAdapter(new ViewPagerTabLayoutAdapter(getSupportFragmentManager()));
        tab.setupWithViewPager(viewPager);

        toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Show status bar
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide status bar
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);

        navigationView = findViewById(R.id.navi);

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationDrawer();


//        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);

        /**  This below uncomment only 3 line**/

//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();

//        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);


        View headerView = navigationView.getHeaderView(0);

        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        TextView versionTextView = headerView.findViewById(R.id.version);
        versionTextView.setText("v" + versionName);


        RelativeLayout settings = headerView.findViewById(R.id.lay);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Setting_Community_Legal.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        });

        RelativeLayout rate = headerView.findViewById(R.id.lay1);
        rate.setOnClickListener(v -> {
            String appPackage = getPackageName();
            Uri uri = Uri.parse("market://details?id=" + appPackage);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Play Store app is not installed, open the website instead
                Uri playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
                startActivity(websiteIntent);
            }
        });


        RelativeLayout share = headerView.findViewById(R.id.lay2);
        share.setOnClickListener(v -> {
            String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String sharedContent = "Check out this amazing TechCru app! " +
                    "Discover a world of knowledge and stay updated with the latest news and information. " +
                    "Download now and start your learning journey!\n\n" +
                    "Download Link: " + appUrl +
                    "\n\n" +
                    "By TechCrunch via TechCru";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharedContent);
            startActivity(Intent.createChooser(shareIntent, "Share the App"));

        });

        RelativeLayout disclaimer = headerView.findViewById(R.id.lay3);
        disclaimer.setOnClickListener(v ->

        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setIcon(R.drawable.imagenotavailable).setTitle("Disclaimer").setMessage("The content provided in this TechCru application is for educational purposes only." + " It is intended to provide general information and guidance on every concepts and lessons. " + "The information presented may not always be complete, accurate, or up to date. " + "Check the legal Policy for more information.").setPositiveButton(" ", (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setText("OK");
                positiveButton.setTextColor(getResources().getColor(R.color.button1));
            });
            dialog.show();
        });

    }

    private void navigationDrawer() {
        try {
            navigationView.bringToFront();
            navigationView.setNavigationItemSelectedListener(this);
            navigation_icon.setOnClickListener(view -> {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private class FetchDataTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> recentPosts = new ArrayList<>();

            try {
                Document document = Jsoup.connect(params[0]).get();

                // Select the container with class "ct-widget widget_recent_entries"
                Element recentEntriesContainer = document.selectFirst(".ct-widget.widget_recent_entries");

                // Check if the container exists
                if (recentEntriesContainer != null) {
                    // Select all list items inside the container
                    Elements recentEntries = recentEntriesContainer.select("li");

                    // Loop through each list item and extract title and URL
                    for (Element entry : recentEntries) {
                        Element link = entry.selectFirst("a");
                        if (link != null) {
                            String title = link.text();
                            String url = link.attr("href");
                            recentPosts.add(title + " - " + url);
                            Log.e("recent", "Error fetching data: " + title);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("FetchDataTask", "Error fetching data: " + e.getMessage());
            }

            return recentPosts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            // Handle the fetched data (result) here
            for (String post : result) {
                Log.d("Recent Post", post);

                // Assuming the format of each post is "Title - URL"
                String[] parts = post.split(" - ");
                if (parts.length == 2) {
                    String title = parts[0];
                    String url = parts[1];
                    // Show the data in AlertDialog
                    showDataInAlertDialog(title, url);
                }
            }
        }

    }

    private void showDataInAlertDialog(String title, String content) {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Recent Posts")
                .setMessage(title);
        builder.setPositiveButton("Next", (dialog, which) -> {
            // Handle the OK button click if needed
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel button click if needed
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), Activity1.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                finish();
            }
        }).setNeutralButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Open" button click if needed
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), Activity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("postLink", content);
                intent.putExtra("title", title);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * @noinspection deprecation
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

}