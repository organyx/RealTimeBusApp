package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.FavoriteItem;
import model.Location;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";

    private SQLiteOpenHelper _opeSqLiteOpenHelper;

    private static final String DATABASE_NAME = "Locations.db";

    private static final String FAVOURITES = "FAVOURITES";
    private static final String FAVOURITES_COL_ID = "FAVOURITES_COL_ID";
    private static final String FAVOURITES_COL_NAME = "FAVOURITES_COL_NAME";
    private static final String FAVOURITES_COL_ADDRESS = "FAVOURITES_COL_ADDRESS";
    private static final String FAVOURITES_COL_LAT = "FAVOURITES_COL_LAT";
    private static final String FAVOURITES_COL_LNG = "FAVOURITES_COL_LNG";
    private static final String FAVOURITES_COL_ZOOM = "FAVOURITES_COL_ZOOM";
    private static final String CREATE_TABLE_FAVOURITES = "create table " + FAVOURITES + " (" +
            FAVOURITES_COL_ID + " integer primary key autoincrement, " +
            FAVOURITES_COL_NAME + " text, " +
            FAVOURITES_COL_ADDRESS + " text, " +
            FAVOURITES_COL_LAT + " double, " +
            FAVOURITES_COL_LNG + " double, " +
            FAVOURITES_COL_ZOOM + " text )";

    private static final String LOCATIONS = "LOCATIONS";
    private static final String LOCATIONS_COL_ID = "LOCATIONS_COL_ID";
    private static final String LOCATIONS_COL_NAME = "LOCATIONS_COL_NAME";
    private static final String LOCATIONS_COL_ADDRESS = "LOCATIONS_COL_ADDRESS";
    private static final String LOCATIONS_COL_LAT = "LOCATIONS_COL_LAT";
    private static final String LOCATIONS_COL_LNG = "LOCATIONS_COL_LNG";
    private static final String LOCATIONS_COL_ZOOM = "LOCATIONS_COL_ZOOM";
    private static final String LOCATIONS_COL_FAVORITED = "LOCATIONS_COL_FAVORITED";
    private static final String CREATE_TABLE_LOCATIONS = "create table " + LOCATIONS + " (" +
            LOCATIONS_COL_ID + " integer primary key autoincrement, " +
            LOCATIONS_COL_NAME + " text, " +
            LOCATIONS_COL_ADDRESS + " text, " +
            LOCATIONS_COL_LAT + " double, " +
            LOCATIONS_COL_LNG + " double, " +
            LOCATIONS_COL_ZOOM + " text, " +
            LOCATIONS_COL_FAVORITED + " integer )";

    private static final String BUS_LINE = "BUS_LINE";
    private static final String BUS_LINE_COL_ID = "BUS_LINE_COL_ID";
    private static final String BUS_LINE_COL_NAME = "BUS_LINE_COL_NAME";
    private static final String CREATE_TABLE_BUS_LINES = "create table " + BUS_LINE_COL_ID + " (" +
            LOCATIONS_COL_ID + " integer primary key autoincrement, " +
            BUS_LINE_COL_NAME + " text )";

    private static final String BUS_LINE_BUS_STOPS = "BUS_LINE_BUS_STOPS";
    private static final String BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID = "BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID";
    private static final String BUS_LINE_BUS_STOPS_COL_LOCATION_ID = "BUS_LINE_BUS_STOPS_COL_LOCATION_ID";
    private static final String CREATE_TABLE_BUS_LINE_BUS_STOPS = "create table " + BUS_LINE_BUS_STOPS + " (" +
            BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID + " integer, " +
            BUS_LINE_BUS_STOPS_COL_LOCATION_ID + " integer, " +
            "CONSTRAINT pk_route_bus_stop PRIMARY KEY ("+BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID+", "+BUS_LINE_BUS_STOPS_COL_LOCATION_ID+")," +
            "CONSTRAINT fk_route_bus_stop_bus_line_id FOREIGN KEY ("+BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID+") REFERENCES "+ BUS_LINE +"("+BUS_LINE_COL_ID+") ON DELETE CASCADE," +
            "CONSTRAINT fk_route_bus_stop_bus_stop_id FOREIGN KEY ("+BUS_LINE_BUS_STOPS_COL_LOCATION_ID+") REFERENCES "+ LOCATIONS +"("+LOCATIONS_COL_ID+") ON DELETE CASCADE)";

    private static final int DATABASE_VERSION = 2;

    private String[] allColumns = {FAVOURITES_COL_ID,
            FAVOURITES_COL_NAME, FAVOURITES_COL_ADDRESS, FAVOURITES_COL_LAT, FAVOURITES_COL_LNG, FAVOURITES_COL_LNG};

    public DatabaseHelper(Context context){
        _opeSqLiteOpenHelper = new mySQLiteOpenHelper(context);
    }

    class mySQLiteOpenHelper extends SQLiteOpenHelper {
        mySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.d(TAG, "onCreate");
            db.execSQL(CREATE_TABLE_FAVOURITES);
            db.execSQL(CREATE_TABLE_LOCATIONS);
            db.execSQL(CREATE_TABLE_BUS_LINES);
            db.execSQL(CREATE_TABLE_BUS_LINE_BUS_STOPS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + FAVOURITES);
            db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + BUS_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + BUS_LINE_BUS_STOPS);
            onCreate(db);
        }
    }

    public List<FavoriteItem> getAllFavourites()
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<FavoriteItem> favoriteItemList = new ArrayList<FavoriteItem>();
        Cursor cursor = db.query(FAVOURITES, allColumns, null, null, null, null, null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            FavoriteItem favoriteItem = cursorToFavouriteItem(cursor);
            favoriteItemList.add(favoriteItem);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d(TAG, "DB Items retrieved = " + favoriteItemList.size());
        return favoriteItemList;
    }

    public long addNewFavourite(String name, String address, double lat, double lng, float zoom)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if(db==null)
        {
            return 0;
        }
        ContentValues row = new ContentValues();
        if(!nameExists(name, FAVOURITES)) {
            row.put(FAVOURITES_COL_NAME, name);
            row.put(FAVOURITES_COL_ADDRESS, address);
            row.put(FAVOURITES_COL_LAT, lat);
            row.put(FAVOURITES_COL_LNG, lng);
            row.put(FAVOURITES_COL_ZOOM, zoom);
            long id = db.insert(FAVOURITES, null, row);
            db.close();
            Log.d(TAG, "DB Inserted ID = " + id + " NAME = " + name + " ADDRESS = " + address + " LAT = " + lat + " LNG = " + lng + " ZOOM = " + zoom);
            return id;
        }
        else
        {
            Log.d(TAG, "DB Inserted = " + 0);
            return 0;
        }

    }

    public void deleteFavouriteItem(String name)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if(db==null)
            return;
        int deleted = db.delete(FAVOURITES, FAVOURITES_COL_NAME + " = ? ", new String[]{String.valueOf(name)});

        if(deleted > 0)
            Log.d(TAG, "DB Deleted ID = " + deleted);
        else
            Log.d(TAG, "DB Deleted = " + deleted);

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
        row.put(FAVOURITES_COL_NAME, name);
        row.put(FAVOURITES_COL_ADDRESS, address);
        if(lat != 0 && lng != 0)
        {
            row.put(FAVOURITES_COL_LAT, lat);
            row.put(FAVOURITES_COL_LNG, lng);
            row.put(FAVOURITES_COL_ZOOM, zoom);
        }
        int updated = db.update(FAVOURITES, row, FAVOURITES_COL_NAME + " = ?", new String[]{String.valueOf(name)});

        if(updated > 0)
            Log.d(TAG, "DB Updated ID = " + updated);
        else
            Log.d(TAG, "DB Updated = " + updated);
        db.close();
    }

    public boolean nameExists(String name, String tableName)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + tableName + "_COL_NAME = ?;", new String[] {name});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return  exists;
    }

    public boolean isEmpty(String table_name)
    {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table_name, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        boolean empty;
        if(count > 0)
        {
            empty = false;
            Log.d(TAG, "DB Table " + table_name + " Empty? = " + empty);
        }
        else
        {
            empty = true;
            Log.d(TAG, "DB Table " + table_name + " Empty? = " + empty);
        }
        cursor.close();
        return empty;
    }

    private FavoriteItem cursorToFavouriteItem(Cursor cursor) {
        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setId(cursor.getLong(0));
        favoriteItem.setName(cursor.getString(1));
        favoriteItem.setAddress(cursor.getString(2));
        favoriteItem.setLat(cursor.getDouble(3));
        favoriteItem.setLng(cursor.getDouble(4));
        favoriteItem.setZoom(cursor.getString(5));
        return favoriteItem;
    }

    private Location cursorToLocation(Cursor cursor) {
        Location location = new Location();
        location.setId(cursor.getLong(0));
        location.setName(cursor.getString(1));
        location.setAddress(cursor.getString(2));
        location.setLat(cursor.getDouble(3));
        location.setLng(cursor.getDouble(4));
        location.setZoom(cursor.getString(5));
        location.setIsFavourited(cursor.getInt(6) != 0);
        return location;
    }
}
