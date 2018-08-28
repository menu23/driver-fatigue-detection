package com.example.admin.rxbeacons;

/**
 * Created by Artemis on 19-May-18.
 */

public class Contact {

    private int img;
    private String name;
    private String no;

    public Contact(int i, String n, String p) {
        img = i;
        name = n;
        no = p;
    }

    public int getImage() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return no;
    }
}
