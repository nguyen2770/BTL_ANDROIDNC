package com.example.btl_android.ui.collector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.utils.RouteHelper;
import com.example.btl_android.viewmodel.ScheduleViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {

    private MapView mapView;
    private ScheduleViewModel scheduleViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint currentLocation;

    // Sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;

    private Marker currentMarker;
    private Polyline routeOverlay;
    private final List<Marker> requestMarkers = new ArrayList<>();
    private List<ScheduleRequest> acceptedRequests = new ArrayList<>();

    // Location updates
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        initLocationCallback();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Sensor setup
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        observeRequests();
        checkLocationPermissionAndLoad();

        return view;
    }

    private void observeRequests() {
        scheduleViewModel.getAcceptedRequests().observe(getViewLifecycleOwner(), requests -> {
            if (requests != null) {
                acceptedRequests = requests;
                // Draw markers once when data arrives
                drawRequestMarkers();
                // Redraw route if we have current location
                if (currentLocation != null && !acceptedRequests.isEmpty()) {
                    drawRoute(currentLocation, acceptedRequests);
                }
            }
        });
    }

    private void drawRequestMarkers() {
        if (!requestMarkers.isEmpty()) return; // already drawn
        for (ScheduleRequest request : acceptedRequests) {
            GeoPoint point = new GeoPoint(request.getLatitude(), request.getLongitude());
            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setTitle("Đơn: " + request.getAddress());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
            requestMarkers.add(marker);
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc != null) {
                    GeoPoint newPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
                    currentLocation = newPoint;
                    requireActivity().runOnUiThread(() -> {
                        if (currentMarker != null) {
                            currentMarker.setPosition(newPoint);
                        }
                        mapView.getController().setCenter(newPoint);
                        mapView.invalidate();
                        if (!acceptedRequests.isEmpty()) {
                            drawRoute(newPoint, acceptedRequests);
                        }
                    });

                    Log.d("DEBUG", "New location: " + newPoint.getLatitude() + ", " + newPoint.getLongitude());
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void checkLocationPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            loadInitialLocation();
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void loadInitialLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mapView.getController().setZoom(14.0);
                        mapView.getController().setCenter(currentLocation);

                        currentMarker = new Marker(mapView);
                        currentMarker.setPosition(currentLocation);
                        currentMarker.setTitle("Vị trí hiện tại");
                        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        currentMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_navigation_24));
                        mapView.getOverlays().add(currentMarker);

                        Log.d("DEBUG", "Overlay size: " + mapView.getOverlays().size());

                        // Draw initial route if have requests
                        if (!acceptedRequests.isEmpty()) {
                            drawRoute(currentLocation, acceptedRequests);
                        }
                    } else {
                        Log.e("GPS", "Không lấy được vị trí hiện tại");
                    }

                    Log.d("DEBUG", "Current marker: " + currentMarker);
                })
                .addOnFailureListener(e -> Log.e("GPS", "Lỗi lấy vị trí: " + e.getMessage()));
    }

    private void drawRoute(GeoPoint startPoint, List<ScheduleRequest> requests) {
        ArrayList<double[]> coords = new ArrayList<>();
        coords.add(new double[]{startPoint.getLatitude(), startPoint.getLongitude()});
        for (ScheduleRequest r : requests) {
            coords.add(new double[]{r.getLatitude(), r.getLongitude()});
        }

        RouteHelper.getRoute(coords, new RouteHelper.RouteCallback() {
            @Override
            public void onSuccess(JSONObject geoJson) {
                try {
                    JSONArray arr = geoJson.getJSONArray("features")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    List<GeoPoint> geoPoints = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONArray c = arr.getJSONArray(i);
                        geoPoints.add(new GeoPoint(c.getDouble(1), c.getDouble(0)));
                    }

                    requireActivity().runOnUiThread(() -> {
                        if (routeOverlay == null) {
                            routeOverlay = new Polyline();
                            routeOverlay.setColor(Color.BLUE);
                            routeOverlay.setWidth(8f);
                            mapView.getOverlays().add(routeOverlay);
                        }
                        routeOverlay.setPoints(geoPoints);
                        mapView.invalidate();
                    });
                } catch (Exception e) {
                    Log.e("ROUTE_DRAW", "Lỗi xử lý GeoJSON: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ROUTE", "Lỗi khi lấy tuyến đường: " + error);
            }
        });
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) gravity = event.values;
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) geomagnetic = event.values;
            if (gravity != null && geomagnetic != null) {
                float[] R = new float[9], I = new float[9];
                if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                    float[] o = new float[3];
                    SensorManager.getOrientation(R, o);
                    float az = (float) Math.toDegrees(o[0]);
                    updateMarkerRotation((az + 360) % 360);
                }
            }
        }

        @Override public void onAccuracyChanged(Sensor s, int a) {}
    };

    private void updateMarkerRotation(float azimuth) {
        if (currentMarker != null) {
            requireActivity().runOnUiThread(() -> {
                currentMarker.setRotation(azimuth);
                mapView.invalidate();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadInitialLocation();
            startLocationUpdates();
        } else {
            Log.e("PERMISSION", "Không được cấp quyền vị trí.");
        }
    }
}
