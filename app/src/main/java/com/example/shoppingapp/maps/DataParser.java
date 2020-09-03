package com.example.shoppingapp.maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * get the place using Hash Map and then send this data
 * Our Hashmap will store one place
 */
class DataParser {

    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlaces = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        // we fetch the data, after that we put it in our hash map
        try {
            if (!googlePlaceJSON.isNull("name")) {
                nameOfPlaces = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name", nameOfPlaces);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    // now we need a list of hash maps, store data in Form of hash maps
    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();
        List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
        HashMap<String, String> nearbyPlaceMap = null;

        // get one by one the places and add them to the list
        for (int i = 0; i < counter; i++) {
            try {
                nearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyPlacesList;
    }

    // parse Method to parse the data to the getAllNearbyPlaces JSON array
    List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);
    }
}
