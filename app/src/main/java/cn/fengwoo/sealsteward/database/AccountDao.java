package cn.fengwoo.sealsteward.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.fengwoo.sealsteward.entity.HistoryInfo;

/**
 * 登录历史增删查改
 */
public class AccountDao {
    public final static String TABLE_NAME = "account";
    private MyHelper helper;
    private String phone;

    public AccountDao(Context context) {
        this.helper = new MyHelper(context);
    }

    public void insert(HistoryInfo info) {
        SQLiteDatabase database = helper.getWritableDatabase();
        //根据手机号判断去重
        String[] colum = {"phone"};
        String where = "phone" + "= ?";
        String[] whereValue = {info.getPhone()};
        Cursor cursor = database.query(TABLE_NAME, colum, where, whereValue, null, null, null);
        while (cursor.moveToNext()) {
            phone = cursor.getString(cursor.getColumnIndex("phone"));
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", info.getPhone());
        contentValues.put("name", info.getName());
        contentValues.put("headPortrait", info.getHeadPortrait());
        contentValues.put("time", info.getTime());

        if (!TextUtils.isEmpty(phone)) {
            database.update(TABLE_NAME, contentValues, "phone" + "=?", new String[]{phone});
        } else {
            database.insert(TABLE_NAME, null, contentValues);
        }
        database.close();

    }

    public int delete(String phone) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int count = database.delete(TABLE_NAME, "phone=?", new String[]{phone + ""});
        database.close();
        return count;
    }

    //查询
    public List<HistoryInfo> quaryAll() {
        SQLiteDatabase database = helper.getWritableDatabase();
        //查看表中所有数据
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        List<HistoryInfo> list = new ArrayList<>();
        //移动到下一行
        try {
            while (cursor.moveToNext()) {
                HistoryInfo historyInfo = new HistoryInfo();
                //返回列名对应的列索引值
                historyInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                historyInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                historyInfo.setHeadPortrait(cursor.getString(cursor.getColumnIndex("headPortrait")));
                historyInfo.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                list.add(historyInfo);
            }
            database.close();
            //关闭游标，释放资源
            cursor.close();

        } catch (Exception e) {
            Log.e("TAG", e + "错误错误错误!!!!!!!!!!!!!!");
        }
        return list;
    }

}
