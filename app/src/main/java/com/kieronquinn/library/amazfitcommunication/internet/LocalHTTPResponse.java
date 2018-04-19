package com.kieronquinn.library.amazfitcommunication.internet;

import java.net.HttpURLConnection;

/**
 * Created by Kieron on 08/04/2018.
 */

public interface LocalHTTPResponse {

    /**
     * Called when the request gets a response. HttpURLConnection works the same as a normal one, but doesn't need
     * to use connect() and disconnect()
     * @param httpURLConnection The connection object
     */
    void onResult(HttpURLConnection httpURLConnection);

    /**
     * Called when the initial request is not responded to, meaning the user does not have the companion app installed
     * Should be used to show a response telling them so
     */
    void onConnectError();

    /**
     * Called when the request times out
     */
    void onTimeout();
}
