package com.kieronquinn.library.amazfitcommunication;

import android.content.Context;

import com.huami.watch.transport.DataBundle;

public class TransporterBLE extends Transporter {
    public static Transporter get(Context paramContext, String paramString) {
        return new TransporterBLE();
    }

    public void addDataListener(Transporter.DataListener paramDataListener) {
    }

    public boolean isAvailable() {
        return false;
    }

    public void removeDataListener(Transporter.DataListener paramDataListener) {
    }

    public void send(String paramString, DataBundle paramDataBundle, Transporter.DataSendResultCallback paramDataSendResultCallback) {
    }

    public void sendTo(String paramString1, String paramString2, DataBundle paramDataBundle, Transporter.DataSendResultCallback paramDataSendResultCallback) {
    }
}