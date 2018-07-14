package com.georgcantor.wallpaperapp.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.ui.adapter.PagerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private boolean doubleTap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView myTitle = (TextView) toolbar.getChildAt(0);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources()
                .getString(R.string.font_name));
        if (Build.VERSION.SDK_INT >= 16) {
            myTitle.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
        } else {
            myTitle.setTypeface(typeface, Typeface.BOLD);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearchActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSearchActivity() {
        startActivity(new Intent(this, SearchActivity.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_editor) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.EXTRA_ID, getResources().getString(R.string.porsche));
            this.startActivity(intent);

        } else if (id == R.id.nav_red_color) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.EXTRA_ID, getResources().getString(R.string.audi));
            this.startActivity(intent);

        } else if (id == R.id.nav_blue_color) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.EXTRA_ID, getResources().getString(R.string.bugatti));
            this.startActivity(intent);

        } else if (id == R.id.nav_black_color) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.EXTRA_ID, getResources().getString(R.string.ferrari));
            this.startActivity(intent);

        } else if (id == R.id.nav_yellow_color) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.EXTRA_ID, getResources().getString(R.string.lamborghini));
            this.startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_license) {
            Intent intent = new Intent(this, LicenseActivity.class);
            this.startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleTap) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, this.getResources().getString(R.string.press_back),
                    Toast.LENGTH_SHORT).show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 2000);
        }
    }
}
