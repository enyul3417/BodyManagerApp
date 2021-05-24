package com.example.bodymanagerapp.menu.Exercise

class ExerciseData(val num : ArrayList<Int>?, val weight : ArrayList<Float>?, val time : ArrayList<Int>?) {
    var date = 0
    var name = ""
    var set = ArrayList<Int>()

    constructor(date : Int, name : String, set : ArrayList<Int>, num : ArrayList<Int>?,
                weight : ArrayList<Float>?, time : ArrayList<Int>?): this(num, weight, time){
        this.date = date
        this.name = name
        this.set = set
    }
}