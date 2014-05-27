package edu.neu.madcourse.bojunpan.finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;

public class Final_Project_Global {
	public static double Yaw = 0;
	public static double pitch = 0;
	public static int mode = 0;
	public static String choose_mode[] = {"Choose Country", "Choose State", "Choose City"};
	public static ArrayList<String> countryName = new ArrayList<String>();
	public static ArrayList<String> countryAddress = new ArrayList<String>();
	public static String choosed_country = "";
	public static String choosed_address = "";
	public static int game_status = -1;
	public static int game_target = 2;
	public static int hint_numb = 1;
	public static long hour = 0;
	public static long min = 0;
	public static long second = 0;
	public static long last_time = 0;
	public static long cur_time = 0;
	public static boolean list_is_touch = false;
//	public static String best = "99:99:99";
	public static boolean is_tutorial = true;
	public static boolean music_on_off = true;
	public static ArrayList<String> WorldRecord = new ArrayList<String>();
	public static void generate_random_place() {
		int num_maxn = countryAddress.size();
		int pos = (int)(Math.random() * num_maxn);
		choosed_address = countryAddress.get(pos);
		choosed_country = countryName.get(pos);
	}
	public static void clean() {
		countryAddress.clear();
		countryName.clear();
	}
	
}
