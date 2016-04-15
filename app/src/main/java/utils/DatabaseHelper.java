package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.BusLineItem;
import model.LocationItem;

/**
 * Created by Aleks on 05-Mar-16.
 * Database Helper class for storing data in SQLite Database.
 */
public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";

    private SQLiteOpenHelper _opeSqLiteOpenHelper;
    private static DatabaseHelper instance = null;

    private static final String DATABASE_NAME = "Locations.db";

    private static final String LOCATIONS = "LOCATIONS";
    private static final String LOCATIONS_COL_ID = "LOCATIONS_COL_ID";
    private static final String LOCATIONS_COL_NAME = "LOCATIONS_COL_NAME";
    private static final String LOCATIONS_COL_ADDRESS = "LOCATIONS_COL_ADDRESS";
    private static final String LOCATIONS_COL_LAT = "LOCATIONS_COL_LAT";
    private static final String LOCATIONS_COL_LNG = "LOCATIONS_COL_LNG";
    private static final String LOCATIONS_COL_ZOOM = "LOCATIONS_COL_ZOOM";
    private static final String LOCATIONS_COL_FAVORITED = "LOCATIONS_COL_FAVORITED";
    private static final String LOCATIONS_COL_VISITS = "LOCATIONS_COL_VISITS";
    private static final String LOCATIONS_COL_DATE = "LOCATIONS_COL_DATE";
    private static final String CREATE_TABLE_LOCATIONS = "create table " + LOCATIONS + " (" +
            LOCATIONS_COL_ID + " integer primary key autoincrement, " +
            LOCATIONS_COL_NAME + " text, " +
            LOCATIONS_COL_ADDRESS + " text, " +
            LOCATIONS_COL_LAT + " double, " +
            LOCATIONS_COL_LNG + " double, " +
            LOCATIONS_COL_ZOOM + " text, " +
            LOCATIONS_COL_FAVORITED + " integer, " +
            LOCATIONS_COL_VISITS + " integer, " +
            LOCATIONS_COL_DATE + " integer )";

    private static final String BUS_LINE = "BUS_LINE";
    private static final String BUS_LINE_COL_ID = "BUS_LINE_COL_ID";
    private static final String BUS_LINE_COL_NAME = "BUS_LINE_COL_NAME";
    private static final String CREATE_TABLE_BUS_LINES = "create table " + BUS_LINE + " (" +
            BUS_LINE_COL_ID + " integer primary key autoincrement, " +
            BUS_LINE_COL_NAME + " text )";

    private static final String BUS_LINE_BUS_STOPS = "BUS_LINE_BUS_STOPS";
    private static final String BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID = "BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID";
    private static final String BUS_LINE_BUS_STOPS_COL_LOCATION_ID = "BUS_LINE_BUS_STOPS_COL_LOCATION_ID";
    private static final String CREATE_TABLE_BUS_LINE_BUS_STOPS = "create table " + BUS_LINE_BUS_STOPS + " (" +
            BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID + " integer, " +
            BUS_LINE_BUS_STOPS_COL_LOCATION_ID + " integer, " +
            "CONSTRAINT pk_route_bus_stop PRIMARY KEY (" + BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID + ", " + BUS_LINE_BUS_STOPS_COL_LOCATION_ID + ")," +
            "CONSTRAINT fk_route_bus_stop_bus_line_id FOREIGN KEY (" + BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID + ") REFERENCES " + BUS_LINE + "(" + BUS_LINE_COL_ID + ") ON DELETE CASCADE," +
            "CONSTRAINT fk_route_bus_stop_bus_stop_id FOREIGN KEY (" + BUS_LINE_BUS_STOPS_COL_LOCATION_ID + ") REFERENCES " + LOCATIONS + "(" + LOCATIONS_COL_ID + ") ON DELETE CASCADE)";

    private static final int DATABASE_VERSION = 3;
    private static final int LOCATION_VISITS = 0;

    private String[] allColumns_locations = {
            LOCATIONS_COL_ID,
            LOCATIONS_COL_NAME,
            LOCATIONS_COL_ADDRESS,
            LOCATIONS_COL_LAT,
            LOCATIONS_COL_LNG,
            LOCATIONS_COL_ZOOM,
            LOCATIONS_COL_FAVORITED,
            LOCATIONS_COL_VISITS,
            LOCATIONS_COL_DATE
    };
    private String[] allColumns_busLines = {
            BUS_LINE_COL_ID,
            BUS_LINE_COL_NAME
    };

    private String[] allColumns_busLine_busStops = {
            BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID,
            BUS_LINE_BUS_STOPS_COL_LOCATION_ID
    };

    private String[] columns = null;
    private String selection = null;
    private String[] selectionArgs = null;
    private String groupBy = null;
    private String having = null;
    private String orderBy = null;

    public DatabaseHelper(Context context) {
        _opeSqLiteOpenHelper = new mySQLiteOpenHelper(context);
    }

    class mySQLiteOpenHelper extends SQLiteOpenHelper {
        mySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate");
            db.execSQL(CREATE_TABLE_LOCATIONS);
            db.execSQL(CREATE_TABLE_BUS_LINES);
            db.execSQL(CREATE_TABLE_BUS_LINE_BUS_STOPS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + BUS_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + BUS_LINE_BUS_STOPS);
            onCreate(db);
        }
    }

    public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public List<LocationItem> getAllFavourites() {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<LocationItem> favoriteItemList = new ArrayList<>();
        columns = allColumns_locations;
        selection = "LOCATIONS_COL_FAVORITED = 1";
        Cursor cursor = db.query(LOCATIONS, columns, selection, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationItem favoriteItem = cursorToLocation(cursor);
            favoriteItemList.add(favoriteItem);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d(TAG, "DB Items retrieved = " + favoriteItemList.size());
        return favoriteItemList;
    }

    public List<LocationItem> getAllLocations() {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<LocationItem> favoriteItemList = new ArrayList<>();
        columns = allColumns_locations;
//        selection = "LOCATIONS_COL_FAVORITED = 1";
        Cursor cursor = db.query(LOCATIONS, columns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationItem favoriteItem = cursorToLocation(cursor);
            favoriteItemList.add(favoriteItem);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d(TAG, "DB Items retrieved = " + favoriteItemList.size());
        return favoriteItemList;
    }

    public List<LocationItem> getRecentHistory() {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<LocationItem> favoriteItemList = new ArrayList<>();
        columns = allColumns_locations;
        selection = "LOCATIONS_COL_VISITS > 0";
        Cursor cursor = db.query(LOCATIONS, columns, selection, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationItem favoriteItem = cursorToLocation(cursor);
            favoriteItemList.add(favoriteItem);
            cursor.moveToNext();
        }
        Collections.sort(favoriteItemList); // Sorting the history items
        cursor.close();
        Log.d(TAG, "DB Items retrieved = " + favoriteItemList.size());
        return favoriteItemList;
    }

    public long addNewLocation(String name, String address, double lat, double lng, double zoom, int favorited) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        if (!nameExists(name, LOCATIONS)) {
            row.put(LOCATIONS_COL_NAME, name);
            row.put(LOCATIONS_COL_ADDRESS, address);
            row.put(LOCATIONS_COL_LAT, lat);
            row.put(LOCATIONS_COL_LNG, lng);
            row.put(LOCATIONS_COL_ZOOM, zoom);
            row.put(LOCATIONS_COL_FAVORITED, favorited);
            if(favorited == 1)
                row.put(LOCATIONS_COL_VISITS, LOCATION_VISITS + 1);
            else
                row.put(LOCATIONS_COL_VISITS, LOCATION_VISITS);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            long id = db.insert(LOCATIONS, null, row);
            db.close();
            Log.d(TAG, "DB Inserted Location ID = " + id + " NAME = " + name + " ADDRESS = " + address + " LAT = " + lat + " LNG = " + lng + " ZOOM = " + zoom + " FAVORITED = " + favorited);
            return id;
        } else {
            Log.d(TAG, "DB Inserted Location = " + 0);
            return 0;
        }
    }

    public void deleteLocation(String name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null)
            return;
        int deleted = db.delete(LOCATIONS, LOCATIONS_COL_NAME + " = ?", new String[]{String.valueOf(name)});

        if (deleted > 0)
            Log.d(TAG, "DB Deleted Bus Line ID = " + deleted);
        else
            Log.d(TAG, "DB Deleted Bus Line = " + deleted);

        db.close();
    }

    public void deleteLocation(int id) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null)
            return;
        int deleted = db.delete(LOCATIONS, LOCATIONS_COL_ID + " = ?", new String[]{String.valueOf(id)});

        if (deleted > 0)
            Log.d(TAG, "DB Deleted Bus Line ID = " + deleted);
        else
            Log.d(TAG, "DB Deleted Bus Line = " + deleted);

        db.close();
    }

    public void updateLocation(String name, String address, double lat, double lng, float zoom) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        int visits = getLocationVisits(name);
        ContentValues row = new ContentValues();
        if (nameExists(name, LOCATIONS)) {
            row.put(LOCATIONS_COL_NAME, name);
            row.put(LOCATIONS_COL_ADDRESS, address);
            if (lat != 0 && lng != 0) {
                row.put(LOCATIONS_COL_LAT, lat);
                row.put(LOCATIONS_COL_LNG, lng);
                row.put(LOCATIONS_COL_ZOOM, zoom);
            }
            row.put(LOCATIONS_COL_VISITS, visits + 1);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            int updated = db.update(LOCATIONS, row, LOCATIONS_COL_NAME + " = ? ", new String[]{String.valueOf(name)});

            if (updated > 0)
                Log.d(TAG, "DB Updated ID = " + updated);
            else
                Log.d(TAG, "DB Updated = " + updated);
            db.close();
        } else {
            Log.d(TAG, "DB Updated = false");
        }
    }

    public void updateLocation(int id, String name, String address, double lat, double lng, float zoom) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        int visits = getLocationVisits(id);
        ContentValues row = new ContentValues();
        if (idExists(id, LOCATIONS)) {
            row.put(LOCATIONS_COL_NAME, name);
            row.put(LOCATIONS_COL_ADDRESS, address);
            if (lat != 0 && lng != 0) {
                row.put(LOCATIONS_COL_LAT, lat);
                row.put(LOCATIONS_COL_LNG, lng);
                row.put(LOCATIONS_COL_ZOOM, zoom);
            }
            row.put(LOCATIONS_COL_VISITS, visits + 1);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            int updated = db.update(LOCATIONS, row, LOCATIONS_COL_ID + " = ? ", new String[]{String.valueOf(id)});

            if (updated > 0)
                Log.d(TAG, "DB Updated ID = " + updated);
            else
                Log.d(TAG, "DB Updated = " + updated);
            db.close();
        } else {
            Log.d(TAG, "DB Updated = false");
        }
    }

    public void updateLocationFavourite(String name, boolean favourite) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        if (nameExists(name, LOCATIONS)) {
            row.put(LOCATIONS_COL_FAVORITED, favourite);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            int updated = db.update(LOCATIONS, row, LOCATIONS_COL_NAME + " = ?", new String[]{String.valueOf(name)});
            if (updated > 0)
                Log.d(TAG, "DB Updated ID = " + updated);
            else
                Log.d(TAG, "DB Updated = " + updated);
            db.close();
        } else {
            Log.d(TAG, "DB Updated = false");
        }
    }

    public void updateLocationFavourite(int id, boolean favourite) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        if (idExists(id, LOCATIONS)) {
            row.put(LOCATIONS_COL_FAVORITED, favourite);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            int updated = db.update(LOCATIONS, row, LOCATIONS_COL_ID + " = ?", new String[]{String.valueOf(id)});
            if (updated > 0)
                Log.d(TAG, "DB Updated ID = " + updated);
            else
                Log.d(TAG, "DB Updated = " + updated);
            db.close();
        } else {
            Log.d(TAG, "DB Updated = false");
        }
    }

    public void updateLocationVisits(String name, int visits) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        if (nameExists(name, LOCATIONS)) {
            row.put(LOCATIONS_COL_VISITS, visits);
            row.put(LOCATIONS_COL_DATE, System.currentTimeMillis());
            int updated = db.update(LOCATIONS, row, LOCATIONS_COL_NAME + " = ?", new String[]{String.valueOf(name)});
            if (updated > 0)
                Log.d(TAG, "DB Updated ID = " + updated);
            else
                Log.d(TAG, "DB Updated = " + updated);
            db.close();
        } else {
            Log.d(TAG, "DB Updated = false");
        }
    }

    public long addNewBusLine(String name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        if (!nameExists(name, BUS_LINE)) {
            row.put(BUS_LINE_COL_NAME, name);
            long id = db.insert(BUS_LINE, null, row);
            db.close();
            Log.d(TAG, "DB Inserted Bus Line ID = " + id + " NAME = " + name);
            return id;
        } else {
            Log.d(TAG, "DB Inserted Bus Line = " + 0);
            return 0;
        }
    }

    public void deleteBusLine(String name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null)
            return;
        int deleted = db.delete(BUS_LINE, BUS_LINE_COL_NAME + " = ?", new String[]{String.valueOf(name)});

        if (deleted > 0)
            Log.d(TAG, "DB Deleted Bus Line ID = " + deleted);
        else
            Log.d(TAG, "DB Deleted Bus Line = " + deleted);
        db.close();
    }

    public void deleteBusLine(int id) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null)
            return;
        int deleted = db.delete(BUS_LINE, BUS_LINE_COL_ID + " = ?", new String[]{String.valueOf(id)});

        if (deleted > 0)
            Log.d(TAG, "DB Deleted Bus Line ID = " + deleted);
        else
            Log.d(TAG, "DB Deleted Bus Line = " + deleted);
        db.close();
    }

    private void populateBusLine(BusLineItem busLineItem) {

        int busLineID = getBusLineID(busLineItem.getBusLineName());
        int[] busStationsIDs = new int[busLineItem.getBusStations().size()];
        for (int i = 0; i < busLineItem.getBusStations().size(); i++) {
            busStationsIDs[i] = getBusStationID(String.valueOf(busLineItem.getBusStations().get(i).getName()));
        }

        SQLiteDatabase db = _opeSqLiteOpenHelper.getWritableDatabase();
        if (db == null)
            return;
        ContentValues row = new ContentValues();
        long inserted_id;
        for (int id : busStationsIDs) {
            row.put(BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID, busLineID);
            row.put(BUS_LINE_BUS_STOPS_COL_LOCATION_ID, id);
            inserted_id = db.insert(BUS_LINE_BUS_STOPS, null, row);
            Log.d(TAG, "DB Inserted Bus Line ID = " + inserted_id);
        }
        db.close();
    }

    public BusLineItem getBusLine(String busLine) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        List<LocationItem> busStationList = new ArrayList<>();
        if (db == null)
            return null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + LOCATIONS +
                " LEFT JOIN " + BUS_LINE_BUS_STOPS + " ON LOCATIONS.LOCATIONS_COL_ID = BUS_LINE_BUS_STOPS.BUS_LINE_BUS_STOPS_COL_LOCATION_ID" +
                " LEFT JOIN " + BUS_LINE + " ON BUS_LINE_BUS_STOPS.BUS_LINE_BUS_STOPS_COL_BUS_LINE_ID = BUS_LINE.BUS_LINE_COL_ID" +
                " WHERE BUS_LINE.BUS_LINE_COL_ID = ?", new String[]{String.valueOf(getBusLineID(busLine))});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationItem busStation = cursorToLocation(cursor);
            busStationList.add(busStation);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "DB Items retrieved = " + busStationList.size());
        return new BusLineItem(busLine, busStationList);
    }

    public void clearRecentHistory() {
        List<LocationItem> items = getAllLocations();
        for (LocationItem i : items) {
            if (isVisited(i.getName())) {
                updateLocationVisits(i.getName(), 0);
                Log.d(TAG, "DB Updated ID = " + i.getId());
            } else {
                Log.d(TAG, "DB Updated = false");
            }
        }
    }

    private int getBusLineID(String busLine) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if (db == null)
            return 0;
        int return_id;
        columns = new String[]{BUS_LINE_COL_ID};
        selection = "BUS_LINE_COL_NAME = ?";
        selectionArgs = new String[]{String.valueOf(busLine)};
        Cursor cursor = db.query(BUS_LINE, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return_id = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "DB Bus Line ID = " + return_id);
        return return_id;
    }

    public int getBusStationID(String busStation) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if (db == null)
            return 0;
        int return_id;
        columns = new String[]{LOCATIONS_COL_ID};
        selection = "LOCATIONS_COL_NAME = ?";
        selectionArgs = new String[]{String.valueOf(busStation)};
        Cursor cursor = db.query(LOCATIONS, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return_id = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "DB Bus Station ID = " + return_id);
        return return_id;
    }

    private int getLocationVisits(String busStation) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if (db == null)
            return 0;
        int return_id;
        columns = new String[]{LOCATIONS_COL_VISITS};
        selection = "LOCATIONS_COL_NAME = ?";
        selectionArgs = new String[]{String.valueOf(busStation)};
        Cursor cursor = db.query(LOCATIONS, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return_id = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "DB Bus Station ID = " + return_id);
        return return_id;
    }

    private int getLocationVisits(int id) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        if (db == null)
            return 0;
        int return_id;
        columns = new String[]{LOCATIONS_COL_VISITS};
        selection = "LOCATIONS_COL_ID = ?";
        selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(LOCATIONS, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return_id = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "DB Bus Station ID = " + return_id);
        return return_id;
    }

    private boolean nameExists(String name, String tableName) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + tableName + "_COL_NAME = ?;", new String[]{name});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean idExists(int id, String tableName) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + tableName + "_COL_ID = ?;", new String[]{String.valueOf(id)});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean isVisited(String name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + LOCATIONS_COL_VISITS + " FROM " + LOCATIONS + " WHERE " + LOCATIONS + "_COL_NAME = ?;", new String[]{name});
        cursor.moveToFirst();
        boolean visited = (cursor.getInt(0) > 0);
        cursor.close();
        return visited;
    }

    private boolean isEmpty(String table_name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table_name, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        boolean empty;
        empty = count <= 0;
//        if(count > 0)
//            empty = false;
//        else
//            empty = true;
        Log.d(TAG, "DB Table " + table_name + " Empty? = " + empty);
        cursor.close();
        return empty;
    }

    private boolean isFavourite(String name) {
        SQLiteDatabase db = _opeSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + LOCATIONS_COL_FAVORITED + " FROM " + LOCATIONS + " WHERE " + LOCATIONS + "_COL_NAME = ?;", new String[]{name});
        cursor.moveToFirst();
        boolean favourite = (cursor.getInt(0) > 0);
        cursor.close();
        return favourite;
    }

    private LocationItem cursorToLocation(Cursor cursor) {
        LocationItem location = new LocationItem();
        location.setId(cursor.getInt(0));
        location.setName(cursor.getString(1));
        location.setAddress(cursor.getString(2));
        location.setLat(cursor.getDouble(3));
        location.setLng(cursor.getDouble(4));
        location.setZoom(cursor.getFloat(5));
        location.setIsFavourited(cursor.getInt(6) != 0);
        return location;
    }

    private List<LocationItem> busStationsRoute1() {
        List<LocationItem> stations = new ArrayList<>();
        stations.add(new LocationItem("Horsens Trafikterminal", 55.8629951, 9.8365588));
        stations.add(new LocationItem("V. Berings Plads", 55.8622125, 9.8420348));
        stations.add(new LocationItem("Nørregade", 55.8630615, 9.8481180));
        stations.add(new LocationItem("Sundvej/Sygehuset", 55.8645606, 9.8721935));
        stations.add(new LocationItem("Bakkesvinget", 55.8696416, 9.8752405));
        stations.add(new LocationItem("Hybenvej", 55.8718567, 9.8820211));
        stations.add(new LocationItem("VIA, Chr. M. Østergårdsve", 55.8695091, 9.8858728));
        stations.add(new LocationItem("Haldrupvej/Sundbakken", 55.8696295, 9.8958077));
        stations.add(new LocationItem("Bygaden/Højmarksvej", 55.8721698, 9.9110426));
        stations.add(new LocationItem("Stensballeskolen", 55.8727596, 9.9206771));
        stations.add(new LocationItem("Risengård", 55.8757571, 9.9293246));
        stations.add(new LocationItem("Griffenfeldtsparken", 55.8778275, 9.9186386));
        stations.add(new LocationItem("Brådhusvej", 55.8729523, 9.9131026));
        return stations;
    }

    private List<LocationItem> busStationsRoute2() {
        List<LocationItem> stations = new ArrayList<>();
        stations.add(new LocationItem("Horsens Trafikterminal", 55.8629951, 9.8365588));
        stations.add(new LocationItem("V. Berings Plads", 55.8622125, 9.8420348));
        stations.add(new LocationItem("Rådhuset/O. Jensens Allé", 55.8601490, 9.8469450));
        stations.add(new LocationItem("Sundhedshuset", 55.8586177, 9.8516971));
        stations.add(new LocationItem("Gasvej", 55.8614597, 9.8604733));
        stations.add(new LocationItem("Sundvej/Sygehuset", 55.8645606, 9.8721935));
        stations.add(new LocationItem("Bakkesvinget", 55.8696416, 9.8752405));
        stations.add(new LocationItem("Hybenvej", 55.8718567, 9.8820211));
        stations.add(new LocationItem("VIA, Chr. M. Østergårdsve", 55.8695091, 9.8858728));
        stations.add(new LocationItem("Husoddevej/Højagervej", 55.8687460, 9.9171801));
        stations.add(new LocationItem("Bygaden/Højmarksvej", 55.8721698, 9.9110426));
        stations.add(new LocationItem("Stensballe v/landevejen", 55.8663139, 9.9040051));
        return stations;
    }

    private List<BusLineItem> busLines() {
        List<BusLineItem> routes = new ArrayList<>();
        routes.add(new BusLineItem("Route 1", busStationsRoute1()));
        routes.add(new BusLineItem("Route 2", busStationsRoute2()));
        return routes;
    }

    public void populateBusLineBusStations() {
        List<BusLineItem> busLineItems = busLines();
        for (BusLineItem item : busLineItems) {
            for (int i = 0; i < item.getBusStations().size(); i++) {
                addNewLocation(item.getBusStations().get(i).getName(),   // NAME
                        "Bus Station",                                   // ADDRESS
                        item.getBusStations().get(i).getLat(),           // LAT
                        item.getBusStations().get(i).getLng(),           // LNG
                        0,                                               // ZOOM
                        0);                                              // FAVOURITED
            }
            addNewBusLine(item.getBusLineName());
            populateBusLine(item);
        }
    }
}
