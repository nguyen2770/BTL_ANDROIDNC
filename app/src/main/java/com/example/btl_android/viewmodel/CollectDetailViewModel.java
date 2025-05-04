package com.example.btl_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.Material;
import com.example.btl_android.data.model.RecyclableMaterial;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.data.repository.MaterialRepository;
import com.example.btl_android.data.repository.ScheduleRepository;
import com.example.btl_android.data.repository.UserRepository;

import java.util.List;
import java.util.function.Consumer;

public class CollectDetailViewModel extends ViewModel {

    private final ScheduleRepository scheduleRepository = new ScheduleRepository();
    private final UserRepository userRepository = new UserRepository();
    private final MaterialRepository materialRepository = new MaterialRepository();

    private final MutableLiveData<List<Material>> materialsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> completionSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Material>> getMaterialsLiveData() {
        return materialsLiveData;
    }

    public LiveData<Boolean> getCompletionSuccess() {
        return completionSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gọi khi cần load danh sách vật liệu
     */
    public void loadAllMaterials() {
        materialRepository.getAllMaterials(
                materials -> materialsLiveData.setValue(materials),
                e -> errorMessage.setValue("Lỗi khi tải vật liệu: " + e.getMessage())
        );
    }

    /**
     * Gọi khi người dùng ấn nút "Hoàn thành"
     */
    public void completeSchedule(String requestId, List<RecyclableMaterial> selectedMaterials, String userId) {
        // Tính điểm thưởng
        double totalPoints = 0;
        List<Material> materials = materialsLiveData.getValue();

        if (materials == null) {
            errorMessage.setValue("Không tìm thấy thông tin vật liệu để tính điểm.");
            return;
        }

        for (RecyclableMaterial rm : selectedMaterials) {
            for (Material m : materials) {
                if (m.getMaterialID() == rm.getId()) {
                    totalPoints += rm.getQuantity() * m.getPricePerKg();
                    break;
                }
            }
        }

        double finalPoints = totalPoints;

        // Cập nhật đơn thu gom
        scheduleRepository.markRequestAsCompleted(requestId, selectedMaterials, task -> {
            if (task.isSuccessful()) {
                // Sau khi cập nhật đơn thành công -> cộng điểm
                userRepository.addPointsToUser(userId, finalPoints, pointTask -> {
                    if (pointTask.isSuccessful()) {
                        completionSuccess.setValue(true);
                    } else {
                        errorMessage.setValue("Cập nhật điểm thất bại: " + pointTask.getException().getMessage());
                    }
                });
            } else {
                errorMessage.setValue("Cập nhật đơn thất bại: " + task.getException().getMessage());
            }
        });
    }
}
