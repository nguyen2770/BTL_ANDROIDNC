package com.example.btl_android.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.*;

public class RouteHelper {
    private static final String API_KEY = "5b3ce3597851110001cf62480aa39181d8354d55b57c41d17b05eb21"; // thay bằng khóa của bạn
    private static final String ORS_URL = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

    public interface RouteCallback {
        void onSuccess(JSONObject geoJson);
        void onError(String error);
    }

    public static void getRoute(List<double[]> coordinates, RouteCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONArray coordsArray = new JSONArray();
            for (double[] coord : coordinates) {
                JSONArray point = new JSONArray();
                point.put(coord[1]); // longitude
                point.put(coord[0]); // latitude
                coordsArray.put(point);
            }

            JSONObject body = new JSONObject();
            body.put("coordinates", coordsArray);

            Request request = new Request.Builder()
                    .url(ORS_URL)
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                    .addHeader("Authorization", API_KEY)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError("ORS Error: " + response.code());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        callback.onSuccess(json);
                    } catch (Exception e) {
                        callback.onError("Parse error: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Build JSON error: " + e.getMessage());
        }
    }
}