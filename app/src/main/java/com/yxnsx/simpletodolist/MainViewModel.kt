package com.yxnsx.simpletodolist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainViewModel(
    private val userId: String
) : ViewModel() {

    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()
    private val database = Firebase.firestore


    init {
        database.collection(userId)
            .orderBy("timeStamp", Query.Direction.ASCENDING) //시간 오름차순으로 정렬
            .addSnapshotListener { value, error ->
                if (error != null) { return@addSnapshotListener }
                if (value != null) { todoLiveData.value = value.documents }
                else { Log.d("TAG", "MainViewModel : 가져올 값이 없음") }
            }
    }

    fun addTodo(todo: TodoModel) {
        database.collection(userId).add(todo)
            .addOnSuccessListener { Log.d("TAG", "addTodo: SUCCESS") }
            .addOnFailureListener { error ->
                Log.d("TAG", "addTodo: Error adding document", error)
            }

    }

    fun deleteTodo(todo: DocumentSnapshot) {
        database.collection(userId).document(todo.id).delete()
            .addOnSuccessListener { Log.d("TAG", "deleteTodo: SUCCESS") }
            .addOnFailureListener { error ->
                Log.d("TAG", "deleteTodo: Error adding document", error)
            }

    }

    fun doneTodo(todo: DocumentSnapshot) {
        val done = todo.getBoolean("done") ?: false
        database.collection(userId).document(todo.id).update("done", !done)
            .addOnSuccessListener { Log.d("TAG", "doneTodo: SUCCESS") }
            .addOnFailureListener { error ->
                Log.d("TAG", "doneTodo: Error adding document", error)
            }
    }
}