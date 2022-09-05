package com.ruse.util.dumper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.tuple.Pair;

public class HtmlUtils {

    private static HttpURLConnection connect(URL url) throws IOException //Connect to the URL
    {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection(); //Opens connection to the website
        huc.setReadTimeout(15000); //Read timeout - 15 seconds
        huc.setConnectTimeout(15000); //Connecting timeout - 15 seconds
        huc.setUseCaches(false); //Don't use cache
        HttpURLConnection.setFollowRedirects(true); //Follow redirects if there are any
        huc.addRequestProperty("Host", url.toString());
        huc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36"); //Chrome user agent
        return huc;
    }

    /**
     * Returns a pair containing the html and the response code.
     */
    public static Pair<String, Integer> read(String urlString) throws IOException {
        URL url = new URL(urlString); //The URL
        HttpURLConnection huc = connect(url); //Connects to the website
        huc.connect(); //Opens the connection
        Pair<String, Integer> response = readBody(huc); //Reads the response
        huc.disconnect(); //Closes
        return response;
    }

    private static Pair<String, Integer> readBody(HttpURLConnection huc) throws IOException
    {
        int code = huc.getResponseCode();
        if (code == 200) {
            InputStream is = huc.getInputStream(); //Inputstream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is)); //BufferedReader
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line + "\n"); //Append the line
            }
            rd.close();
            return Pair.of(response.toString(), code);
        } else {
            return Pair.of(null, code);
        }
    }

}
