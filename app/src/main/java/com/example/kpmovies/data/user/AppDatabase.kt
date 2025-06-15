package com.example.kpmovies.data.user

import androidx.room.*
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [UserEntity::class, FriendEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

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