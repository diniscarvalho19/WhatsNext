package com.dinis.whatsnext;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{



    Fragment mainPageFragment = new MainPageFragment();
    Fragment profileFrag = new ProfileFragment();
    Fragment watchlistFrag = new WatchlistFragment();
    Fragment groupFrag = new AllGroupsFragment();
    BottomNavigationView bottomNavigationView;



    public void getWatchlistFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, watchlistFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getProfileFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, profileFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void getGroupsFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, groupFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getMainPageFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, mainPageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getMainPageFragment();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.page_1:
                    getMainPageFragment();
                    break;
                case R.id.page_2:
                    getWatchlistFrag();
                    break;
                case R.id.page_3:
                    getProfileFrag();
                    break;
                case R.id.page_5:
                    getGroupsFrag();
                    break;
            }
            return true;
        });

    }




}