package edu.neu.madcourse.bojunpan.communication;

public class Communication_Globals
{
    public static final String TAG = "GCM_Globals";
    public static final String GCM_SENDER_ID = "248102853395";
    public static final String BASE_URL = "https://android.googleapis.com/gcm/send";
    public static final String PREFS_NAME = "GCM_Communication";
    public static final String GCM_API_KEY = "AIzaSyCh6nwdjr33tm5iJtt9nhnncxDJ_oMTSgQ";
    public static final String BACKUP_USERNAME = "pbj7922";
    public static final String BACKUP_PASSWORD = "1312789";
    public static final int SIMPLE_NOTIFICATION = 22;
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks
    public static boolean is_client = false;
    public static boolean is_front = true;
}