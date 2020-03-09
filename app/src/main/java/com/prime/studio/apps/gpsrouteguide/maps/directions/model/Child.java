package com.prime.studio.apps.gpsrouteguide.maps.directions.model;

/**
 * Created by Asad on 9/9/2016.
 */
public class Child {
    String childname;
    int childimg;

    public Child(int childimg, String childname) {
        this.childimg = childimg;
        this.childname = childname;
    }

    public String getChildname() {
        return childname;
    }

    public void setChildname(String childname) {
        this.childname = childname;
    }

    public int getChildimg() {
        return childimg;
    }

    public void setChildimg(int childimg) {
        this.childimg = childimg;
    }
}
