package com.example.sciflare_interview.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Message::class],
    version = 1,
    exportSchema = true
)
abstract class MessageDataBase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDataBase? = null
        fun getDatabase(context: Context): MessageDataBase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): MessageDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                MessageDataBase::class.java,
                "messages_database"
            )
                .build()
        }
    }
}