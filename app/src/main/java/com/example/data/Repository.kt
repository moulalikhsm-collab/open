package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val dao: AppDao) {
    val allPlants: Flow<List<TrackedPlant>> = dao.getAllPlants()
    val allScans: Flow<List<ScanHistory>> = dao.getAllScanHistory()
    val allPosts: Flow<List<CommunityPost>> = dao.getAllPosts()
    val userStats: Flow<UserStats?> = dao.getUserStatsFlow()

    suspend fun getPlantById(id: Int): TrackedPlant? = dao.getPlantById(id)

    suspend fun insertPlant(plant: TrackedPlant): Long = dao.insertPlant(plant)

    suspend fun updatePlant(plant: TrackedPlant) = dao.updatePlant(plant)

    suspend fun deletePlant(plant: TrackedPlant) = dao.deletePlant(plant)

    suspend fun insertScan(scan: ScanHistory): Long = dao.insertScan(scan)

    suspend fun deleteScan(scan: ScanHistory) = dao.deleteScan(scan)

    suspend fun insertPost(post: CommunityPost): Long = dao.insertPost(post)

    suspend fun updatePost(post: CommunityPost) = dao.updatePost(post)

    suspend fun getUserStats(): UserStats? = dao.getUserStats()

    // Stats and progression updates
    suspend fun ensureDefaultStatsExist() {
        val current = dao.getUserStats()
        if (current == null) {
            dao.insertUserStats(UserStats(
                id = 1,
                greenPoints = 120,
                level = 1,
                badgesUnlocked = "Seed Sower, Eco Novice",
                challengeProgress = "0,1,0" // 3 items
            ))
        }

        // Prepopulate some friendly discussion posts to keep the interface lively at launch
        val posts = dao.getAllPosts().firstOrNull() ?: emptyList()
        if (posts.isEmpty()) {
            dao.insertPost(CommunityPost(
                username = "Arun_NatureLover",
                role = "Forest Ranger",
                message = "Just planted 5 Tulsi saplings in my backyard! Best feeling in the morning. 🌱 #GreenEarth",
                timestamp = System.currentTimeMillis() - 7200000,
                likes = 12,
                userLiked = false
            ))
            dao.insertPost(CommunityPost(
                username = "Sophia_GreenThumb",
                role = "Leaf Guardian",
                message = "My Aloe Vera is showing slight yellowing on the tips. After a soil check, realized it had poor drainage. Switched to coco peat & sand mix and it is already looking perky again!",
                timestamp = System.currentTimeMillis() - 14400000,
                likes = 8,
                userLiked = false
            ))
        }

        // Save at least one template plant if none exists for a friendly start
        val plants = dao.getAllPlants().firstOrNull() ?: emptyList()
        if (plants.isEmpty()) {
            dao.insertPlant(TrackedPlant(
                name = "Home Aloe Vera",
                species = "Aloe barbadensis",
                category = "Medicinal",
                description = "Growing in the balcony ceramic pot. High medicinal value and purifies indoor air.",
                datePlanted = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L, // 30 days ago
                lastWatered = System.currentTimeMillis() - 2 * 24 * 3600 * 1000L,  // 2 days ago
                waterFrequencyDays = 6,
                heightProgressHistory = "8.0,9.2,11.5,14.0",
                heightDatesHistory = "${System.currentTimeMillis() - 30 * 24 * 3600 * 1000L},${System.currentTimeMillis() - 20 * 24 * 3600 * 1000L},${System.currentTimeMillis() - 10 * 24 * 3600 * 1000L},${System.currentTimeMillis()}",
                expectedHeightMax = 50.0f,
                yieldPrediction = "Outer gel harvest ready in 40 days",
                healthScore = 98
            ))
        }
    }

    suspend fun addGreenPoints(points: Int) {
        val current = dao.getUserStats() ?: UserStats(id = 1)
        val newPoints = current.greenPoints + points
        // Level up condition: 150 points per level
        val newLevel = (newPoints / 150) + 1
        var newBadges = current.badgesUnlocked
        if (newLevel > current.level) {
            val nextBadge = when (newLevel) {
                2 -> "Sapling Protector"
                3 -> "Tree Guardian"
                4 -> "Eco Mastermind"
                else -> "Earth Champion"
            }
            if (!newBadges.contains(nextBadge)) {
                newBadges = "$newBadges, $nextBadge"
            }
        }
        dao.insertUserStats(current.copy(
            greenPoints = newPoints,
            level = newLevel,
            badgesUnlocked = newBadges
        ))
    }

    suspend fun updateChallengeProgress(index: Int, completed: Boolean) {
        val current = dao.getUserStats() ?: return
        val items = current.challengeProgress.split(",").map { it.toInt() }.toMutableList()
        if (index in 0 until items.size) {
            items[index] = if (completed) 1 else 0
            dao.updateUserStats(current.copy(
                challengeProgress = items.joinToString(",")
            ))
        }
    }
}
