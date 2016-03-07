package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.FavouriteItem;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class DatabaseHelper {

    private SQLiteOpenHelper _opeSqLiteOpenHelper;

    private static final String DATABASE_NAME = "Favourites.db";
    private static final String TABLE_NAME = "favourites_table";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_ADDRESS = "ADDRESS";
    private static final String COL_LAT = "LAT";
    private static final String COL_LNG = "LNG";
    private static final String COL_ZOOM = "ZOOM";

    private String[] allColumns = { COL_ID,
            COL_NAME, COL_ADDRESS, COL_LAT, COL_LNG, COL_LNG };

    public DatabaseHelper(Context context){
        _opeSqLiteOpenHelper = new mySQLiteOpenHelper(context);
    }

    class mySQLiteOpenHelper extends SQLiteOpenHelper {
        mySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("create table " + TABLE_NAME + " (" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_NAME + " text, " +
                    COL_ADDRESS + " text, " +
                    COL_LAT + " double, " +
                    COL_LNG + " double, " +
                    COL_ZOOM + " text )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public Cursor getAll()
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if(db == null)
        {
            return null;
        }
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " order by " + COL_ID + "", null);
    }

    public ContentValues getAll2() {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cur.moveToNext()) {
            row.put("Name", cur.getString(1));
            row.put("Address", cur.getString(2));
            row.put("Lat", cur.getDouble(3));
            row.put("Lng", cur.getDouble(4));
            row.put("Zoom", cur.getString(5));
        }
        cur.close();
        db.close();
        return row;
    }

    public List<FavouriteItem> getAllFavourites()
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<FavouriteItem> favouriteItemList = new ArrayList<FavouriteItem>();
        Cursor cursor = db.query(TABLE_NAME, allColumns, null, null, null, null, null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            FavouriteItem favouriteItem = cursorToFavouriteItem(cursor);
            favouriteItemList.add(favouriteItem);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("DB_Helper", "DB Items retrieved = " + favouriteItemList.size());
        return favouriteItemList;
    }

    public long addNewFavourite(String name, String address, double lat, double lng, float zoom)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if(db==null)
        {
            return 0;
        }
        ContentValues row = new ContentValues();
        if(!nameExists(name)) {
            row.put(COL_NAME, name);
            row.put(COL_ADDRESS, address);
            row.put(COL_LAT, lat);
            row.put(COL_LNG, lng);
            row.put(COL_ZOOM, zoom);
            long id = db.insert(TABLE_NAME, null, row);
            db.close();
            Log.d("DB_Helper", "DB Inserted ID = " + id + " NAME = " + name + " ADDRESS = " + address + " LAT = " + lat + " LNG = " + lng + " ZOOM = " + zoom);
            return id;
        }
        else
        {
            Log.d("DB_Helper", "DB Inserted = " + 0);
            return 0;
        }

    }

    public void deleteFavouriteItem(String name)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if(db==null)
            return;
        int deleted = db.delete(TABLE_NAME, COL_NAME + " = ? ", new String[]{String.valueOf(name)});

        if(deleted > 0)
            Log.d("DB_Helper", "DB Deleted ID = " + deleted);
        else
            Log.d("DB_Helper", "DB Deleted = " + deleted);

        db.close();
    }

    public void updateFavouriteItem(String name, String address, double lat, double lng, float zoom)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if(db==null)
        {
            return;
        }
        ContentValues row = new ContentValues();
        row.put(COL_NAME, name);
        row.put(COL_ADDRESS, address);
        if(lat != 0 && lng != 0)
        {
            row.put(COL_LAT, lat);
            row.put(COL_LNG, lng);
            row.put(COL_ZOOM, zoom);
        }
        int updated = db.update(TABLE_NAME, row, COL_NAME + " = ?", new String[]{String.valueOf(name)});

        if(updated > 0)
            Log.d("DB_Helper", "DB Updated ID = " + updated);
        else
            Log.d("DB_Helper", "DB Updated = " + updated);
        db.close();
    }

    public boolean nameExists(String name)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = ?;", new String[] {name});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return  exists;
    }

    public boolean isEmpty()
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        boolean empty;
        if(count > 0)
        {
            empty = false;
            Log.d("DB_Helper", "DB Empty = " + empty);
        }
        else
        {
            empty = true;
            Log.d("DB_Helper", "DB Empty = " + empty);
        }
        cursor.close();
        return empty;
    }

    private FavouriteItem cursorToFavouriteItem(Cursor cursor) {
        FavouriteItem favouriteItem = new FavouriteItem();
        favouriteItem.setId(cursor.getLong(0));
        favouriteItem.setName(cursor.getString(1));
        favouriteItem.setAddress(cursor.getString(2));
        favouriteItem.setLat(cursor.getDouble(3));
        favouriteItem.setLng(cursor.getDouble(4));
        favouriteItem.setZoom(cursor.getString(5));
        return favouriteItem;
    }
}
