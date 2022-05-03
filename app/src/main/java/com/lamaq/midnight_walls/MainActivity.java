package com.lamaq.midnight_walls;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.lamaq.midnight_walls.adapters.SuggestedAdapter;
import com.lamaq.midnight_walls.adapters.WallpaperAdapter;
import com.lamaq.midnight_walls.interfaces.RecyclerViewClickListner;
import com.lamaq.midnight_walls.models.SuggestedModel;
import com.lamaq.midnight_walls.models.WallpaperModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListner {

    static final float END_SCALE = (float) 0.7;
    ImageView menuIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RecyclerView recyclerView, topMostRecyclerView;
    RecyclerView.Adapter adapter;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;

    ArrayList<SuggestedModel> suggestedModels = new ArrayList<>();

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    ProgressBar progressBar;

    TextView replaceTitle;

    EditText searchEt;
    ImageView searchIv;
    String searchWall;

    int pageNumber = 1;
    String url = "https://api.pexels.com/v1/curated?page=" + pageNumber + "&per_page=80"; //we need multiple pages when scrolled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationDrawer();

        //navigation drawer profile
        View headerView = navigationView.getHeaderView(0);
        ImageView appLogo = headerView.findViewById(R.id.app_image);

        recyclerView = findViewById(R.id.recyclerView);
        topMostRecyclerView = findViewById(R.id.suggestedRecyclerView);

        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList);

        recyclerView.setAdapter(wallpaperAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //scrolling behaviour
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)){
                    isScrolling = false;
//                    fetchWallpaper();
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        replaceTitle = findViewById(R.id.topMostTitle);

        //search editText and imageView
        searchEt = findViewById(R.id.searchEv);
        searchIv = findViewById(R.id.search_image);

        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                searchWall = searchEt.getText().toString();
                replaceTitle.setText(searchWall);
                url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=" + searchWall;
                wallpaperModelList.clear();
                fetchWallpaper();
                progressBar.setVisibility(View.GONE);
            }
        });

        fetchWallpaper();
        suggestedItems();

    }

    private void navigationDrawer() {
        //navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //animation in the drawer
        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //scale the view based on the current slide effect
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //translate the view accounting of the scaled width
                final float xOffset = drawerLayout.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "logged out", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void suggestedItems() {
        topMostRecyclerView.setHasFixedSize(true);
        topMostRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //suggestedModels.add(new SuggestedModel(R.drawable.all_back, "Trending"));
        //suggestedModels.add(new SuggestedModel(R.drawable.landscape_back, "Landscape"));
        //suggestedModels.add(new SuggestedModel(R.drawable.car_back, "Car"));
        suggestedModels.add(new SuggestedModel(R.drawable.cityscape_back, "Cityscape"));
        suggestedModels.add(new SuggestedModel(R.drawable.aesthetic_back, "Aesthetic"));
        suggestedModels.add(new SuggestedModel(R.drawable.wildlife_back, "Wildlife"));
        suggestedModels.add(new SuggestedModel(R.drawable.black_back, "Dark"));
        suggestedModels.add(new SuggestedModel(R.drawable.abstract_back, "Abstract"));
        suggestedModels.add(new SuggestedModel(R.drawable.cloud_back, "Cloud"));
        suggestedModels.add(new SuggestedModel(R.drawable.flower_back, "Flower"));
        suggestedModels.add(new SuggestedModel(R.drawable.quotes_back, "Quotes"));

        adapter = new SuggestedAdapter(suggestedModels, MainActivity.this);
        topMostRecyclerView.setAdapter(adapter);

    }

    private void fetchWallpaper(){
        //fetch img url and name from pexels api
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for (int i=0; i < length; i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");
                                String photographerName = object.getString("photographer");

                                JSONObject objectImage = object.getJSONObject("src");
                                String originalUrl = objectImage.getString("portrait");
                                String mediumUrl = objectImage.getString("medium");

                                WallpaperModel wallpaperModel = new WallpaperModel(id, originalUrl, mediumUrl, photographerName);
                                wallpaperModelList.add(wallpaperModel);
                            }
                            pageNumber += 1;
                            wallpaperAdapter.notifyDataSetChanged();
//                            url = "https://api.pexels.com/v1/curated?page=" + pageNumber + "&per_page=80";

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "563492ad6f9170000100000144dcbee8cf26471983b04c011eed09f2");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        progressBar.setVisibility(View.VISIBLE);
        if (position == 0){
            replaceTitle.setText("Trending");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=trending";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }else if (position == 1){
            replaceTitle.setText("Landscape");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=landscape";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }else if (position == 2){
            replaceTitle.setText("Car");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=car";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 3){
            replaceTitle.setText("Cityscape");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=cityscape";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }else if (position == 4){
            replaceTitle.setText("Aesthetic");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=aesthetic";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 5){
            replaceTitle.setText("Wildlife");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=wildlife";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 6){
            replaceTitle.setText("Dark");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=dark";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 7){
            replaceTitle.setText("Abstract");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=abstract";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 8){
            replaceTitle.setText("Cloud");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=cloud";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 9){
            replaceTitle.setText("Flower");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=flower";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
        else if (position == 10){
            replaceTitle.setText("Quotes");
            url="https://api.pexels.com/v1/search?page=" + pageNumber + "&per_page=80&query=quotes";
            wallpaperModelList.clear();
            fetchWallpaper();
            searchEt.setText("");
            progressBar.setVisibility(View.GONE);
        }
    }
}