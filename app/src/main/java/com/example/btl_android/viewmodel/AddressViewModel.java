package com.example.btl_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.SavedAddress;
import com.example.btl_android.data.repository.AddressRepository;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;

public class AddressViewModel extends ViewModel {
    private final AddressRepository repository;
    private final MutableLiveData<List<SavedAddress>> savedAddresses = new MutableLiveData<>();

    public AddressViewModel() {
        repository = new AddressRepository();
    }

    public LiveData<List<SavedAddress>> getSavedAddresses() {
        return savedAddresses;
    }

    public void fetchSavedAddresses() {
        repository.getSavedAddresses(addressList -> savedAddresses.setValue(addressList));
    }

    public void saveAddress(SavedAddress address, OnCompleteListener<Void> listener) {
        repository.saveAddress(address, listener);
    }

    public void deleteAddress(String documentId, OnCompleteListener<Void> listener) {
        repository.deleteAddress(documentId, listener);
    }

    public void updateAddress(SavedAddress address, OnCompleteListener<Void> listener) {
        repository.updateAddress(address, listener);
    }
}
