package com.example.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class NoteEntity(
        @ColumnInfo(name = "email")val email : String,
        @ColumnInfo(name = "name")val name : String,
        @ColumnInfo(name = "heading")val title : String,
        @ColumnInfo(name = "description")val notes : String
){
        @PrimaryKey(autoGenerate = true)
        var id = 0
}