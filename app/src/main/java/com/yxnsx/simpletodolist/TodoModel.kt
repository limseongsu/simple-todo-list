package com.yxnsx.simpletodolist

data class TodoModel(
    val text: String,
    var isDone: Boolean = false
)