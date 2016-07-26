package com.telesom.selfcare.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.telesom.selfcare.R;
import com.telesom.selfcare.adapters.NewsAdapter;
import com.telesom.selfcare.entity.NewsFeed;
import com.telesom.selfcare.handlers.DatabaseHandler;
import com.telesom.selfcare.listener.DataChangeListener;
import com.telesom.selfcare.service.DownloadResultReceiver;
import com.telesom.selfcare.service.NewsUpdateService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by rana on 7/18/16.
 */
public class MainFragment extends Fragment   {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DatabaseHandler mDatabaseHandler;
    private GregorianCalendar calendar;
    private PendingIntent pendingIntent;
    private static ServiceReceiver serviceReceiver;

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


        serviceReceiver = new ServiceReceiver();
        Intent alarmIntent = new Intent(this.getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this.getActivity(), 0, alarmIntent, 0);




        calendar = (GregorianCalendar) Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+1000,
                60*1000 , pendingIntent);

        //getActivity().startService(intent);
    }

    public static ServiceReceiver getServiceReceiver()
    {
        return serviceReceiver;
    }



//    @Override
//    public void onReceiveResult(int resultCode, Bundle resultData) {
//        switch (resultCode) {
//            case NewsUpdateService.STATUS_RUNNING:
//
//                getActivity().setProgressBarIndeterminateVisibility(true);
//                break;
//            case NewsUpdateService.STATUS_FINISHED:
//                /* Hide progress & extract result from bundle */
//                getActivity().setProgressBarIndeterminateVisibility(false);
//
//                NewsFeed[] results = (NewsFeed[])resultData.getParcelableArray("result");
//
//                /* Update ListView with result */
//
//                int count = 0;
//
//                for(NewsFeed news : results)
//                {
//
//                    mDatabaseHandler.addNewsFeed(news);
//                }
//
//                int index = 1;
//
//                for(Fragment fragment : ((ViewPagerAdapter)viewPager.getAdapter()).getmFragmentList())
//                {
//                    ((DataChangeListener)fragment).noitifyDataChangeListener(new NewsAdapter(mDatabaseHandler.getAllNewsByCategory(index)));
//                    index++;
//                }
//
//                //mRecyclerView.setAdapter(new NewsAdapter(mDatabaseHandler.getAllNewsByCategory(1)));
//
//                break;
//            case NewsUpdateService.STATUS_ERROR:
//                /* Handle the error */
//                String error = resultData.getString(Intent.EXTRA_TEXT);
//                Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
//                break;
//        }
//    }

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

    public static  class AlarmReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            // For our recurring task, we'll just display a message

            DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
            mReceiver.setReceiver(MainFragment.getServiceReceiver());
            Intent serviceIntent = new Intent(Intent.ACTION_SYNC, null, context, NewsUpdateService.class);


            String requestUrl = "http://197.157.246.110:3000/api/getupdate/";
            serviceIntent.putExtra("url", requestUrl);
            serviceIntent.putExtra("receiver", mReceiver);
            serviceIntent.putExtra("requestId", 101);

            context.startService(serviceIntent);

            Log.d("test", "-------------------test");
        }


    }

    class ServiceReceiver implements DownloadResultReceiver.Receiver
    {

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

                    break;
                case NewsUpdateService.STATUS_ERROR:
                /* Handle the error */
                    String error = resultData.getString(Intent.EXTRA_TEXT);
                    Toast.makeText(MainFragment.this.getActivity(), error, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
