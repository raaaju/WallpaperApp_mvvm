package com.georgcantor.wallpaperapp;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

@SimpleSQLConfig(name = "WallpaperProvider", authority = "com.georgcantor.wallpaperapp",
        database = "wallDownload.db", version = 11)
public class PicProviderConfig implements ProviderConfig {

    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
