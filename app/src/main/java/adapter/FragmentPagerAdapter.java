package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import fragment.BusLinesFragment;
import fragment.LocationFragment;

/**
 * Created by Vacho on 3/19/2016.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Locations", "Lines" };

    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new BusLinesFragment();
                break;
            case 1:
                fragment = new LocationFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return tabTitles[0];
            case 1:
                return tabTitles[1];
        }
        return null;
    }
}
