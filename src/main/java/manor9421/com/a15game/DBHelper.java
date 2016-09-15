package manor9421.com.a15game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by manor on 8/10/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "15game";
    private static final int DB_VERSION = 1;
    private String recordsTableName = "records";//records

    DBHelper(Context c){
        super(c,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDB(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //delete tables????????

        //use on oncreate
    }

    private void updateDB(SQLiteDatabase db,int oldVersion,int newVersion) {
        if (oldVersion < 1) {

            db.execSQL("CREATE TABLE "+recordsTableName+"("
                    +"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +"moves INTEGER,"
                    +"time INTEGER,"
                    +"usedNums INTEGER,"
                    +"rows INTEGER,"
                    +"cols INTEGER,"
                    +"date DOUBLE);");
        }


    }

    public Cursor checkTop(SQLiteDatabase db){
        Cursor c = db.query(recordsTableName,
                new String[]{"moves","time","usedNums","rows","cols","date"},
                null, null, null, null,
                "moves ASC, time ASC",//ORDER BY NAME
                "50");
        return c;
    }

    public void saveGameResults(SQLiteDatabase db,int movesCount,long time,boolean usedNums,int rows,int cols){
        ContentValues newValues = new ContentValues();
        Date d = new Date();
        long timestamp = d.getTime();
        newValues.put("moves",movesCount);
        newValues.put("time", time);
        int nums = 0;
        if(usedNums)
            nums = 1;
        newValues.put("usedNums", nums);
        newValues.put("rows",rows);
        newValues.put("cols",cols);
        newValues.put("date", timestamp);///////////////////
        db.insert(recordsTableName, null, newValues);
    }
}
