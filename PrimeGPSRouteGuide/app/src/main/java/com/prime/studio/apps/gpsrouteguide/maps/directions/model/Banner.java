package com.prime.studio.apps.gpsrouteguide.maps.directions.model;

/**
 * Created by Asad on 9/20/2016.
 */
public class Banner {
    int app_banner;
    String app_path;

    public Banner(int app_banner, String app_path) {
        this.app_banner = app_banner;
        this.app_path = app_path;
    }

    public int getApp_banner() {
        return app_banner;
    }

    public void setApp_banner(int app_banner) {
        this.app_banner = app_banner;
    }

    public String getApp_path() {
        return app_path;
    }

    public void setApp_path(String app_path) {
        this.app_path = app_path;
    }
}
