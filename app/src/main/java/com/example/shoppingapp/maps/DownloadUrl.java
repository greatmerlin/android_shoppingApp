package com.example.shoppingapp.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * class to retrieve EntityInShoppingList from the URL
 * returns the data in json Format
 */

class DownloadUrl {

    String ReadTheURL(String placeURL) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        // create the connection, open it, connect, then read data from url
        try {
            URL url = new URL(placeURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            // now read the line one by one, init with empty String, then append to SB
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            //now convert the SB to String
            data = stringBuffer.toString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}
