package com.example.mapsgt.database.converters;

import androidx.room.TypeConverter;

import com.example.mapsgt.enumeration.PrivacyLevelStatusEnum;

public class PrivacyLevelStatusConverter {
    @TypeConverter
    public static PrivacyLevelStatusEnum fromString(String value) {
        return PrivacyLevelStatusEnum.valueOf(value);
    }

    @TypeConverter
    public static String enumToString(PrivacyLevelStatusEnum value) {
        return value.name();
    }
}
