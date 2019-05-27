package com.example.notes

import android.arch.persistence.room.Room
import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_category_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // Initializing an empty ArrayList to be filled with categories
    private val notes: ArrayList<NoteCategory> = ArrayList()
    private lateinit var database : NotesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://quotes.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val chuck = retrofit.create(QodAPI::class.java)
        val call = chuck.findJoke()

        call.enqueue( object : Callback<QuoteOfTheDay> {
            override fun onFailure(call: Call<QuoteOfTheDay>, t: Throwable) {
                Log.d("am2019", t.message)
            }

            override fun onResponse(call: Call<QuoteOfTheDay>, response: Response<QuoteOfTheDay>) {
                val body = response.body()
                qod.text = body!!.contents.quotes[0].quote
            }

        })

        AsyncTask.execute {

            try {
                database = Room.databaseBuilder(
                    this,
                    NotesDatabase::class.java,
                    "notes.db"
                ).fallbackToDestructiveMigration().build()
            } catch (e: Exception) {
                Log.i("am2019", e.message)
            }

//            database.notesDao().deleteAll()
//            database.notesDao().deleteAllDescr()

        }

        // Creates a vertical Layout Manager
        rv_notes.layoutManager = LinearLayoutManager(this)

        // Access the RecyclerView Adapter and load the data into it
        rv_notes.adapter = MainAdapter(notes, this, { noteCategory: NoteCategory -> categoryClicked(noteCategory) })

        addButton.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_category_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Add Note")
            val mAlertDialog = mBuilder.show()
            mDialogView.addCategory.setOnClickListener {
                mAlertDialog.dismiss()
                val title = mDialogView.ettitle.text.toString()
                val description = mDialogView.etdescription.text.toString()
                AsyncTask.execute {
                    database.notesDao().insertNoteDescr(NoteDescription(title, description))
                    notes.add(NoteCategory(database.notesDao().getLastAdded().id, title, description))
                }
            }
        }

        rv_notesInit()

    }

    private fun rv_notesInit() {

        AsyncTask.execute {

            try {
                database = Room.databaseBuilder(
                    this,
                    NotesDatabase::class.java,
                    "notes.db"
                ).fallbackToDestructiveMigration().build()
            } catch (e: Exception) {
                Log.i("am2019", e.message)
            }

            var list = database.notesDao().getAllCategories()

            for(i in 0..list.size-1) {
                notes.add(NoteCategory(list[i].id, list[i].title, list[i].description))
            }
        }

    }

    private fun categoryClicked(noteCategory: NoteCategory) {

        Toast.makeText(this, "Clicked: ${noteCategory.title}", Toast.LENGTH_LONG).show()

      //  if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            var myintent = Intent(this, NoteActivity::class.java)
            myintent.putExtra("title", noteCategory.title)
            myintent.putExtra("description", noteCategory.description)
            myintent.putExtra("idInDB", noteCategory.idInDB)
            startActivity(myintent)
      //  } else {
      //  }


    }
}