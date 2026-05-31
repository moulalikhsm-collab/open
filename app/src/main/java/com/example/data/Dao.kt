package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Tracked Plants ----
    @Query("SELECT * FROM tracked_plants ORDER BY datePlanted DESC")
    fun getAllPlants(): Flow<List<TrackedPlant>>

    @Query("SELECT * FROM tracked_plants WHERE id = :id")
    suspend fun getPlantById(id: Int): TrackedPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: TrackedPlant): Long

    @Update
    suspend fun updatePlant(plant: TrackedPlant)

    @Delete
    suspend fun deletePlant(plant: TrackedPlant)

    // --- Scan History ---
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScanHistory(): Flow<List<ScanHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistory): Long

    @Delete
    suspend fun deleteScan(scan: ScanHistory)

    // --- Community Posts ---
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPost): Long

    @Update
    suspend fun updatePost(post: CommunityPost)

    // --- User Stats ---
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)
}
