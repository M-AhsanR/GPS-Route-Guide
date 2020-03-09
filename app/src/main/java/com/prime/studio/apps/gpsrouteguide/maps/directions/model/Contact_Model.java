package com.prime.studio.apps.gpsrouteguide.maps.directions.model;

/**
 * Created by Asad on 5/25/2016.
 */
public class Contact_Model implements Comparable<Contact_Model> {
    String name;
    String phone_number;
    private boolean checked;


    public Contact_Model() {
    }

    public Contact_Model(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int compareTo(Contact_Model another) {
        Long date1 = Long.valueOf(this.name.substring(1, 15));
        Long date2 = Long.valueOf(another.name.substring(1, 15));
        return (date2>date1 ? -1 : (date2==date1 ? 0 : 1));
    }
}
