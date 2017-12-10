package io.hefuyi.listener.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by hefuyi on 2016/11/3.
 */

public class FileUtil {

    private static final String HTTP_CACHE_DIR = "http";

    public static File getHttpCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(context.getExternalCacheDir(), HTTP_CACHE_DIR);
        }
        return new File(context.getCacheDir(), HTTP_CACHE_DIR);
    }

    public static String sizeFormatNum2String(long size) {
        String s = "";
        if(size>1024*1024)
            s=String.format("%.2f", (double)size/(1024*1024))+"M";
        else
            s=String.format("%.2f", (double)size/(1024))+"KB";
        return s;
    }

    public static String convertDuration(String duration) {
        duration = duration.substring(2);  // del. PT-symbols
        String H, M, S;
        // Get Hours:
        int indOfH = duration.indexOf("H");  // position of H-symbol
        if (indOfH > -1) {  // there is H-symbol
            H = duration.substring(0,indOfH);      // take number for hours
            duration = duration.substring(indOfH); // del. hours
            duration = duration.replace("H","");   // del. H-symbol
        } else {
            H = "";
        }
        // Get Minutes:
        int indOfM = duration.indexOf("M");  // position of M-symbol
        if (indOfM > -1) {  // there is M-symbol
            M = duration.substring(0,indOfM);      // take number for minutes
            duration = duration.substring(indOfM); // del. minutes
            duration = duration.replace("M","");   // del. M-symbol
            // If there was H-symbol and less than 10 minutes
            // then add left "0" to the minutes
            if (H.length() > 0 && M.length() == 1) {
                M = "0" + M;
            }
        } else {
            // If there was H-symbol then set "00" for the minutes
            // otherwise set "0"
            if (H.length() > 0) {
                M = "00";
            } else {
                M = "0";
            }
        }
        // Get Seconds:
        int indOfS = duration.indexOf("S");  // position of S-symbol
        if (indOfS > -1) {  // there is S-symbol
            S = duration.substring(0,indOfS);      // take number for seconds
            duration = duration.substring(indOfS); // del. seconds
            duration = duration.replace("S","");   // del. S-symbol
            if (S.length() == 1) {
                S = "0" + S;
            }
        } else {
            S = "00";
        }
        if (H.length() > 0) {
            return H + ":" +  M + ":" + S;
        } else {
            return M + ":" + S;
        }
    }

}
