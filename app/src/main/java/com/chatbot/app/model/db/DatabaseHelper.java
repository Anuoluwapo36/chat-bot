package com.chatbot.app.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.model.entity.Message;
import com.chatbot.app.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseContract.Users.TABLE + " (" +
                DatabaseContract.Users.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.Users.COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                DatabaseContract.Users.COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                DatabaseContract.Users.COL_DISPLAY_NAME + " TEXT, " +
                DatabaseContract.Users.COL_CREATED_AT + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + DatabaseContract.Conversations.TABLE + " (" +
                DatabaseContract.Conversations.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.Conversations.COL_USER_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Conversations.COL_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.Conversations.COL_MODEL + " TEXT NOT NULL, " +
                DatabaseContract.Conversations.COL_CREATED_AT + " INTEGER NOT NULL, " +
                DatabaseContract.Conversations.COL_UPDATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + DatabaseContract.Conversations.COL_USER_ID + ") REFERENCES " +
                DatabaseContract.Users.TABLE + "(" + DatabaseContract.Users.COL_ID + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + DatabaseContract.Messages.TABLE + " (" +
                DatabaseContract.Messages.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.Messages.COL_CONVERSATION_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Messages.COL_ROLE + " TEXT NOT NULL, " +
                DatabaseContract.Messages.COL_CONTENT + " TEXT NOT NULL, " +
                DatabaseContract.Messages.COL_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + DatabaseContract.Messages.COL_CONVERSATION_ID + ") REFERENCES " +
                DatabaseContract.Conversations.TABLE + "(" + DatabaseContract.Conversations.COL_ID + ") ON DELETE CASCADE)");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Messages.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Conversations.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Users.TABLE);
        onCreate(db);
    }

    public long registerUser(String email, String passwordHash, String displayName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.COL_EMAIL, email.toLowerCase().trim());
        values.put(DatabaseContract.Users.COL_PASSWORD_HASH, passwordHash);
        values.put(DatabaseContract.Users.COL_DISPLAY_NAME, displayName);
        values.put(DatabaseContract.Users.COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(DatabaseContract.Users.TABLE, null, values);
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Users.TABLE, null,
                DatabaseContract.Users.COL_EMAIL + "=?",
                new String[]{email.toLowerCase().trim()}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = mapUser(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Users.TABLE, null,
                DatabaseContract.Users.COL_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = mapUser(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean updateDisplayName(long userId, String displayName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.COL_DISPLAY_NAME, displayName);
        return db.update(DatabaseContract.Users.TABLE, values,
                DatabaseContract.Users.COL_ID + "=?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean deleteUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        deleteAllConversationsForUser(userId);
        return db.delete(DatabaseContract.Users.TABLE,
                DatabaseContract.Users.COL_ID + "=?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public long createConversation(long userId, String title, String model) {
        SQLiteDatabase db = getWritableDatabase();
        long now = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Conversations.COL_USER_ID, userId);
        values.put(DatabaseContract.Conversations.COL_TITLE, title);
        values.put(DatabaseContract.Conversations.COL_MODEL, model);
        values.put(DatabaseContract.Conversations.COL_CREATED_AT, now);
        values.put(DatabaseContract.Conversations.COL_UPDATED_AT, now);
        return db.insert(DatabaseContract.Conversations.TABLE, null, values);
    }

    public List<Conversation> getConversations(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Conversations.TABLE, null,
                DatabaseContract.Conversations.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null,
                DatabaseContract.Conversations.COL_UPDATED_AT + " DESC");
        List<Conversation> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(mapConversation(cursor));
        }
        cursor.close();
        return list;
    }

    public Conversation getConversation(long conversationId, long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Conversations.TABLE, null,
                DatabaseContract.Conversations.COL_ID + "=? AND " +
                        DatabaseContract.Conversations.COL_USER_ID + "=?",
                new String[]{String.valueOf(conversationId), String.valueOf(userId)},
                null, null, null);
        Conversation conversation = null;
        if (cursor.moveToFirst()) {
            conversation = mapConversation(cursor);
        }
        cursor.close();
        return conversation;
    }

    public boolean updateConversationTitle(long conversationId, long userId, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Conversations.COL_TITLE, title);
        values.put(DatabaseContract.Conversations.COL_UPDATED_AT, System.currentTimeMillis());
        return db.update(DatabaseContract.Conversations.TABLE, values,
                DatabaseContract.Conversations.COL_ID + "=? AND " +
                        DatabaseContract.Conversations.COL_USER_ID + "=?",
                new String[]{String.valueOf(conversationId), String.valueOf(userId)}) > 0;
    }

    public boolean updateConversationTimestamp(long conversationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Conversations.COL_UPDATED_AT, System.currentTimeMillis());
        return db.update(DatabaseContract.Conversations.TABLE, values,
                DatabaseContract.Conversations.COL_ID + "=?",
                new String[]{String.valueOf(conversationId)}) > 0;
    }

    public boolean deleteConversation(long conversationId, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DatabaseContract.Messages.TABLE,
                DatabaseContract.Messages.COL_CONVERSATION_ID + "=?",
                new String[]{String.valueOf(conversationId)});
        return db.delete(DatabaseContract.Conversations.TABLE,
                DatabaseContract.Conversations.COL_ID + "=? AND " +
                        DatabaseContract.Conversations.COL_USER_ID + "=?",
                new String[]{String.valueOf(conversationId), String.valueOf(userId)}) > 0;
    }

    public void deleteAllConversationsForUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        List<Conversation> conversations = getConversations(userId);
        for (Conversation conversation : conversations) {
            db.delete(DatabaseContract.Messages.TABLE,
                    DatabaseContract.Messages.COL_CONVERSATION_ID + "=?",
                    new String[]{String.valueOf(conversation.getId())});
        }
        db.delete(DatabaseContract.Conversations.TABLE,
                DatabaseContract.Conversations.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    public long saveMessage(long conversationId, String role, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Messages.COL_CONVERSATION_ID, conversationId);
        values.put(DatabaseContract.Messages.COL_ROLE, role);
        values.put(DatabaseContract.Messages.COL_CONTENT, content);
        values.put(DatabaseContract.Messages.COL_CREATED_AT, System.currentTimeMillis());
        long id = db.insert(DatabaseContract.Messages.TABLE, null, values);
        updateConversationTimestamp(conversationId);
        return id;
    }

    public List<Message> getMessages(long conversationId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Messages.TABLE, null,
                DatabaseContract.Messages.COL_CONVERSATION_ID + "=?",
                new String[]{String.valueOf(conversationId)}, null, null,
                DatabaseContract.Messages.COL_CREATED_AT + " ASC");
        List<Message> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(mapMessage(cursor));
        }
        cursor.close();
        return list;
    }

    private User mapUser(Cursor cursor) {
        return new User(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COL_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COL_PASSWORD_HASH)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COL_DISPLAY_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COL_CREATED_AT))
        );
    }

    private Conversation mapConversation(Cursor cursor) {
        return new Conversation(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_MODEL)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_CREATED_AT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Conversations.COL_UPDATED_AT))
        );
    }

    private Message mapMessage(Cursor cursor) {
        return new Message(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Messages.COL_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Messages.COL_CONVERSATION_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Messages.COL_ROLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Messages.COL_CONTENT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Messages.COL_CREATED_AT))
        );
    }
}
