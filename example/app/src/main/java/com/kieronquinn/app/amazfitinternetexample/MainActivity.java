package com.kieronquinn.app.amazfitinternetexample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kieronquinn.library.amazfitcommunication.internet.LocalHTTPRequest;
import com.kieronquinn.library.amazfitcommunication.internet.LocalHTTPResponse;
import com.kieronquinn.library.amazfitcommunication.internet.LocalURLConnection;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setText(getString(R.string.loading));
        /*
         * Getting data from the internet (HTTP GET)
         */
        LocalURLConnection localURLConnection = new LocalURLConnection();
        try {
            localURLConnection.setUrl(new URL("http://quinny898.co.uk/test.txt"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        LocalHTTPRequest localHTTPRequest = new LocalHTTPRequest(this, localURLConnection, new LocalHTTPResponse() {
            @Override
            public void onResult(HttpURLConnection httpURLConnection) {
                try {
                    setText(IOUtils.toString(httpURLConnection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectError() {
                setText(getString(R.string.error));
            }

            @Override
            public void onTimeout() {
                setText(getString(R.string.timeout));
            }
        });
    }

    private void setText(final String text) {
        //Has to be run on the UI thread, a lot of receiver stuff isn't
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.textView);
                textView.setText(text);
            }
        });
    }

}
