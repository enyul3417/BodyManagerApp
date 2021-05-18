package com.example.bodymanagerapp.menu.Stats.Body

class BodyDietData(var date : Int) {
    var time : Int = 0
    var weight = 0f
    var muscle_mass = 0f
    var fat_mass = 0f
    var bmi = 0f
    var fat_percent = 0f

    constructor(date: Int, time : Int) : this(date) {
        this.time = time
    }

    constructor(date: Int, weight : Float, muscle_mass : Float, fat_mass : Float,
                bmi : Float, fat_percent : Float) : this(date) {
        this.weight = weight
        this.muscle_mass = muscle_mass
        this.fat_mass = fat_mass
        this.bmi = bmi
        this.fat_percent = fat_percent
    }
}