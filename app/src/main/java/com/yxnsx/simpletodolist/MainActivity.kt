package com.yxnsx.simpletodolist

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yxnsx.simpletodolist.databinding.ActivityMainBinding
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth
    private var userID: String = ""

    companion object {
        const val TAG = "디버깅"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        val currentUser = firebaseAuth.currentUser
        Log.d(TAG, "onCreate: currentUser = $currentUser")

        if(currentUser != null) {
            userID = currentUser.uid
        } else {
            firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInAnonymously:success")
                    val newUser = firebaseAuth.currentUser
                    userID = newUser!!.uid
                    Log.d(TAG, "onCreate: newUser = $newUser")

                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                }
            }
        }

        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TodoAdapter(
                emptyList(),
                onClickDeleteIcon = {
                    mainViewModel.deleteTodo(it)
                },
                onClickTodoItem = {
                    mainViewModel.doneTodo(it)
                }
            )
        }
        viewBinding.buttonAdd.setOnClickListener {
            val todo: TodoModel = TodoModel(viewBinding.editTextTodo.text.toString())
            mainViewModel.addTodo(todo)

            hideKeyboard(viewBinding.root)
            viewBinding.editTextTodo.setText("")
        }

        mainViewModel.todoLiveData.observe(this, Observer {
            (viewBinding.recyclerView.adapter as TodoAdapter).setLiveData(it)
        })
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}