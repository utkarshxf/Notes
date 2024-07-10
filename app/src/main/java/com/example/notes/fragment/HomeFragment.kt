package com.example.notes.fragment

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.notes.R
import com.example.notes.activity.MainActivity
import com.example.notes.adapter.NotesAdapter
import com.example.notes.adapter.OnItemClickListener
import com.example.notes.database.NoteDatabase
import com.example.notes.database.NoteEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

//In this class we are implementing OnItemClickListener which is an interface in NotesAdapter.kt
//When an item in recycler view is clicked, onBindViewHolder() of the NotesAdapter class returns
//the postition and action to be performed. For more details see NotesAdapter.kt file
class HomeFragment : Fragment(), OnItemClickListener {

    lateinit var toolbar: Toolbar
    lateinit var addNote : FloatingActionButton
    lateinit var imgEmpty : ImageView
    lateinit var recyclerNote : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : NotesAdapter

    //It will contain list of all the notes
    var dbNoteList  = mutableListOf<NoteEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)



        toolbar = view.findViewById(R.id.toolbar)
        addNote = view.findViewById(R.id.fabAddNote)
        imgEmpty = view.findViewById(R.id.imgEmptyNote)
        recyclerNote = view.findViewById(R.id.recyclerNote)
        layoutManager = LinearLayoutManager(activity)


        //Removing the background image from main activity and changing its background color to white
        MainActivity.background.visibility = View.GONE
        MainActivity.firstScreen.setBackgroundColor(Color.WHITE)


        //This sets up toolbar and write the users name on it
        setUpToolbar()

        //As we have logout menu option and we will put it on toolbar
        setHasOptionsMenu(true)


        //Extract emailId of currently loggedin user
        MainActivity.acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        val emaiId = MainActivity.acct.email.toString()


        //Will get the list of all the notes which have emailId of currently logged in user
        dbNoteList = RetrieveNotes(requireActivity().applicationContext,emaiId).execute().get()

        //This will call Adapter class and will show the data in recycler view
        showNotes(dbNoteList)


        //When clicked on add button, we will go to Note fragment where we can add/edit notes
        addNote.setOnClickListener {
            val fragNote = NoteFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame,fragNote)
                .addToBackStack("Home")
                .commit()
        }

        return view
    }

    fun showNotes(noteList: MutableList<NoteEntity>)
    {
        //If list is not empty and activity is not null, we will attach recycler view to Adapter class and set its layout
        //Here I used Linear Layout
        if(noteList.isNotEmpty())
        {
            if(activity!=null)
            {
                imgEmpty.visibility = View.INVISIBLE
                recyclerAdapter = NotesAdapter(activity as Context,noteList,this)
                recyclerNote.adapter = recyclerAdapter
                recyclerNote.layoutManager = layoutManager
            }
            else
            {
                Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }
        }

        //If list is empty, we will display the backgroung image
        else
        {

            imgEmpty.visibility = View.VISIBLE
        }


    }

    fun setUpToolbar()
    {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        MainActivity.acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        (activity as AppCompatActivity).supportActionBar?.title = MainActivity.acct.displayName + "'s Notes"
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.log_out)
        {
            //when clicke on logout icon, it will create dailog box which will ask for confirmation
           showAlertDailogLogout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDailogLogout()
    {
        val dailog = AlertDialog.Builder(activity as Context)
        dailog.setTitle("Logout")
        dailog.setMessage("Are you sure ?")
        dailog.setNegativeButton("No"){text,listener ->
            dailog.create().dismiss()
        }
        dailog.setPositiveButton("Yes"){text,listener->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(activity as Context, gso);
            mGoogleSignInClient.signOut()
            MainActivity.sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
            val fragLogin = LoginFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,fragLogin)
                    .commit()
        }
        dailog.create()
        dailog.show()
    }


    //The method of this class is called by dbNoteList and it returns a list of all notes for a given emailId from the database
    class RetrieveNotes(val context: Context,val email:String) : AsyncTask<Void, Void, MutableList<NoteEntity>>() {

        override fun doInBackground(vararg p0: Void?): MutableList<NoteEntity> {
            val db = Room.databaseBuilder(context, NoteDatabase::class.java, "notes-db").build()

            return db.noteDao().getAllNotes(email)
        }

    }

    //Interface from NotesAdapter.kt.
    //If action = 1 then user cliced on Edit icon and we perform action for edit
    //If action = 2 then user cliked o delete icon and we perform action for delete
    override fun onItemClicked(position: Int,action : Int) {
        if(action==1)
        {
            //If edit is clicked, we send data in bundles from home to note fragment so that it can
            //set values of title and notes
            val bundle = Bundle()
            bundle.putString("title",dbNoteList[position].title)
            bundle.putString("description",dbNoteList[position].notes)
            bundle.putBoolean("update",true)
            bundle.putInt("id",dbNoteList[position].id)
            val noteFrag = NoteFragment()
            noteFrag.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame,noteFrag)
                .commit()
        }
        if(action==2)
        {
            //This call deletes the note for which delete icon was clicked
           val deleteNote = DeleteNotes(requireActivity().applicationContext,dbNoteList[position]).execute().get()
            if(deleteNote)
            {
                //shows dailog box for confirmation
                showAlertDailogDelete(position)
            }
            else
            {
                Toast.makeText(activity as Context,"Some error occured while deletion",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showAlertDailogDelete(position: Int)
    {
        val dailog = AlertDialog.Builder(activity as Context)
        dailog.setTitle("Confirmation")
        dailog.setMessage("Do you want to delete this note?")
        dailog.setNegativeButton("No"){text,listener ->
            dailog.create().dismiss()
        }
        //when yes is clicked, delete the note from dbNoteList and notify recycler adapter about the changes
        dailog.setPositiveButton("Yes"){text,listener->
            dbNoteList.removeAt(position)
            recyclerAdapter.notifyDataSetChanged()
            if(dbNoteList.isEmpty())
            {
                imgEmpty.visibility = View.VISIBLE
            }
            Toast.makeText(activity as Context,"Your Note was Deleted",Toast.LENGTH_SHORT).show()
        }
        dailog.create()
        dailog.show()
    }



    //Calls made on this class deletes a given note from the database
    class DeleteNotes(val context: Context,val noteEntity: NoteEntity) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, NoteDatabase::class.java, "notes-db").build()
            db.noteDao().delete(noteEntity)
            return true
        }

    }
}