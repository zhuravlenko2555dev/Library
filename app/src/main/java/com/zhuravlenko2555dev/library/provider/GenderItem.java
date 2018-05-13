package com.zhuravlenko2555dev.library.provider;

/**
 * Created by zhura on 20.03.2018.
 */

public class GenderItem {
    private String name;
    private int icon;

    public GenderItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
