package com.example.gpscw2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class LocationContentProvider extends ContentProvider {

    public static final String AUTHORITY = "location_app";
    public static final String[] LOCATION_COLUMNS= {"lat","lon","distanceMetres","removeAfterNotify"
    , "timeoutTimeSeconds","title","description"};
    public static final String[] TRAVEL_COLUMNS = {"id","movementType","date","lengthSeconds","distance",
            "positive","description","weather","title"};
    private static final int CODE_LOCATION = 1;
    private static final int CODE_LOCATION_SINGLE = 2;
    private static final int CODE_TRAVEL = 3;
    private static final int CODE_TRAVEL_SINGLE = 4;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    LocationRepo repo;

    static {
        MATCHER.addURI(AUTHORITY,"location_notification", CODE_LOCATION);
        MATCHER.addURI(AUTHORITY,"location_notification" + "/*", CODE_LOCATION_SINGLE);
        MATCHER.addURI(AUTHORITY,"travel" + "/*", CODE_TRAVEL_SINGLE);
        MATCHER.addURI(AUTHORITY,"travel", CODE_TRAVEL);
    }

    public LocationContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) throws IllegalArgumentException {
        final int code = MATCHER.match(uri);

        final int id = (int) ContentUris.parseId(uri);
        if(code == CODE_LOCATION_SINGLE) {
            repo.deleteNotificationById(id);
        } else if(code == CODE_TRAVEL_SINGLE) {
            repo.deleteTravelById(id);
        } else {
            throw new IllegalArgumentException("to delete URI " + uri + " needs to contain an id");
        }

        return 1;
    }

    @Override
    public String getType(Uri uri) throws IllegalArgumentException {
        switch (MATCHER.match(uri)) {
            case CODE_LOCATION:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "location_notification";
            case CODE_TRAVEL:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "travel";
            case CODE_LOCATION_SINGLE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + "location_notification";
            case CODE_TRAVEL_SINGLE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + "travel";
            default:
               throw new IllegalArgumentException("URI Not recongnised");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws IllegalArgumentException {
        final int code = MATCHER.match(uri);

        if(code == CODE_TRAVEL)  {
            int id = repo.insertTravelAndReturnId(new TravelEntity(
                    values.getAsString("movementType"),
                    values.getAsLong("date"),
                    values.getAsInteger("distance"),
                    values.getAsBoolean("positive"),
                    values.getAsString("description"),
                    Weather.values()[values.getAsInteger("weather")],
                    values.getAsLong("lengthSeconds"),
                    values.getAsString("title")
                    )
            );

            return Uri.parse(AUTHORITY + "/" + "travel" + "/" + id);
        } else if(code == CODE_LOCATION) {
            int id = repo.insertLocationAndReturnId(new LocationNotificationEntity(
                    values.getAsDouble("lat"),
                    values.getAsDouble("lon"),
                    values.getAsInteger("distanceMetres"),
                    values.getAsString("title"),
                    values.getAsString("description"),
                    values.getAsBoolean("removeAfterNotify"),
                    values.getAsInteger("timeoutTimeSeconds")
            ));

            return Uri.parse(AUTHORITY + "/" + "travel" + "/" + id);
        } else if(code == CODE_LOCATION_SINGLE || code == CODE_TRAVEL_SINGLE) {
            throw new IllegalArgumentException("id cannot be used when inserting in uri");
        } else {
            throw new IllegalArgumentException("invalid uri");
        }
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        repo = new LocationRepo(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) throws IllegalArgumentException {
        final int code = MATCHER.match(uri);

        Cursor cursor;
        String columns = TextUtils.join(", ", projection);
        if(code == CODE_LOCATION) {
            cursor = repo.getLocationCustom(columns,selection);
        } else {
            cursor = repo.getTravelCustom(columns,selection);
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int code = MATCHER.match(uri);
        final int id = (int) ContentUris.parseId(uri);
        if(code == CODE_TRAVEL_SINGLE)  {
            TravelEntity entity = new TravelEntity(
                    values.getAsString("movementType"),
                    values.getAsLong("date"),
                    values.getAsInteger("distance"),
                    values.getAsBoolean("positive"),
                    values.getAsString("description"),
                    Weather.values()[values.getAsInteger("weather")],
                    values.getAsLong("lengthSeconds"),
                    values.getAsString("title")
            );

            entity.setId(id);
            repo.updateTravelEntity(entity);
        } else if(code == CODE_LOCATION_SINGLE) {
            LocationNotificationEntity entity = new LocationNotificationEntity(
                    values.getAsDouble("lat"),
                    values.getAsDouble("lon"),
                    values.getAsInteger("distanceMetres"),
                    values.getAsString("title"),
                    values.getAsString("description"),
                    values.getAsBoolean("removeAfterNotify"),
                    values.getAsInteger("timeoutTimeSeconds"));

            entity.setId(id);
            repo.updateNotification(entity);
        } else if(code == CODE_LOCATION || code == CODE_TRAVEL) {
            throw new IllegalArgumentException("id cannot be used when inserting in uri");
        } else {
            throw new IllegalArgumentException("invalid uri");
        }

        return 1;
    }
}