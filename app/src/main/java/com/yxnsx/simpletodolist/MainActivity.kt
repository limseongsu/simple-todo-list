package com.yxnsx.simpletodolist

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yxnsx.simpletodolist.databinding.ActivityMainBinding
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private val todoData = arrayListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerView.adapter = TodoAdapter(todoData)
        viewBinding.buttonAdd.setOnClickListener {
            addTodo()
        }
    }

    private fun addTodo() {
        val todo = Todo(viewBinding.editTextTodo.text.toString())
        todoData.add(todo)

        hideKeyboard(viewBinding.root)
        viewBinding.editTextTodo.setText("")
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

data class Todo (
    val text: String,
    var isDone: Boolean = false
)

class TodoAdapter(private val myDataset: List<Todo>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(val todoBinding: ItemTodoBinding) : RecyclerView.ViewHolder(todoBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TodoAdapter.TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.todoBinding.textView.text = myDataset[position].text
    }

    override fun getItemCount() = myDataset.size
}