package com.alynva.doarei.doarei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_near.*
import com.google.android.gms.tasks.Continuation
import com.google.firebase.functions.HttpsCallableResult
import com.google.android.gms.tasks.Task
import java.util.*
import kotlin.collections.HashMap


class NearActivity : AppCompatActivity() {

    companion object {
        val mFunctions = FirebaseFunctions.getInstance();
    }

    var nearList : MutableList<NearEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

        btnCampanha.setOnClickListener {
            addMessage("Qualquercoisa").addOnCompleteListener {
                task: Task<String> -> Toast.makeText(this, task.result, Toast.LENGTH_SHORT).show()
            }
        }

        carregaLista()
    }

    override fun onResume() {
        super.onResume()

        carregaLista()
    }

    private fun carregaLista() {
        val adapter = NearAdapter(nearList)
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        rvNear.adapter = adapter
        rvNear.layoutManager = layoutManager
        rvNear.addItemDecoration(dividerItemDecoration)
    }

    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>().apply {
            put("text", text)
            put("push", true)
        }

        return mFunctions
                .getHttpsCallable("helloWorld")
                .call(data)
                .continueWith(object : Continuation<HttpsCallableResult, String> {
                    @Throws(Exception::class)
                    override fun then(task: Task<HttpsCallableResult>): String {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return task.result?.data as String
                    }
                })
    }
}
