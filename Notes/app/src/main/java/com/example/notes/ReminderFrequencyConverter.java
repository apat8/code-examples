package com.example.notes;

import androidx.room.TypeConverter;

/**
 *  TypeConverter for ReminderFrequency.
 *  ReminderFrequency is stored in the database as an int which represents the position as defined
 *  For example WEEKLY is defined as the second ReminderFrequency thus its int value is 2.
 */

public class ReminderFrequencyConverter {

    @TypeConverter
    public static ReminderFrequency toReminderFrequency(int frequencyInteger){
        switch (frequencyInteger){
            case 1:
                return ReminderFrequency.DAILY;
            case 2:
                return ReminderFrequency.WEEKLY;
            case 3:
                return ReminderFrequency.MONTHLY;
            default:
                return ReminderFrequency.ONCE;
        }
    }

    @TypeConverter
    public static int toInt(ReminderFrequency reminderFrequency){
        return reminderFrequency.ordinal();
    }
}
