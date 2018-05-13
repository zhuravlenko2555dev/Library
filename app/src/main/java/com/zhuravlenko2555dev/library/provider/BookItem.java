package com.zhuravlenko2555dev.library.provider;

/**
 * Created by zhura on 31.03.2018.
 */

public class BookItem {
    private String key, name, ipv4, imageSmall;

    public BookItem(String key, String name, String ipv4, String imageSmall) {
        this.key = key;
        this.name = name;
        this.ipv4 = ipv4;
        this.imageSmall = imageSmall;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }
}
