package com.georgcantor.wallpaperapp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.georgcantor.wallpaperapp.MyApplication;
import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.ui.fragment.BmwFragment;
import com.georgcantor.wallpaperapp.ui.fragment.CategoryFragment;
import com.georgcantor.wallpaperapp.ui.fragment.MercedesFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private String tabTitles[] = new String[]{MyApplication.getInstance().getResources()
            .getString(R.string.popular_tab), MyApplication.getInstance().getResources()
            .getString(R.string.latest_tab), MyApplication.getInstance().getResources()
            .getString(R.string.category_tab)};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MercedesFragment.newInstance();
            case 1:
                return BmwFragment.newInstance();
            case 2:
                return CategoryFragment.newInstance();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
