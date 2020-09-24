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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
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
            val todo: Todo = Todo(viewBinding.editTextTodo.text.toString())
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

data class Todo(
    val text: String,
    var isDone: Boolean = false
)

class TodoAdapter(
    private var myDataset: List<Todo>,
    val onClickDeleteIcon: (todo: Todo) -> Unit,
    val onClickTodoItem: (todo: Todo) -> Unit
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(val todoBinding: ItemTodoBinding) :
        RecyclerView.ViewHolder(todoBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = myDataset[position]

        if (todo.isDone) {
            holder.todoBinding.textView.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            holder.todoBinding.textView.apply {
                paintFlags = 0
            }
        }

        holder.todoBinding.apply {
            textView.text = todo.text
            imageButton.setOnClickListener {
                onClickDeleteIcon.invoke(todo)
            }
            root.setOnClickListener {
                onClickTodoItem.invoke(todo)
            }
        }
    }

    override fun getItemCount() = myDataset.size

    fun setLiveData(newData: List<Todo>) {
        myDataset = newData
        notifyDataSetChanged()
    }
}

class MainViewModel: ViewModel() {
    private val todoData = arrayListOf<Todo>()
    val todoLiveData = MutableLiveData<List<Todo>>()

    fun addTodo(todo: Todo) {
        todoData.add(todo)
        todoLiveData.value = todoData
    }

    fun deleteTodo(todo: Todo) {
        todoData.remove(todo)
        todoLiveData.value = todoData
    }

    fun doneTodo(todo: Todo) {
        todo.isDone = !todo.isDone
        todoLiveData.value = todoData
    }
}