package com.alynva.doarei.doarei

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_near.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
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

        carregaInformacoes()

        btn_mapa.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        carregaLista()
    }

    private fun carregaInformacoes() {
        obtemLocalizacao(LocationServices.getFusedLocationProviderClient(this)) { lat, long ->
            Toast.makeText(this, "${lat} - ${long}", Toast.LENGTH_SHORT).show()

            getNears(lat, long).addOnCompleteListener { task ->
                val json = task.result
                val jsonObj = JSONObject(json?.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))
                val nearJson = jsonObj.getJSONArray("pertos")
                nearList.clear()
                for (i in 0 until nearJson!!.length()) {
                    val jObj = nearJson.getJSONObject(i)

                    val nome = jObj.getString("nome")
                    val adress = jObj.getString("adress")
                    val distancia = jObj.getString("distancia")
                    val angulo = jObj.getDouble("angulo")
                    var image = ""
                    if (jObj.has("picture"))
                        image = jObj.getString("picture")

                    val entidade = NearEntity(nome, "", adress, distancia, angulo, image)
                    nearList.add(entidade)
                }
                carregaLista()
            }.addOnFailureListener { err ->
                Log.e("Erro no functions", err.toString())
            }
        }
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
