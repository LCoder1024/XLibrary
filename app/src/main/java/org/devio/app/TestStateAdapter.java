package org.devio.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class TestStateAdapter extends FragmentStateAdapter {
    private final String[] titles;

    public TestStateAdapter(@NonNull FragmentActivity fragmentActivity, String[] titles) {
        super(fragmentActivity);
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String title = titles[position];
        return TestFragment.newInstance(title);
    }


    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        return super.containsItem(itemId);
    }
}
