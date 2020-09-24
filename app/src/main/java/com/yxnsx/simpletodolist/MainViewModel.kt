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
    private val database = Firebase.firestore
    private val user = Firebase.auth.currentUser

    companion object {
        const val TAG = "디버깅"
    }

    init {
        // 유저가 null이 아닐 경우
        if (user != null) {
            // uid 값을 바탕으로 데이터베이스 가져오기
            database.collection(user.uid)
                .addSnapshotListener { value, error ->
                    // 에러가 발생한 경우
                    if(error != null) {
                        return@addSnapshotListener
                    }
                    // 가져올 값이 있을 경우
                    if(value != null) {
                        // todoLiveData 리스트에 DocumentSnapshot 형태로 값 담기
                        todoLiveData.value = value.documents

                    } else { // 가져올 값이 없을 경우
                        Log.d(TAG, "MainViewModel : 가져올 값이 없음")
                    }
                }
        }
    }

    fun addTodo(todo: TodoModel) {
        // 유저가 null이 아닐 경우
        user?.let { user ->
            // uid 값을 바탕으로 데이터베이스에 데이터 추가
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
        // 유저가 null이 아닐 경우
        user?.let { user ->
            // uid 값을 바탕으로 데이터베이스에서 DocumentSnapshot id 값과 일치하는 데이터 삭제
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
        // 유저가 null이 아닐 경우
        user?.let { user ->
            // isDone이 null일 경우 false로 지정
            val isDone = todo.getBoolean("isDone") ?: false

            // uid 값을 바탕으로 데이터베이스에서 DocumentSnapshot id 값과 일치하는 데이터의 isDone 값 반전
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