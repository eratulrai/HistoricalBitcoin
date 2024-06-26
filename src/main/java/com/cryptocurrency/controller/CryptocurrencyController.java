package com.cryptocurrency.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class CryptocurrencyController {

    @RequestMapping("/getCryptocurrencyDetailsByDate")
    public @ResponseBody
    JsonObject getCryptocurrencyDetailsbyDate(String cryptocurrency) throws IOException {
        JsonObject jsonObject = new JsonObject();


        return jsonObject;
    }

    @RequestMapping("/getCryptocurrencyDetailsByName")
    public @ResponseBody
    JsonObject getCryptocurrencyDetails(String cryptocurrency) throws IOException {

        JsonObject jsonObject = new JsonObject();
        String data = getCryptocurrencyData(cryptocurrency);

        // Once we receive the data we need to parse that
        // and hence these special characters has to be removed
        data = data.replaceAll("^\"|\"$", "");
        StringTokenizer jsonTokenizer = new StringTokenizer(data,",");
        String internalData[];
        String expectedCryptocurrencyOutput = null;

        // After parsing, retrieving Rank, Symbol and priceUsd from it
        while (jsonTokenizer.hasMoreTokens()) {
            expectedCryptocurrencyOutput = jsonTokenizer.nextToken();
            internalData = StringUtils.split(expectedCryptocurrencyOutput,":");
            System.out.println(internalData[0]+internalData[1]);
            if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("rank")) {
                jsonObject.addProperty("rank", internalData[1].substring(1,internalData[1].length()-1));

            }
            if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("symbol")) {
                jsonObject.addProperty("symbol", internalData[1].substring(1,internalData[1].length()-1));
            }
            if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("priceUsd")) {
                jsonObject.addProperty("priceUsd", internalData[1].substring(1,internalData[1].length()-1));
            }
        }

        jsonObject.addProperty("data", data);

        return jsonObject;
    }

    private String getCryptocurrencyData(String cryptocurrency) throws IOException {

        String data = null;
        StringBuilder responseData = new StringBuilder();
        JsonObject jsonObject = null;
        URL url = null;

        // REST API call that got used along with the search string
        url = new URL("https://api.coincap.io/v2/assets/"+cryptocurrency);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }
            jsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);
            data = jsonObject.get("data").toString();
        }
        System.out.println(data);
        return data;
    }
}

