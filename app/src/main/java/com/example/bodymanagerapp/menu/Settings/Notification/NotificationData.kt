package com.example.bodymanagerapp.menu.Settings.Notification

class NotificationData(val id : Int, val int : Int, val string : String, val isChecked : Boolean) {
    var days = ""
    constructor(id : Int, int : Int, string : String, isChecked : Boolean, days : String) : this(id, int, string, isChecked) {
        this.days = days
    }
}