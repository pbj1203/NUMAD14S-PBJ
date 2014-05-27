/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.bojunpan.wordgame;
import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.R.xml;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class WordGame_Prefs extends PreferenceActivity {
   // Option names and default values
   private static final String OPT_MUSIC = "music";
   private static final boolean OPT_MUSIC_DEF = true;
   private static final String OPT_ROW = "row";
   private static final String OPT_COL = "column";
   

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.letris_settings);
   }
   /** Get the current value of the music option */
   
   public static boolean getMusic(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
   }
   
   /** Get the current value of the row option */
   
   public static String getRow(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(OPT_ROW, "5");
   }

   public static String getColumn(Context context) {
	      return PreferenceManager.getDefaultSharedPreferences(context)
	            .getString(OPT_COL, "7");
	   }
}
