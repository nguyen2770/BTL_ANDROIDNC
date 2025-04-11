package com.example.btl_android.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.User;
import com.example.btl_android.data.repository.AuthRepository;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repository = new AuthRepository();

    public LiveData<String> getVerificationId() {
        return repository.verificationIdLiveData;
    }

    public LiveData<FirebaseUser> getUser() {
        return repository.userLiveData;
    }

    public LiveData<Boolean> getResetSuccess() {
        return repository.resetSuccessLiveData;
    }

    public void sendOtp(String phone, Activity activity) {
        repository.sendOtp(phone, activity);
    }

    public void verifyOtp(String verificationId, String code) {
        repository.verifyOtp(verificationId, code);
    }

    public void registerWithEmail(String email, String password) {
        repository.registerWithEmail(email, password);
    }

    public void loginWithEmail(String email, String password) {
        repository.loginWithEmail(email, password);
    }

    public String getCurrentUserId() {
        FirebaseUser user = repository.getFirebaseAuth().getCurrentUser();
        return user != null ? user.getUid() : null;
    }


    public void sendPasswordResetEmail(String email) {
        repository.sendPasswordResetEmail(email);
    }

    public void updatePassword(String newPassword) {
        repository.updatePassword(newPassword);
    }

    public FirebaseAuth getFirebaseAuth() {
        return repository.getFirebaseAuth();
    }

    public void saveUserToFirestore(User user) {
        repository.saveUserToFirestore(user);
    }

}
