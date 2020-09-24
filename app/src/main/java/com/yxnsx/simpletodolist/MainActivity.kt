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

        // 뷰바인딩 적용
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        // 파이어베이스 익명로그인을 위한 유저 객체 생성
        firebaseAuth = Firebase.auth
        val currentUser = firebaseAuth.currentUser
        Log.d(TAG, "onCreate: currentUser = $currentUser")

        // 유저 객체가 null이 아닐 경우
        if (currentUser != null) {
            // userID 값 받아오기
            userID = currentUser.uid

        } else { // 유저 객체가 null인 경우
            // 파이어베이스 익명로그인 실행
            firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->

                // 익명로그인이 성공했을 경우
                if (task.isSuccessful) {
                    // 새 유저 객체 생성 후 userID 값 받아오기
                    val newUser = firebaseAuth.currentUser
                    userID = newUser!!.uid
                    Log.d(TAG, "signInAnonymously:success")
                    Log.d(TAG, "onCreate: newUser = $newUser")

                } else { // 익명로그인이 실패했을 경우
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                }
            }
        }

        // 리사이클러뷰 설정 - 레이아웃 매니저, 어댑터
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

        // 추가 버튼 클릭리스너 설정
        viewBinding.buttonAdd.setOnClickListener {
            // editText에 입력한 값 바탕으로 todoModel 객체 생성 (done 값은 기본 false)
            val todo = TodoModel(viewBinding.editTextTodo.text.toString())
            // 뷰모델을 통해 데이터베이스에 todo 추가
            mainViewModel.addTodo(todo)

            // 추가 후 키보드 숨기기, editText 입력 폼 비우기
            hideKeyboard(viewBinding.root)
            viewBinding.editTextTodo.setText("")
        }

        // 뷰모델의 Observer를 통해 리사이클러뷰의 TodoAdapter에 변경값 갱신
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