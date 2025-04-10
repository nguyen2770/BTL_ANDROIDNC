package com.example.btl_android.ui.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.btl_android.R;

public class GuideFragment extends Fragment {

    public GuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment hướng dẫn phân loại rác
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        // Bạn có thể thực hiện thêm thao tác với view tại đây nếu cần
        // Ví dụ: Thiết lập sự kiện click hoặc cập nhật nội dung động

        return view;
    }
}
