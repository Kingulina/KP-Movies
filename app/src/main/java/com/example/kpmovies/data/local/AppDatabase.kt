package com.example.kpmovies.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kpmovies.data.local.dao.MovieDao
import com.example.kpmovies.data.local.dao.ReviewDao
import com.example.kpmovies.data.local.dao.WatchlistDao
import com.example.kpmovies.data.local.entity.MovieEntity
import com.example.kpmovies.data.local.entity.ReviewEntity
import com.example.kpmovies.data.local.entity.WatchlistEntity
import com.example.kpmovies.data.user.FriendDao
import com.example.kpmovies.data.user.FriendEntity
import com.example.kpmovies.data.user.UserDao
import com.example.kpmovies.data.user.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FriendEntity::class,
        MovieEntity::class,
        WatchlistEntity::class,
        ReviewEntity::class
    ],
    version = 3,
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

        // migracja 1->2 (friends), 2->3 (status i updatedAt dla watchlist)
        private val MIGRATION_1_2 = object: Migration(1,2) {
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
        private val MIGRATION_2_3 = object: Migration(2,3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE watchlist ADD COLUMN status TEXT NOT NULL DEFAULT 'TODO'")
                db.execSQL("ALTER TABLE watchlist ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kp_movies.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }

    }
}