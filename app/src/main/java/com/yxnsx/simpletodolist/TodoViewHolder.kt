package com.yxnsx.simpletodolist

import android.graphics.Paint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding


class TodoViewHolder(
    val viewBinding: ItemTodoBinding
) : RecyclerView.ViewHolder(viewBinding.root) {


    fun onBind(todo: DocumentSnapshot) {
        when (todo.getBoolean("done")) {
            true -> setDoneItemView()
            false -> setUndoneItemView()
        }
        viewBinding.textViewTodo.text = todo.getString("text").toString()
        Log.d("TAG", "onBind: ${todo.getString("text")}")
        Log.d("TAG", "onBind: ${viewBinding.textViewTodo.text}")
    }

    private fun setDoneItemView() {
        viewBinding.apply {
            textViewTodo.paintFlags = textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack_15))
            buttonDelete.setBackgroundResource(R.drawable.icon_delete_15)
        }
    }

    private fun setUndoneItemView() {
        viewBinding.apply {
            textViewTodo.paintFlags = 0
            textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack))
            buttonDelete.setBackgroundResource(R.drawable.icon_delete)
        }
    }
}