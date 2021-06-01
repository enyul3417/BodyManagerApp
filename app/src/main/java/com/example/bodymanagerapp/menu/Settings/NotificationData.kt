package com.example.bodymanagerapp.menu.Settings

class NotificationData(val id : Int, val int : Int, val string : String) {
    var isChecked = false
    constructor(id : Int, int : Int, string : String, isChecked : Boolean) : this(id, int, string) {
        this.isChecked = isChecked
    }
}