package com.chatbot.app.model.db;

public final class DatabaseContract {

    private DatabaseContract() {
    }

    public static final String DB_NAME = "chatbot.db";
    public static final int DB_VERSION = 1;

    public static final class Users {
        public static final String TABLE = "users";
        public static final String COL_ID = "id";
        public static final String COL_EMAIL = "email";
        public static final String COL_PASSWORD_HASH = "password_hash";
        public static final String COL_DISPLAY_NAME = "display_name";
        public static final String COL_CREATED_AT = "created_at";
    }

    public static final class Conversations {
        public static final String TABLE = "conversations";
        public static final String COL_ID = "id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_TITLE = "title";
        public static final String COL_MODEL = "model";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
    }

    public static final class Messages {
        public static final String TABLE = "messages";
        public static final String COL_ID = "id";
        public static final String COL_CONVERSATION_ID = "conversation_id";
        public static final String COL_ROLE = "role";
        public static final String COL_CONTENT = "content";
        public static final String COL_CREATED_AT = "created_at";
    }
}
