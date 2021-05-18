package com.example.bodymanagerapp.menu.Stats.Exercise

class ExerciseStatsData(var date : Int, var setNum : Int, var weightList : ArrayList<Float>?,
                        var exerciseCount : ArrayList<Int>?, var timeList : ArrayList<Int>?) {

    var part : String = ""
    var name : String = ""

    constructor(date : Int, name : String, part : String, setNum : Int, weightList : ArrayList<Float>?,
                exerciseCount : ArrayList<Int>?, timeList : ArrayList<Int>? )
            : this(date, setNum, weightList, exerciseCount, timeList) {
        this.name = name
        this.part = part
    }
}