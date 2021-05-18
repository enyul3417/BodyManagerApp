package com.example.bodymanagerapp.menu.Stats.Exercise

class ExerciseStatsData(var date : Int) {

    var part : String = ""
    var name : String = ""
    var setNum : Int = 0
    var weightList : ArrayList<Float>? = null
    var exerciseCount : ArrayList<Int>? = null
    var timeList : ArrayList<Int>? = null

    constructor(date : Int, setNum : Int, weightList : ArrayList<Float>?,
                exerciseCount : ArrayList<Int>?, timeList : ArrayList<Int>?) : this(date) {
        this.setNum = setNum
        this.weightList = weightList
        this.exerciseCount = exerciseCount
        this.timeList = timeList
    }
    constructor(date : Int, name : String, part : String) : this(date) {
        this.name = name
        this.part = part
    }
}