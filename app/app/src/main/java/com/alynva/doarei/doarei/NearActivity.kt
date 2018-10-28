package com.alynva.doarei.doarei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_near.*

class NearActivity : AppCompatActivity() {

    var nearList : MutableList<NearEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

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
}
