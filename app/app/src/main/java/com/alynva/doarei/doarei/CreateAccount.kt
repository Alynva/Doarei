package com.alynva.doarei.doarei

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        btn_back_activity.setOnClickListener {
            this.finish()
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.account_types, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_account_type.setAdapter(adapter)
        spinner_account_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position)) {
                    "Pessoa física" -> {
                        ipt_group_cpf.visibility = View.VISIBLE
                        ipt_group_cnpj.visibility = View.GONE

                        ipt_group_age.visibility = View.VISIBLE
                    }
                    "Pessoa jurídica" -> {
                        ipt_group_cpf.visibility = View.GONE
                        ipt_group_cnpj.visibility = View.VISIBLE

                        ipt_group_age.visibility = View.GONE
                    }

                    else -> {
                        ipt_group_cpf.visibility = View.VISIBLE
                        ipt_group_cnpj.visibility = View.VISIBLE

                        ipt_group_age.visibility = View.VISIBLE
                    }
                }
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
            }
        }
    }

}
