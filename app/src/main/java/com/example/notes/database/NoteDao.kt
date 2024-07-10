package com.example.notes.database

import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    fun insert(noteEntity: NoteEntity)

    @Delete
    fun delete(noteEntity: NoteEntity)

    @Query("UPDATE data SET heading = :title, description = :note WHERE id = :id")
    fun update(title:String,note:String,id:Int)

    @Query("SELECT * FROM data where email = :email order by id DESC")
    fun getAllNotes(email:String): MutableList<NoteEntity>

}