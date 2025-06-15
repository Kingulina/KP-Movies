package com.example.kpmovies.data.user

import androidx.room.*

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(friend: FriendEntity)

    @Delete
    suspend fun remove(friend: FriendEntity)

    @Query("""
        SELECT EXISTS(
          SELECT 1 FROM friends
          WHERE owner = :owner AND followee = :followee
        )
    """)
    suspend fun isFriend(owner: String, followee: String): Boolean

    @Query("""
        SELECT u.* FROM users u
        INNER JOIN friends f ON u.login = f.followee
        WHERE f.owner = :owner
        ORDER BY u.login
    """)
    suspend fun getFriends(owner: String): List<UserEntity>
}