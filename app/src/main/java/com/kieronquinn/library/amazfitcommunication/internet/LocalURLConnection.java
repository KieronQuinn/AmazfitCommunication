package com.kieronquinn.library.amazfitcommunication.internet;

import com.huami.watch.transport.DataBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kieron on 08/04/2018.
 */

public class LocalURLConnection {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final String OPTIONS = "HEAD";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";

    private URL url = null;
    private boolean followRedirects = true;
    private String requestMethod = LocalURLConnection.GET;
    private boolean useCaches = true;
    private boolean doInput = true;
    private boolean doOutput = true;
    private Map<String, String> headers = null;
    private int timeout = -1;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public boolean isUseCaches() {
        return useCaches;
    }

    public void setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
    }

    public boolean isDoInput() {
        return doInput;
    }

    public void setDoInput(boolean doInput) {
        this.doInput = doInput;
    }

    public boolean isDoOutput() {
        return doOutput;
    }

    public void setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value){
        if(headers == null)headers = new HashMap<>();
        headers.put(key, value);
    }

    public DataBundle toDataBundle(){
        DataBundle dataBundle = new DataBundle();
        dataBundle.putString("url", getUrl().toString());
        dataBundle.putBoolean("followRedirects", isFollowRedirects());
        dataBundle.putString("requestMethod", getRequestMethod());
        dataBundle.putBoolean("useCaches", isUseCaches());
        dataBundle.putBoolean("doInput", isDoInput());
        dataBundle.putBoolean("doOutput", isDoOutput());
        JSONArray headers = new JSONArray();
        if(getHeaders() != null) {
            for (String key : getHeaders().keySet()) {
                String value = getHeaders().get(key);
                JSONObject item = new JSONObject();
                try {
                    item.put("key", key);
                    item.put("value", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                headers.put(item);
            }
        }
        dataBundle.putString("requestHeaders", headers.toString());
        return dataBundle;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
