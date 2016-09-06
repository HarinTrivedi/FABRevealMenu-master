package com.hlab.fabrevealmenu.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class FABMenuItem {
    private int id;
    private String title;
    private Drawable iconDrawable;
    private Bitmap iconBitmap;

    public FABMenuItem(String title, Drawable iconDrawable) {
        this.title = title;
        this.iconDrawable = iconDrawable;
    }

    public FABMenuItem(int id, String title, Drawable iconDrawable) {
        this.id = id;
        this.title = title;
        this.iconDrawable = iconDrawable;
    }

    public FABMenuItem(String title, Bitmap iconBitmap) {
        this.title = title;
        this.iconBitmap = iconBitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

}
