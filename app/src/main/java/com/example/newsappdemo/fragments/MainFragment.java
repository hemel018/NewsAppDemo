package com.example.newsappdemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import android.app.PendingIntent;
import android.app.AlarmManager;

import com.example.newsappdemo.R;
import com.example.newsappdemo.adapters.NewsAdapter;
import com.example.newsappdemo.entity.NewsFeed;
import com.example.newsappdemo.handlers.DatabaseHandler;
import com.example.newsappdemo.listener.DataChangeListener;
import com.example.newsappdemo.service.DownloadResultReceiver;
import com.example.newsappdemo.service.NewsUpdateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by rana on 7/18/16.
 */
public class MainFragment extends Fragment implements DownloadResultReceiver.Receiver  {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DatabaseHandler mDatabaseHandler;
    private GregorianCalendar calendar;
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.main_fragment, parent, false);

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tabs);

        mDatabaseHandler = new DatabaseHandler(getActivity());

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        startServiceToUpdateNews();

        //tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);


        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "Home");
        adapter.addFrag(new TwoFragment(), "News");
        adapter.addFrag(new ThreeFragment(), "Sports");

        viewPager.setAdapter(adapter);
    }

    private void startServiceToUpdateNews() {

        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this.getActivity(), NewsUpdateService.class);

        /* Send optional extras to Download IntentService */
        String requestUrl = "http://197.157.246.110:3000/api/getupdate/" + Long.toString( this.readLastSyncedTime() );
        intent.putExtra("url", requestUrl);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        AlarmManager alarmManager;
        calendar = (GregorianCalendar) Calendar.getInstance();

        pendingIntent = PendingIntent.getService(this.getActivity(), 0,
                intent, 0);

        alarmManager = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis()+1000,
                60*1000 , pendingIntent);

        //getActivity().startService(intent);
    }

    private void updateLastSyncedTime(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        long defaultValue = System.currentTimeMillis() - 1000 * 2 * 60;
        editor.putLong("lastsyncedtime", defaultValue);
        editor.commit();
    }

    private long readLastSyncedTime(){

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        long defaultValue = System.currentTimeMillis() - 1000 * 60*24 * 60;
        return sharedPref.getLong("lastsyncedtime", defaultValue);
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

                int count = 0;

                for(NewsFeed news : results)
                {

                    mDatabaseHandler.addNewsFeed(news);
                }

                int index = 1;

                for(Fragment fragment : ((ViewPagerAdapter)viewPager.getAdapter()).getmFragmentList())
                {
                    ((DataChangeListener)fragment).noitifyDataChangeListener(new NewsAdapter(mDatabaseHandler.getAllNewsByCategory(index)));
                    index++;
                }

                //mRecyclerView.setAdapter(new NewsAdapter(mDatabaseHandler.getAllNewsByCategory(1)));
                this.updateLastSyncedTime();
                break;
            case NewsUpdateService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public List<Fragment> getmFragmentList()
        {
            return mFragmentList;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        //ft.addToBackStack(null);
        ft.commit();
    }
}
