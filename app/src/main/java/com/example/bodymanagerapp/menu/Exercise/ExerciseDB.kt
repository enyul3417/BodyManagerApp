package com.example.bodymanagerapp.menu.Exercise

class ExerciseDB(var exerciseName : String, var parts : String) {
    @JvmName("getExerciseName1")
    fun getExerciseName() : String{
        return exerciseName
    }
    @JvmName("getParts1")
    fun getParts() : String {
        return  parts
    }

    override fun toString(): String {
        return "Exercise { exerciseName = ${exerciseName}, parts = ${parts}}"
    }
}