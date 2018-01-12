package me.akshanshjain.manage.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.akshanshjain.manage.Fragments.Overview;
import me.akshanshjain.manage.Fragments.Profile;

/**
 * Created by Akshansh on 06-01-2018.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Overview();
            case 1:
                return new Profile();
            default:
                return new Overview();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Changing";
            case 1:
                return "Overview";
            case 2:
                return "Profile";
        }
        return null;
    }
}
