package com.ort.smartacc;

import android.database.Cursor;

public interface SQLiteConnector {
    Cursor doQuery(String table, String[] columns, String selection,
                   String[] selectionArgs, String groupBy, String having,
                   String orderBy);
    Cursor doRawQuery(String sql, String[] args);
}
