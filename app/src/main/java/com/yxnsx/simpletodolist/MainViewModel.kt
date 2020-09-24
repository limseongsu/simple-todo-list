package com.yxnsx.simpletodolist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainViewModel: ViewModel() {
    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()
    val database = Firebase.firestore
    val user = Firebase.auth.currentUser

    companion object {
        const val TAG = "디버깅"
    }

    init {
        if (user != null) {
            database.collection(user.uid)
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        return@addSnapshotListener
                    }
                    if(value != null) {
                        todoLiveData.value = value.documents
                    }
                }
        }
    }

    fun addTodo(todo: TodoModel) {
        user?.let { user ->
            database.collection(user.uid).add(todo)
                .addOnSuccessListener {
                    Log.d(TAG, "addTodo: SUCCESS")
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "addTodo: Error adding document", error)
                }
        }
    }

    fun deleteTodo(todo: DocumentSnapshot) {
        user?.let { user ->
            database.collection(user.uid).document(todo.id).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteTodo: SUCCESS")
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "deleteTodo: Error adding document", error)
                }
        }
    }

    fun doneTodo(todo: DocumentSnapshot) {
        user?.let { user ->
            val isDone = todo.getBoolean("isDone") ?: false
            database.collection(user.uid).document(todo.id).update("isDone", !isDone)
                .addOnSuccessListener {
                    Log.d(TAG, "doneTodo: SUCCESS")
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "doneTodo: Error adding document", error)
                }
        }
    }
}