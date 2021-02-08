package com.yxnsx.simpletodolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding
import kotlinx.android.synthetic.main.item_todo.view.*


class TodoAdapter(
    private var documentSnapshotList: List<DocumentSnapshot>,
    private val itemListeners: ItemListeners
) :
    RecyclerView.Adapter<TodoViewHolder>() {

    private var _viewBinding: ItemTodoBinding? = null
    private val viewBinding get() = _viewBinding!!


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        _viewBinding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context))
        setMatchParentToRecyclerView()
        return TodoViewHolder(viewBinding)
    }

    override fun getItemCount() = documentSnapshotList.size
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = documentSnapshotList[position]
        holder.apply {
            onBind(todo)
            viewBinding.root.setOnClickListener { itemListeners.onClickTodoItem(todo) }
            viewBinding.buttonDelete.setOnClickListener { itemListeners.onClickDeleteIcon(todo) }
        }
    }

    fun setLiveData(newData: List<DocumentSnapshot>) {
        documentSnapshotList = newData
        notifyDataSetChanged()
    }

    private fun setMatchParentToRecyclerView() {
        val layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        viewBinding.root.layoutParams = layoutParams
    }
}