package com.example.mapsgt.database.converters;

import androidx.room.TypeConverter;

import com.example.mapsgt.enumeration.UserGenderEnum;


public class UserGenderConverter {
    @TypeConverter
    public static UserGenderEnum fromString(String value) {
        return UserGenderEnum.valueOf(value);
    }

    @TypeConverter
    public static String enumToString(UserGenderEnum value) {
        return value.name();
    }
}

