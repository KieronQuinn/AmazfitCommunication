package com.kieronquinn.library.amazfitcommunication.internet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.TransportDataItem;
import com.kieronquinn.library.amazfitcommunication.Transporter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Kieron on 09/04/2018.
 */

public class LocalHTTPRequest {


    private final LocalURLConnection localURLConnection;
    private final LocalHTTPResponse localHTTPResponse;
    private final Transporter transporter;
    private final UUID uuid;
    private final int timeout;
    private final Transporter.DataListener dataListener;
    private final Transporter.ChannelListener channelListener;

    /**
     * Create a HTTP request from a LocalURLConnection object, with a response callback
     * @param context Your context
     * @param localURLConnection Fully set up LocalURLConnection
     * @param localHTTPResponse Response callback
     */
    public LocalHTTPRequest(final Context context, LocalURLConnection localURLConnection, LocalHTTPResponse localHTTPResponse) {
        this.localURLConnection = localURLConnection;
        this.localHTTPResponse = localHTTPResponse;
        this.timeout = localURLConnection.getTimeout();
        transporter = Transporter.get(context, "com.kieronquinn.app.amazfitinternetcompanion");
        uuid = UUID.randomUUID();
        dataListener = new Transporter.DataListener() {
            @Override
            public void onDataReceived(TransportDataItem item) {
                if (item.getAction().equals("com.huami.watch.companion.transport.amazfitcommunication.HTTP_PINGBACK") && item.getData().getString("uuid").equals(uuid.toString())) {
                    if(requestTimeoutTimer != null){
                        requestTimeoutHandler.removeCallbacks(requestTimeoutTimer);
                        requestTimeoutTimer = null;
                    }
                }
                if (item.getAction().equals("com.huami.watch.companion.transport.amazfitcommunication.HTTP_RESULT") && item.getData().getString("uuid").equals(uuid.toString())) {
                    if(timeoutTimer != null){
                        timeoutHandler.removeCallbacks(timeoutTimer);
                        timeoutTimer = null;
                    }
                    LocalHTTPRequest.this.localHTTPResponse.onResult(convertResponse(item.getData()));
                    transporter.removeDataListener(this);
                    transporter.removeChannelListener(channelListener);
                    transporter.disconnectTransportService();
                }
            }
        };
        transporter.addDataListener(dataListener);
        channelListener = new Transporter.ChannelListener() {
            @Override
            public void onChannelChanged(boolean paramBoolean) {
                if(paramBoolean)send();
            }
        };
        transporter.addChannelListener(channelListener);
        transporter.connectTransportService();
    }

    /**
     * Send request
     */
    private void send() {
        final DataBundle dataBundle = localURLConnection.toDataBundle();
        dataBundle.putString("uuid", uuid.toString());
        requestTimeoutTimer = new Runnable() {
            @Override
            public void run() {
                localHTTPResponse.onConnectError();
                requestTimeoutTimer = null;
                transporter.removeDataListener(dataListener);
                transporter.removeChannelListener(channelListener);
                transporter.disconnectTransportService();
            }
        };
        requestTimeoutHandler.postDelayed(requestTimeoutTimer, 5000);
        if(timeout != -1){
            timeoutTimer = new Runnable() {
                @Override
                public void run() {
                    localHTTPResponse.onTimeout();
                    transporter.removeDataListener(dataListener);
                    transporter.removeChannelListener(channelListener);
                    transporter.disconnectTransportService();
                }
            };
            timeoutHandler.postDelayed(timeoutTimer, timeout);
        }
        transporter.send("com.huami.watch.companion.transport.amazfitcommunication.HTTP_REQUEST", dataBundle);
    }

    private Runnable timeoutTimer;
    private Handler timeoutHandler = new Handler();
    private Runnable requestTimeoutTimer;
    private Handler requestTimeoutHandler = new Handler();

    private HttpURLConnection convertResponse(DataBundle dataBundle) {
        String url = dataBundle.getString("url");
        boolean followRedirects = dataBundle.getBoolean("followRedirects", true);
        String requestMethod = dataBundle.getString("requestMethod");
        String responseMessage = dataBundle.getString("responseMessage");
        int responseCode = dataBundle.getInt("responseCode");
        boolean useCaches = dataBundle.getBoolean("useCaches", true);
        boolean doInput = dataBundle.getBoolean("doInput", true);
        boolean doOutput = dataBundle.getBoolean("doOutput", true);
        boolean usingProxy = dataBundle.getBoolean("usingProxy", true);
        InputStream inputStream = null;
        if (dataBundle.containsKey("inputStream")) {
            inputStream = new ByteArrayInputStream(dataBundle.getByteArray("inputStream"));
        }
        InputStream errorStream = null;
        if (dataBundle.containsKey("errorStream")) {
            errorStream = new ByteArrayInputStream(dataBundle.getByteArray("errorStream"));
        }
        JSONArray requestHeaders;
        Map<String, List<String>> requestHeaderList = new HashMap<>();
        if (dataBundle.containsKey("requestHeaders")) {
            try {
                requestHeaders = new JSONArray(dataBundle.getString("requestHeaders"));
                for (int x = 0; x < requestHeaders.length(); x++) {
                    JSONObject headerItem = requestHeaders.getJSONObject(x);
                    if (headerItem.has("key") && headerItem.has("values")) {
                        String key = headerItem.getString("key");
                        JSONArray values = headerItem.getJSONArray("values");
                        List<String> valuesList = new ArrayList<>();
                        for (int y = 0; y < values.length(); y++) {
                            valuesList.add(values.getString(x));
                        }
                        requestHeaderList.put(key, valuesList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Map<String, List<String>> responseHeaderList = new HashMap<>();
        if (dataBundle.containsKey("responseHeaders")) {
            JSONArray responseHeaders;
            try {
                responseHeaders = new JSONArray(dataBundle.getString("responseHeaders"));
                for (int x = 0; x < responseHeaders.length(); x++) {
                    JSONObject headerItem = responseHeaders.getJSONObject(x);
                    if (headerItem.has("key") && headerItem.has("values")) {
                        String key = headerItem.getString("key");
                        JSONArray values = headerItem.getJSONArray("values");
                        List<String> valuesList = new ArrayList<>();
                        for (int y = 0; y < values.length(); y++) {
                            valuesList.add(values.getString(x));
                        }
                        responseHeaderList.put(key, valuesList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        //Convert back to HTTPUrlConnection
        try {
            return new CustomHTTPURLConnection(new URL(url), followRedirects, useCaches, doInput, doOutput, usingProxy, responseCode, responseMessage, requestMethod, requestHeaderList, responseHeaderList, inputStream, errorStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
