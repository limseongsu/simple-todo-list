package com.yxnsx.simpletodolist

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yxnsx.simpletodolist.databinding.ActivityMainBinding
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TodoAdapter(
                mainViewModel.todoData,
                onClickDeleteIcon = {
                    mainViewModel.deleteTodo(it)
                    viewBinding.recyclerView.adapter?.notifyDataSetChanged()
                },
                onClickTodoItem = {
                    mainViewModel.doneTodo(it)
                    viewBinding.recyclerView.adapter?.notifyDataSetChanged()
                }
            )
        }
        viewBinding.buttonAdd.setOnClickListener {
            val todo: Todo = Todo(viewBinding.editTextTodo.text.toString())
            mainViewModel.addTodo(todo)
            viewBinding.recyclerView.adapter?.notifyDataSetChanged()

            hideKeyboard(viewBinding.root)
            viewBinding.editTextTodo.setText("")
        }
    }

//    private fun addTodo() {
//        val todo = Todo(viewBinding.editTextTodo.text.toString())
//        todoData.add(todo)
//
//        hideKeyboard(viewBinding.root)
//        viewBinding.editTextTodo.setText("")
//        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
//    }
//
//    private fun deleteTodo(todo: Todo) {
//        todoData.remove(todo)
//        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
//    }
//
//    private fun doneTodo(todo: Todo) {
//        todo.isDone = !todo.isDone
//        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
//    }

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
    private val myDataset: List<Todo>,
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
}

class MainViewModel: ViewModel() {
    val todoData = arrayListOf<Todo>()

    fun addTodo(todo: Todo) {
        todoData.add(todo)
    }

    fun deleteTodo(todo: Todo) {
        todoData.remove(todo)
    }

    fun doneTodo(todo: Todo) {
        todo.isDone = !todo.isDone
    }
}