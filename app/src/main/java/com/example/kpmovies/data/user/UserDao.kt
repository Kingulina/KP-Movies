package com.example.kpmovies.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Query("""
    SELECT * FROM users
    WHERE login LIKE '%' || :query || '%' 
      AND login != :exclude          -- nie pokazuj mnie samego
    ORDER BY login
""")
    suspend fun searchUsers(query: String, exclude: String): List<UserEntity>

    @Query("""
        SELECT * FROM users 
        WHERE login = :login AND password = :password 
        LIMIT 1
    """)
    suspend fun getUser(login: String, password: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE login = :login)")
    suspend fun exists(login: String): Boolean
}

