package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuravlenko2555dev.library.provider.BookEditionItem;

import java.util.ArrayList;

public class RetainedSelectedBookFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_selected_book_fragment";

    private ArrayList<BookEditionItem> itemList = new ArrayList<>();

    public RetainedSelectedBookFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<BookEditionItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<BookEditionItem> itemList) {
        this.itemList = itemList;
    }
}
