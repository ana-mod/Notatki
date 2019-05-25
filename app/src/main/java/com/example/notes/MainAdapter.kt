package com.example.notes
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.note_category.view.*

class MainAdapter(private var items : ArrayList<NoteCategory>, private val context: Context, private val clickListener: (NoteCategory) -> Unit) : RecyclerView.Adapter<ViewHolder>() {


    // Gets the number of categories in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.note_category, parent, false))
    }

    // Binds each note category in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNoteTitle.text = items[position].title
        holder.tvNoteDescription.text = items[position].description

        //delete item with the same position
        holder.tvNoteDelete.setOnClickListener {
            items.removeAt(position)
            notifyDataSetChanged()
        }

        //bind listener to every item
        holder.view.setOnClickListener { clickListener(items[position]) }
    }
}

class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each note category to
    val tvNoteTitle = view.title!!
    val tvNoteDescription = view.description!!
    val tvNoteDelete = view.del_cat!!

}