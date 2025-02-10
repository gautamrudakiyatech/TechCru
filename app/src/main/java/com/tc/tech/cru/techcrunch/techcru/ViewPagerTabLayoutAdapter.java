package com.tc.tech.cru.techcrunch.techcru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


@SuppressWarnings("deprecation")
public class ViewPagerTabLayoutAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 6;

    public ViewPagerTabLayoutAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return Frag2.newInstance();
            case 2:
                return Frag3.newInstance();
            case 3:
                return Frag4.newInstance();
            case 4:
                return Frag5.newInstance();
            case 5:
                return Frag6.newInstance();
            default:
                return Frag1.newInstance();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "AI";
            case 1:
                return "Startups";
            case 2:
                return "Venture";
            case 3:
                return "Security";
            case 4:
                return "Crypto";
            case 5:
                return "Apps";
            default:
                return null;
        }

    }

}
