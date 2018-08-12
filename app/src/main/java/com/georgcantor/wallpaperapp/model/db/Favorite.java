package com.georgcantor.wallpaperapp.model.db;

public class Favorite {

    public static final String TABLE_NAME = "favorite";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "imageUrl";
    public static final String COLUMN_HD_URL = "hdUrl";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String imageUrl;
    private String hdUrl;
    private String timestamp;

    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_URL + " TEXT,"
            + COLUMN_HD_URL + " TEXT,"
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public Favorite() {
    }

    public Favorite(int id, String imageUrl, String hdUrl, String timestamp) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.hdUrl = hdUrl;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
