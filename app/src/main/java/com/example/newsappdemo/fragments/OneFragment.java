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
import com.example.newsappdemo.entity.NewsFeed;
import com.example.newsappdemo.handlers.DatabaseHandler;
import com.example.newsappdemo.listener.DataChangeListener;
import com.example.newsappdemo.utils.CustomBrowser;
import com.example.newsappdemo.utils.DividerItemDecoration;
import com.example.newsappdemo.utils.RecyclerItemClickListener;

public class OneFragment extends Fragment implements DataChangeListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private NewsFeed[] mDataset;
    private DatabaseHandler mDatabaseHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {


        // Defines the xml file for the fragment
        View v =  inflater.inflate(R.layout.fragment_one,parent, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "http://197.157.246.110:3000/show/" +  mDataset[position].getSid();
                CustomBrowser browser = new CustomBrowser(getActivity());
                browser.open(url);
            }
        }));


        mDatabaseHandler = new DatabaseHandler(getActivity());

        mDataset = mDatabaseHandler.getAllNewsByCategory(1);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new NewsAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }


    @Override
    public void noitifyDataChangeListener(NewsAdapter adapter) {
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }
}
