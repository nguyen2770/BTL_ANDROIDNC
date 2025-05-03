package com.example.btl_android.ui.waste_owner.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_android.R;
import com.example.btl_android.databinding.FragmentHomeBinding;
import com.example.btl_android.ui.waste_owner.Material.MaterialAdapter;
import com.example.btl_android.ui.common.SlideAdapter;
import com.example.btl_android.ui.waste_owner.statistics.StatisticsViewModel;
import com.example.btl_android.utils.DataSeeder;
import com.example.btl_android.viewmodel.MaterialViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager2 viewPager2;
    private List<String> imageList;
    private Handler sliderHandler = new Handler();

    private MaterialViewModel viewModel;
    private MaterialAdapter adapter;
    private LinearLayout toGift;
    private StatisticsViewModel statisticsViewModel;


    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2.getCurrentItem() < imageList.size() - 1) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            } else {
                viewPager2.setCurrentItem(0);
            }
            sliderHandler.postDelayed(this, 3000); // đổi ảnh sau 3 giây
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager2 = binding.viewPager;

        imageList = new ArrayList<>();
        imageList.add("https://cdn-media.sforum.vn/storage/app/media/wp-content/uploads/2024/03/hinh-nen-PowerPoint-bao-ve-moi-truong-thumbnail.jpg");
        imageList.add("https://cellphones.com.vn/sforum/wp-content/uploads/2024/03/hinh-nen-PowerPoint-bao-ve-moi-truong-1.jpg");
        imageList.add("https://cellphones.com.vn/sforum/wp-content/uploads/2024/03/hinh-nen-PowerPoint-bao-ve-moi-truong-2.jpg");

        viewPager2.setAdapter(new SlideAdapter(imageList));

        // Bắt đầu auto slide
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Reset khi lướt tay
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        RecyclerView recyclerView = binding.recyclerMaterials;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        viewModel = new ViewModelProvider(this).get(MaterialViewModel.class);

        adapter = new MaterialAdapter(getContext(), material -> {
            // Khi click vào item, mở chi tiết
            Bundle bundle = new Bundle();
            bundle.putString("material", new Gson().toJson(material));

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_home_to_detail_material, bundle);

            // Cập nhật thống kê
            statisticsViewModel.updateStatistics(material.getWeight(), material.getType());
        });

        recyclerView.setAdapter(adapter);

        // Hiển thị loading
        CircularProgressIndicator progressIndicator = binding.progressIndicator;
        progressIndicator.setVisibility(View.VISIBLE);

        // Kiểm tra dữ liệu vật liệu
        FirebaseFirestore.getInstance().collection("materials").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Nếu không có dữ liệu, tạo dữ liệu mẫu
                        DataSeeder.seedMaterialData(success -> {
                            if (success) {
                                Toast.makeText(getContext(), "Đã tạo dữ liệu mẫu cho vật liệu", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Không thể tạo dữ liệu mẫu", Toast.LENGTH_SHORT).show();
                            }
                            progressIndicator.setVisibility(View.GONE);
                        });
                    } else {
                        progressIndicator.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        viewModel.getMaterials().observe(getViewLifecycleOwner(), materials -> {
            progressIndicator.setVisibility(View.GONE);
            if (materials != null && !materials.isEmpty()) {
                adapter.setMaterialList(materials);
            } else {
                // Hiển thị thông báo hoặc UI khi không có dữ liệu
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });

        toGift = binding.toGift;
        toGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_giftFragment);
            }
        });

        // Khởi tạo StatisticsViewModel
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}