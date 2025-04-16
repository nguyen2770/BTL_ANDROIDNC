package com.example.btl_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.data.repository.ScheduleRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ScheduleViewModel extends ViewModel {

    private final ScheduleRepository repository = new ScheduleRepository();


    // Gọi hàm tạo đơn thu gom từ Repository
    public LiveData<Boolean> createSchedule(ScheduleRequest request) {
        return repository.createSchedule(request);
    }

    // Hàm tổng quát cho phép truyền status động
    public LiveData<List<ScheduleRequest>> getSchedulesByStatus(String status) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return repository.getSchedulesByStatusAndUserId(status, uid);
    }

    // hàm xoá đơn hàng theo id
    public LiveData<Boolean> deleteScheduleById(String id) {
        return repository.deleteScheduleById(id);
    }








}
