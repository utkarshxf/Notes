package com.example.notes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.NoteEntity


class NotesAdapter(val context: Context,val noteList: MutableList<NoteEntity>,val itemClickListener: OnItemClickListener):RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_note, parent, false)

        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val result = noteList[position]
        holder.txtTitle.text = result.title
        holder.txtNote.text = result.notes

        //Action 1 on Edit and 2 on Delete
        holder.setOnClick(itemClickListener, position, 1)
        holder.setOnClick(itemClickListener, position, 2)

    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtNote: TextView = view.findViewById(R.id.txtNote)
        val imgEdit: ImageView = view.findViewById(R.id.imgEdit)
        val imgDelete: ImageView = view.findViewById(R.id.imgDelete)

        fun setOnClick(clickListener: OnItemClickListener, position: Int, action: Int) {

            when (action) {
                1 -> {
                    imgEdit.setOnClickListener {
                        clickListener.onItemClicked(position, action)
                    }
                }
                2 -> {
                    imgDelete.setOnClickListener {
                        clickListener.onItemClicked(position, action)
                    }
                }
            }
        }

    }
}
interface OnItemClickListener {
    fun onItemClicked(position: Int, action: Int)
}

