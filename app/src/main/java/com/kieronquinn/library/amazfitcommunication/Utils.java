package com.kieronquinn.library.amazfitcommunication;

import android.os.Build;

/**
 * Created by Kieron on 08/04/2018.
 */

public class Utils {
    /**
     * Get whether the device is the watch
     *
     * @return true if watch, false otherwise
     */
    public static boolean isWatch() {
        return Build.BRAND.equals("Huami");
    }

}
