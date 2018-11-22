package com.alynva.doarei.doarei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_near.*
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import kotlin.collections.HashMap


class NearActivity : AppCompatActivity() {

    companion object {
        val mFunctions = FirebaseFunctions.getInstance()!!
    }

    private var nearList : MutableList<NearEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

        btnCampanha.setOnClickListener {
            getNears(37.422, -122.084).addOnCompleteListener { task ->
                val json = task.result
                val jsonObj = JSONObject(json?.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))
                val nearJson = jsonObj.getJSONArray("pertos")
                nearList.clear()
                for (i in 0 until nearJson!!.length()) {
                    val jObj = nearJson.getJSONObject(i)

                    val nome = jObj.getString("nome")
                    val adress = jObj.getString("adress")

                    val entidade = NearEntity(nome, "", adress, "")
                    nearList.add(entidade)
                }
                carregaLista()
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

    private fun getNears(lat: Double, long: Double): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>().apply {
            put("lat", lat)
            put("long", long)
        }

        return mFunctions
                .getHttpsCallable("get_nears")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result?.data as String
                }
    }
}
