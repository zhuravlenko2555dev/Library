package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuravlenko2555dev.library.provider.BookItem;

import java.util.ArrayList;

public class RetainedBookFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_book_fragment";

    private ArrayList<BookItem> itemList = new ArrayList<>();

    public RetainedBookFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<BookItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<BookItem> itemList) {
        this.itemList = itemList;
    }
}
