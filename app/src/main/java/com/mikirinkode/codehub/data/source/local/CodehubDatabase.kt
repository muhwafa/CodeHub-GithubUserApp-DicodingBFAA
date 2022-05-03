package com.mikirinkode.codehub.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mikirinkode.codehub.data.model.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class CodehubDatabase: RoomDatabase(){
    companion object {
        private var INSTANCE : CodehubDatabase? = null

        fun getDatabase(context: Context): CodehubDatabase?{
            if(INSTANCE == null){
                synchronized(CodehubDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, CodehubDatabase::class.java, "codehub_db").build()
                }
            }
            return INSTANCE
        }
    }

    abstract fun favoriteUserDao(): CodehubDao
}