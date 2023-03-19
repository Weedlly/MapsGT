package com.example.mapsgt.database.converters;

import androidx.room.TypeConverter;

import com.example.mapsgt.enumeration.FriendshipStatusEnum;

public class FriendshipStatusConverter {
    @TypeConverter
    public static FriendshipStatusEnum fromString(String value) {
        return FriendshipStatusEnum.valueOf(value);
    }

    @TypeConverter
    public static String enumToString(FriendshipStatusEnum value) {
        return value.name();
    }
}
