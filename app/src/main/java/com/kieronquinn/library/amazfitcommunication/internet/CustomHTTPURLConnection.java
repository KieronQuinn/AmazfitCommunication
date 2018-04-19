package com.kieronquinn.library.amazfitcommunication.internet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Kieron on 10/04/2018.
 */

public class CustomHTTPURLConnection extends HttpURLConnection {

    private final boolean usingProxy;
    private final String requestMethod;
    private final Map<String, List<String>> requestProperties;
    private final Map<String, List<String>> responseHeaders;
    private final InputStream inputStream;
    private final InputStream errorStream;

    public CustomHTTPURLConnection(URL url, boolean instanceFollowRedirects, boolean useCaches, boolean doInput, boolean doOutput, boolean usingProxy, int responseCode, String responseMessage, String requestMethod, Map<String, List<String>> requestProperties, Map<String, List<String>> responseHeaders, InputStream inputStream, InputStream errorStream){
        super(url);
        this.url = url;
        this.instanceFollowRedirects = instanceFollowRedirects;
        this.useCaches = useCaches;
        this.doInput = doInput;
        this.doOutput = doOutput;
        this.usingProxy = usingProxy;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.requestMethod = requestMethod;
        this.requestProperties = requestProperties;
        this.responseHeaders = responseHeaders;
        this.inputStream = inputStream;
        this.errorStream = errorStream;
    }

    @Override
    public boolean usingProxy(){
        return usingProxy;
    }

    @Override
    public String getRequestMethod(){
        return requestMethod;
    }

    @Override
    public Map<String, List<String>> getRequestProperties(){
        return requestProperties;
    }

    @Override
    public String getRequestProperty(String key) {
        return requestProperties.get(key).get(0);
    }

    @Override
    public String getHeaderField(int n) {
        int x = 0;
        for (String key : requestProperties.keySet()) {
            if (x == n) {
                return requestProperties.get(key).get(0);
            }
            x++;
        }
        return null;
    }

    @Override
    public long getHeaderFieldDate(String name, long Default) {
        List<String> value = requestProperties.get(name);
        if (value == null) return Default;
        return Long.parseLong(value.get(0));
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public String getHeaderField(String name) {
        return requestProperties.get(name).get(0);
    }

    @Override
    public String getHeaderFieldKey(int n) {
        int x = 0;
        for (String key : requestProperties.keySet()) {
            if (x == n) {
                return key;
            }
            x++;
        }
        return null;
    }

    @Override
    public int getHeaderFieldInt(String name, int Default) {
        List<String> value = requestProperties.get(name);
        if (value == null) return Default;
        return Integer.parseInt(value.get(0));
    }

    @Override
    public long getHeaderFieldLong(String name, long Default) {
        List<String> value = requestProperties.get(name);
        if (value == null) return Default;
        return Long.parseLong(value.get(0));
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

}
