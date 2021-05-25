package com.example.bodymanagerapp.menu.Exercise

class ExerciseDB() {
    var exerciseName = ""
    var parts = ""

    constructor(exerciseName : String, parts : String) : this() {
        this.exerciseName = exerciseName
        this.parts = parts
    }

    /*@JvmName("getExerciseName1")
    fun getExerciseName() : String{
        return exerciseName
    }
    @JvmName("getParts1")
    fun getParts() : String {
        return  parts
    }

    override fun toString(): String {
        return "Exercise { exerciseName = ${exerciseName}, parts = ${parts}}"
    }*/
}