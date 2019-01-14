package cn.fengwoo.sealsteward.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {

    public MyHelper(Context context){
        super(context,"myaccount.db",null,3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE account(_id INTEGER primary key autoincrement,phone VARCHAR(20),name VARCHAR(20),time INTEGER(100),fullName VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
