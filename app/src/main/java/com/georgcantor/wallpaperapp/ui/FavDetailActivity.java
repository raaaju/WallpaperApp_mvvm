package com.georgcantor.wallpaperapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FavDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String previewURL;
    private String hdUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_detail);

        db = new DatabaseHelper(this);

        previewURL = getIntent().getStringExtra("preview");
        hdUrl = getIntent().getStringExtra("hd");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView wallp = findViewById(R.id.wallpaper_detail_fav);

        Picasso.with(this)
                .load(hdUrl)
                .placeholder(R.drawable.plh)
                .into(wallp, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
//                        Toast.makeText(FavDetailActivity.this,
//                                getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext())
                                .load(previewURL)
                                .placeholder(R.drawable.plh)
                                .into(wallp);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fav, menu);
        if (db.containFav(previewURL)) {
            menu.findItem(R.id.action_add_to_fav).setIcon(R.drawable.ic_star_red_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add_to_fav:
                if (!db.containFav(previewURL)) {
                    addToFavorite(previewURL, hdUrl);
                    item.setIcon(R.drawable.ic_star_red_24dp);
                    Toast.makeText(this, R.string.add_to_fav_toast, Toast.LENGTH_SHORT).show();
                } else {
                    db.deleteFromFavorites(previewURL);
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
                    Toast.makeText(this, R.string.del_from_fav_toast, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String sAux = hdUrl;
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, getString(R.string.choose_share)));
                } catch (AndroidRuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Can not share image", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToFavorite(String imageUrl, String hdUrl) {
        db.insertToFavorites(imageUrl, hdUrl);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavDetailActivity.this, FavoriteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
