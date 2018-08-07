package com.georgcantor.wallpaperapp.ui;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Hit;
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper;
import com.georgcantor.wallpaperapp.network.NetworkUtilities;
import com.georgcantor.wallpaperapp.ui.adapter.TagAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PIC = "picture";
    public static final String ORIGIN = "caller";
    private Hit hit;
    private List<String> tags = new ArrayList<>();
    int first = 0;
    public NetworkUtilities networkUtilities;
    public RecyclerView recyclerView;
    public TagAdapter tagAdapter;
    private File file;
    private TextView tagTitle;
    private int permissionCheck1;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private DatabaseHelper db;
    public boolean isDownloaded = false;
    public boolean isCallerCollection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtilities = new NetworkUtilities(this);
        setContentView(R.layout.activity_pic_detail);
        fab = findViewById(R.id.fab_download);

        progressBar = findViewById(R.id.progressBarDetail);
        progressBar.setVisibility(View.VISIBLE);
        db = new DatabaseHelper(this);

        tagTitle = findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fileIsExist()) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermisson();
                        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    if (networkUtilities.isInternetConnectionPresent()) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(PicDetailActivity.this);
                        builder.setTitle(R.string.download);
                        builder.setIcon(R.drawable.ic_download);
                        builder.setMessage(R.string.choose_format);

                        builder.setPositiveButton("HD", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.VISIBLE);
                                if (permissionCheck1 == PackageManager.PERMISSION_GRANTED) {
                                    if (!fileIsExist()) {
                                        String uri = hit.getWebformatURL();
                                        Uri image_uri = Uri.parse(uri);
                                        downloadData(image_uri);
                                        fab.setImageDrawable(getApplicationContext().getResources()
                                                .getDrawable(R.drawable.ic_photo));
                                    } else {
                                        Toast.makeText(PicDetailActivity.this, getResources()
                                                        .getString(R.string.image_downloaded),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });

                        builder.setNeutralButton(hit.getImageWidth() + " x "
                                + hit.getImageHeight(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.VISIBLE);
                                if (permissionCheck1 == PackageManager.PERMISSION_GRANTED) {
                                    if (!fileIsExist()) {
                                        String uri = hit.getImageURL();
                                        Uri image_uri = Uri.parse(uri);
                                        downloadData(image_uri);
                                        fab.setImageDrawable(getApplicationContext().getResources()
                                                .getDrawable(R.drawable.ic_photo));
                                    } else {
                                        Toast.makeText(PicDetailActivity.this, getResources()
                                                        .getString(R.string.image_downloaded),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });

                        builder.setNegativeButton("FullHD", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.VISIBLE);
                                if (permissionCheck1 == PackageManager.PERMISSION_GRANTED) {
                                    if (!fileIsExist()) {
                                        String uri = hit.getFullHDURL();
                                        Uri image_uri = Uri.parse(uri);
                                        downloadData(image_uri);
                                        fab.setImageDrawable(getApplicationContext().getResources()
                                                .getDrawable(R.drawable.ic_photo));
                                    } else {
                                        Toast.makeText(PicDetailActivity.this, getResources()
                                                        .getString(R.string.image_downloaded),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                        builder.create().show();
                    } else {
                        Toast.makeText(PicDetailActivity.this, getResources()
                                .getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Uri sendUri2 = Uri.fromFile(file);
                    Log.d(getResources().getString(R.string.URI), sendUri2.toString());
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.setDataAndType(sendUri2, getResources().getString(R.string.image_jpg));
                    intent.putExtra(getResources().getString(R.string.mimeType),
                            getResources().getString(R.string.image_jpg));
                    startActivityForResult(Intent.createChooser(intent,
                            getResources().getString(R.string.Set_As)), 200);
                }
            }
        });
    }

    private void initView() {
        permissionCheck1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (getIntent().hasExtra(EXTRA_PIC)) {
            hit = getIntent().getParcelableExtra(EXTRA_PIC);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a Hit parcelable");
        }
        String title = hit.getTags();
        while (title.contains(",")) {
            String f = title.substring(0, title.indexOf(","));
            tags.add(f);
            first = title.indexOf(",");
            title = title.substring(++first);
        }
        tags.add(title);
        tagTitle.setText(tags.get(0));
        ImageView wallp = findViewById(R.id.wallpaper_detail);
        TextView fav = findViewById(R.id.fav);
        TextView userId = findViewById(R.id.user_name);
        ImageView userImage = findViewById(R.id.user_image);
        TextView downloads = findViewById(R.id.down);
        recyclerView = findViewById(R.id.tagsRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        tagAdapter = new TagAdapter(this);
        tagAdapter.setTagList(tags);
        recyclerView.setAdapter(tagAdapter);
        file = new File(Environment.getExternalStoragePublicDirectory("/"
                + getResources().getString(R.string.app_name)), hit.getId()
                + getResources().getString(R.string.jpg));
        if (fileIsExist()) {
            fab.setImageDrawable(getApplicationContext().getResources()
                    .getDrawable(R.drawable.ic_photo));
        }

        if (getIntent().hasExtra(ORIGIN)) {
            Picasso.with(this)
                    .load(file)
                    .placeholder(R.drawable.plh)
                    .into(wallp);
            isCallerCollection = true;
        } else {
            Picasso.with(this)
                    .load(hit.getWebformatURL())
                    .placeholder(R.drawable.plh)
                    .into(wallp, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PicDetailActivity.this,
                                    getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        userId.setText(hit.getUser());
        downloads.setText(String.valueOf(hit.getDownloads()));
        fav.setText(String.valueOf(hit.getFavorites()));
        if (!networkUtilities.isInternetConnectionPresent()) {
            Picasso.with(this)
                    .load(R.drawable.memb)
                    .transform(new CropCircleTransformation())
                    .into(userImage);
        } else {
            if (!hit.getUserImageURL().isEmpty()) {
                Picasso.with(this)
                        .load(hit.getUserImageURL())
                        .transform(new CropCircleTransformation())
                        .into(userImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.memb)
                        .transform(new CropCircleTransformation())
                        .into(userImage);
            }
        }
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fav, menu);
        if (db.containFav(String.valueOf(hit.getPreviewURL()))) {
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
                if (!db.containFav(String.valueOf(hit.getPreviewURL()))) {
                    addToFavorite(String.valueOf(hit.getPreviewURL()));
                    item.setIcon(R.drawable.ic_star_red_24dp);
                    Toast.makeText(this, R.string.add_to_fav_toast, Toast.LENGTH_SHORT).show();
                } else {
                    db.deleteFromFavorites(String.valueOf(hit.getPreviewURL()));
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
                    Toast.makeText(this, R.string.del_from_fav_toast, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String sAux = hit.getImageURL();
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

    private long downloadData(Uri uri) {
        long downloadReference;
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        String name = Environment.getExternalStorageDirectory().getAbsolutePath();
        name += "/YourDirectoryName/";

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(tags.get(0) + getResources().getString(R.string.down));
        request.setDescription(getResources().getString(R.string.down_wallpapers));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            request.setDestinationInExternalPublicDir("/"
                    + getResources().getString(R.string.app_name), hit.getId()
                    + getResources().getString(R.string.jpg));
        }
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast toast = Toast.makeText(context, tags.get(0) + getResources()
                    .getString(R.string.down_complete), Toast.LENGTH_SHORT);
            toast.show();
            isDownloaded = true;
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onDestroy() {
        try {
            if (downloadReceiver != null)
                unregisterReceiver(downloadReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public boolean fileIsExist() {
        return file.exists();
    }

    public void checkPermisson() {
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 102;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            restartActivity();
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            finish();
            Toast.makeText(this, R.string.you_need_perm_toast, Toast.LENGTH_LONG).show();
        }
    }

    public void restartActivity() {
        hit = getIntent().getParcelableExtra(EXTRA_PIC);
        Intent intent = new Intent(PicDetailActivity.this,
                PicDetailActivity.class);
        intent.putExtra(EXTRA_PIC, hit);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void addToFavorite(String imageUrl) {
        db.insertToFavorites(imageUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
