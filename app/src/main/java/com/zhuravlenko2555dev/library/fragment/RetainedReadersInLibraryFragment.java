package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuravlenko2555dev.library.provider.ReadersInLibraryRow;

import java.util.ArrayList;

public class RetainedReadersInLibraryFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_readers_in_library_fragment";

    private ArrayList<ReadersInLibraryRow> rowList = new ArrayList<>();

    public RetainedReadersInLibraryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<ReadersInLibraryRow> getRowList() {
        return rowList;
    }

    public void setRowList(ArrayList<ReadersInLibraryRow> rowList) {
        this.rowList = rowList;
    }
}
