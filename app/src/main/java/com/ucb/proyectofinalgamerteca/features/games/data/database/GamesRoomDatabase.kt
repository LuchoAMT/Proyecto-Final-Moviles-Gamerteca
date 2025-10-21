package com.ucb.proyectofinalgamerteca.features.games.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ucb.proyectofinalgamerteca.features.games.data.database.dao.IGameDao
import com.ucb.proyectofinalgamerteca.features.games.data.database.entity.GameEntity

@Database(entities = [GameEntity::class], version = 4, exportSchema = false)
abstract class GamesRoomDatabase : RoomDatabase() {
    abstract fun gameDao(): IGameDao

    companion object {
        @Volatile
        private var Instance: GamesRoomDatabase? = null

        fun getDatabase(context: Context): GamesRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    GamesRoomDatabase::class.java,
                    "games_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}