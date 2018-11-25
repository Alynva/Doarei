package com.alynva.doarei.doarei

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.near_list_item.view.*

class NearAdapter(private val nearEntities: List<NearEntity>) : RecyclerView.Adapter<NearAdapter.ViewHolder>() {

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
            itemView.tv_item_dist.text = entity.distancia
            if (entity.photoPath != "")
                Glide.with(itemView.context)
                        .load(entity.photoPath)
                        .into(itemView.civ_profile_picture)

            GlideApp.with(itemView.context)
                    .load("https://firebasestorage.googleapis.com/v0/b/doarei-ufscar.appspot.com/o/images%2F25426.png?alt=media&token=6a82d3ca-4003-47e6-8d07-000efe4834fb")
                    .transform(RotateTransformation(itemView.context, entity.angulo.toFloat()))
                    .into(itemView.iv_direction)
        }
    }
}