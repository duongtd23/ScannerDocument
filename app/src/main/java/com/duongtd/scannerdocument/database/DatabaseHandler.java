package com.duongtd.scannerdocument.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duongtd.scannerdocument.object.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duongtd on 17/02/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "documents_db";

    // Documents table name
    private static final String TABLE_DOCUMENTS = "documents";

    // Documents Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMG_THUMB = "img_thumb";
    private static final String KEY_DATE = "date";
    private static final String KEY_PDF_FILE = "pdf_file";

    private static DatabaseHandler instance;

    public static synchronized DatabaseHandler getInstance(Context context){
        if (instance == null) {
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DOCUMENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IMG_THUMB + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_DATE + " TEXT," + KEY_PDF_FILE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new document
    public void addDocument(Document document) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMG_THUMB, document.getImg_thumb());
        values.put(KEY_NAME, document.getName()); // Document Name
        values.put(KEY_DATE, document.getDate()); // Document Date
        values.put(KEY_PDF_FILE, document.getPdf_file());

        // Inserting Row
        db.insert(TABLE_DOCUMENTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single document
    Document getDocument(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_DOCUMENTS,
                new String[] { KEY_ID, KEY_IMG_THUMB, KEY_NAME, KEY_DATE, KEY_PDF_FILE },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Document document = new Document(
                Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        // return document
        return document;
    }

    // Getting All Documents
    public List<Document> getAllDocuments() {
        List<Document> documentList = new ArrayList<Document>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_DOCUMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Document document = new Document(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                                                cursor.getString(2), cursor.getString(3), cursor.getString(4));
                // Adding document to list
                documentList.add(document);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return document list
        return documentList;
    }

    // Updating single document
    public int updateDocument(Document document) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMG_THUMB, document.getImg_thumb());
        values.put(KEY_NAME, document.getName()); // Document Name
        values.put(KEY_DATE, document.getDate()); // Document Date
        values.put(KEY_PDF_FILE, document.getPdf_file());

        // updating row
        return db.update(TABLE_DOCUMENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(document.getId()) });
    }

    // Deleting single document
    public void deleteDocument(Document document) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(document.getId()) });
        db.close();
    }


    // Getting documents Count
    public int getDocumentsCount() {
        String countQuery = "SELECT * FROM " + TABLE_DOCUMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int re = cursor.getCount();
        cursor.close();

        // return count
        return re;
    }

}
