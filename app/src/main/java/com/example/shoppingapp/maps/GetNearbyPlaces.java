package com.example.shoppingapp.maps;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap map;

    @Override
    protected String doInBackground(Object... objects) {
        map = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        displayNearbyPlaces(nearbyPlacesList);
    }

    // show all the places of the list, add a marker to the place and move the camera there
    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            double latitude = Double.parseDouble(googleNearbyPlace.get("lat"));
            double longitude = Double.parseDouble(googleNearbyPlace.get("lng"));
            String reference = googleNearbyPlace.get("reference");
            LatLng latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}
