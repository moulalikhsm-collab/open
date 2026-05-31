package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_plants")
data class TrackedPlant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val species: String,
    val category: String,
    val description: String,
    val datePlanted: Long,
    val lastWatered: Long,
    val waterFrequencyDays: Int,
    val heightProgressHistory: String = "5.0", // comma-separated heights in cm
    val heightDatesHistory: String = System.currentTimeMillis().toString(), // comma-separated timestamps
    val expectedHeightMax: Float = 50.0f,
    val yieldPrediction: String = "Expect harvest in 60 days",
    val healthScore: Int = 95
)

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "LEAF" or "SOIL"
    val plantName: String,
    val timestamp: Long,
    val title: String,
    val severity: String, // "None", "Low", "Moderate", "High"
    val info: String,
    val remedies: String
)

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val role: String,
    val message: String,
    val timestamp: Long,
    val likes: Int = 0,
    val userLiked: Boolean = false
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val greenPoints: Int = 50,
    val level: Int = 1,
    val badgesUnlocked: String = "Seed Sower", // comma-separated
    val challengeProgress: String = "0,0,0" // tracking three standard daily challenges
)
