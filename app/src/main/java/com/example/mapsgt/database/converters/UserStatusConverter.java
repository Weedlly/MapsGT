package com.example.mapsgt.database.converters;

import androidx.room.TypeConverter;

import com.example.mapsgt.enumeration.UserStatusEnum;

public class UserStatusConverter {
    @TypeConverter
    public static UserStatusEnum fromString(String value) {
        return UserStatusEnum.valueOf(value);
    }

    @TypeConverter
    public static String enumToString(UserStatusEnum value) {
        return value.name();
    }
}
