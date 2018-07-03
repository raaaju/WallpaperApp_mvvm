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
//        if (networkUtilities.isInternetConnectionPresent()) {
            switch (type) {
                case "latest":
                    FetchBmwTask fetchNavTask = new FetchBmwTask(context, output, index);
                    fetchNavTask.execute();
                    break;

                case "popular":
                    FetchMercedesTask fetchWallpTask = new FetchMercedesTask(context, output, index);
                    fetchWallpTask.execute();
                    break;

                case "Porsche":
                    FetchPorscheTask fetchEditTask = new FetchPorscheTask(context, output, index);
                    fetchEditTask.execute();
                    break;

                case "Audi":
                    FetchAudiTask fetchRedTask = new FetchAudiTask(context, output, index);
                    fetchRedTask.execute();
                    break;

                case "Bugatti":
                    FetchBlueTask fetchBlueTask = new FetchBlueTask(context, output, index);
                    fetchBlueTask.execute();
                    break;

                case "Ferrari":
                    FetchFerrariTask fetchBlackTask = new FetchFerrariTask(context, output, index);
                    fetchBlackTask.execute();
                    break;

                case "Lamborghini":
                    FetchLamboTask fetchYellowTask = new FetchLamboTask(context, output, index);
                    fetchYellowTask.execute();
                    break;

                default:
                    FetchCatTask fetchCatTask = new FetchCatTask(context, output, index, type);
                    fetchCatTask.execute();
                    break;
            }
        }
//    }
}
