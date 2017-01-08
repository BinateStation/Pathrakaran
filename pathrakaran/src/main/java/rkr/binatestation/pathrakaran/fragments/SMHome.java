package rkr.binatestation.pathrakaran.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.adapters.ProductAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SMHome extends Fragment {


    public SMHome() {
        // Required empty public constructor
    }


    public static SMHome newInstance() {

        Bundle args = new Bundle();

        SMHome fragment = new SMHome();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sm_home, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.SMH_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new ProductAdapter());
        return view;
    }

}
