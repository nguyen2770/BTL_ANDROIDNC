package com.example.btl_android.ui.waste_owner;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_android.R;
import com.example.btl_android.data.model.Reward;
import com.example.btl_android.ui.common.RewardItemAdapter;
import com.example.btl_android.viewmodel.RewardViewModel;
import com.example.btl_android.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RewardViewModel rewardViewModel;
    private UserViewModel userViewModel;
    private RewardItemAdapter adapter;

    public RewardListFragment() {
        // Required empty public constructor
    }

    public static RewardListFragment newInstance(String param1, String param2) {
        RewardListFragment fragment = new RewardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_rewards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rewardViewModel = new ViewModelProvider(requireActivity()).get(RewardViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        adapter = new RewardItemAdapter(getContext(), reward -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int currentPoints = userViewModel.getCurrentPoints();
            rewardViewModel.exchangeReward(userId, reward, currentPoints);
        });

        recyclerView.setAdapter(adapter);

        rewardViewModel.getRewardList().observe(getViewLifecycleOwner(), adapter::setRewardList);

        rewardViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

            // Cập nhật điểm người dùng sau khi đổi quà
            int rewardPoints = rewardViewModel.getLastExchangedRewardPoints(); // (Thêm hàm này ở bước dưới)
            int currentPoints = userViewModel.getCurrentPoints();
            int newPoints = currentPoints - rewardPoints;

            userViewModel.updateUserPoints(newPoints);
        });

        rewardViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());

        rewardViewModel.fetchAvailableRewards();
        userViewModel.fetchCurrentUser(); // Gọi để có dữ liệu điểm người dùng

        return view;
    }
}
