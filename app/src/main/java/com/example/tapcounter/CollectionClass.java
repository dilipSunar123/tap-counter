package com.example.tapcounter;

import android.provider.BaseColumns;

public class CollectionClass {
    CollectionClass () {}

    public static class CollectionInnerClass implements BaseColumns {
        public static final String TABLE_NAME = "counter";
        public static final String COLUMN_NAME = "name";
    }
}
