package com.alynva.doarei.doarei

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.near_list_item.view.*

/**
 * Created by Alynva on 28/10/2018.
 */

class NearAdapter(val nearEntities: List<NearEntity>) : RecyclerView.Adapter<NearAdapter.ViewHolder>() {

    // Método responsável por inflar as views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.near_list_item, parent, false)
        return ViewHolder(view)
    }

    // Retorna a quantidade de itens
    override fun getItemCount(): Int {
        return nearEntities.size
    }

    // Popula a ViewHolder com as informações do nearEntities
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(nearEntities[position])
    }

    // Referência da view para cada item da lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(entity: NearEntity) {
            itemView.tv_item_nome.text = entity.nome
            itemView.tv_item_desc.text = entity.desc

        }
    }
}