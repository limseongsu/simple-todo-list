package com.yxnsx.simpletodolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding


class TodoAdapter(
    var documentSnapshotList: List<DocumentSnapshot>,
    val itemListeners: ItemListeners
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var _viewBinding: ItemTodoBinding? = null
    private val viewBinding get() = _viewBinding!!


    class TodoViewHolder(val todoBinding: ItemTodoBinding) :
        RecyclerView.ViewHolder(todoBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        _viewBinding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context))
        return TodoViewHolder(viewBinding)
    }

    override fun getItemCount() = documentSnapshotList.size
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = documentSnapshotList[position]
        setTodoData(holder, todo)
        setListeners(holder, todo)
    }

    private fun setTodoData(holder: TodoViewHolder, todo: DocumentSnapshot) {
        when (todo.getBoolean("done")) {
            true -> {
                holder.todoBinding.apply {
                    textViewTodo.text = todo.getString("text")
                    textViewTodo.paintFlags = textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack_15))
                    buttonDelete.setBackgroundResource(R.drawable.icon_delete_15)
                }
            }
            else -> {
                holder.todoBinding.apply {
                    textViewTodo.text = todo.getString("text")
                    textViewTodo.paintFlags = 0
                    textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack))
                    buttonDelete.setBackgroundResource(R.drawable.icon_delete)
                }
            }
        }
    }

    private fun setListeners(holder: TodoViewHolder, todo: DocumentSnapshot) {
        holder.todoBinding.apply {
            buttonDelete.setOnClickListener { itemListeners.onClickDeleteIcon(todo) }
            root.setOnClickListener { itemListeners.onClickTodoItem(todo) }
        }
    }

    fun setLiveData(newData: List<DocumentSnapshot>) {
        documentSnapshotList = newData
        notifyDataSetChanged()
    }
}