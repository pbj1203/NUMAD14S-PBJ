package edu.neu.madcourse.bojunpan.twoplayergame;

public class TwoPlayerGame_Global {
	public static boolean Asyn_or_Syn = false;
	public static String Username = "";
	public static String Matched_name = "";
	public static final String GCM_SENDER_ID = "248102853395";
	public static final String BASE_URL = "https://android.googleapis.com/gcm/send";
	public static final String PREFS_NAME = "GCM_Communication";
	public static final String GCM_API_KEY = "AIzaSyBCt6wzlFq1XJFtp6SjCdmaVC_Z8bOWZ_Y";
	public static final String BACKUP_USERNAME = "pbj7922";
	public static final String BACKUP_PASSWORD = "1312789";
	public static final int SIMPLE_NOTIFICATION = 22;
	public static boolean user_turn = false;
	public static boolean is_new = true;
	public static String regid = "";
	public static final double probability[] = { 0.08, 0.11, 0.14, 0.17, 0.24,
			0.27, 0.3, 0.33, 0.4, 0.43, 0.46, 0.49, 0.52, 0.56, 0.62, 0.65,
			0.68, 0.71, 0.74, 0.77, 0.84, 0.88, 0.91, 0.94, 0.97, 1 };
	public static final String THREE_WORDS_STRING[] = { "cut", "cat", "run",
			"pat", "fat", "aka", "aku", "bat", "ran", "yes", "her", "his",
			"eye", "ear", "aye", "bar", "baa", "abo", "how", "now", "see",
			"saw", "try", "tie", "pie", "zoo", "car", "dba", "dna", "oak" };
}
