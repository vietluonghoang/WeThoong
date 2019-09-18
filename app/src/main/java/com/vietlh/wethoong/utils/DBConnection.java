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
    private static final int DATABASE_VERSION = GeneralSettings.dbVersion;
    private static final String DATABASE_NAME = "Hieuluat";

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
        Log.i(TAG, "copyDatabase");
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
            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
                    DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();

        } catch (IOException e) {
            Log.i(TAG, "ERROR: Failed on Copying Database: \n" + e.getMessage());
            for (StackTraceElement trace :
                    e.getStackTrace()) {
                Log.i(TAG, trace.toString());
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate db");
        createDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade db");
        upgradeDb = true;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "onOpen db");
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
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = this.getWritableDatabase();
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
        return this.database.rawQuery(query, null);
    }
}
