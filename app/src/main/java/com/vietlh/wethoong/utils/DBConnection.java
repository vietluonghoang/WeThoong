package com.vietlh.wethoong.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vietlh on 2/23/18.
 */

public class DBConnection extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";

    private final Context context;
    private static String assetPath = "database";
    private static final int DATABASE_VERSION = GeneralSettings.requiredDBVersion;
    private static final String DATABASE_NAME = "Hieuluat";
    private int currentDBVersion = 0;

    private boolean createDb = false, upgradeDb = false;

    private static DBConnection instance;
    private SQLiteDatabase database;

    private DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBConnection getInstance(Context context) {
        if (instance == null) {
            instance = new DBConnection(context);
        }
        return instance;
    }

    /**
     * Copy packaged database from assets folder to the database created in the
     * application package context.
     *
     * @param db The target database in the application package context.
     */
    private void copyDatabaseFromAssets(SQLiteDatabase db) {
        Log.i(TAG, "===SQLITE: copyDatabase");
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            // Open db packaged as asset as the input stream
            Log.i(TAG, "===" + context.getAssets().list(assetPath).length + " - " + context.getAssets().list(assetPath)[0]);
            myInput = context.getAssets().open("database/Hieuluat.sqlite");
            // Open the db in the application package context:
            myOutput = new FileOutputStream(db.getPath());

            // Transfer db file contents:
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            // Set the version of the copied database to the current
            // version:
//            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
//                    DATABASE_NAME, 0, null);
//            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
//            copiedDb.close();

        } catch (IOException e) {
            Log.i(TAG, "=== ERROR: Failed on Copying Database: \n" + e.getMessage());
            for (StackTraceElement trace :
                    e.getStackTrace()) {
                Log.i(TAG, "=== " + trace.toString());
            }

            e.printStackTrace();
            throw new Error(TAG + " Error copying database");
        } finally {
            // Close the streams
            try {
                if (myOutput != null) {
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(TAG + " Error closing streams");
            }
        }
    }

    //update the current database version for future reference
    private void updateDatabaseVersion(){
        Log.i(TAG, "===SQLITE: updateDatabaseVersion db");
        SQLiteDatabase copiedDb = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
        Cursor cursor = copiedDb.rawQuery("PRAGMA user_version",null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                currentDBVersion = cursor.getInt(0);
                Log.i(TAG, "===SQLITE: updateDatabaseVersion db: " + currentDBVersion);
                cursor.moveToNext();
            }
        } else{
            Log.i(TAG, "===SQLITE: updateDatabaseVersion db.... Failed");
        }
        copiedDb.close();
        AnalyticsHelper.dbVersion = instance.getCurrentDBVersion(); //update database version to analytics
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "===SQLITE: onCreate db");
        createDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "===SQLITE: onUpgrade db");
        upgradeDb = true;
    }

    //override onDowngrad method because some crash issues happen on some devices on production without any reason.
    // We suspect that there are something wrong with the DATABASE_VERSION variable then it causes
    // onDowngrad method to be performed. Originally, the onDowngrad method will throw exception
    // then it causes a crash
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing to bypass the crash due to the exception thrown by the original onDowngrade method.
        Log.i(TAG, "===SQLITE: onDowngrade db");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "===SQLITE: onOpen db");
        super.onOpen(db);
        db.disableWriteAheadLogging();
        if (createDb) {// The db in the application package
            // context is being created.
            // So copy the contents from the db
            // file packaged in the assets
            // folder:
            createDb = false;
            copyDatabaseFromAssets(db);

        }
        if (upgradeDb) {// The db in the application package
            // context is being upgraded from a lower to a higher version.
            upgradeDb = false;
            copyDatabaseFromAssets(db);
            // Your db upgrade logic here:
        }

        if (currentDBVersion == 0) {
            updateDatabaseVersion();
        }
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = this.getWritableDatabase();
    }

    public int getCurrentDBVersion(){
        return this.currentDBVersion;
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (this != null) {
            this.database.close();
        }
    }

    public Cursor executeQuery(String query) {
        if (query.toLowerCase().startsWith("select")) {
            return this.database.rawQuery(query, null);
        }else {
            this.database.execSQL(query);
            return null;
        }
    }
}
