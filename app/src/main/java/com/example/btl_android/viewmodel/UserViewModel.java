package com.example.btl_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.User;
import com.example.btl_android.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final UserRepository userRepository = new UserRepository();

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void fetchCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserData(uid,
                user -> userLiveData.setValue(user),
                e -> {}
        );
    }

    public int getCurrentPoints() {
        User user = userLiveData.getValue();
        return user != null ? user.getPoints() : 0;
    }

    public void updateUserPoints(int newPoints) {
        User currentUser = userLiveData.getValue();
        if (currentUser != null) {
            currentUser.setPoints(newPoints);
            userLiveData.setValue(currentUser); // Trigger LiveData observer
        }
    }

}
