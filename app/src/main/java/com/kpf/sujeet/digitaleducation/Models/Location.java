//package com.kpf.sujeet.digitaleducation.Models;
//
///*
//The contents of this file are subject to the Mozilla Public License
//Version 1.1 (the "License"); you may not use this file except in
//compliance with the License. You may obtain a copy of the License at
//http://www.mozilla.org/MPL/
//Software distributed under the License is distributed on an "AS IS"
//basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//License for the specific language governing rights and limitations
//under the License.
//The Original Code is collection of files collectively known as Open Camera.
//The Initial Developer of the Original Code is Almalence Inc.
//Portions created by Initial Developer are Copyright (C) 2013
//by Almalence Inc. All Rights Reserved.
// */
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
////        import com.disarm.sanna.pdm.Logger;
////        import com.disarm.sanna.pdm.Util.PrefUtils;
////        import com.nextgis.maplib.util.SettingsConstants;
//
//import com.kpf.sujeet.digitaleducation.Activity.RegisterLocation;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FilenameFilter;
//import java.io.InputStreamReader;
//import java.util.List;
//
////        import static com.disarm.sanna.pdm.DisarmConnect.MyService.phoneVal;
//
//public class MLocation {
//    public static LocationManager lm;
//
//    public static void subscribe(Context context) {
//        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//
//        boolean gps_enabled = false;
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        boolean network_enabled = false;
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        if (gps_enabled) {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
//        }
//
//        if (network_enabled) {
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
//        }
//    }
//
//    public static void unsubscribe() {
//        if (lm != null) {
//            if (ActivityCompat.checkSelfPermission(RegisterLocation.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            lm.removeUpdates(locationListenerGps);
//            lm.removeUpdates(locationListenerNetwork);
//        }
//    }
//
//    public static Location getLocation(Context context) {
//        if (lastGpsLocation != null) {
//            unsubscribe();
//            return lastGpsLocation;
//        } else if (lastNetworkLocation != null) {
//            unsubscribe();
//            return lastNetworkLocation;
//        } else {
//            unsubscribe();
//            return getLastChanceLocation(context);
//        }
//    }
//
//    private static Location getLastChanceLocation(Context ctx) {
//        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
//        List<String> providers = lm.getProviders(true);
//
//        // Loop over the array backwards, and if you get an accurate location,
//        // then break out the loop
//        Location l = null;
//
//        for (int i = providers.size() - 1; i >= 0; i--) {
//            if (ActivityCompat.checkSelfPermission(RegisterLocation.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return null;
//            }
//            l = lm.getLastKnownLocation(providers.get(i));
//            if (l != null)
//                break;
//        }
//        return l;
//    }
//
//    private static Location lastGpsLocation			= null;
//    private static Location lastNetworkLocation		= null;
//    private static LocationListener locationListenerGps		= new LocationListener()
//    {
//        public void onLocationChanged(Location location)
//        {
//            LocChangeExecute(location);
//            //lm.removeUpdates(this);
//            //lm.removeUpdates(locationListenerGps);
//
//            lastGpsLocation = location;
//        }
//
//        public void onProviderDisabled(String provider)
//        {
//        }
//
//        public void onProviderEnabled(String provider)
//        {
//        }
//
//        public void onStatusChanged(String provider,
//                                    int status, Bundle extras)
//        {
//        }
//    };
//
//    private static LocationListener locationListenerNetwork	= new LocationListener()
//    {
//        public void onLocationChanged(Location location)
//        {	LocChangeExecute(location);
//            //lm.removeUpdates(this);
//            //lm.removeUpdates(locationListenerGps);
//
//            lastNetworkLocation = location;
//        }
//
//        public void onProviderDisabled(String provider)
//        {
//        }
//
//        public void onProviderEnabled(String provider)
//        {
//        }
//
//        public void onStatusChanged(String provider,
//                                    int status, Bundle extras)
//        {
//        }
//    };
//
//    public static void LocChangeExecute(Location location){
//        Logger logger = new Logger();
//        String sCurrentLine;
//        BufferedReader br;
//        FileInputStream in = null;
//        File inFolder, logFile;
//        double latitude, longitude;
//        float speed;
//        Double distance = 0.0, bearing = 0.0;
//
//        speed = location.getSpeed();
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        // Calculate from GPS
//        inFolder = Environment.getExternalStoragePublicDirectory("DMS/Working/");
//        File[] foundFiles = inFolder.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//
//                return name.startsWith("MapDisarm_Log_" + phoneVal);
//            }
//        });
//        if(foundFiles != null && foundFiles.length > 0) {
//            logFile = new File(foundFiles[0].toString());
//            Log.v("LogFile:", foundFiles[0].toString());
//            try {
//                in = new FileInputStream(logFile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            String lastLine = "";
//
//            br = new BufferedReader(new InputStreamReader(in));
//            try {
//                while ((sCurrentLine = br.readLine()) != null) {
//                    lastLine = sCurrentLine;
//                }
//            } catch (Exception e) {
//            }
//            Log.v("LastLine:", lastLine);
//            Log.v("LastLine Location New:", "Lat:" + latitude + "Long:" + longitude);
//
//            // Calculate Distance between 2 GPS coordinates
//            distance = LocCalculate.calculateGPSDistance(lastLine.split(",")[0], lastLine.split(",")[1], latitude, longitude);
//            Log.v("Distance :", distance.toString());
//
//            if (distance.doubleValue() > 0.00) {
//                bearing = LocCalculate.bearing(lastLine.split(",")[0], lastLine.split(",")[1], latitude, longitude);
//
//                if (bearing > 90.00 && bearing < 270.00) {
//                    bearing = Math.abs(180.00 - bearing);
//                } else if (bearing > 270.00) {
//                    bearing = Math.abs(360.00 - bearing);
//                }
//                Log.v("Bearing:", bearing.toString());
//
//                if (latitude != 0.0 && longitude != 0.0 && bearing.doubleValue() > 0.00) {
//                    logger.addRecordToLog(String.valueOf(latitude) + "," + String.valueOf(longitude) + "," + String.valueOf(speed) + "," + String.valueOf(distance) + "," + String.valueOf(bearing));
//                }
//                Log.v("Speed :", Float.toString(speed));
//
//            }
//
//        }
//        else {
//            logger.addRecordToLog(String.valueOf(latitude) + "," + String.valueOf(longitude) + "," + String.valueOf(speed) + "," + String.valueOf(distance) + "," + String.valueOf(bearing));
//
//        }
//    }
//}