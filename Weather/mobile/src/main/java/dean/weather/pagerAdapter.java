package dean.weather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class pagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentsList = new ArrayList<>();
    private List<String> fragmentsTitleList = new ArrayList<>();

    public pagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);

    }

    @Override
    public int getCount() {
        return fragmentsTitleList.size();
    }

    public void addFragment(Fragment fragment, String title){
        fragmentsList.add(fragment);
        fragmentsTitleList.add(title);
    }
    public CharSequence getPageTitle(int position){
        return fragmentsTitleList.get(position);
    }
}
