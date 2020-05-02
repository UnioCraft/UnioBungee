package com.uniocraft.UnioBungee.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utils {
    public static String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.setUseCaches(false);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getUUID(String playerName) {
        String jsonString = getJSON("https://ss.gameapis.net/profile/" + playerName);
        if (jsonString == null) {
            return null;
        }
        JSONObject json = new JSONObject(jsonString);
        return formatFromInput(json.getString("id")).toString();
    }

    public static ArrayList<String> getSkin(String uuid) {
        if (uuid == null) {
            return null;
        }
        String jsonString = getJSON("https://api.mineskin.org/generate/user/" + uuid);
        if (jsonString == null) {
            return null;
        }
        ArrayList<String> valueAndSignature = new ArrayList<String>();
        JSONObject json = new JSONObject(jsonString);

        valueAndSignature.add(json.getJSONObject("data").getJSONObject("texture").get("value").toString());
        valueAndSignature.add(json.getJSONObject("data").getJSONObject("texture").get("signature").toString());
        return valueAndSignature;
    }

    private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static UUID formatFromInput(String uuid) {
        return UUID.fromString(UUID_FIX.matcher(uuid.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }
}
