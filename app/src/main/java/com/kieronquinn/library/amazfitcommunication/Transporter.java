package com.kieronquinn.library.amazfitcommunication;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.DataTransportResult;
import com.huami.watch.transport.TransportDataItem;

public abstract class Transporter {
    public static Transporter get(Context paramContext, String paramString) {
        return TransporterClassic.get(paramContext, paramString);
    }

    public static Transporter getBLE(Context paramContext, String paramString) {
        return TransporterBLE.get(paramContext, paramString);
    }

    public void addChannelListener(ChannelListener paramChannelListener) {
    }

    public abstract void addDataListener(DataListener paramDataListener);

    public void addServiceConnectionListener(ServiceConnectionListener paramServiceConnectionListener) {
    }

    public void connectTransportService() {
    }

    public void disconnectTransportService() {
    }

    public abstract boolean isAvailable();

    public boolean isTransportServiceConnected() {
        return true;
    }

    public void removeChannelListener(ChannelListener paramChannelListener) {
    }

    public abstract void removeDataListener(DataListener paramDataListener);

    public void removeServiceConnectionListener(ServiceConnectionListener paramServiceConnectionListener) {
    }

    public void send(String paramString) {
        send(paramString, null, null);
    }

    public void send(String paramString, DataBundle paramDataBundle) {
        send(paramString, paramDataBundle, null);
    }

    public abstract void send(String paramString, DataBundle paramDataBundle, DataSendResultCallback paramDataSendResultCallback);

    public void send(String paramString, DataSendResultCallback paramDataSendResultCallback) {
        send(paramString, null, paramDataSendResultCallback);
    }

    public abstract void sendTo(String paramString1, String paramString2, DataBundle paramDataBundle, DataSendResultCallback paramDataSendResultCallback);

    public interface ChannelListener extends a {
        void onChannelChanged(boolean isAvailable);
    }

    public static class ConnectionResult
            implements Parcelable {
        public static final Parcelable.Creator<ConnectionResult> CREATOR = new ParcelableCreator();
        public static final int R_SERVICE_AUTH_FALIED = 2;
        public static final int R_SERVICE_CONNECTING = 4;
        public static final int R_SERVICE_DISCONNECTED = 3;
        public static final int R_SERVICE_UNAVAILABLE = 1;
        public static final int R_SUCCESS = 0;
        private int a;

        static final class ParcelableCreator implements Parcelable.Creator {
            ParcelableCreator() {
                super();
            }

            public ConnectionResult a(Parcel arg3) {
                return new ConnectionResult(arg3);
            }

            public ConnectionResult[] a(int arg2) {
                return new ConnectionResult[arg2];
            }

            public Object createFromParcel(Parcel arg2) {
                return this.a(arg2);
            }

            public Object[] newArray(int arg2) {
                return this.a(arg2);
            }
        }

        private ConnectionResult(Parcel paramParcel) {
            this.a = paramParcel.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public int getResultCode() {
            return this.a;
        }

        public String toString() {
            switch (this.a) {
                default:
                    return "service authentication failure";
                case 0:
                    return "success";
                case 1:
                    return "service is unavailable";
            }
        }

        public void writeToParcel(Parcel paramParcel, int paramInt) {
            paramParcel.writeInt(this.a);
        }
    }

    public interface DataListener extends b {
        void onDataReceived(TransportDataItem paramTransportDataItem);
    }

    public interface DataSendResultCallback extends Transporter.c {
        void onResultBack(DataTransportResult paramDataTransportResult);
    }

    public interface ServiceConnectionListener {
        void onServiceConnected(Bundle paramBundle);

        void onServiceConnectionFailed(Transporter.ConnectionResult paramConnectionResult);

        void onServiceDisconnected(Transporter.ConnectionResult paramConnectionResult);
    }

    interface a {
    }

    interface b {
    }

    interface c {
    }
}
