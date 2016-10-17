package rkr.larc.mynewspaperagent.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.larc.mynewspaperagent.R;
import rkr.larc.mynewspaperagent.adapters.HomeAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SM_Home extends Fragment {


    public SM_Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sm_home, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.SMH_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HomeAdapter());
        return view;
    }

}
