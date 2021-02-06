package com.yxnsx.simpletodolist

import android.view.View
import com.google.firebase.firestore.DocumentSnapshot

interface ItemListeners {
    fun onClickDeleteIcon(todo: DocumentSnapshot)
    fun onClickTodoItem(todo: DocumentSnapshot)
}