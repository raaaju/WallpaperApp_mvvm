package com.georgcantor.wallpaperapp.network;

import android.content.Context;

public class WallpService {

    private final NetworkUtilities networkUtilities;
    private Context context;
    private AsyncResponse output;
    private int index;
    private String type;

    public WallpService(NetworkUtilities networkUtilities, Context context,
                        AsyncResponse output, int index, String type) {
        this.networkUtilities = networkUtilities;
        this.context = context;
        this.output = output;
        this.index = index;
        this.type = type;
    }

    public void loadWallp() {
        if (networkUtilities.isInternetConnectionPresent()) {
            switch (type) {
                case "latest":
                    FetchNavTask fetchNavTask = new FetchNavTask(context, output, index);
                    fetchNavTask.execute();
                    break;

                case "popular":
                    FetchWallpTask fetchWallpTask = new FetchWallpTask(context, output, index);
                    fetchWallpTask.execute();
                    break;

                case "editors_choice":
                    FetchEditTask fetchEditTask = new FetchEditTask(context, output, index);
                    fetchEditTask.execute();
                    break;

                default:
                    FetchCatTask fetchCatTask = new FetchCatTask(context, output, index, type);
                    fetchCatTask.execute();
                    break;
            }
        }
    }
}
