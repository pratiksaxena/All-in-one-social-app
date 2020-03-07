package messenger.people.messenger.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceKeeper {

    private static Context context;

    public static void setContext(Context context1) {
        context = context1;
    }

    public static void setOverlayShown(Boolean isOverlayShown) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOverlayShown", isOverlayShown);
        editor.apply();
    }

    public static boolean isOverlayShown() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("isOverlayShown", false);
    }

}
