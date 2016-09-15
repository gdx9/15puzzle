package manor9421.com.a15game;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by manor on 8/12/16.
 */
public class UserPreferences {
    private static final String PREF_USE_NUM = "useNumbers";
    private static final String PREF_ROW_COUNT = "rowNum";
    private static final String PREF_COL_COUNT = "colNum";
    private static final String PREF_SEL_IMAGE = "selImage";

    //возвращает значение запроса, хранящееся в общих настройках
    public static Boolean getPrefUseNum(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_USE_NUM, true);//определяет возвращаемое значение по умолчанию, которое
        // должно возвращаться при отсутствии записи с ключом PREF_SEARCH_QUERY
    }

    public static int getPrefRowCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PREF_ROW_COUNT, 4);
    }

    public static int getPrefColCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PREF_COL_COUNT, 4);
    }

    public static String getPrefSelImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_SEL_IMAGE, "");
    }



    public static void setPrefUseNum(Context context,Boolean useNum) {
        PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(PREF_USE_NUM, useNum)
        .apply();
    }

    public static void setPrefRowCount(Context context,int rowCount) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putInt(PREF_ROW_COUNT, rowCount)
        .apply();
    }
    public static void setPrefColCount(Context context,int colCount) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(PREF_COL_COUNT, colCount)
            .apply();
    }
    public static void setPrefSelImage(Context context,String selImage) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEL_IMAGE, selImage)
            .apply();
    }
}
