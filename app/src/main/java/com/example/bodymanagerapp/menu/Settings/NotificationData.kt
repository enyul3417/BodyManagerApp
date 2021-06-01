package com.example.bodymanagerapp.menu.Settings

class NotificationData(val int : Int, val string : String) {
    var isChecked = false
    constructor(int : Int, string : String, isChecked : Boolean) : this(int, string) {
        this.isChecked = isChecked
    }
}