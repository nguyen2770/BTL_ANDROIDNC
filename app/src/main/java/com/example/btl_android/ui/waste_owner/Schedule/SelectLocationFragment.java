package com.example.btl_android.ui.waste_owner.Schedule;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;

import com.example.btl_android.R;
import com.example.btl_android.data.model.SavedAddress;
import com.example.btl_android.viewmodel.AddressViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "SelectLocationFragment";

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText edtSearch;
    private TextView tvSelectedAddress;
    private Button btnConfirm;
    private LatLng selectedLatLng;
    private AddressViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);

        edtSearch = view.findViewById(R.id.etSearchAddress);
        tvSelectedAddress = view.findViewById(R.id.tvSelectedAddress);
        btnConfirm = view.findViewById(R.id.btnConfirmLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        setupSearchAddress();
        setupConfirmButton();

        return view;
    }

    private void setupSearchAddress() {
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String locationName = edtSearch.getText().toString();
                if (!locationName.isEmpty()) {
                    searchAddress(locationName);
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập địa chỉ cần tìm", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    private void searchAddress(String locationName) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    selectedLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    requireActivity().runOnUiThread(() -> {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Vị trí tìm kiếm"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                        tvSelectedAddress.setText(address.getAddressLine(0));
                    });
                } else {
                    showToastOnUI("Không tìm thấy địa chỉ");
                }
            } catch (IOException e) {
                Log.e(TAG, "Lỗi khi tìm địa chỉ", e);
                showToastOnUI("Lỗi khi tìm địa chỉ");
            }
        }).start();
    }

    private void setupConfirmButton() {
        btnConfirm.setOnClickListener(v -> {
            String addressText = tvSelectedAddress.getText().toString();
            if (selectedLatLng != null && !addressText.isEmpty()) {
                SavedAddress address = new SavedAddress(
                        "", // Document ID sẽ được Firestore tạo tự động
                        "Nhà Riêng",
                        addressText,
                        selectedLatLng.latitude,
                        selectedLatLng.longitude
                );

                viewModel.saveAddress(address, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn vị trí", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền nếu chưa được cấp
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        // Đã có quyền → lấy vị trí hiện tại
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                        setAddressFromLatLng(selectedLatLng);
                        mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Vị trí hiện tại"));
                    }
                });

        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            setAddressFromLatLng(latLng);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã chọn"));
        });
    }

    private void setAddressFromLatLng(LatLng latLng) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String detailedAddress = address.getAddressLine(0); // Địa chỉ đầy đủ
                    String street = address.getThoroughfare(); // Tên đường
                    String locality = address.getLocality(); // Thành phố/quận
                    String country = address.getCountryName(); // Quốc gia

                    requireActivity().runOnUiThread(() -> {
                        // Hiển thị địa chỉ chi tiết
                        tvSelectedAddress.setText(detailedAddress);

                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi khi lấy địa chỉ", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showToastOnUI(String message) {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
}