package com.yxnsx.simpletodolist

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.yxnsx.simpletodolist.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ItemListeners {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setUserAuth()
        setListeners()
        setRecyclerView()
    }

    private fun setUserAuth() {
        firebaseAuth = Firebase.auth
        when (firebaseAuth.currentUser) {
            null -> setInitialUserAuth()
            else -> setViewModel(firebaseAuth.currentUser!!.uid)
        }
    }

    private fun setInitialUserAuth() {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->
            when (task.isSuccessful) {
                true -> setViewModel(firebaseAuth.currentUser!!.uid)
                false -> Log.d("TAG", "setUserAuth: failure", task.exception)
            }
        }
    }

    private fun setListeners() {
        viewBinding.buttonAdd.setOnClickListener(activityOnClickListener)
    }

    private val activityOnClickListener = View.OnClickListener {
        when (it) {
            viewBinding.buttonAdd -> addToDoItem()
        }
    }

    private fun addToDoItem() {
        val todo = TodoModel(viewBinding.editTextTodo.text.toString())
        mainViewModel.addTodo(todo)
        viewBinding.editTextTodo.setText("")
        hideKeyboard()
    }

    private fun setRecyclerView() {
        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TodoAdapter(emptyList(), this@MainActivity)
        }
    }

    private fun setViewModel(userId: String) {
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(userId))
            .get(MainViewModel::class.java)
        mainViewModel.todoLiveData.observe(this, Observer {
            (viewBinding.recyclerView.adapter as TodoAdapter).setLiveData(it)
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(viewBinding.root.windowToken, 0)
    }

    override fun onClickDeleteIcon(todo: DocumentSnapshot) {
        mainViewModel.deleteTodo(todo)
    }

    override fun onClickTodoItem(todo: DocumentSnapshot) {
        mainViewModel.doneTodo(todo)
    }
}