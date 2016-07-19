package com.example.newsappdemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.newsappdemo.MainActivity;
import com.example.newsappdemo.R;
import com.example.newsappdemo.adapters.NewsAdapter;
import com.example.newsappdemo.entity.NewsFeed;
import com.example.newsappdemo.handlers.DatabaseHandler;
import com.example.newsappdemo.service.DownloadResultReceiver;
import com.example.newsappdemo.service.NewsUpdateService;
import com.example.newsappdemo.utils.CustomBrowser;
import com.example.newsappdemo.utils.DividerItemDecoration;
import com.example.newsappdemo.utils.RecyclerItemClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OneFragment extends Fragment implements DownloadResultReceiver.Receiver {

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
                CustomBrowser browser = new CustomBrowser(getActivity());
                browser.open("https://www.google.com");
            }
        }));


        mDatabaseHandler = new DatabaseHandler(getActivity());

        mDataset = mDatabaseHandler.getAllNewsByCategory(1);

        startServiceToUpdateNews();



        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new NewsAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }



    private void startServiceToUpdateNews() {

        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this.getActivity(), NewsUpdateService.class);

        /* Send optional extras to Download IntentService */
        String requestUrl = "http://197.157.246.110:3000/api/getupdate/" + Long.toString(System.currentTimeMillis() - 1000 *5 * 60);
        intent.putExtra("url", requestUrl);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        getActivity().startService(intent);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case NewsUpdateService.STATUS_RUNNING:

                getActivity().setProgressBarIndeterminateVisibility(true);
                break;
            case NewsUpdateService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                getActivity().setProgressBarIndeterminateVisibility(false);

                NewsFeed[] results = (NewsFeed[])resultData.getParcelableArray("result");

                /* Update ListView with result */

                for(NewsFeed news : results)
                {
                    mDatabaseHandler.addNewsFeed(news);
                }

                mRecyclerView.setAdapter(new NewsAdapter(mDatabaseHandler.getAllNewsByCategory(1)));

                break;
            case NewsUpdateService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
