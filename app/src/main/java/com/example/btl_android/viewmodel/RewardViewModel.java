package com.example.btl_android.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.Reward;
import com.example.btl_android.data.model.UserReward;
import com.example.btl_android.data.repository.RewardRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardViewModel extends ViewModel {

    private final RewardRepository repository = new RewardRepository();

    private final MutableLiveData<List<Reward>> rewardList = new MutableLiveData<>();
    private final MutableLiveData<List<UserReward>> userRewardList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<Reward>> getRewardList() {
        return rewardList;
    }

    public LiveData<List<UserReward>> getUserRewardList() {
        return userRewardList;
    }

    public void fetchAvailableRewards() {
        isLoading.setValue(true);
        repository.getAvailableRewards(new RewardRepository.Callback<List<Reward>>() {
            @Override
            public void onSuccess(List<Reward> data) {
                isLoading.setValue(false);
                rewardList.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue("Không thể tải danh sách phần thưởng");
                Log.e("RewardViewModel", "fetchAvailableRewards: " + e.getMessage());
            }
        });
    }

    public void fetchUserRewards(String userId) {
        isLoading.setValue(true);
        repository.getUserRewards(userId, new RewardRepository.Callback<List<UserReward>>() {
            @Override
            public void onSuccess(List<UserReward> userRewards) {
                if (userRewards.isEmpty()) {
                    isLoading.setValue(false);
                    userRewardList.setValue(new ArrayList<>());
                    return;
                }

                List<UserReward> completedList = new ArrayList<>();
                AtomicInteger counter = new AtomicInteger(0);
                Date now = new Date();

                for (UserReward ur : userRewards) {
                    repository.getRewardById(ur.getRewardID(), new RewardRepository.Callback<Reward>() {
                        @Override
                        public void onSuccess(Reward reward) {
                            ur.setTitle(reward.getTitle());
                            ur.setDescription(reward.getDescription());
                            ur.setExpiryDate(reward.getExpiryDate());
                            ur.setImageUrl(reward.getImageUrl());

                            // Chỉ thêm nếu còn hạn
                            if (reward.getExpiryDate().toDate().after(now)) {
                                completedList.add(ur);
                            }

                            if (counter.incrementAndGet() == userRewards.size()) {
                                isLoading.setValue(false);
                                userRewardList.setValue(completedList);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("RewardViewModel", "getRewardById: " + e.getMessage());
                            if (counter.incrementAndGet() == userRewards.size()) {
                                isLoading.setValue(false);
                                userRewardList.setValue(completedList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue("Không thể tải quà đã đổi");
                Log.e("RewardViewModel", "fetchUserRewards: " + e.getMessage());
            }
        });
    }

    private int lastExchangedPoints = 0; // Biến tạm lưu điểm của phần quà vừa đổi

    public int getLastExchangedRewardPoints() {
        return lastExchangedPoints;
    }

    // Thêm hàm để xử lý đổi phần thưởng
    public void exchangeReward(String userId, Reward reward, int userPoints) {
        isLoading.setValue(true);
        repository.exchangeReward(userId, reward, userPoints, new RewardRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                isLoading.setValue(false);
                lastExchangedPoints = reward.getPointsRequired();
                successMessage.setValue("Đổi quà thành công!");
                fetchAvailableRewards(); // Refresh lại danh sách
                fetchUserRewards(userId); // Cập nhật danh sách quà đã đổi
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue(e.getMessage());
                Log.e("RewardViewModel", "exchangeReward: " + e.getMessage());
            }
        });
    }
}
