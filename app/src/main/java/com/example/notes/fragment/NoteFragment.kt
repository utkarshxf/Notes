package com.example.notes.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.example.notes.R
import com.example.notes.activity.MainActivity
import com.example.notes.database.NoteDatabase
import com.example.notes.database.NoteEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.properties.Delegates

class NoteFragment : Fragment() {


    lateinit var etTitle : EditText
    lateinit var etNote : EditText
    lateinit var saveNote: FloatingActionButton
    var edit : Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etNote = view.findViewById(R.id.etNote)
        saveNote = view.findViewById(R.id.fabSaveNote)
        var id:Int

        //we get this value from home fragment when edit icon is clicked
        val value = arguments?.getBoolean("update")
        if(value!=null)
        {
            edit = value
        }
        if(edit)
        {
            //If edit icon  is clicked we extract the data we sent in bundle from home fragment and set fields of title and note
            val updateTitle = arguments?.getString("title")
            val updateNote = arguments?.getString("description")
            val num = arguments?.getInt("id")!!
            id = num

            etTitle.setText(updateTitle)
            etNote.setText(updateNote)
        }


        saveNote.setOnClickListener {

            MainActivity.acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
            val title = etTitle.text.toString()
            val note = etNote.text.toString()

            //check if both title and note field is not empty
            if(title.trim().isNotEmpty() && note.trim().isNotEmpty())
            {
                //we create an object noteEntity of data class NoteEntity
                val noteEntity = NoteEntity(
                    MainActivity.acct.email.toString(),
                    MainActivity.acct.displayName.toString(),
                    etTitle.text.toString(),
                    etNote.text.toString()
                )
                //This is done so that count will become zero in MainActivity.kt when we again reach to home from login fragment
                //On reaching home fragment if user now press back, then activity finishes and app closes
                requireActivity().supportFragmentManager.popBackStack()
                if(edit)
                {
                    //Call to UpdateNote class which performs update action in a given note
                    val updateNote = UpdateNote(requireActivity().applicationContext,etTitle.text.toString(),etNote.text.toString()
                        ,arguments?.getInt("id")!!)
                        .execute().get()
                    if(updateNote)
                    {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.frame,HomeFragment())
                            .commit()
                        Toast.makeText(activity as Context,"Your Note is Updated",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(activity as Context,"Some Error Occured while Updation",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    //If !edit then we are in this fragment because of clicking of add button on home fragment, So we will insert new note.
                    val addNote =  AddNote(requireActivity().applicationContext,noteEntity).execute().get()

                    if(addNote)
                    {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.frame,HomeFragment())
                            .commit()
                        Toast.makeText(activity as Context,"Your Note is Saved",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(activity as Context,"Some Error Occured while Saving Note",Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else
            {
                // when either of title or heading is not given but save button is clicked.
                Toast.makeText(activity as Context, "Please write both title and note",Toast.LENGTH_SHORT).show()
            }

        }
        return view
    }

    //Updates Note in database
    class UpdateNote(val context: Context,val head:String,val body:String,val id:Int) : AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context, NoteDatabase::class.java,"notes-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            db.noteDao().update(head,body,id)
            return true

        }

    }


    //Inserts new note in database
    class AddNote(val context: Context,val noteEntity: NoteEntity) : AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, NoteDatabase::class.java,"notes-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.noteDao().insert(noteEntity)
            return true
        }

    }

}