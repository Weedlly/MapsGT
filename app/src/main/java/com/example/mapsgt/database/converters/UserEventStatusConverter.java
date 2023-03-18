package com.example.mapsgt.database.converters;

import androidx.room.TypeConverter;

import com.example.mapsgt.enumeration.UserEventStatusEnum;

public class UserEventStatusConverter {
    @TypeConverter
    public static UserEventStatusEnum fromString(String value) {
        return UserEventStatusEnum.valueOf(value);
    }

    @TypeConverter
    public static String enumToString(UserEventStatusEnum value) {
        return value.name();
    }
}
