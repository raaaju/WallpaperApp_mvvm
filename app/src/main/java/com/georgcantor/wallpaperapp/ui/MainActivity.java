package com.georgcantor.wallpaperapp.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.ui.adapter.PagerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView myTitle = (TextView) toolbar.getChildAt(0);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                getResources().getString(R.string.font_name));
        myTitle.setTypeface(tf, Typeface.BOLD);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_latest) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.Extra_id, getResources().getString(R.string.latest_small));
            this.startActivity(intent);

        } else if (id == R.id.nav_editor) {
            Intent intent = new Intent(this, NavActivity.class);
            intent.putExtra(NavActivity.Extra_id, getResources().getString(R.string.editor_small));
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
}
