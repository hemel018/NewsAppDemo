package com.example.newsappdemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newsappdemo.R;
import com.example.newsappdemo.adapters.NewsAdapter;
import com.example.newsappdemo.utils.DividerItemDecoration;

public class TwoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v =  inflater.inflate(R.layout.fragment_one,parent, false);

//        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
//
//        mAdapter = new NewsAdapter(new String[]{"Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title", "Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title","Entertain News Title"});
//        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }
}
