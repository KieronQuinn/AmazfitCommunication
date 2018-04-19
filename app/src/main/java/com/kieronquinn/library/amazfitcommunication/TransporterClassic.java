package com.kieronquinn.library.amazfitcommunication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.LongSparseArray;

import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.DataTransportResult;
import com.huami.watch.transport.ITransportCallbackListener;
import com.huami.watch.transport.ITransportChannelListener;
import com.huami.watch.transport.ITransportDataListener;
import com.huami.watch.transport.ITransportDataService;
import com.huami.watch.transport.TransportDataItem;
import com.huami.watch.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TransporterClassic extends Transporter {
    public static final boolean DEBUG = true;
    private static Map<String, WeakReference<Transporter>> a = new HashMap<>();
    private static a b;
    private static Intent c;
    private WeakReference<Context> d;
    private String e;
    private String f;
    private ITransportDataService g;
    private boolean h;
    private boolean i;
    private final List<Transporter.b> j = new ArrayList();
    private final LongSparseArray<Transporter.c> k = new LongSparseArray();
    private final List<Transporter.a> l = new ArrayList();
    private final List<Transporter.ServiceConnectionListener> m = new ArrayList();
    private ITransportCallbackListener n = new StubOne(this);
    private ITransportDataListener o = new StubTwo(this);
    private ITransportChannelListener p = new StubThree(this);
    private Transporter.ServiceConnectionListener q = new StubFour(this);

    private TransporterClassic(Context paramContext, String paramString) {
        this.d = new WeakReference(paramContext);
        this.e = paramString;
        this.f = ("Transporter-Classic[" + paramString + "]");
    }

    private Intent a(PackageManager paramPackageManager, String paramString) {
        Intent localIntent = new Intent(paramString);
        List<ResolveInfo> packages = paramPackageManager.queryIntentServices(localIntent, 0);
        if ((packages == null) || (packages.size() == 0)) {
            return null;
        }
        Iterator iterator = packages.iterator();
        if (iterator.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) iterator.next();
            Intent intent = new Intent();
            intent.setPackage(resolveInfo.serviceInfo.packageName);
            intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
            return intent;
        }
        return null;
    }

    private void a(String arg7, String arg8, DataBundle arg9, c arg10) {
        LongSparseArray v2;
        TransportDataItem v1 = new TransportDataItem(this.e);
        v1.addAction(arg8);
        if (arg9 == null) {
            arg9 = new DataBundle();
        }

        v1.setData(arg9);
        if (this.isTransportServiceConnected()) {
            if (TransporterClassic.DEBUG) {
                Log.d(this.f, "Send : " + v1 + ", " + arg7, new Object[0]);
            }

            if (arg10 != null) {
                v2 = this.k;
                try {
                    this.k.put(v1.getCreateTime(), arg10);
                } catch (Throwable v0) {
                    throw v0;
                }
            }

            if (arg7 != null) {
                this.g.sendDataTo(arg7, v1);
                return;
            }

            try {
                this.g.sendData(v1);
            } catch (Exception v0_1) {
                v0_1.printStackTrace();
                v2 = this.k;
                try {
                    this.k.remove(v1.getCreateTime());
                } catch (Throwable v0) {
                    throw v0;
                }
            }
        }else{
            if (TransporterClassic.DEBUG) {
                Log.w(this.f, "Send : " + v1 + ", without service connected!!", new Object[0]);
            }
        }



        if (arg10 != null) {
            ((DataSendResultCallback) arg10).onResultBack(new DataTransportResult(v1, 4));
        }
    }

    public static Transporter get(Context arg4, String arg5) {
        TransporterClassic v0_1 = null;
        StubOne v1 = null;
        Object v0 = TransporterClassic.a.get(arg5);
        v0 = v0 != null ? ((WeakReference) v0).get() : v1;
        if (v0 == null) {
            v0_1 = new TransporterClassic(arg4, arg5);
            TransporterClassic.a.put(arg5, new WeakReference(v0_1));
        }else{
            v0_1 = new TransporterClassic(arg4, arg5);
            TransporterClassic.a.put(arg5, new WeakReference(v0_1));
        }

        if (TransporterClassic.b == null) {
            TransporterClassic.b = new a(v1);
            IntentFilter v1_1 = new IntentFilter();
            v1_1.addAction("com.huami.watch.transport.DataTransportService.Start");
            arg4.getApplicationContext().registerReceiver(TransporterClassic.b, v1_1);
        }

        return ((Transporter) v0_1);
    }

    public void addChannelListener(Transporter.ChannelListener paramChannelListener) {
        synchronized (this.l) {
            if (!this.l.contains(paramChannelListener)) {
                this.l.add(paramChannelListener);
            }
            return;
        }
    }

    public void addDataListener(Transporter.DataListener paramDataListener) {
        synchronized (this.j) {
            if (!this.j.contains(paramDataListener)) {
                this.j.add(paramDataListener);
            }
            return;
        }
    }

    public void addServiceConnectionListener(Transporter.ServiceConnectionListener paramServiceConnectionListener) {

        synchronized (this.m) {
            if (!this.m.contains(paramServiceConnectionListener)) {
                this.m.add(paramServiceConnectionListener);
                Log.d("AmazfitTest", "adding service connection listener");
            }
            return;
        }
    }

    public ServiceConnection localServiceConnection;

    public void connectTransportService() {
        if (TransporterClassic.DEBUG) {
            Log.d(this.f, "Connect TransportService, Now Is Connected : " + this.i + ", Is Connecting : " + this.h, new Object[0]);
        }

        if (!this.i && !this.h) {
            Object v0 = this.d.get();
            if (v0 == null) {
                Log.w(this.f, "Context is NULL!!", new Object[0]);
            } else {
                if (TransporterClassic.c == null) {
                    TransporterClassic.c = this.a(((Context) v0).getPackageManager(), "com.huami.watch.transport.DataTransportService");
                    if (TransporterClassic.c == null) {
                        Log.e(this.f, "DataTransportService Not Found!!", new Object[0]);
                        return;
                    }
                }

                this.h = true;
                localServiceConnection = new ServiceConnection() {
                    public void onServiceConnected(ComponentName arg5, IBinder arg6) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("component", arg5);
                        Log.d(TransporterClassic.b(TransporterClassic.this), "OnServiceConnected!!", new Object[0]);
                        TransporterClassic.q(TransporterClassic.this).onServiceConnected(bundle);
                        TransporterClassic.a(TransporterClassic.this, com.huami.watch.transport.ITransportDataService.Stub.asInterface(arg6));
                        TransporterClassic.a(TransporterClassic.this, false);
                        TransporterClassic.b(TransporterClassic.this, true);
                        TransporterClassic.g(TransporterClassic.this).registersendCallbackListener(TransporterClassic.f(TransporterClassic.this));
                        TransporterClassic.g(TransporterClassic.this).registerChannelListener(TransporterClassic.h(TransporterClassic.this), TransporterClassic.i(TransporterClassic.this));
                        TransporterClassic.g(TransporterClassic.this).registerDataListener(TransporterClassic.h(TransporterClassic.this), TransporterClassic.j(TransporterClassic.this));
                    }

                    public void onServiceDisconnected(ComponentName arg5) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("component", arg5);
                        TransporterClassic.q(TransporterClassic.this).onServiceDisconnected(null);
                        Log.d(TransporterClassic.b(TransporterClassic.this), "OnServiceDisconnected!!", new Object[0]);
                        if (TransporterClassic.g(TransporterClassic.this) != null) {
                            TransporterClassic.g(TransporterClassic.this).unregistersendCallbackListener(TransporterClassic.f(TransporterClassic.this));
                            TransporterClassic.g(TransporterClassic.this).unregisterChannelListener(TransporterClassic.h(TransporterClassic.this));
                            TransporterClassic.g(TransporterClassic.this).unregisterDataListener(TransporterClassic.h(TransporterClassic.this));
                            TransporterClassic.a(TransporterClassic.this, null);
                            TransporterClassic.a(TransporterClassic.this, false);
                            TransporterClassic.b(TransporterClassic.this, false);
                            Log.w(TransporterClassic.b(TransporterClassic.this), "Re-Connect To TransportService!!", new Object[0]);
                            TransporterClassic.this.connectTransportService();
                        }
                    }
                };
                boolean v0_1 = ((Context) v0).bindService(TransporterClassic.c, localServiceConnection, Context.BIND_AUTO_CREATE);
                if (!TransporterClassic.DEBUG) {
                    return;
                }

                String v1 = this.f;
                StringBuilder v2 = new StringBuilder().append("Connect TransportService : ");
                String v0_2 = v0_1 ? "Success" : "Failed";
                Log.d(v1, v2.append(v0_2).toString(), new Object[0]);
            }
        }
    }

    static String h(TransporterClassic arg1) {
        return arg1.e;
    }

    static ITransportChannelListener i(TransporterClassic arg1) {
        return arg1.p;
    }

    static ITransportCallbackListener f(TransporterClassic arg1) {
        return arg1.n;
    }

    static boolean a(TransporterClassic arg0, boolean arg1) {
        arg0.h = arg1;
        return arg1;
    }

    static ServiceConnectionListener q(TransporterClassic arg0){
        return arg0.q;
    }

    static boolean b(TransporterClassic arg0, boolean arg1) {
        arg0.i = arg1;
        return arg1;
    }

    static ITransportDataService a(TransporterClassic arg0, ITransportDataService arg1) {
        arg0.g = arg1;
        return arg1;
    }

    static ITransportDataService g(TransporterClassic arg1) {
        return arg1.g;
    }

    static ITransportDataListener j(TransporterClassic arg1) {
        return arg1.o;
    }

    public void disconnectTransportService() {
        if (DEBUG) {
            Log.d(this.f, "Disconnect TransportService, Now Is Connected : " + this.i + ", Is Connecting : " + this.h, new Object[0]);
        }
        if (this.i) {
            this.h = false;
        }
        this.g.unregistersendCallbackListener(this.n);
        Object v0 = this.d.get();
        if(localServiceConnection != null && v0 != null){
            try {
                ((Context) v0).unbindService(localServiceConnection);
            }catch (Exception e){

            }
        }
        return;
    }

    public boolean isAvailable() {
        return isTransportServiceConnected();
    }

    public boolean isTransportServiceConnected() {
        if (DEBUG) {
            Log.d(this.f, "TransportService Now Is Connected : " + this.i, new Object[0]);
        }
        return this.i;
    }

    public void removeChannelListener(Transporter.ChannelListener paramChannelListener) {
        synchronized (this.l) {
            if (this.l.contains(paramChannelListener)) {
                this.l.remove(paramChannelListener);
            }
            return;
        }
    }

    public void removeAllChannelListeners(){
        this.l.clear();
    }

    public void removeDataListener(Transporter.DataListener paramDataListener) {
        synchronized (this.j) {
            if (this.j.contains(paramDataListener)) {
                this.j.remove(paramDataListener);
            }
            return;
        }
    }

    public void removeAllDataListeners(){
        this.j.clear();
    }

    public void removeServiceConnectionListener(Transporter.ServiceConnectionListener paramServiceConnectionListener) {
        synchronized (this.m) {
            if (this.m.contains(paramServiceConnectionListener)) {
                this.m.remove(paramServiceConnectionListener);
            }
            return;
        }
    }

    public void removeAllServiceConnectionListeners(){
        this.m.clear();
    }

    public void send(String paramString, DataBundle paramDataBundle, Transporter.DataSendResultCallback paramDataSendResultCallback) {
        a(null, paramString, paramDataBundle, paramDataSendResultCallback);
    }

    public void sendTo(String paramString1, String paramString2, DataBundle paramDataBundle, Transporter.DataSendResultCallback paramDataSendResultCallback) {
        a(paramString1, paramString2, paramDataBundle, paramDataSendResultCallback);
    }

    static class a extends BroadcastReceiver {
        a(StubOne arg1) {

        }

        private a() {
            super();
        }

        public void onReceive(Context arg4, Intent arg5) {
            if (TransporterClassic.DEBUG) {
                Log.d("Transporter-Classic", "On TransportService Start Received!!", new Object[0]);
            }

            Iterator v1 = TransporterClassic.a().values().iterator();
            while (v1.hasNext()) {
                Object v0 = v1.next();
                if (v0 == null) {
                    continue;
                }

                v0 = ((WeakReference) v0).get();
                if (v0 == null) {
                    continue;
                }

                ((Transporter) v0).connectTransportService();
            }
        }
    }

    static Map a() {
        return TransporterClassic.a;
    }

    static LongSparseArray a(TransporterClassic arg1) {
        return arg1.k;
    }

    static String b(TransporterClassic arg1) {
        return arg1.f;
    }

    class StubOne extends ITransportCallbackListener.Stub {
        private final TransporterClassic a;

        StubOne(TransporterClassic arg1) {
            super();
            this.a = arg1;
        }

        public void onResultBack(DataTransportResult arg6) {
            Object v0_1;
            long v2 = arg6.getDataItem().getCreateTime();
            LongSparseArray v1 = TransporterClassic.a(this.a);
            try {
                v0_1 = TransporterClassic.a(this.a).get(v2);
                TransporterClassic.a(this.a).remove(v2);
                if (v0_1 == null) {
                    return;
                }
            } catch (Throwable v0) {
                throw v0;
            }
            if (TransporterClassic.DEBUG) {
                Log.d(TransporterClassic.b(this.a), "OnResultBack : " + arg6 + ", " + v0_1, new Object[0]);
            }
            ((DataSendResultCallback) v0_1).onResultBack(arg6);
        }
    }

    class StubTwo extends ITransportDataListener.Stub {
        private final TransporterClassic a;

        StubTwo(TransporterClassic arg1) {
            super();
            this.a = arg1;
        }

        public void onDataReceived(TransportDataItem arg7) {
            String v0 = arg7.getAction();
            if (TransporterClassic.DEBUG) {
                Log.d(TransporterClassic.b(this.a), "OnDataReceived Action : " + v0, new Object[0]);
            }
            try {
                if (TransporterClassic.c(this.a).size() <= 0) {
                    return;
                }
                Iterator v2 = TransporterClassic.c(this.a).iterator();
                while (true) {
                    if (!v2.hasNext()) {
                        return;
                    }

                    Object v0_2 = v2.next();
                    try {
                        ((DataListener) v0_2).onDataReceived(arg7);
                    } catch (Exception v0_3) {
                        try {
                            v0_3.printStackTrace();
                            Log.e(TransporterClassic.b(this.a), "OnDataReceived Err", ((Throwable) v0_3), new Object[0]);
                            //Analytics.exception(new Exception("OnDataReceived Err", ((Throwable) v0_3)));
                            continue;
                        } catch (Throwable v0_1) {
                            break;
                        }
                    }
                }
            } catch (Throwable v0_1) {
                throw v0_1;
            }
        }
    }

    static List c(TransporterClassic arg1) {
        return arg1.j;
    }

    class StubThree extends ITransportChannelListener.Stub {
        private final TransporterClassic a;

        StubThree(TransporterClassic arg1) {
            super();
            this.a = arg1;
        }

        public void onChannelChanged(boolean arg7) {
            if (TransporterClassic.DEBUG) {
                Log.d(TransporterClassic.b(this.a), "OnChannelChanged Available : " + arg7, new Object[0]);
            }

            List v1 = TransporterClassic.d(this.a);
            try {
                if (TransporterClassic.d(this.a).size() <= 0) {
                    return;
                }

                Iterator v2 = TransporterClassic.d(this.a).iterator();
                while (true) {
                    if (!v2.hasNext()) {
                        return;
                    }

                    Object v0_1 = v2.next();
                    try {
                        ((ChannelListener) v0_1).onChannelChanged(arg7);
                    } catch (Exception v0_2) {
                        try {
                            v0_2.printStackTrace();
                            Log.e(TransporterClassic.b(this.a), "OnChannelChanged Err", ((Throwable) v0_2), new Object[0]);
                            //Analytics.exception(new Exception("OnChannelChanged Err", ((Throwable) v0_2)));
                        } catch (Throwable v0) {
                            break;
                        }
                    }
                }
            } catch (Throwable v0) {
                throw v0;
            }
        }
    }

    static List d(TransporterClassic arg1) {
        return arg1.l;
    }

    class StubFour implements ServiceConnectionListener {
        private final TransporterClassic a;

        StubFour(TransporterClassic arg1) {
            super();
            this.a = arg1;
        }

        public void onServiceConnected(Bundle arg4) {
            Log.d("AmazfitTestInternal", "onServiceConnected");
            if (TransporterClassic.DEBUG) {
                Log.d(TransporterClassic.b(this.a), "Transport Service Connected : " + arg4, new Object[0]);
            }
            List v1 = TransporterClassic.e(this.a);
            try {
                if (TransporterClassic.e(this.a).size() > 0) {
                    Iterator<ServiceConnectionListener> v2 = TransporterClassic.e(this.a).iterator();
                    while (v2.hasNext()) {
                        v2.next().onServiceConnected(arg4);
                    }
                }
                return;
            } catch (Throwable v0) {
                throw v0;
            }
        }

        public void onServiceConnectionFailed(ConnectionResult arg4) {
            if (TransporterClassic.DEBUG) {
                Log.w(TransporterClassic.b(this.a), "Transport Service Connect Failed : " + arg4, new Object[0]);
            }

            List v1 = TransporterClassic.e(this.a);
            try {
                if (TransporterClassic.e(this.a).size() > 0) {
                    Iterator<Transporter.ServiceConnectionListener> v2 = TransporterClassic.e(this.a).iterator();
                    while (v2.hasNext()) {
                        v2.next().onServiceConnectionFailed(arg4);
                    }
                }
            } catch (Throwable v0) {
                throw v0;
            }
        }

        public void onServiceDisconnected(ConnectionResult arg4) {
            if (TransporterClassic.DEBUG) {
                Log.d(TransporterClassic.b(this.a), "Transport Service Disconnected : " + arg4, new Object[0]);
            }

            List v1 = TransporterClassic.e(this.a);
            try {
                if (TransporterClassic.e(this.a).size() > 0) {
                    Iterator<Transporter.ServiceConnectionListener> v2 = TransporterClassic.e(this.a).iterator();
                    while (v2.hasNext()) {
                        v2.next().onServiceDisconnected(arg4);
                    }
                }
            } catch (Throwable v0) {
                throw v0;
            }
        }
    }

    static List<Transporter.ServiceConnectionListener> e(TransporterClassic arg1) {
        return arg1.m;
    }
}
