package com.zhuravlenko2555dev.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuravlenko2555dev.library.provider.ReadersInLibraryLogRow;

import java.util.ArrayList;

public class RetainedReadersInLibraryLogFragment extends Fragment {
    public static final String FRAGMENT_TAG = "retained_readers_in_library_log_fragment";

    private ArrayList<ReadersInLibraryLogRow> rowList = new ArrayList<>();

    public RetainedReadersInLibraryLogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public ArrayList<ReadersInLibraryLogRow> getRowList() {
        return rowList;
    }

    public void setRowList(ArrayList<ReadersInLibraryLogRow> rowList) {
        this.rowList = rowList;
    }
}
