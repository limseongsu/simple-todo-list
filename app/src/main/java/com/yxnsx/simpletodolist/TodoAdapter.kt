package com.yxnsx.simpletodolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.yxnsx.simpletodolist.databinding.ItemTodoBinding


class TodoAdapter(
    var documentSnapshotList: List<DocumentSnapshot>,
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
        // item_todo 뷰 인플레이팅
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        // 뷰바인딩 및 뷰홀더 생성
        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        // documentSnapshotList의 인덱스 값 바탕으로 todo 객체 생성
        val todo = documentSnapshotList[position]

        // todo 객체의 done 불리언 값이 true일 경우
        if (todo.getBoolean("done") == true) {
            holder.todoBinding.apply {
                textViewTodo.paintFlags = textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack_30))
                buttonDelete.setBackgroundResource(R.drawable.icon_delete_30)
            }

        } else { // todo 객체의 done 불리언 값이 false일 경우
            holder.todoBinding.apply {
                textViewTodo.paintFlags = 0
                textViewTodo.setTextColor(root.context.getColor(R.color.colorBlack))
                buttonDelete.setBackgroundResource(R.drawable.icon_delete)
            }
        }

        holder.todoBinding.apply {
            // 각 뷰홀더의 textViewTodo에 todo 객체의 text 값 적용
            textViewTodo.text = todo.getString("text")

            // 삭제 버튼 클릭리스너 설정
            buttonDelete.setOnClickListener {
                onClickDeleteIcon.invoke(todo)
            }
            // 아이템 클릭리스너 설정
            root.setOnClickListener {
                onClickTodoItem.invoke(todo)
            }
        }
    }

    override fun getItemCount() = documentSnapshotList.size

    fun setLiveData(newData: List<DocumentSnapshot>) {
        documentSnapshotList = newData
        notifyDataSetChanged()
    }
}