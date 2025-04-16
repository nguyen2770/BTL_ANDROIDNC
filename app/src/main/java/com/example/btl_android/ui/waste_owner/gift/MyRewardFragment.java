package com.example.btl_android.ui.waste_owner.gift;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_android.R;
import com.example.btl_android.viewmodel.AuthViewModel;
import com.example.btl_android.viewmodel.RewardViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRewardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRewardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RewardViewModel viewModel;
    private MyRewardAdapter adapter;


    public MyRewardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyRewardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRewardFragment newInstance(String param1, String param2) {
        MyRewardFragment fragment = new MyRewardFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_reward, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_rewards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyRewardAdapter(getContext());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(RewardViewModel.class);
        viewModel.getUserRewardList().observe(getViewLifecycleOwner(), adapter::setRewardList);

        AuthViewModel authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        String userId = authViewModel.getCurrentUserId();
        if (userId != null) {
            viewModel.fetchUserRewards(userId);
        }

        viewModel.fetchUserRewards(userId);

        return view;
    }
}