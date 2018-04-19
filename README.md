# Amazfit Communication Library
The Amazfit Communication Library is a reverse engineered and modified version of the Huami "Transformer" class (and its subclass "TransformerClassic"), along with its dependencies. This allows both sending and receiving data on both the phone and the watch, using the same library.

Bascially, this allows apps on the watch to access data via a companion app on the phone. To make this easier, this library comes hand in hand with the "AmazfitInternetCommunication" app, which can be installed on the phone and allows apps on the watch to access the internet and perform simple requests, with data in response. 

However, you can use this library with your own companion app on the phone to access any data the phone app has access to, for example a companion app on the phone with access to calendar appointments would allow a watch app to display events on a calendar, on the watch.

## Compiling

This repository contains the source code for the **modified** Huami code, as well as the original classes created to make it easier to use. The original, unmodified classes, can be found in Transporter.jar, in the libs folder.

If you wish to build the library to a **jar** file, use the "build", then "deleteJar", then "createJar" Gradle tasks. Note that this does not include the original Huami classes, these must manually be copied from the Transporter.jar after compilation into the AmazfitCommunication.jar file. Downloading the release jar from this repository does not require this.

## Usage

There are two components to this library. The first is the **LocalHTTPRequest** option, which relies on the AmazfitInternetCommunication app on the phone, and thus doesn't require you to build your own companion app. It provides a limited way of accessing the internet, as follows:

### Internet

```java
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
```

This code sends a request to "http://quinny898.co.uk/test.txt", and calls the setText method with its content, using the [Commons-IO](https://commons.apache.org/proper/commons-io/dependency-info.html) library (which must be in your build.gradle)

Note that the setText method (or whatever method you wish) is called **off the main thread**, so you must handle this yourself

**LocalURLConnection** behaves like a URLConnection, and can be set up in the same way. A **HttpURLConnection** is returned, which is custom, and does not respond to .connect() or .disconnect(). A full example can be found in the [example project](https://github.com/KieronQuinn/AmazfitCommunication/blob/master/example/app/src/main/java/com/kieronquinn/app/amazfitinternetexample/MainActivity.java)

### Sending & Receiving Custom Actions

**Sending**
```java
//Create the transporter **WARNING** The second parameter MUST be the same on both your watch and phone companion apps!
//Please change the module name to something unique, but keep it the same for both apps!
transporter = Transporter.get(this, "example_module");
//Add a channel listener to listen for ready event
transporter.addChannelListener(new Transporter.ChannelListener() {
    @Override
    public void onChannelChanged(boolean ready) {
        //Transporter is ready if ready is true, send an action now. This will **NOT** work before the transporter is ready!
        //You can change the action to whatever you want, there's also an option for a data bundle to be added (see below)
        if(ready)transporter.send("hello_world!");
    }
});
transporter.connectTransportService();
```

**Receiving**

```java
transporter.addDataListener(new Transporter.DataListener() {
    @Override
    public void onDataReceived(TransportDataItem transportDataItem) {
        Log.d("TransporterExample", "Item received action: " + transportDataItem.getAction());
        if(transportDataItem.getAction().equals("hello_world")) {
            DataBundle receivedData = transportDataItem.getData();
            //Do whatever with your action & data. You can send data back in the same way using the same transporter
        }
    }
});
```

**Please remember to disconnect from the service and remove any listeners when your app is closed**

The same code works on both phone and watch, but obviously don't use identical code on both or it would just send the same actions to each other. For help making a simple companion app, see the source code for the AmazfitInternetCompanion app.

**Full usage**
To see how to use DataBundles, callbacks and more, see the [example folder](https://github.com/KieronQuinn/AmazfitCommunication/tree/master/example) with an example project in it.
