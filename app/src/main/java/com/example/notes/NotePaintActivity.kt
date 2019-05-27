package com.example.notes

import android.arch.persistence.room.Room
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_note_paint.*
import kotlinx.android.synthetic.main.fragment_paint.*

import java.lang.Exception

class NotePaintActivity : AppCompatActivity() {

    private lateinit var database : NotesDatabase

    private lateinit var noteFragment : Fragment
    private lateinit var menuFragment : Fragment

    var idInDB: Long? = null

    lateinit var note : Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_paint)

        idInDB = intent.getLongExtra("idInDB", 0)

        noteFragment = noteFrag
        menuFragment = menuFrag
        (menuFragment as PaintMenuFragment).setPaintFragment(noteFragment as PaintFragment)

        setInitDataFromDB()
    }


    fun onClick(view: View) {
        when(view.getId()) {
            R.id.buttonSave -> {
                sendDataToDB()
            }
        }
    }

    private fun setInitDataFromDB() {
        AsyncTask.execute {
            try {
                database = Room.databaseBuilder(
                    this,
                    NotesDatabase::class.java,
                    "notes.db"
                ).build()
            } catch (e: Exception) {
                Log.i("am2019", e.message)
            }
            note = database.notesDao().getNote(idInDB!!)
            (noteFragment as PaintFragment).setColor(note.brushColor)
            (noteFragment as PaintFragment).setBrushWidth(note.brushWidth)
            (noteFragment as PaintFragment).setLowestY(note.lowestY)
            (noteFragment as PaintFragment).setPathsJSON(note.Paths)
        }
    }


    private fun sendDataToDB() {
        AsyncTask.execute {
            try {
                database = Room.databaseBuilder(
                    this,
                    NotesDatabase::class.java,
                    "notes.db"
                ).build()
            } catch (e: Exception) {
                Log.i("am2019", e.message)
            }
            database.notesDao().updatePaintNote(idInDB!!, (noteFragment as PaintFragment).getColor(), (noteFragment as PaintFragment).getBrushWidth(), (noteFragment as PaintFragment).getLowestY(), (noteFragment as PaintFragment).getPathsJSON())
        }
    }
}
