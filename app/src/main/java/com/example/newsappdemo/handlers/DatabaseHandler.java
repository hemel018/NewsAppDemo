package com.example.newsappdemo.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.newsappdemo.entity.NewsFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rana on 7/16/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "newsDB";

    // Contacts table name
    private static final String TABLE_NEWS = "news";

    private static final String KEY_ID = "id";
    private static final String KEY_SERVER_ID = "sid";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NEWS_DETAILS = "url";
    private static final String KEY_AUTHOR = "author";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_SERVER_ID + " TEXT, " + KEY_CATEGORY_ID + " INTEGER," + KEY_IMAGE_URL + " TEXT,"
                + KEY_TITLE + " TEXT," + KEY_NEWS_DETAILS + " TEXT, " + KEY_AUTHOR + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);

        // Create tables again
        onCreate(db);
    }

    public NewsFeed[] getAllNewsByCategory(int categoryId)
    {
        List<NewsFeed> newsFeedList = new ArrayList<NewsFeed>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NEWS + " WHERE " + KEY_CATEGORY_ID + "=" + categoryId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String sid = cursor.getString(1);
                int category = Integer.parseInt(cursor.getString(2));
                String titleImageUrl = cursor.getString(3);
                String title = cursor.getString(4);
                String url = cursor.getString(5);
                String author = cursor.getString(6);

                NewsFeed newsFeed = new NewsFeed(sid, category, titleImageUrl, title, url, author);
                newsFeedList.add(newsFeed);
                count++;
            } while (cursor.moveToNext());
        }

        NewsFeed[] result = new NewsFeed[newsFeedList.size()];
        return newsFeedList.toArray(result);
    }

    public void addNewsFeed(NewsFeed newsFeed)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            db.delete(TABLE_NEWS, KEY_SERVER_ID+" = ?", new String[] { newsFeed.getSid() });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, newsFeed.getId());
        values.put(KEY_SERVER_ID, newsFeed.getSid());
        values.put(KEY_CATEGORY_ID, newsFeed.getCategoryId());
        values.put(KEY_IMAGE_URL, newsFeed.getTitleImage());
        values.put(KEY_TITLE, newsFeed.getTitle());
        values.put(KEY_NEWS_DETAILS, "");
        values.put(KEY_AUTHOR, newsFeed.getAuthor());
        // Inserting Row
        db.insert(TABLE_NEWS, null, values);
        db.close(); // Closing database connection
    }
}