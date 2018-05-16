package com.georgcantor.wallpaperapp.network;

import com.georgcantor.wallpaperapp.model.Pic;

public interface AsyncResponse {

    void processFinish(Pic output);
}
