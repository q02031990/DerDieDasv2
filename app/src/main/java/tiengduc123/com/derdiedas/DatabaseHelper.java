package tiengduc123.com.derdiedas;

/**
 * Created by qadmin on 21.12.15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import static android.net.Uri.encode;


/**
 * Created by qadmin on 19.12.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Derdiedas.db";
    public static final String TABLE_NAME = "woerter";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "ARTIKEL";
    public static final String COL_3 = "WOERTER";
    public static final String COL_4 = "WOERTER_SUCHEN";
    public static final String COL_5 = "DEFINITION";
    public static final String COL_6 = "PLURAL";

    public static final String History_COL_2 = "idWoerter";

    public boolean checkDBExit(){

        File file = new File("/data/data/tiengduc123.com.derdiedas/databases/Derdiedas.db");
        //long fileExists = getFolderSize(file);
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;

        if(fileSizeInKB > 670) {
            return true;
        }
        return false;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ARTIKEL TEXT, WOERTER TEXT, WOERTER_SUCHEN TEXT, DEFINITION TEXT, PLURAL TEXT)");
        db.execSQL("create table history_Search (id INTEGER PRIMARY KEY AUTOINCREMENT, idWoerter INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean updateData(String id, String ARTIKEL,String WOERTER,String WOERTER_SUCHEN,String DEFINITION,String PLURAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,ARTIKEL);
        contentValues.put(COL_3,WOERTER);
        contentValues.put(COL_4,WOERTER_SUCHEN);
        contentValues.put(COL_5, DEFINITION);
        contentValues.put(COL_6, PLURAL);

        String where = "ID = '" + id + "'";

        long resualt = db.update(TABLE_NAME, contentValues, where, null);
        //db.delete(TABLE_NAME,where,null);
        if(resualt == -1)
            return false;
        else
            return true;

    }

    public boolean DeleteWord(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = " ID = '" + id + "'";
        long resualt = db.delete("woerter", where, null);
        if(resualt == -1) {
            DeleteData(id);
            return false;
        }
        else
            return true;
    }

    public boolean DeleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = " idWoerter = '" + id + "'";
        long resualt = db.delete("history_Search", where, null);
        if(resualt == -1)
            return false;
        else
            return true;
    }

    public boolean insertData(String ARTIKEL,String WOERTER,String WOERTER_SUCHEN,String DEFINITION,String PLURAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_1,"1800");
        contentValues.put(COL_2,ARTIKEL);
        contentValues.put(COL_3,WOERTER);
        contentValues.put(COL_4,WOERTER_SUCHEN);
        contentValues.put(COL_5, DEFINITION);
        contentValues.put(COL_6, PLURAL);
        long resualt = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if(resualt == -1)
            return false;
        else
            /*woeter _word = new woeter(ARTIKEL,WOERTER,DEFINITION"",PLURAL);
            UpdateWordImServer(_word);*/
            return true;
    }

    public woeter searchWordByID(String id)
    {
        woeter a = new woeter("","","","","");
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String qr = "SELECT  * FROM " + TABLE_NAME + " where id = " + id ;
        Cursor res = db.rawQuery(qr, null);

        boolean insertOnceRowIntoDatabase = false;

        if(res.getCount() >0) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                String Atikel = res.getString(res.getColumnIndex(COL_2)).toString().trim();

                String word = res.getString(res.getColumnIndex(COL_3));
                String definition = res.getString(res.getColumnIndex(COL_5));
                String plural = res.getString(res.getColumnIndex(COL_6));

                //if(res.getString(4).toString().trim() == "") {

                String _id = res.getString(res.getColumnIndex(COL_1)).toString().trim();
                /*if(definition == null || definition == "") {
                    definition =readFileFromInternet(word);
                    boolean resualt = updateData(_id, Atikel, word, word, definition);
                    if (resualt) {
                        //Toast.makeText(getApplicationContext(), "You need to connect to the Internet", Toast.LENGTH_LONG).show();
                    }
                }*/
                //}

                if (Atikel.equalsIgnoreCase("1") || Atikel.equalsIgnoreCase("3")) {
                    Atikel = "der";
                } else if (Atikel.equalsIgnoreCase("2")) {
                    Atikel = "die";
                } else{
                    Atikel = "das";
                }

                a = new woeter(Atikel,word, definition, _id, plural);
                res.moveToNext();
            }
        }
        return a;
    }

    public ArrayList<woeter> searchWord(String str)
    {
        if(str == "" || str == null ){
            str ="a";
        }

        ArrayList<woeter> array_list = new ArrayList<woeter>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String qr = "SELECT  * FROM " + TABLE_NAME + " where WOERTER_SUCHEN like '" + str + "%' ORDER BY WOERTER_SUCHEN LIMIT 129;" ;
        Cursor res = db.rawQuery(qr, null);

        if(res.getCount() >1) {
            res.moveToFirst();
            int i=1;
            while (res.isAfterLast() == false) {
                String Atikel = res.getString(res.getColumnIndex(COL_2)).toString().trim();

                String _id = res.getString(res.getColumnIndex(COL_1)).toString().trim();
                String word = res.getString(res.getColumnIndex(COL_3));
                String definition = res.getString(res.getColumnIndex(COL_5));
                String plural = res.getString(res.getColumnIndex(COL_6));

                /*if(definition == null || definition == "" && i == 2) {
                    i++;
                    definition =readFileFromInternet(word);
                    boolean resualt = updateData(_id, Atikel, word, word, definition);
                    if (resualt) {
                        //Toast.makeText(getApplicationContext(), "You need to connect to the Internet", Toast.LENGTH_LONG).show();
                    }
                }*/
                if (Atikel.equalsIgnoreCase("1") || Atikel.equalsIgnoreCase("3")) {
                    Atikel = "der";
                } else if (Atikel.equalsIgnoreCase("2")) {
                    Atikel = "die";
                } else{
                    Atikel = "das";
                }

                woeter a = new woeter(Atikel,word,definition, _id,plural);

                array_list.add(a);
                res.moveToNext();
            }
        }
        return array_list;
    }

    public ArrayList<woeter> ShowHistorySearch()
    {

        ArrayList<woeter> array_list = new ArrayList<woeter>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String qr = "SELECT history_Search.id, woerter.ID , woerter.ARTIKEL,  woerter.WOERTER, woerter.WOERTER_SUCHEN, woerter.PLURAL, woerter.DEFINITION" +
                    " FROM woerter" +
                    " INNER JOIN history_Search" +
                    " ON woerter.ID = history_Search.idWoerter" +
                    " ORDER BY history_Search.id DESC LIMIT 500;" ;
        Cursor res = db.rawQuery(qr, null);

        if(res.getCount() >1) {
            res.moveToFirst();
            int i=1;
            while (res.isAfterLast() == false) {
                String Atikel = res.getString(res.getColumnIndex(COL_2)).toString().trim();

                String _id = res.getString(res.getColumnIndex(COL_1)).toString().trim();
                String word = res.getString(res.getColumnIndex(COL_3));
                String definition = res.getString(res.getColumnIndex(COL_5));
                String plural = res.getString(res.getColumnIndex(COL_6));

                if (Atikel.equalsIgnoreCase("1") || Atikel.equalsIgnoreCase("3")) {
                    Atikel = "der";
                } else if (Atikel.equalsIgnoreCase("2")) {
                    Atikel = "die";
                } else if (Atikel.equalsIgnoreCase("4")) {
                    Atikel = "das";
                }

                woeter a = new woeter(Atikel,word,definition, _id,plural);

                array_list.add(a);
                res.moveToNext();
            }
        }
        return array_list;
    }
    public String readFileFromInternet(String word){
        Random rn = new Random();
        int n = rn.nextInt();
        int m = rn.nextInt();
        String url = "http://tiengduc123.com/app/Translate.php?m=" + m + "&n=" + n + "&word=" + encode(word); ;
        URLConnection feedUrl;
        String str = "";
        try {
            feedUrl = new URL(url).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();
            str = sb.toString();

        } catch (Exception e) {
            Log.e("Error", "readFileFromInternet: " + e.toString());
            //e.printStackTrace();
        }
        return str;
    }

    public boolean insertDataToHistory(String idWoert){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(History_COL_2,idWoert);
        DeleteData(idWoert);
        long resualt = db.insert("history_Search", null, contentValues);
        //db.delete(TABLE_NAME,where,null);
        if(resualt == -1)
            return false;
        else
            return true;
    }

    public ArrayList<woeter> getWordForGame()
    {
        ArrayList<woeter> array_list = ShowHistorySearch();
        return array_list;
    }

    public boolean UpdateWordInServer(woeter word){
        Random rn = new Random();
        int n = rn.nextInt();
        int m = rn.nextInt();
        String url = "http://tiengduc123.com/app/UpdateDatabaseInServer.php?m=" + m + "&n=" + n +
                "&woeter=" + encode(word.woeter) +
                "&status=" + "1" + //1 la du lieu moi 0 la du lieu cu nguoi dung se update
                "&id=" + word.ID +
                "&artikel=" + word.artikel +
                "&definition=" + word.definition +
                "&plural=" + word.plural;
        URLConnection feedUrl;
        String str = "";
        try {
            feedUrl = new URL(url).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();
            str = sb.toString();

            if(str=="1"){
                return true;
            }else{
                return false;

            }
        } catch (Exception e) {
            Log.e("Error", "readFileFromInternet: " + e.toString());
            return false;
        }
    }
}