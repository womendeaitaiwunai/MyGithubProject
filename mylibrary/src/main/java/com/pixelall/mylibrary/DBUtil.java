package com.pixelall.mylibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lxl on 2016/8/31.
 * 数据库封装类
 */
public class DBUtil {
    private static String dbname = "PhotoStudioDB";
    private String dbShare="DbShare";
    private int dbNum=0;
    private static int version = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private DbHelper dbHelper;
    private Context context;

    public  void insertDate(Class<?> tClass){
        version=sharedPreferences.getInt("dbVersion",1);
        edit.putInt("dbVersion",version+1);
        edit.commit();

        dbNum=sharedPreferences.getInt("DbNum",0);
        edit.putInt("DbNum",dbNum+1);
        edit.commit();

        edit.putString("dbNum"+(dbNum+1),tClass.getName());
        edit.commit();

        dbHelper=new DbHelper(context,version);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        try {
            db.execSQL(getInsertSQL(tClass),getSqlValue(tClass));
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception ex) {
            db.close();
            ex.printStackTrace();
        } finally {
            db.close();
        }
    }

    public String getInsertSQL(Class classes){
        Field[] field = classes.getDeclaredFields();
        String column="";
        String values="";
        for (int i = 0; i < field.length; i++) {
            column=column+field[i].getName()+",";
            values=values+"?,";
        }
        if (field.length>0){
            //取消最后一个逗号
            column=column.substring(0,column.length()-1);
            column="("+column+")";
            values=values.substring(0,values.length()-1);
            values="("+values+")";
        }else Log.e("传递Class","属性值空");
        Log.i("插入--->","Insert into "+classes.getSimpleName()+column+"values"+values);
        return  "Insert into "+classes.getSimpleName()+column+"values"+values;
    }

    public Object[] getSqlValue(Class valueClass){
        Field[] field = valueClass.getDeclaredFields();
        Object[] objects=new Object[field.length];
        for (int i = 0; i < field.length; i++) {
            try {
                Method method = valueClass.getMethod("get" + firstUp(field[i].getName()));
                method.invoke(valueClass.newInstance());
            } catch (Exception e) {
                Log.i("获取Method","错误");
                e.printStackTrace();
            }
        }
        return objects;
    }

    public void delectData(Class deleteClass){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        try {
            writableDatabase.beginTransaction();
            writableDatabase.execSQL( "DELETE FROM " +deleteClass.getSimpleName());
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            writableDatabase.close();
        } catch (Exception ex) {
            Log.e("删除数据库","失败");
            writableDatabase.close();
            ex.printStackTrace();
        } finally {
            writableDatabase.close();
        }
    }

    public String firstUp(String changeString){
        char[] cs=changeString.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }



    public DBUtil(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(dbShare,Context.MODE_PRIVATE);
        version=sharedPreferences.getInt("dbVersion",1);
        //dbHelper=new DbHelper(context,version);
        edit=sharedPreferences.edit();
        dbNum= sharedPreferences.getInt("DbNum",0);
    }

    public class DbHelper extends SQLiteOpenHelper{
        public DbHelper(Context context,int version) {
            super(context,dbname,null,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            dbNum=sharedPreferences.getInt("DbNum",0);
            for (int i = 0; i < dbNum; i++) {
                String className=sharedPreferences.getString("dbNum"+(i+1),"");
                if (!TextUtils.isEmpty(createDbSql(className)))
                    db.execSQL(createDbSql(className));
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dbNum=sharedPreferences.getInt("DbNum",0);
            for (int i = 0; i < dbNum; i++) {
                try {
                    String className=sharedPreferences.getString("dbNum"+(i+1),"");
                    Class<?> dbClass=Class.forName(className);
                    db.execSQL("DROP TABLE IF EXISTS"+dbClass.getSimpleName());
                } catch (ClassNotFoundException e) {
                    Log.e("删除数据库","失败");
                    e.printStackTrace();
                }

            }
        }
    }

    public String createDbSql(String className){
        String sql="";
        try {
            Class<?> classes=Class.forName(className);
            sql="create table if not exists "+classes.getSimpleName()+"(_id integer primary key autoincrement,";
            Field[] field = classes.getDeclaredFields();
            for (int i = 0; i < field.length; i++) {
                Class<?> type = field[i].getType();
                String classType=type.getName();
                String value=field[i].getName();
                sql=sql+value+" "+selectType(classType)+",";
            }
            if (sql.endsWith(",")){
                sql=sql.substring(0,sql.length()-1);
                sql=sql+")";
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("创建数据库",sql);
        return sql;
    }

    public String selectType(String classType){
        switch (classType.toLowerCase()){
            case "int":
                return "INTEGER";
            case "java.lang.string":
                return "varchar(60)";
        }
        return "varchar(60)";
    }
}
