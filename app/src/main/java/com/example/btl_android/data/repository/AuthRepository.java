package com.example.btl_android.data.repository;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.btl_android.data.model.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    public MutableLiveData<String> verificationIdLiveData = new MutableLiveData<>();
    public MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> resetSuccessLiveData = new MutableLiveData<>();

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void sendOtp(String phoneNumber, Activity activity) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        firebaseAuth.signInWithCredential(credential)
                                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("AuthRepo", "Verification failed: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        verificationIdLiveData.postValue(verificationId);
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(String verificationId, String otpCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
    }

    public void registerWithEmail(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
    }

    public void loginWithEmail(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
    }

    public void updatePassword(String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> resetSuccessLiveData.postValue(true))
                    .addOnFailureListener(e -> resetSuccessLiveData.postValue(false));
        } else {
            resetSuccessLiveData.postValue(false);
        }
    }

    public void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> resetSuccessLiveData.postValue(true))
                .addOnFailureListener(e -> resetSuccessLiveData.postValue(false));
    }


    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void saveUserToFirestore(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).set(user)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User profile added successfully."))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding user profile: " + e.getMessage()));
        }
    }
}
