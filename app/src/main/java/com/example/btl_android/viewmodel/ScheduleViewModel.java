package com.example.btl_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.data.repository.ScheduleRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ScheduleViewModel extends ViewModel {

    private final ScheduleRepository repository = new ScheduleRepository();
    private MutableLiveData<List<ScheduleRequest>> pendingRequests = new MutableLiveData<>();


    // Gọi hàm tạo đơn thu gom từ Repository
    public LiveData<Boolean> createSchedule(ScheduleRequest request) {
        return repository.createSchedule(request);
    }

    // Hàm tổng quát cho phép truyền status động
    public LiveData<List<ScheduleRequest>> getSchedulesByStatus(String status) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return repository.getSchedulesByStatusAndUserId(status, uid);
    }

    // lấy ra yêu cầu thu gom có status = pending và idColector = null
    public LiveData<List<ScheduleRequest>> getPendingRequests() {
        if (pendingRequests.getValue() == null) {
            pendingRequests = (MutableLiveData<List<ScheduleRequest>>) repository.getPendingRequests();
        }
        return pendingRequests;
    }

    // hàm xoá đơn hàng theo id
    public LiveData<Boolean> deleteScheduleById(String id) {
        return repository.deleteScheduleById(id);
    }

    public void acceptRequest(String requestId) {
        String collectorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.acceptRequest(requestId,collectorId);
    }

    public LiveData<List<ScheduleRequest>> getAcceptedRequests() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return repository.getAcceptedRequests(uid);
    }









}
