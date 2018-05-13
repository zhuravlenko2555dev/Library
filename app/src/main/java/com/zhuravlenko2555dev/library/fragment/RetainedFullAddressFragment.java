package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by zhura on 27.03.2018.
 */

public class RetainedFullAddressFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_full_address_fragment";

    private ArrayList<String> itemList = new ArrayList<>();

    public RetainedFullAddressFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
    }
}
