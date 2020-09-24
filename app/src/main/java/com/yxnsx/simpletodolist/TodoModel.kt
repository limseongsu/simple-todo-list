package com.yxnsx.simpletodolist

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class TodoModel(
    val text: String,
    var done: Boolean = false,
    @ServerTimestamp val timeStamp: Timestamp? = null
)