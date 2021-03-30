package com.example.bodymanagerapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class myDBHelper(context : Context) : SQLiteOpenHelper(context, "bmDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // 외래키 활성화
        db?.execSQL("PRAGMA foreign_keys = 1;")

        // 운동 정보 테이블 생성
        db?.execSQL("CREATE TABLE exercise_info (exercise_name TEXT PRIMARY KEY, part TEXT);")

        // 루틴 정보 테이블 생성
        db?.execSQL("CREATE TABLE routine_info (routine_name TEXT, Exercise_name TEXT);")

        // 운동 기록 테이블 생성

        // 식단 기록 테이블 생성
        db?.execSQL("CREATE TABLE diet_record (" +
                "date INTEGER, " +
                "time INTEGER, " +
                "diet_photo BLOB, " +
                "memo TEXT," +
                "PRIMARY KEY(date, time));")

        // 신체 기록 테이블 생성
        db?.execSQL("CREATE TABLE body_record (date INTEGER PRIMARY KEY, height INTEGER, weight INTEGER, " +
                "muscle_mass INTEGER, fat_mass INTEGER, body_photo BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS exercise_info")
        db?.execSQL("DROP TABLE IF EXISTS routine_info")
        db?.execSQL("DROP TABLE IF EXISTS diet_record")
        db?.execSQL("DROP TABLE IF EXISTS body_record")
        onCreate(db)
    }


}