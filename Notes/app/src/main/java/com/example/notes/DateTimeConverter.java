package com.example.notes;

import java.util.Date;

import androidx.room.TypeConverter;

/**
 * TypeConverter for Date object.
 * Date is stored in the database as a Long.
 */

public class DateTimeConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
