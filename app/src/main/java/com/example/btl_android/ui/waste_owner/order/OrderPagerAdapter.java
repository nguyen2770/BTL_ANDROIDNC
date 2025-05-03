package com.example.btl_android.ui.waste_owner.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return OrderListFragment.newInstance("Pending");
            case 1: return OrderListFragment.newInstance("InProgress");
            case 2: return OrderListFragment.newInstance("Completed");
            default: return OrderListFragment.newInstance("Pending");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
