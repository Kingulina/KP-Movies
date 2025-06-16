package com.example.kpmovies.data.local

import androidx.room.*
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kpmovies.data.local.entity.MovieEntity
import com.example.kpmovies.data.local.entity.WatchlistEntity
import com.example.kpmovies.data.local.entity.ReviewEntity
import com.example.kpmovies.data.user.UserEntity
import com.example.kpmovies.data.user.FriendEntity
import com.example.kpmovies.data.local.dao.MovieDao
import com.example.kpmovies.data.local.dao.WatchlistDao
import com.example.kpmovies.data.local.dao.ReviewDao
import com.example.kpmovies.data.user.UserDao
import com.example.kpmovies.data.user.FriendDao


@Database(
    entities = [UserEntity::class, FriendEntity::class, MovieEntity::class, WatchlistEntity::class, ReviewEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS friends(
                        owner    TEXT NOT NULL,
                        followee TEXT NOT NULL,
                        PRIMARY KEY(owner, followee)
                    )
                """.trimIndent())
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kp_movies.db"
                )
                    .addMigrations(MIGRATION_1_2)          // ← tu
                    .build()
                    .also { INSTANCE = it }
            }
    }
}