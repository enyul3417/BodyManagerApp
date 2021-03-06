package com.example.bodymanagerapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context : Context) : SQLiteOpenHelper(context, "bmDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // 외래키 활성화
        db?.execSQL("PRAGMA foreign_keys = 1;")

        // 루틴 정보 테이블 생성
        db?.execSQL("CREATE TABLE routine_info (" +
                "routine_name TEXT, " +
                "exercise_name TEXT, " +
                "tag TEXT, " +
                "set_num INTEGER, " +
                "weight REAL, " +
                "exercise_count INTEGER, " +
                "time INTEGER" +
                ")")

        // 운동 기록 테이블 생성
        db?.execSQL("CREATE TABLE exercise_record (" +
                "date INTEGER, " +
                "total_time INTEGER, " +
                "steps INTEGER);")

        // 운동 카운터 테이블 생성
        db?.execSQL("CREATE TABLE exercise_counter (" +
                "date INTEGER, " +
                "exercise_name TEXT, " +
                "tag TEXT, "+
                "set_num INTEGER, " +
                "weight REAL, " +
                "exercise_count INTEGER, " +
                "time INTEGER, " +
                "is_complete INTEGER);")

        // 식단 기록 테이블 생성
        db?.execSQL("CREATE TABLE diet_record (" +
                "DId INTEGER, " +
                "date INTEGER, " +
                "time INTEGER, " +
                "diet_photo BLOB, " +
                "memo TEXT, " +
                "PRIMARY KEY(DId AUTOINCREMENT));")

        // 신체 기록 테이블 생성
        db?.execSQL("CREATE TABLE body_record (" +
                "date INTEGER PRIMARY KEY, " +
                "height REAL, " +
                "weight REAL, " +
                "muscle_mass REAL, " +
                "fat_mass REAL, " +
                "bmi REAL, " +
                "fat_percent REAL, " +
                "body_photo BLOB);")

        // 목표 데이블 생성
        db?.execSQL("CREATE TABLE goal_table (" +
                "gId INTEGER, " +
                "goal TEXT, " +
                "date INTEGER, " +
                "achievement INTEGER, " +
                "PRIMARY KEY(gId AUTOINCREMENT));")

        db?.execSQL("CREATE TABLE time_table (" +
                "tId INTEGER, " +
                "time INTEGER, " +
                "days TEXT, " +
                "memo TEXT, " +
                "isOn INTEGER, " +
                "PRIMARY KEY(tId AUTOINCREMENT));")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS exercise_info")
        db?.execSQL("DROP TABLE IF EXISTS routine_info")
        db?.execSQL("DROP TABLE IF EXISTS exercise_record")
        db?.execSQL("DROP TABLE IF EXISTS exercise_counter")
        db?.execSQL("DROP TABLE IF EXISTS diet_record")
        db?.execSQL("DROP TABLE IF EXISTS body_record")
        db?.execSQL("DROP TABLE IF EXISTS goal_table")
        db?.execSQL("DROP TABLE IF EXISTS time_table")
        onCreate(db)
    }
}