package com.yxnsx.simpletodolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding


class TodoAdapter(
    private var myDataset: List<DocumentSnapshot>,
    val onClickDeleteIcon: (todo: DocumentSnapshot) -> Unit,
    val onClickTodoItem: (todo: DocumentSnapshot) -> Unit
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

        if (todo.getBoolean("isDone") == true) {
            holder.todoBinding.textViewTodo.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            holder.todoBinding.textViewTodo.apply {
                paintFlags = 0
            }
        }

        holder.todoBinding.apply {
            textViewTodo.text = todo.getString("text")
            imageButtonDelete.setOnClickListener {
                onClickDeleteIcon.invoke(todo)
            }
            root.setOnClickListener {
                onClickTodoItem.invoke(todo)
            }
        }
    }

    override fun getItemCount() = myDataset.size

    fun setLiveData(newData: List<DocumentSnapshot>) {
        myDataset = newData
        notifyDataSetChanged()
    }
}