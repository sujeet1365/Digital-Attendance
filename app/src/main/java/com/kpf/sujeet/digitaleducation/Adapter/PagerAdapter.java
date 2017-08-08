package com.kpf.sujeet.digitaleducation.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kpf.sujeet.digitaleducation.Fragments.ActivitiesFragment;
import com.kpf.sujeet.digitaleducation.Fragments.HomeFragment;


/**
 * Created by SUJEET on 1/11/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    String titles[] = new String[]{"Home","Activities"};


    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new ActivitiesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}