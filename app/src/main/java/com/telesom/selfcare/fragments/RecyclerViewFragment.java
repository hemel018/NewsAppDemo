package com.telesom.selfcare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telesom.selfcare.R;
import com.telesom.selfcare.adapters.NewsAdapter;
import com.telesom.selfcare.entity.NewsFeed;
import com.telesom.selfcare.handlers.DatabaseHandler;
import com.telesom.selfcare.listener.DataChangeListener;
import com.telesom.selfcare.utils.CustomBrowser;
import com.telesom.selfcare.utils.DividerItemDecoration;
import com.telesom.selfcare.utils.RecyclerItemClickListener;

public class RecyclerViewFragment extends Fragment implements DataChangeListener{

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
                String url = "http://197.157.246.110:3000/articledetails/" +  mDataset[position].getSid();
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
    }


    @Override
    public void noitifyDataChangeListener(NewsAdapter adapter) {
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }
}
