package com.example.bodymanagerapp.menu.Exercise

class ExerciseData() {
    var date = 0
    var name = ""
    var parts = ""
    var set = ArrayList<Int>()
    var num = ArrayList<Int>()
    var weight = ArrayList<Float>()
    var time = ArrayList<Int>()
    var complete = ArrayList<Int>()

    constructor(name : String, parts : String) : this() {
        this.name = name
        this.parts = parts
    }

    constructor(num : ArrayList<Int>?, weight : ArrayList<Float>?, time : ArrayList<Int>?) : this(){
        this.num = num!!
        this.weight = weight!!
        this.time = time!!
    }

    constructor(date : Int, name : String, set : ArrayList<Int>, num : ArrayList<Int>?,
                weight : ArrayList<Float>?, time : ArrayList<Int>?, complete : ArrayList<Int>): this(num, weight, time){
        this.date = date
        this.name = name
        this.set = set
        this.complete = complete
    }
}