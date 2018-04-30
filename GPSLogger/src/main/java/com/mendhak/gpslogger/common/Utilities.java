/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.mendhak.gpslogger.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mendhak.gpslogger.senders.ftp.FtpHelper;

import net.kataplop.gpslogger.R;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Utilities
{

    private static final int LOGLEVEL = 5;
    private static ProgressDialog pd;

    private static void LogToDebugFile(String message)
    {
        if (AppSettings.isDebugToFile())
        {
            DebugLogger.Write(message);
        }
    }

    public static void LogInfo(String message)
    {
        if (LOGLEVEL >= 3)
        {
            Log.i("GPSLogger", message);
        }

        LogToDebugFile(message);

    }

    public static void LogError(String methodName, Exception ex)
    {
        Log.e("GPSLogger", methodName, ex);
        LogToDebugFile(methodName + ":" + ex.getMessage());
    }

    private static void LogError(String message)
    {
        Log.e("GPSLogger", message);
        LogToDebugFile(message);
    }

    @SuppressWarnings("unused")
    public static void LogDebug(String message)
    {
        if (LOGLEVEL >= 4)
        {
            Log.d("GPSLogger", message);
        }
        LogToDebugFile(message);
    }

    public static void LogDebug(String message, Exception ex)
    {
        if (LOGLEVEL >= 4)
        {
            Log.d("GPSLogger", message, ex);
        }
        LogToDebugFile(message + ":" + ex.getMessage());
    }

    public static void LogWarning(String message)
    {
        if (LOGLEVEL >= 2)
        {
            Log.w("GPSLogger", message);
        }
        LogToDebugFile(message);
    }

    @SuppressWarnings("unused")
    public static void LogVerbose(String message)
    {
        if (LOGLEVEL >= 5)
        {
            Log.v("GPSLogger", message);
        }
        LogToDebugFile(message);
    }

    /**
     * Gets user preferences, populates the AppSettings class.
     */
    public static void PopulateAppSettings(Context context)
    {

        Utilities.LogInfo("Getting preferences");
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        try {
            PackageInfo pInfo =  context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            AppSettings.setVersionName(pInfo.versionName);
        } catch(Exception e){
            AppSettings.setVersionName("unknown");
        }

        AppSettings.setUseImperial(prefs.getBoolean("useImperial", false));
        AppSettings.setLogToKml(prefs.getBoolean("log_kml", true));
        AppSettings.setLogToGpx(prefs.getBoolean("log_gpx", false));
        AppSettings.setLogToPlainText(prefs.getBoolean("log_plain_text", false));
        AppSettings.setLogToIgc(prefs.getBoolean("log_igc", false));
        AppSettings.setIgcPrivateKey(context.getString(R.string.igc_private_key));
        AppSettings.setUseModularView(prefs.getBoolean("modular_view_advancedP", false));
        AppSettings.setForceScreenOn(prefs.getBoolean("force_screen_on", false));

        AppSettings.setPilotName(prefs.getString("flying_pref_pilot_name", "-"));
        AppSettings.setGliderName(prefs.getString("flying_pref_glider_name", "-"));
        AppSettings.setGliderId(prefs.getString("flying_pref_glider_id", "-"));

        final int skylines_interval_default = Integer.parseInt(context.getString(R.string.skylines_interval_default));
        final int skylines_server_port_default = Integer.parseInt(context.getString(R.string.skylines_server_port_default));

        AppSettings.setLogToSkylines(prefs.getBoolean("log_skylines", false));
        try {
            AppSettings.setSkylinesInterval(Integer.parseInt(prefs.getString("skylines_interval",
                    Integer.toString(skylines_interval_default))));
        } catch (java.lang.NumberFormatException nfe){
            AppSettings.setSkylinesInterval(skylines_interval_default);
        }

        final int skylines_dist_default = Integer.parseInt(context.getString(R.string.skylines_distance_before_logging_default));
        try {
            AppSettings.setSkylinesMinimumDistanceInMeters(Integer.parseInt(prefs.getString("skylines_distance_before_logging",
                    Integer.toString(skylines_dist_default))));
        } catch (java.lang.NumberFormatException nfe){
            AppSettings.setSkylinesMinimumDistanceInMeters(skylines_dist_default);
        }

        AppSettings.setSkylinesKey(prefs.getString("skylines_key", ""));

        try {
            AppSettings.setSkylinesServerPort(Integer.parseInt(prefs.getString("skylines_server_port",
                    Integer.toString(skylines_server_port_default))));
        } catch (java.lang.NumberFormatException nf){
            AppSettings.setSkylinesServerPort(skylines_server_port_default);
        }
        AppSettings.setSkylinesServer(prefs.getString("skylines_server", ""));

        final int livetrack24_interval_default = Integer.parseInt(context.getString(R.string.livetrack24_interval_default));

        AppSettings.setLogToLivetrack24(prefs.getBoolean("log_livetrack24", false));
        try {
            AppSettings.setLivetrack24Interval(Integer.parseInt(prefs.getString("livetrack24_interval",
                    Integer.toString(livetrack24_interval_default))));
        } catch (Exception e){
            AppSettings.setLivetrack24Interval(livetrack24_interval_default);
        }

        final int livetrack24_dist_default = Integer.parseInt(context.getString(R.string.livetrack24_distance_before_logging_default));
        try {
            AppSettings.setLivetrack24MinimumDistanceInMeters(Integer.parseInt(prefs.getString("livetrack24_distance_before_logging",
                    Integer.toString(livetrack24_dist_default))));
        } catch (java.lang.NumberFormatException nfe){
            AppSettings.setLivetrack24MinimumDistanceInMeters(livetrack24_dist_default);
        }

        String livetrack24_preset = prefs.getString("livetrack24_presets", "");
        if (livetrack24_preset.equals("leonardo")){
            AppSettings.setLivetrack24ServerURL(context.getString(R.string.livetrack24_server_leonardo, ""));
        } else if (livetrack24_preset.equals("ffvl")){
            AppSettings.setLivetrack24ServerURL(context.getString(R.string.livetrack24_server_ffvl, ""));
        } else {
            AppSettings.setLivetrack24ServerURL(prefs.getString("livetrack24_server_url", context.getString(R.string.livetrack24_server_leonardo)));
        }

        AppSettings.setLivetrack24Username(prefs.getString("livetrack24_username", ""));
        AppSettings.setLivetrack24Password(prefs.getString("livetrack24_password", ""));
        AppSettings.setLogToOpenGTS(prefs.getBoolean("log_opengts", false));
        AppSettings.setShowInNotificationBar(prefs.getBoolean("show_notification", true));
        AppSettings.setPreferCellTower(prefs.getBoolean("prefer_celltower", false));

        String minimumDistanceString = prefs.getString("distance_before_logging", "0");
        if (minimumDistanceString != null && minimumDistanceString.length() > 0)
        {
            AppSettings.setMinimumDistanceInMeters(Integer.valueOf(minimumDistanceString));
        }
        else
        {
            AppSettings.setMinimumDistanceInMeters(0);
        }

        String minimumAccuracyString = prefs.getString("accuracy_before_logging", "0");

        if (minimumAccuracyString != null && minimumAccuracyString.length() > 0)
        {
            AppSettings.setMinimumAccuracyInMeters(Integer.valueOf(minimumAccuracyString));
        }
        else
        {
            AppSettings.setMinimumAccuracyInMeters(0);
        }

        if (AppSettings.shouldUseImperial())
        {
            AppSettings.setMinimumDistanceInMeters(Utilities.FeetToMeters(AppSettings.getMinimumDistanceInMeters()));

            AppSettings.setMinimumAccuracyInMeters(Utilities.FeetToMeters(AppSettings.getMinimumAccuracyInMeters()));
        }

        String minimumSecondsString = prefs.getString("time_before_logging", "60");

        if (minimumSecondsString != null && minimumSecondsString.length() > 0)
        {
            AppSettings.setMinimumSeconds(Integer.valueOf(minimumSecondsString));
        }
        else
        {
            AppSettings.setMinimumSeconds(60);
        }

        AppSettings.setKeepFix(prefs.getBoolean("keep_fix", false));

        String retryIntervalString = prefs.getString("retry_time", "60");

        if (retryIntervalString != null && retryIntervalString.length() > 0)
        {
            AppSettings.setRetryInterval(Integer.valueOf(retryIntervalString));
        }
        else
        {
             AppSettings.setRetryInterval(60);
        }

        AppSettings.setNewFileCreation(prefs.getString("new_file_creation", "onceaday"));

        if (AppSettings.getNewFileCreation().equals("onceaday"))
        {
            AppSettings.setNewFileOnceADay(true);
            AppSettings.setStaticFile(false);
        }
        else if(AppSettings.getNewFileCreation().equals("static"))
        {
            AppSettings.setStaticFile(true);
            AppSettings.setStaticFileName(prefs.getString("new_file_static_name","gpslogger"));
        }
        else
        {
            AppSettings.setNewFileOnceADay(false);
            AppSettings.setStaticFile(false);
        }

        AppSettings.setAutoSendEnabled(prefs.getBoolean("autosend_enabled", false));
        AppSettings.setAutoEmailEnabled(prefs.getBoolean("autoemail_enabled", false));

        if (Float.valueOf(prefs.getString("autosend_frequency", "0")) >= 8f)
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("autosend_frequency", "8");
            editor.commit();
        }

        AppSettings.setAutoSendDelay(Float.valueOf(prefs.getString("autosend_frequency", "0")));

        AppSettings.setSmtpServer(prefs.getString("smtp_server", ""));
        AppSettings.setSmtpPort(prefs.getString("smtp_port", "25"));
        AppSettings.setSmtpSsl(prefs.getBoolean("smtp_ssl", true));
        AppSettings.setSmtpFrom(prefs.getString("smtp_from", ""));
        AppSettings.setSmtpUsername(prefs.getString("smtp_username", ""));
        AppSettings.setSmtpPassword(prefs.getString("smtp_password", ""));
        AppSettings.setAutoEmailTargets(prefs.getString("autoemail_target", ""));
        AppSettings.setDebugToFile(prefs.getBoolean("debugtofile_advancedP", false));
        AppSettings.setShouldSendZipFile(prefs.getBoolean("autosend_sendzip", true));
        AppSettings.setOpenGTSEnabled(prefs.getBoolean("opengts_enabled", false));
        AppSettings.setAutoOpenGTSEnabled(prefs.getBoolean("autoopengts_enabled", false));
        AppSettings.setOpenGTSServer(prefs.getString("opengts_server", ""));
        AppSettings.setOpenGTSServerPort(prefs.getString("opengts_server_port", "80"));
        AppSettings.setOpenGTSServerCommunicationMethod(prefs.getString("opengts_server_communication_method", ""));
        AppSettings.setOpenGTSServerPath(prefs.getString("autoopengts_server_path", ""));
        AppSettings.setOpenGTSDeviceId(prefs.getString("opengts_device_id", ""));
        final int opengts_interval_default = Integer.parseInt(context.getString(R.string.opengts_interval_default));

        try {
            AppSettings.setOpenGTSInterval(Integer.parseInt(prefs.getString("opengts_interval",
                    Integer.toString(opengts_interval_default))));
        } catch (Exception e){
            AppSettings.setOpenGTSInterval(opengts_interval_default);
        }

        final int opengts_dist_default = Integer.parseInt(context.getString(R.string.opengts_distance_before_logging_default));
        try {
            AppSettings.setOpenGTSMinimumDistanceInMeters(Integer.parseInt(prefs.getString("opengts_distance_before_logging",
                    Integer.toString(opengts_dist_default))));
        } catch (java.lang.NumberFormatException nfe){
            AppSettings.setOpenGTSMinimumDistanceInMeters(opengts_dist_default);
        }

        AppSettings.setAutoFtpEnabled(prefs.getBoolean("autoftp_enabled",false));
        AppSettings.setFtpServerName(prefs.getString("autoftp_server",""));
        AppSettings.setFtpUsername(prefs.getString("autoftp_username",""));
        AppSettings.setFtpPassword(prefs.getString("autoftp_password",""));
        AppSettings.setFtpPort(Integer.valueOf(prefs.getString("autoftp_port", "21")));
        AppSettings.setFtpUseFtps(prefs.getBoolean("autoftp_useftps", false));
        AppSettings.setFtpProtocol(prefs.getString("autoftp_ssltls",""));
        AppSettings.setFtpImplicit(prefs.getBoolean("autoftp_implicit", false));
        AppSettings.setMsgTemplate(prefs.getString("msg_template",context.getString(R.string.sharing_template_default)));

        final int critical_battery_level_default = Integer.parseInt(context.getString(R.string.crit_battery_default));

        try {
            AppSettings.setCritBattLevel(Integer.parseInt(prefs.getString("crit_battery",
                    Integer.toString(critical_battery_level_default))));
        } catch (Exception e) {
            AppSettings.setCritBattLevel(critical_battery_level_default);
        }

        final int al_minbufsize_default = 1;

        try {
            AppSettings.setALMinBufSize(Integer.parseInt(prefs.getString("abst_logger_minbufsize",
                    Integer.toString(al_minbufsize_default))));
        } catch (Exception e) {
            AppSettings.setALMinBufSize(al_minbufsize_default);
        }

        final int al_maxbufsize_default = 32;

        try {
            AppSettings.setALMaxBufSize(Integer.parseInt(prefs.getString("abst_logger_maxbufsize",
                    Integer.toString(al_maxbufsize_default))));
        } catch (Exception e) {
            AppSettings.setALMaxBufSize(al_maxbufsize_default);
        }

        final int maxwaittime_default = 300;    // 5 mins

        try {
            AppSettings.setMaxWaitTime(Integer.parseInt(prefs.getString("maxwaittime",
                    Integer.toString(maxwaittime_default))));
        } catch (Exception e) {
            AppSettings.setMaxWaitTime(maxwaittime_default);
        }

        final int maxwaitcycles_default = 4;

        try {
            AppSettings.setMaxWaitCycles(Integer.parseInt(prefs.getString("maxwaitcycles",
                    Integer.toString(maxwaitcycles_default))));
        } catch (Exception e) {
            AppSettings.setMaxWaitCycles(maxwaitcycles_default);
        }

    }

    public static void ShowProgress(Context ctx, String title, String message)
    {
        if (ctx != null)
        {
            pd = new ProgressDialog(ctx, ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(100);
            pd.setIndeterminate(true);

            pd = ProgressDialog.show(ctx, title, message, true, true);
        }
    }

    public static void HideProgress()
    {
        if (pd != null)
        {
            pd.dismiss();
        }
    }

    /**
     * Displays a message box to the user with an OK button.
     *
     * @param title
     * @param message
     * @param className The calling class, such as GpsMainActivity.this or
     *                  mainActivity.
     */
    public static void MsgBox(String title, String message, Context className)
    {
        MsgBox(title, message, className, null);
    }

    /**
     * Displays a message box to the user with an OK button.
     *
     * @param title
     * @param message
     * @param className   The calling class, such as GpsMainActivity.this or
     *                    mainActivity.
     * @param msgCallback An object which implements IHasACallBack so that the click
     *                    event can call the callback method.
     */
    private static void MsgBox(String title, String message, Context className,
                               final IMessageBoxCallback msgCallback)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(className).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(className.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (msgCallback != null)
                        {
                            msgCallback.MessageBoxResult(which);
                        }
                    }
                });
        alertDialog.show();
    }

    /**
     * Converts seconds into friendly, understandable description of time.
     *
     * @param numberOfSeconds
     * @return
     */
    public static String GetDescriptiveTimeString(int numberOfSeconds,
                                                  Context context)
    {

        String descriptive;
        int hours;
        int minutes;
        int seconds;

        int remainingSeconds;

        // Special cases
        if (numberOfSeconds == 1)
        {
            return context.getString(R.string.time_onesecond);
        }

        if (numberOfSeconds == 30)
        {
            return context.getString(R.string.time_halfminute);
        }

        if (numberOfSeconds == 60)
        {
            return context.getString(R.string.time_oneminute);
        }

        if (numberOfSeconds == 900)
        {
            return context.getString(R.string.time_quarterhour);
        }

        if (numberOfSeconds == 1800)
        {
            return context.getString(R.string.time_halfhour);
        }

        if (numberOfSeconds == 3600)
        {
            return context.getString(R.string.time_onehour);
        }

        if (numberOfSeconds == 4800)
        {
            return context.getString(R.string.time_oneandhalfhours);
        }

        if (numberOfSeconds == 9000)
        {
            return context.getString(R.string.time_twoandhalfhours);
        }

        // For all other cases, calculate

        hours = numberOfSeconds / 3600;
        remainingSeconds = numberOfSeconds % 3600;
        minutes = remainingSeconds / 60;
        seconds = remainingSeconds % 60;

        // Every 5 hours and 2 minutes
        // XYZ-5*2*20*

        descriptive = context.getString(R.string.time_hms_format,
                String.valueOf(hours), String.valueOf(minutes),
                String.valueOf(seconds));

        return descriptive;

    }

    /**
     * Converts given bearing degrees into a rough cardinal direction that's
     * more understandable to humans.
     *
     * @param bearingDegrees
     * @return
     */
    public static String GetBearingDescription(float bearingDegrees,
                                               Context context)
    {

        String direction;
        String cardinal;

        if (bearingDegrees > 348.75 || bearingDegrees <= 11.25)
        {
            cardinal = context.getString(R.string.direction_north);
        }
        else if (bearingDegrees > 11.25 && bearingDegrees <= 33.75)
        {
            cardinal = context.getString(R.string.direction_northnortheast);
        }
        else if (bearingDegrees > 33.75 && bearingDegrees <= 56.25)
        {
            cardinal = context.getString(R.string.direction_northeast);
        }
        else if (bearingDegrees > 56.25 && bearingDegrees <= 78.75)
        {
            cardinal = context.getString(R.string.direction_eastnortheast);
        }
        else if (bearingDegrees > 78.75 && bearingDegrees <= 101.25)
        {
            cardinal = context.getString(R.string.direction_east);
        }
        else if (bearingDegrees > 101.25 && bearingDegrees <= 123.75)
        {
            cardinal = context.getString(R.string.direction_eastsoutheast);
        }
        else if (bearingDegrees > 123.75 && bearingDegrees <= 146.26)
        {
            cardinal = context.getString(R.string.direction_southeast);
        }
        else if (bearingDegrees > 146.25 && bearingDegrees <= 168.75)
        {
            cardinal = context.getString(R.string.direction_southsoutheast);
        }
        else if (bearingDegrees > 168.75 && bearingDegrees <= 191.25)
        {
            cardinal = context.getString(R.string.direction_south);
        }
        else if (bearingDegrees > 191.25 && bearingDegrees <= 213.75)
        {
            cardinal = context.getString(R.string.direction_southsouthwest);
        }
        else if (bearingDegrees > 213.75 && bearingDegrees <= 236.25)
        {
            cardinal = context.getString(R.string.direction_southwest);
        }
        else if (bearingDegrees > 236.25 && bearingDegrees <= 258.75)
        {
            cardinal = context.getString(R.string.direction_westsouthwest);
        }
        else if (bearingDegrees > 258.75 && bearingDegrees <= 281.25)
        {
            cardinal = context.getString(R.string.direction_west);
        }
        else if (bearingDegrees > 281.25 && bearingDegrees <= 303.75)
        {
            cardinal = context.getString(R.string.direction_westnorthwest);
        }
        else if (bearingDegrees > 303.75 && bearingDegrees <= 326.25)
        {
            cardinal = context.getString(R.string.direction_northwest);
        }
        else if (bearingDegrees > 326.25 && bearingDegrees <= 348.75)
        {
            cardinal = context.getString(R.string.direction_northnorthwest);
        }
        else
        {
            direction = context.getString(R.string.unknown_direction);
            return direction;
        }

        direction = context.getString(R.string.direction_roughly, cardinal);
        return direction;

    }

    /**
     * Makes string safe for writing to XML file. Removes lt and gt. Best used
     * when writing to file.
     *
     * @param desc
     * @return
     */
    public static String CleanDescription(String desc)
    {
        desc = desc.replace("<", "");
        desc = desc.replace(">", "");
        desc = desc.replace("&", "&amp;");
        desc = desc.replace("\"", "&quot;");

        return desc;
    }


    /**
     * Given a Date object, returns an ISO 8601 date time string in UTC.
     * Example: 2010-03-23T05:17:22Z but not 2010-03-23T05:17:22+04:00
     *
     * @param dateToFormat The Date object to format.
     * @return The ISO 8601 formatted string.
     */
    public static String GetIsoDateTime(Date dateToFormat)
    {
        // GPX specs say that time given should be in UTC, no local time.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(dateToFormat);
    }

    public static String GetReadableDateTime(Date dateToFormat)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        return sdf.format(dateToFormat);
    }

    /**
     * Converts given meters to feet.
     *
     * @param m
     * @return
     */
    public static int MetersToFeet(int m)
    {
        return (int) Math.round(m * 3.2808399);
    }


    /**
     * Converts given feet to meters
     *
     * @param f
     * @return
     */
    public static int FeetToMeters(int f)
    {
        return (int) Math.round(f / 3.2808399);
    }


    /**
     * Converts given meters to feet and rounds up.
     *
     * @param m
     * @return
     */
    public static int MetersToFeet(double m)
    {
        return MetersToFeet((int) m);
    }

    public static boolean IsEmailSetup()
    {
        return AppSettings.isAutoEmailEnabled()
                && AppSettings.getAutoEmailTargets().length() > 0
                && AppSettings.getSmtpServer().length() > 0
                && AppSettings.getSmtpPort().length() > 0
                && AppSettings.getSmtpUsername().length() > 0;

    }

    public static boolean IsOpenGTSSetup()
    {
        return AppSettings.isOpenGTSEnabled() &&
                AppSettings.getOpenGTSServer().length() > 0
                && AppSettings.getOpenGTSServerPort().length() > 0
                && AppSettings.getOpenGTSServerCommunicationMethod().length() > 0
                && AppSettings.getOpenGTSDeviceId().length() > 0;
    }


    public static boolean IsFtpSetup()
    {

        FtpHelper helper = new FtpHelper(null);

        return helper.ValidSettings(AppSettings.getFtpServerName(), AppSettings.getFtpUsername(),
                AppSettings.getFtpPassword(), AppSettings.getFtpPort(), AppSettings.FtpUseFtps(),
                AppSettings.getFtpProtocol(), AppSettings.FtpImplicit());
    }

    /**
     * Uses the Haversine formula to calculate the distance between to lat-long coordinates
     *
     * @param latitude1  The first point's latitude
     * @param longitude1 The first point's longitude
     * @param latitude2  The second point's latitude
     * @param longitude2 The second point's longitude
     * @return The distance between the two points in meters
     */
    public static double CalculateDistance(double latitude1, double longitude1, double latitude2, double longitude2)
    {
        /*
            Haversine formula:
            A = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
            C = 2.atan2(√a, √(1−a))
            D = R.c
            R = radius of earth, 6371 km.
            All angles are in radians
            */

        double deltaLatitude = Math.toRadians(Math.abs(latitude1 - latitude2));
        double deltaLongitude = Math.toRadians(Math.abs(longitude1 - longitude2));
        double latitude1Rad = Math.toRadians(latitude1);
        double latitude2Rad = Math.toRadians(latitude2);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) +
                (Math.cos(latitude1Rad) * Math.cos(latitude2Rad) * Math.pow(Math.sin(deltaLongitude / 2), 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c * 1000; //Distance in meters

    }


    /**
     * Checks if a string is null or empty
     *
     * @param text
     * @return
     */
    public static boolean IsNullOrEmpty(String text)
    {
        return text == null || text.length() == 0;
    }


    public static byte[] GetByteArrayFromInputStream(InputStream is)
    {

        try
        {
            int length;
            int size = 1024;
            byte[] buffer;

            if (is instanceof ByteArrayInputStream)
            {
                size = is.available();
                buffer = new byte[size];
                is.read(buffer, 0, size);
            }
            else
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                buffer = new byte[size];
                while ((length = is.read(buffer, 0, size)) != -1)
                {
                    outputStream.write(buffer, 0, length);
                }

                buffer = outputStream.toByteArray();
            }
            return buffer;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (Exception e)
            {
                Utilities.LogWarning("GetStringFromInputStream - could not close stream");
            }
        }

        return null;

    }

    /**
     * Loops through an input stream and converts it into a string, then closes the input stream
     *
     * @param is
     * @return
     */
    public static String GetStringFromInputStream(InputStream is)
    {
        String line;
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        try
        {
            while ((line = rd.readLine()) != null)
            {
                total.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (Exception e)
            {
                Utilities.LogWarning("GetStringFromInputStream - could not close stream");
            }
        }

        // Return full string
        return total.toString();
    }


    /**
     * Converts an input stream containing an XML response into an XML Document object
     *
     * @param stream
     * @return
     */
    public static Document GetDocumentFromInputStream(InputStream stream)
    {
        Document doc;

        try
        {
            DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
            xmlFactory.setNamespaceAware(true);
            DocumentBuilder builder = xmlFactory.newDocumentBuilder();
            doc = builder.parse(stream);
        }
        catch (Exception e)
        {
            doc = null;
        }

        return doc;
    }

    /**
     * Gets the GPSLogger-specific MIME type to use for a given filename/extension
     *
     * @param fileName
     * @return
     */
    public static String GetMimeTypeFromFileName(String fileName)
    {

        if (fileName == null || fileName.length() == 0)
        {
            return "";
        }


        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
        {
            return "application/octet-stream";
        }
        else
        {

            String extension = fileName.substring(pos + 1, fileName.length());


            if (extension.equalsIgnoreCase("gpx"))
            {
                return "application/gpx+xml";
            }
            else if (extension.equalsIgnoreCase("kml"))
            {
                return "application/vnd.google-earth.kml+xml";
            }
            else if (extension.equalsIgnoreCase("zip"))
            {
                return "application/zip";
            }
        }

        //Unknown mime type
        return "application/octet-stream";

    }

// which=1 - current location, which=-1 - previous location, which=0 - current or (of current is not defined) previous location
    public static String GetBodyFormatted(Context ctx, int which)
    {
        double latdd=0.0;
        double londd=0.0;
        String timestamp="";
        String bodyText=ctx.getString(R.string.no_location);
        boolean LocationDefined=false;

        boolean CurrValidLocation=Session.hasValidLocation();
        boolean PrevValidLocation=false;
        if(Session.getPreviousLocationInfo()!=null) PrevValidLocation=true;

        if( (which==1) && CurrValidLocation )
        {
            latdd=Session.getCurrentLatitude();
            londd=Session.getCurrentLongitude();
            timestamp=GetReadableDateTime(new Date(Session.getCurrentLocationInfo().getTime()));
            LocationDefined=true;
        }

        if( (which==-1) && PrevValidLocation )
        {
            latdd=Session.getPreviousLatitude();
            londd=Session.getPreviousLongitude();
            timestamp=GetReadableDateTime(new Date(Session.getPreviousLocationInfo().getTime()));
            LocationDefined=true;
        }

        if(which==0)
        {
            if(CurrValidLocation)
            {
                latdd=Session.getCurrentLatitude();
                londd=Session.getCurrentLongitude();
                timestamp=GetReadableDateTime(new Date(Session.getCurrentLocationInfo().getTime()));
                LocationDefined=true;
            }
            else if(PrevValidLocation)
                {
                    latdd=Session.getPreviousLatitude();
                    londd=Session.getPreviousLongitude();
                    timestamp=GetReadableDateTime(new Date(Session.getPreviousLocationInfo().getTime()));
                    LocationDefined=true;
                }
        }

        if(LocationDefined)
        {
            int latd=(int)latdd;
            int lond=(int)londd;
            double latmd=(latdd-latd)*60.0;
            double lonmd=(londd-lond)*60.0;
            int latm=(int)latmd;
            int lonm=(int)lonmd;
            double latsd=(latmd-latm)*60.0;
            double lonsd=(lonmd-lonm)*60.0;
            int lats=(int)latsd;
            int lons=(int)lonsd;

            /*
            String slatdd = String.format("%.6f",latdd);
            String slondd = String.format("%.6f",londd);
            String slatmd = String.format("%.4f",latmd);
            String slonmd = String.format("%.4f",lonmd);
            String slatsd = String.format("%.2f",latsd);
            String slonsd = String.format("%.2f",lonsd);
            */
            // Temporary fix to always use US locale in messages
            // TODO: add settings to force certain decimal separator or locale
            String slatdd = String.format(Locale.US,"%.6f",latdd);
            String slondd = String.format(Locale.US,"%.6f",londd);
            String slatmd = String.format(Locale.US,"%.4f",latmd);
            String slonmd = String.format(Locale.US,"%.4f",lonmd);
            String slatsd = String.format(Locale.US,"%.2f",latsd);
            String slonsd = String.format(Locale.US,"%.2f",lonsd);

            String slatd = String.format("%d",latd);
            String slond = String.format("%d",lond);
            String slatm = String.format("%d",latm);
            String slonm = String.format("%d",lonm);
            String slats = String.format("%d",lats);
            String slons = String.format("%d",lons);

            bodyText=AppSettings.getMsgTemplate();

            bodyText=bodyText.replace(ctx.getString(R.string.latitude_degree_decimal),slatdd);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_degree_decimal),slondd);
            bodyText=bodyText.replace(ctx.getString(R.string.latitude_degree),slatd);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_degree),slond);

            bodyText=bodyText.replace(ctx.getString(R.string.latitude_min_decimal),slatmd);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_min_decimal),slonmd);
            bodyText=bodyText.replace(ctx.getString(R.string.latitude_min),slatm);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_min),slonm);

            bodyText=bodyText.replace(ctx.getString(R.string.latitude_sec_decimal),slatsd);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_sec_decimal),slonsd);
            bodyText=bodyText.replace(ctx.getString(R.string.latitude_sec),slats);
            bodyText=bodyText.replace(ctx.getString(R.string.longitude_sec),slons);
            bodyText=bodyText.replace(ctx.getString(R.string.timestamp),timestamp);
            bodyText=bodyText.replace("\\n", System.getProperty("line.separator"));
        }

        return bodyText;
    }

}
