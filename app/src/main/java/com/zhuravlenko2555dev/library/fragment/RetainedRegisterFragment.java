package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuravlenko2555dev.library.provider.GenderItem;

import java.util.ArrayList;

public class RetainedRegisterFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_register_fragment";

    ArrayList<GenderItem> genderItemList = new ArrayList<>();

    public RetainedRegisterFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<GenderItem> getGenderItemList() {
        return genderItemList;
    }

    public void setGenderItemList(ArrayList<GenderItem> genderItemList) {
        this.genderItemList = genderItemList;
    }
}
