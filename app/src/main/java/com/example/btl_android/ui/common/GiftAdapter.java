package com.example.btl_android.ui.common;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.btl_android.ui.waste_owner.MyRewardFragment;
import com.example.btl_android.ui.waste_owner.RewardListFragment;

public class GiftAdapter extends FragmentStateAdapter {
    public GiftAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new RewardListFragment(); // Danh sách phần quà
        } else {
            return new MyRewardFragment();   // Quà đã đổi
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
