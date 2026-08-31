package com.brightnest.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.brightnest.app.AdultStore
import com.brightnest.app.Expense
import com.brightnest.app.Goal
import com.brightnest.app.Habit
import com.brightnest.app.Note
import com.brightnest.app.Prefs
import com.brightnest.app.Task
import kotlinx.coroutines.tasks.await

data class SubscriptionInfo(
    val isActive: Boolean = false,
    val tier: String = "free",
    val subscriptionKey: String = ""
)

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "parent",
    val activeMode: String = "kids",
    val parentPin: String = "",
    val subscription: SubscriptionInfo = SubscriptionInfo()
)

data class KidsProgress(
    val coins: Int = 0,
    val stars: Int = 0,
    val streak: Int = 0,
    val badges: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)

object FirestoreRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build()
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                val subMap = doc.get("subscription") as? Map<*, *>
                val subInfo = SubscriptionInfo(
                    isActive = (subMap?.get("isActive") as? Boolean) ?: false,
                    tier = (subMap?.get("tier") as? String) ?: "free",
                    subscriptionKey = (subMap?.get("subscriptionKey") as? String) ?: ""
                )
                UserProfile(
                    uid = doc.getString("uid") ?: uid,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    role = doc.getString("role") ?: "parent",
                    activeMode = doc.getString("activeMode") ?: "kids",
                    parentPin = doc.getString("parentPin") ?: "",
                    subscription = subInfo
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        return try {
            val data = hashMapOf(
                "uid" to profile.uid,
                "email" to profile.email,
                "name" to profile.name,
                "role" to profile.role,
                "activeMode" to profile.activeMode,
                "parentPin" to profile.parentPin,
                "subscription" to hashMapOf(
                    "isActive" to profile.subscription.isActive,
                    "tier" to profile.subscription.tier,
                    "subscriptionKey" to profile.subscription.subscriptionKey
                )
            )
            firestore.collection("users").document(profile.uid).set(data)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateParentPin(uid: String, pin: String): Boolean {
        return try {
            firestore.collection("users").document(uid)
                .update("parentPin", pin).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateActiveMode(uid: String, mode: String): Boolean {
        return try {
            firestore.collection("users").document(uid)
                .update("activeMode", mode).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // =========================================================================
    // 3NF SENIOR ARCHITECTURE: Normalized Kids Progress Sub-Collection
    // Entity: users/{uid}/kids_progress/stats
    // =========================================================================

    suspend fun saveKidsProgress(uid: String, progress: KidsProgress): Boolean {
        if (uid.isBlank()) return false
        return try {
            val data = hashMapOf(
                "coins" to progress.coins,
                "stars" to progress.stars,
                "streak" to progress.streak,
                "badges" to progress.badges,
                "lastUpdated" to progress.lastUpdated
            )
            firestore.collection("users").document(uid)
                .collection("kids_progress").document("stats")
                .set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getKidsProgress(uid: String): KidsProgress? {
        if (uid.isBlank()) return null
        return try {
            val doc = firestore.collection("users").document(uid)
                .collection("kids_progress").document("stats")
                .get().await()
            if (doc.exists()) {
                @Suppress("UNCHECKED_CAST")
                KidsProgress(
                    coins = (doc.getLong("coins") ?: 0L).toInt(),
                    stars = (doc.getLong("stars") ?: 0L).toInt(),
                    streak = (doc.getLong("streak") ?: 0L).toInt(),
                    badges = (doc.get("badges") as? List<String>) ?: emptyList(),
                    lastUpdated = doc.getLong("lastUpdated") ?: 0L
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // =========================================================================
    // 3NF SENIOR ARCHITECTURE: Normalized Adult Collections
    // 1-to-Many Decoupled Collections:
    // users/{uid}/adult_tasks, adult_notes, adult_expenses, adult_habits, adult_goals
    // =========================================================================

    suspend fun syncAdultDataToCloud(uid: String, store: AdultStore) {
        if (uid.isBlank()) return
        try {
            val userRef = firestore.collection("users").document(uid)

            // 1. Sync Tasks
            val tasksCol = userRef.collection("adult_tasks")
            store.tasks.forEach { t ->
                val map = hashMapOf(
                    "id" to t.id,
                    "title" to t.title,
                    "date" to t.date,
                    "done" to t.done,
                    "updatedAt" to System.currentTimeMillis()
                )
                tasksCol.document(t.id).set(map)
            }

            // 2. Sync Notes
            val notesCol = userRef.collection("adult_notes")
            store.notes.forEach { n ->
                val map = hashMapOf(
                    "id" to n.id,
                    "title" to n.title,
                    "body" to n.body,
                    "updatedAt" to n.updatedAt
                )
                notesCol.document(n.id).set(map)
            }

            // 3. Sync Expenses
            val expCol = userRef.collection("adult_expenses")
            store.expenses.forEach { e ->
                val map = hashMapOf(
                    "id" to e.id,
                    "type" to e.type,
                    "category" to e.category,
                    "amount" to e.amount,
                    "date" to e.date,
                    "note" to e.note
                )
                expCol.document(e.id).set(map)
            }

            // 4. Sync Habits
            val habCol = userRef.collection("adult_habits")
            store.habits.forEach { h ->
                val map = hashMapOf(
                    "id" to h.id,
                    "name" to h.name,
                    "color" to h.color,
                    "history" to h.history
                )
                habCol.document(h.id).set(map)
            }

            // 5. Sync Goals
            val goalCol = userRef.collection("adult_goals")
            store.goals.forEach { g ->
                val map = hashMapOf(
                    "id" to g.id,
                    "name" to g.name,
                    "targetDate" to g.targetDate,
                    "progress" to g.progress
                )
                goalCol.document(g.id).set(map)
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error syncing adult data to cloud", e)
        }
    }

    suspend fun fetchAdultDataFromCloud(uid: String, store: AdultStore) {
        if (uid.isBlank()) return
        try {
            val userRef = firestore.collection("users").document(uid)

            // 1. Fetch Tasks
            val taskSnap = userRef.collection("adult_tasks").get().await()
            if (!taskSnap.isEmpty) {
                val cloudTasks = taskSnap.documents.mapNotNull { d ->
                    Task(
                        id = d.getString("id") ?: d.id,
                        title = d.getString("title") ?: "",
                        date = d.getString("date") ?: "",
                        done = d.getBoolean("done") ?: false
                    )
                }
                if (cloudTasks.isNotEmpty()) store.tasks = cloudTasks
            }

            // 2. Fetch Notes
            val noteSnap = userRef.collection("adult_notes").get().await()
            if (!noteSnap.isEmpty) {
                val cloudNotes = noteSnap.documents.mapNotNull { d ->
                    Note(
                        id = d.getString("id") ?: d.id,
                        title = d.getString("title") ?: "",
                        body = d.getString("body") ?: "",
                        updatedAt = d.getString("updatedAt") ?: ""
                    )
                }
                if (cloudNotes.isNotEmpty()) store.notes = cloudNotes
            }

            // 3. Fetch Expenses
            val expSnap = userRef.collection("adult_expenses").get().await()
            if (!expSnap.isEmpty) {
                val cloudExpenses = expSnap.documents.mapNotNull { d ->
                    Expense(
                        id = d.getString("id") ?: d.id,
                        type = d.getString("type") ?: "expense",
                        category = d.getString("category") ?: "General",
                        amount = d.getDouble("amount") ?: 0.0,
                        date = d.getString("date") ?: "",
                        note = d.getString("note") ?: ""
                    )
                }
                if (cloudExpenses.isNotEmpty()) store.expenses = cloudExpenses
            }

            // 4. Fetch Habits
            val habSnap = userRef.collection("adult_habits").get().await()
            if (!habSnap.isEmpty) {
                val cloudHabits = habSnap.documents.mapNotNull { d ->
                    @Suppress("UNCHECKED_CAST")
                    Habit(
                        id = d.getString("id") ?: d.id,
                        name = d.getString("name") ?: "",
                        color = d.getString("color") ?: "#4B0082",
                        history = (d.get("history") as? List<String>) ?: emptyList()
                    )
                }
                if (cloudHabits.isNotEmpty()) store.habits = cloudHabits
            }

            // 5. Fetch Goals
            val goalSnap = userRef.collection("adult_goals").get().await()
            if (!goalSnap.isEmpty) {
                val cloudGoals = goalSnap.documents.mapNotNull { d ->
                    Goal(
                        id = d.getString("id") ?: d.id,
                        name = d.getString("name") ?: "",
                        targetDate = d.getString("targetDate") ?: "",
                        progress = (d.getLong("progress") ?: 0L).toInt()
                    )
                }
                if (cloudGoals.isNotEmpty()) store.goals = cloudGoals
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error fetching adult data from cloud", e)
        }
    }

    // =========================================================================
    // Senior Bidirectional Sync Manager
    // =========================================================================

    suspend fun syncUserProgressOnLogin(uid: String, prefs: Prefs, store: AdultStore) {
        if (uid.isBlank()) return
        try {
            // Kids progress sync
            val cloudKids = getKidsProgress(uid)
            if (cloudKids != null && (cloudKids.coins > 0 || cloudKids.stars > 0 || cloudKids.streak > 0 || cloudKids.badges.isNotEmpty())) {
                if (cloudKids.coins > prefs.coins) prefs.coins = cloudKids.coins
                if (cloudKids.stars > prefs.stars) prefs.stars = cloudKids.stars
                if (cloudKids.streak > prefs.streak) prefs.streak = cloudKids.streak
                if (cloudKids.badges.isNotEmpty()) prefs.badges = (prefs.badges + cloudKids.badges).toSet()
            } else {
                // Cloud is empty, push our local stats
                backupAllLocalProgressToCloud(uid, prefs, store)
            }

            // Adult data sync
            fetchAdultDataFromCloud(uid, store)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Sync error on login", e)
        }
    }

    suspend fun backupAllLocalProgressToCloud(uid: String, prefs: Prefs, store: AdultStore) {
        if (uid.isBlank()) return
        try {
            saveKidsProgress(
                uid,
                KidsProgress(
                    coins = prefs.coins,
                    stars = prefs.stars,
                    streak = prefs.streak,
                    badges = prefs.badges.toList(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
            syncAdultDataToCloud(uid, store)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Backup error", e)
        }
    }

    /**
     * Seed local Room database instantly from offline seed data.
     */
    suspend fun seedLocalRoomDbFast(db: BrightNestDatabase) {
        try {
            val gson = com.google.gson.Gson()
            if (db.kalmaDao().count() == 0) db.kalmaDao().insertAll(SeedData.kalmas)
            db.duaDao().insertAll(SeedData.duas)
            if (db.quizDao().count() == 0) db.quizDao().insertAll(SeedData.quiz)
            if (db.storyDao().count() == 0) {
                val localStories = com.brightnest.app.ui.StoriesData.stories.map { s ->
                    StoryEntity(
                        id = s.id, emoji = s.emoji,
                        titleEn = s.titleEn, titleUr = s.titleUr, titleAr = s.titleAr, titleHi = s.titleHi,
                        taglineEn = s.taglineEn, taglineUr = s.taglineUr, taglineAr = s.taglineAr, taglineHi = s.taglineHi,
                        lessonEn = s.lessonEn, lessonUr = s.lessonUr, lessonAr = s.lessonAr, lessonHi = s.lessonHi,
                        contentEn = s.contentEn, contentUr = s.contentUr, contentAr = s.contentAr, contentHi = s.contentHi
                    )
                }
                db.storyDao().insertAll(localStories)
            }
            if (db.poemDao().count() == 0) db.poemDao().insertAll(SeedData.poems)
            if (db.namazPrayerDao().count() == 0) db.namazPrayerDao().insertAll(SeedData.namazPrayers)
            if (db.namazStepDao().count() == 0) db.namazStepDao().insertAll(SeedData.namazSteps)
            if (db.wuduStepDao().count() == 0) db.wuduStepDao().insertAll(SeedData.wuduSteps)
            if (db.quranLessonDao().count() == 0) db.quranLessonDao().insertAll(SeedData.quranLessons)

            seedExtraOfflineBackup(db)
        } catch (e: Throwable) {
            android.util.Log.e("FirestoreRepository", "seedLocalRoomDbFast error", e)
        }
    }

    /**
     * Fetches Kalmas, Duas, Quizzes, Stories, Poems, Namaz, Wudu, and Quran lessons from Cloud Firestore.
     * If cloud collections exist, stores them in local Room DB for instant offline access.
     * If cloud collections are empty, gracefully uses local SeedData and seeds the cloud.
     */
    suspend fun syncRemoteContentToLocalDb(db: BrightNestDatabase) {
        seedLocalRoomDbFast(db)
        try {
            // 1. Sync Kalmas
            SeedData.kalmas.forEach { k ->
                val map = hashMapOf(
                    "id" to k.id,
                    "number" to k.number,
                    "title" to k.title,
                    "arabic" to k.arabic,
                    "transliteration" to k.transliteration,
                    "translation" to k.translation
                )
                firestore.collection("content_kalmas").document("kalma_${k.id}").set(map)
            }
            db.kalmaDao().insertAll(SeedData.kalmas)

            // 2. Sync Duas
            val duaSnap = firestore.collection("content_duas").get().await()
            if (!duaSnap.isEmpty) {
                val duas = duaSnap.documents.mapIndexedNotNull { idx, d ->
                    val id = d.getLong("id")?.toInt() ?: d.id.removePrefix("dua_").toIntOrNull() ?: (idx + 1)
                    Dua(
                        id = id,
                        title = d.getString("title") ?: return@mapIndexedNotNull null,
                        arabic = d.getString("arabic") ?: "",
                        transliteration = d.getString("transliteration") ?: "",
                        translation = d.getString("translation") ?: "",
                        category = d.getString("category") ?: "daily"
                    )
                }
                if (duas.isNotEmpty()) {
                    db.duaDao().deleteAll()
                    db.duaDao().insertAll(duas)
                }
            } else {
                SeedData.duas.forEach { d ->
                    val map = hashMapOf(
                        "id" to d.id,
                        "title" to d.title,
                        "arabic" to d.arabic,
                        "transliteration" to d.transliteration,
                        "translation" to d.translation,
                        "category" to d.category
                    )
                    firestore.collection("content_duas").document("dua_${d.id}").set(map)
                }
                if (db.duaDao().count() == 0) db.duaDao().insertAll(SeedData.duas)
            }

            // 3. Sync Quizzes
            val quizSnap = firestore.collection("content_quizzes").get().await()
            if (!quizSnap.isEmpty) {
                val quizzes = quizSnap.documents.mapNotNull { d ->
                    val diff = d.getString("difficulty") ?: "easy"
                    QuizQuestion(
                        difficulty = diff,
                        question = d.getString("question") ?: return@mapNotNull null,
                        optionA = d.getString("optionA") ?: "",
                        optionB = d.getString("optionB") ?: "",
                        optionC = d.getString("optionC") ?: "",
                        optionD = d.getString("optionD") ?: "",
                        correctIndex = d.getLong("correctIndex")?.toInt() ?: 0
                    )
                }
                if (quizzes.isNotEmpty()) db.quizDao().insertAll(quizzes)
            } else {
                SeedData.quiz.forEachIndexed { idx, q ->
                    val map = hashMapOf(
                        "difficulty" to q.difficulty,
                        "question" to q.question,
                        "optionA" to q.optionA,
                        "optionB" to q.optionB,
                        "optionC" to q.optionC,
                        "optionD" to q.optionD,
                        "correctIndex" to q.correctIndex
                    )
                    firestore.collection("content_quizzes").document("quiz_$idx").set(map)
                }
                if (db.quizDao().count() == 0) db.quizDao().insertAll(SeedData.quiz)
            }

            // 4. Sync Stories
            val localStories = com.brightnest.app.ui.StoriesData.stories.map { s ->
                StoryEntity(
                    id = s.id, emoji = s.emoji,
                    titleEn = s.titleEn, titleUr = s.titleUr, titleAr = s.titleAr, titleHi = s.titleHi,
                    taglineEn = s.taglineEn, taglineUr = s.taglineUr, taglineAr = s.taglineAr, taglineHi = s.taglineHi,
                    lessonEn = s.lessonEn, lessonUr = s.lessonUr, lessonAr = s.lessonAr, lessonHi = s.lessonHi,
                    contentEn = s.contentEn, contentUr = s.contentUr, contentAr = s.contentAr, contentHi = s.contentHi
                )
            }
            val storySnap = firestore.collection("content_stories").get().await()
            if (!storySnap.isEmpty) {
                val cloudStories = storySnap.documents.mapNotNull { d ->
                    StoryEntity(
                        id = d.getString("id") ?: d.id,
                        emoji = d.getString("emoji") ?: "📖",
                        titleEn = d.getString("titleEn") ?: "", titleUr = d.getString("titleUr") ?: "",
                        titleAr = d.getString("titleAr") ?: "", titleHi = d.getString("titleHi") ?: "",
                        taglineEn = d.getString("taglineEn") ?: "", taglineUr = d.getString("taglineUr") ?: "",
                        taglineAr = d.getString("taglineAr") ?: "", taglineHi = d.getString("taglineHi") ?: "",
                        lessonEn = d.getString("lessonEn") ?: "", lessonUr = d.getString("lessonUr") ?: "",
                        lessonAr = d.getString("lessonAr") ?: "", lessonHi = d.getString("lessonHi") ?: "",
                        contentEn = d.getString("contentEn") ?: "", contentUr = d.getString("contentUr") ?: "",
                        contentAr = d.getString("contentAr") ?: "", contentHi = d.getString("contentHi") ?: ""
                    )
                }
                if (cloudStories.isNotEmpty() && cloudStories.all { it.contentHi.isNotEmpty() && it.contentAr.isNotEmpty() }) {
                    db.storyDao().insertAll(cloudStories)
                } else {
                    localStories.forEach { s ->
                        val map = hashMapOf(
                            "id" to s.id, "emoji" to s.emoji,
                            "titleEn" to s.titleEn, "titleUr" to s.titleUr, "titleAr" to s.titleAr, "titleHi" to s.titleHi,
                            "taglineEn" to s.taglineEn, "taglineUr" to s.taglineUr, "taglineAr" to s.taglineAr, "taglineHi" to s.taglineHi,
                            "lessonEn" to s.lessonEn, "lessonUr" to s.lessonUr, "lessonAr" to s.lessonAr, "lessonHi" to s.lessonHi,
                            "contentEn" to s.contentEn, "contentUr" to s.contentUr, "contentAr" to s.contentAr, "contentHi" to s.contentHi
                        )
                        firestore.collection("content_stories").document("story_${s.id}").set(map)
                    }
                    db.storyDao().insertAll(localStories)
                }
            } else {
                localStories.forEach { s ->
                    val map = hashMapOf(
                        "id" to s.id, "emoji" to s.emoji,
                        "titleEn" to s.titleEn, "titleUr" to s.titleUr, "titleAr" to s.titleAr, "titleHi" to s.titleHi,
                        "taglineEn" to s.taglineEn, "taglineUr" to s.taglineUr, "taglineAr" to s.taglineAr, "taglineHi" to s.taglineHi,
                        "lessonEn" to s.lessonEn, "lessonUr" to s.lessonUr, "lessonAr" to s.lessonAr, "lessonHi" to s.lessonHi,
                        "contentEn" to s.contentEn, "contentUr" to s.contentUr, "contentAr" to s.contentAr, "contentHi" to s.contentHi
                    )
                    firestore.collection("content_stories").document("story_${s.id}").set(map)
                }
                db.storyDao().insertAll(localStories)
            }

            // 5. Sync Poems
            val poemSnap = firestore.collection("content_poems").get().await()
            val cloudPoems = if (!poemSnap.isEmpty) {
                poemSnap.documents.mapNotNull { d ->
                    PoemEntity(
                        title = d.getString("title") ?: return@mapNotNull null,
                        color = d.getString("color") ?: "#EF4444",
                        linesJson = d.getString("linesJson") ?: "[]"
                    )
                }
            } else emptyList()

            val isCloudFull = cloudPoems.isNotEmpty() && cloudPoems.all { p ->
                val linesCount = try {
                    val trimmed = p.linesJson.trim().removePrefix("[").removeSuffix("]")
                    trimmed.split("\",\"").size
                } catch (e: Exception) { 0 }
                linesCount >= 6
            }

            if (isCloudFull) {
                db.poemDao().insertAll(cloudPoems)
            } else {
                SeedData.poems.forEachIndexed { idx, p ->
                    val map = hashMapOf("title" to p.title, "color" to p.color, "linesJson" to p.linesJson)
                    firestore.collection("content_poems").document("poem_$idx").set(map)
                }
                db.poemDao().insertAll(SeedData.poems)
            }

            // 6. Sync Namaz Prayers & Steps
            val namazSnap = firestore.collection("content_namaz_prayers").get().await()
            if (!namazSnap.isEmpty) {
                val cloudPrayers = namazSnap.documents.mapNotNull { d ->
                    NamazPrayerEntity(
                        name = d.getString("name") ?: return@mapNotNull null,
                        arabic = d.getString("arabic") ?: "", urdu = d.getString("urdu") ?: "",
                        time = d.getString("time") ?: "", timeUrdu = d.getString("timeUrdu") ?: "",
                        icon = d.getString("icon") ?: "🕌", about = d.getString("about") ?: "",
                        aboutUrdu = d.getString("aboutUrdu") ?: "", breakdownJson = d.getString("breakdownJson") ?: "[]"
                    )
                }
                if (cloudPrayers.isNotEmpty()) db.namazPrayerDao().insertAll(cloudPrayers)
            } else {
                SeedData.namazPrayers.forEach { p ->
                    val map = hashMapOf(
                        "name" to p.name, "arabic" to p.arabic, "urdu" to p.urdu, "time" to p.time,
                        "timeUrdu" to p.timeUrdu, "icon" to p.icon, "about" to p.about,
                        "aboutUrdu" to p.aboutUrdu, "breakdownJson" to p.breakdownJson
                    )
                    firestore.collection("content_namaz_prayers").document("prayer_${p.name.lowercase()}").set(map)
                }
                if (db.namazPrayerDao().count() == 0) db.namazPrayerDao().insertAll(SeedData.namazPrayers)
            }

            val stepSnap = firestore.collection("content_namaz_steps").get().await()
            if (!stepSnap.isEmpty) {
                val cloudSteps = stepSnap.documents.mapNotNull { d ->
                    NamazStepEntity(
                        stepNumber = d.getLong("stepNumber")?.toInt() ?: return@mapNotNull null,
                        step = d.getString("step") ?: "", stepUrdu = d.getString("stepUrdu") ?: "",
                        desc = d.getString("desc") ?: "", descUrdu = d.getString("descUrdu") ?: ""
                    )
                }
                if (cloudSteps.isNotEmpty()) db.namazStepDao().insertAll(cloudSteps)
            } else {
                SeedData.namazSteps.forEach { s ->
                    val map = hashMapOf(
                        "stepNumber" to s.stepNumber, "step" to s.step, "stepUrdu" to s.stepUrdu,
                        "desc" to s.desc, "descUrdu" to s.descUrdu
                    )
                    firestore.collection("content_namaz_steps").document("step_${s.stepNumber}").set(map)
                }
                if (db.namazStepDao().count() == 0) db.namazStepDao().insertAll(SeedData.namazSteps)
            }

            // 7. Sync Wudu Steps
            val wuduSnap = firestore.collection("content_wudu_steps").get().await()
            if (!wuduSnap.isEmpty) {
                val cloudWudu = wuduSnap.documents.mapNotNull { d ->
                    WuduStepEntity(
                        stepNumber = d.getLong("stepNumber")?.toInt() ?: return@mapNotNull null,
                        titleEn = d.getString("titleEn") ?: "", titleUr = d.getString("titleUr") ?: "",
                        descEn = d.getString("descEn") ?: "", descUr = d.getString("descUr") ?: "",
                        icon = d.getString("icon") ?: "❤️"
                    )
                }
                if (cloudWudu.isNotEmpty()) db.wuduStepDao().insertAll(cloudWudu)
            } else {
                SeedData.wuduSteps.forEach { w ->
                    val map = hashMapOf(
                        "stepNumber" to w.stepNumber, "titleEn" to w.titleEn, "titleUr" to w.titleUr,
                        "descEn" to w.descEn, "descUr" to w.descUr, "icon" to w.icon
                    )
                    firestore.collection("content_wudu_steps").document("wudu_${w.stepNumber}").set(map)
                }
                if (db.wuduStepDao().count() == 0) db.wuduStepDao().insertAll(SeedData.wuduSteps)
            }

            // 8. Sync Quran Lessons
            val quranSnap = firestore.collection("content_quran_lessons").get().await()
            if (!quranSnap.isEmpty) {
                val cloudQuran = quranSnap.documents.mapNotNull { d ->
                    QuranLessonEntity(
                        id = d.getString("id") ?: d.id, icon = d.getString("icon") ?: "📖",
                        title = d.getString("title") ?: "", subtitle = d.getString("subtitle") ?: "",
                        content = d.getString("content") ?: ""
                    )
                }
                if (cloudQuran.isNotEmpty()) db.quranLessonDao().insertAll(cloudQuran)
            } else {
                SeedData.quranLessons.forEach { q ->
                    val map = hashMapOf(
                        "id" to q.id, "icon" to q.icon, "title" to q.title,
                        "subtitle" to q.subtitle, "content" to q.content
                    )
                    firestore.collection("content_quran_lessons").document("quran_${q.id}").set(map)
                }
                if (db.quranLessonDao().count() == 0) db.quranLessonDao().insertAll(SeedData.quranLessons)
            }

            // Sync Extra Collections (Allah Names, Kids Screens, Tenses, Vowels, Basics)
            syncExtraCollections(db)

        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Sync error or offline, loading local backup", e)
            if (db.kalmaDao().count() == 0) db.kalmaDao().insertAll(SeedData.kalmas)
            if (db.duaDao().count() == 0) db.duaDao().insertAll(SeedData.duas)
            if (db.quizDao().count() == 0) db.quizDao().insertAll(SeedData.quiz)
            if (db.storyDao().count() == 0) {
                val localStories = com.brightnest.app.ui.StoriesData.stories.map { s ->
                    StoryEntity(
                        id = s.id, emoji = s.emoji,
                        titleEn = s.titleEn, titleUr = s.titleUr, titleAr = s.titleAr, titleHi = s.titleHi,
                        taglineEn = s.taglineEn, taglineUr = s.taglineUr, taglineAr = s.taglineAr, taglineHi = s.taglineHi,
                        lessonEn = s.lessonEn, lessonUr = s.lessonUr, lessonAr = s.lessonAr, lessonHi = s.lessonHi,
                        contentEn = s.contentEn, contentUr = s.contentUr, contentAr = s.contentAr, contentHi = s.contentHi
                    )
                }
                db.storyDao().insertAll(localStories)
            }
            if (db.poemDao().count() == 0) db.poemDao().insertAll(SeedData.poems)
            if (db.namazPrayerDao().count() == 0) db.namazPrayerDao().insertAll(SeedData.namazPrayers)
            if (db.namazStepDao().count() == 0) db.namazStepDao().insertAll(SeedData.namazSteps)
            if (db.wuduStepDao().count() == 0) db.wuduStepDao().insertAll(SeedData.wuduSteps)
            if (db.quranLessonDao().count() == 0) db.quranLessonDao().insertAll(SeedData.quranLessons)
            
            // Backup offline seeding for extra collections
            seedExtraOfflineBackup(db)
        }
    }

    private suspend fun syncExtraCollections(db: BrightNestDatabase) {
        val gson = com.google.gson.Gson()
        // 9. Allah Names
        val namesSnap = firestore.collection("content_allah_names").get().await()
        if (!namesSnap.isEmpty) {
            val cloudNames = namesSnap.documents.mapNotNull { d ->
                NameEntity(
                    id = d.getLong("id")?.toInt() ?: return@mapNotNull null,
                    arabic = d.getString("arabic") ?: "", transliteration = d.getString("transliteration") ?: "",
                    translation = d.getString("translation") ?: "", urdu = d.getString("urdu") ?: ""
                )
            }
            if (cloudNames.isNotEmpty()) db.nameDao().insertAll(cloudNames)
        } else {
            val localNames = com.brightnest.app.ui.NamesFragment.allNames.map { n ->
                NameEntity(id = n.n, arabic = n.ar, transliteration = n.tr, translation = n.en, urdu = n.ur)
            }
            localNames.forEach { n ->
                val map = hashMapOf("id" to n.id, "arabic" to n.arabic, "transliteration" to n.transliteration, "translation" to n.translation, "urdu" to n.urdu)
                firestore.collection("content_allah_names").document("name_${n.id}").set(map)
            }
            db.nameDao().insertAll(localNames)
        }

        // 10. Kids Screens (ABC, Animals, Colors, Shapes, etc.)
        val kidsSnap = firestore.collection("content_kids_screens").get().await()
        if (!kidsSnap.isEmpty) {
            val cloudScreens = kidsSnap.documents.mapNotNull { d ->
                KidsScreenEntity(
                    id = d.getString("id") ?: d.id,
                    title = d.getString("title") ?: "", titleUr = d.getString("titleUr") ?: "",
                    titleAr = d.getString("titleAr") ?: "", titleHi = d.getString("titleHi") ?: "",
                    icon = d.getString("icon") ?: "⭐", headerColor = d.getString("headerColor") ?: "coral",
                    columns = d.getLong("columns")?.toInt() ?: 4, variant = d.getString("variant") ?: "ICON",
                    itemsJson = d.getString("itemsJson") ?: "[]"
                )
            }
            if (cloudScreens.isNotEmpty()) db.kidsScreenDao().insertAll(cloudScreens)
        } else {
            val localScreens = com.brightnest.app.content.KidsContent.screens.map { s ->
                val id = s.title.lowercase().replace(" ", "_")
                val json = gson.toJson(s.items)
                KidsScreenEntity(
                    id = id, title = s.title, titleUr = s.urdu, titleAr = "", titleHi = "",
                    icon = if (s.items.isNotEmpty()) s.items[0].emoji else "⭐", headerColor = s.headerColor,
                    columns = s.columns, variant = s.variant.name, itemsJson = json
                )
            }
            localScreens.forEach { s ->
                val map = hashMapOf(
                    "id" to s.id, "title" to s.title, "titleUr" to s.titleUr, "titleAr" to s.titleAr, "titleHi" to s.titleHi,
                    "icon" to s.icon, "headerColor" to s.headerColor, "columns" to s.columns, "variant" to s.variant, "itemsJson" to s.itemsJson
                )
                firestore.collection("content_kids_screens").document("screen_${s.id}").set(map)
            }
            db.kidsScreenDao().insertAll(localScreens)
        }

        // Push individual collections for instant Firestore visibility
        com.brightnest.app.content.KidsContent.screens.forEach { s ->
            val collName = when (s.title) {
                "ABC" -> "content_alphabets"
                "Animals" -> "content_animals"
                "Colors" -> "content_colors"
                "Shapes" -> "content_shapes"
                "Body Parts" -> "content_body_parts"
                "Science" -> "content_science"
                "Fruits" -> "content_fruits"
                "Numbers" -> "content_numbers"
                "Urdu" -> "content_urdu_letters"
                "Arabic" -> "content_arabic_letters"
                else -> "content_kids_${s.title.lowercase().replace(" ", "_")}"
            }
            s.items.forEachIndexed { idx, item ->
                val map = hashMapOf(
                    "label" to item.label, "urdu" to item.urdu, "emoji" to item.emoji,
                    "colorHex" to item.colorHex, "detailText" to item.detailText, "speakText" to item.speakText, "category" to item.category
                )
                firestore.collection(collName).document("item_$idx").set(map)
            }
        }

        // 11. Tenses
        val tensesSnap = firestore.collection("content_tenses").get().await()
        if (!tensesSnap.isEmpty) {
            val cloudTenses = tensesSnap.documents.mapNotNull { d ->
                TensesEntity(
                    id = d.getString("id") ?: d.id, title = d.getString("title") ?: "",
                    urduTitle = d.getString("urduTitle") ?: "", color = d.getString("color") ?: "#22C55E",
                    tensesJson = d.getString("tensesJson") ?: "[]"
                )
            }
            if (cloudTenses.isNotEmpty()) db.tensesDao().insertAll(cloudTenses)
        } else {
            val localTenses = com.brightnest.app.ui.TensesFragment.allGroups.map { g ->
                val id = g.title.lowercase().replace(" ", "_")
                val json = gson.toJson(g.tenses)
                TensesEntity(id = id, title = g.title, urduTitle = g.ur, color = g.color, tensesJson = json)
            }
            localTenses.forEach { t ->
                val map = hashMapOf("id" to t.id, "title" to t.title, "urduTitle" to t.urduTitle, "color" to t.color, "tensesJson" to t.tensesJson)
                firestore.collection("content_tenses").document("tense_${t.id}").set(map)
            }
            db.tensesDao().insertAll(localTenses)
        }

        // 12. Vowels
        val vowelsSnap = firestore.collection("content_vowels").get().await()
        if (!vowelsSnap.isEmpty) {
            val cloudVowels = vowelsSnap.documents.mapNotNull { d ->
                VowelsEntity(
                    letter = d.getString("letter") ?: d.id, color = d.getString("color") ?: "#EF4444",
                    wordsJson = d.getString("wordsJson") ?: "[]"
                )
            }
            if (cloudVowels.isNotEmpty()) db.vowelsDao().insertAll(cloudVowels)
        } else {
            val localVowels = com.brightnest.app.ui.VowelsFragment.allVowels.map { v ->
                val json = gson.toJson(v.words)
                VowelsEntity(letter = v.letter, color = v.color, wordsJson = json)
            }
            localVowels.forEach { v ->
                val map = hashMapOf("letter" to v.letter, "color" to v.color, "wordsJson" to v.wordsJson)
                firestore.collection("content_vowels").document("vowel_${v.letter.lowercase()}").set(map)
            }
            db.vowelsDao().insertAll(localVowels)
        }

        // 13. Basics
        val basicsSnap = firestore.collection("content_basics").get().await()
        if (!basicsSnap.isEmpty) {
            val cloudBasics = basicsSnap.documents.mapNotNull { d ->
                BasicsEntity(
                    id = d.getString("id") ?: d.id, title = d.getString("title") ?: "",
                    urduTitle = d.getString("urduTitle") ?: "", emoji = d.getString("emoji") ?: "💡",
                    color = d.getString("color") ?: "#22C55E", chips = d.getBoolean("chips") ?: true,
                    itemsJson = d.getString("itemsJson") ?: "[]"
                )
            }
            if (cloudBasics.isNotEmpty()) db.basicsDao().insertAll(cloudBasics)
        } else {
            val localBasics = com.brightnest.app.ui.BasicsFragment.allSections.map { s ->
                val id = s.title.lowercase().replace(" ", "_")
                val json = gson.toJson(s.items)
                BasicsEntity(id = id, title = s.title, urduTitle = s.ur, emoji = s.emoji, color = s.color, chips = s.chips, itemsJson = json)
            }
            localBasics.forEach { b ->
                val map = hashMapOf("id" to b.id, "title" to b.title, "urduTitle" to b.urduTitle, "emoji" to b.emoji, "color" to b.color, "chips" to b.chips, "itemsJson" to b.itemsJson)
                firestore.collection("content_basics").document("basic_${b.id}").set(map)
            }
            db.basicsDao().insertAll(localBasics)
        }

        // Push individual collections for Basics (Days, Months, Seasons, Good Habits)
        com.brightnest.app.ui.BasicsFragment.allSections.forEach { s ->
            val collName = when (s.title) {
                "Days of the Week" -> "content_days"
                "Months" -> "content_months"
                "Seasons" -> "content_seasons"
                "Good Habits" -> "content_good_habits"
                else -> "content_${s.title.lowercase().replace(" ", "_")}"
            }
            s.items.forEachIndexed { idx, item ->
                val map = hashMapOf("en" to item.en, "ur" to item.ur)
                firestore.collection(collName).document("item_$idx").set(map)
            }
        }
    }

    private suspend fun seedExtraOfflineBackup(db: BrightNestDatabase) {
        val gson = com.google.gson.Gson()
        if (db.nameDao().count() == 0) {
            val localNames = com.brightnest.app.ui.NamesFragment.allNames.map { n ->
                NameEntity(id = n.n, arabic = n.ar, transliteration = n.tr, translation = n.en, urdu = n.ur)
            }
            db.nameDao().insertAll(localNames)
        }
        if (db.kidsScreenDao().count() == 0) {
            val localScreens = com.brightnest.app.content.KidsContent.screens.map { s ->
                val id = s.title.lowercase().replace(" ", "_")
                KidsScreenEntity(
                    id = id, title = s.title, titleUr = s.urdu, titleAr = "", titleHi = "",
                    icon = if (s.items.isNotEmpty()) s.items[0].emoji else "⭐", headerColor = s.headerColor,
                    columns = s.columns, variant = s.variant.name, itemsJson = gson.toJson(s.items)
                )
            }
            db.kidsScreenDao().insertAll(localScreens)
        }
        if (db.tensesDao().count() == 0) {
            val localTenses = com.brightnest.app.ui.TensesFragment.allGroups.map { g ->
                val id = g.title.lowercase().replace(" ", "_")
                TensesEntity(id = id, title = g.title, urduTitle = g.ur, color = g.color, tensesJson = gson.toJson(g.tenses))
            }
            db.tensesDao().insertAll(localTenses)
        }
        if (db.vowelsDao().count() == 0) {
            val localVowels = com.brightnest.app.ui.VowelsFragment.allVowels.map { v ->
                VowelsEntity(letter = v.letter, color = v.color, wordsJson = gson.toJson(v.words))
            }
            db.vowelsDao().insertAll(localVowels)
        }
        if (db.basicsDao().count() == 0) {
            val localBasics = com.brightnest.app.ui.BasicsFragment.allSections.map { s ->
                val id = s.title.lowercase().replace(" ", "_")
                BasicsEntity(id = id, title = s.title, urduTitle = s.ur, emoji = s.emoji, color = s.color, chips = s.chips, itemsJson = gson.toJson(s.items))
            }
            db.basicsDao().insertAll(localBasics)
        }
    }

    fun seedCloudFirestoreDirectly() {
        try {
            val gson = com.google.gson.Gson()
            SeedData.kalmas.forEach { k ->
                val map = hashMapOf(
                    "id" to k.id, "number" to k.number, "title" to k.title,
                    "arabic" to k.arabic, "transliteration" to k.transliteration, "translation" to k.translation
                )
                firestore.collection("content_kalmas").document("kalma_${k.id}").set(map)
            }
            SeedData.duas.forEach { d ->
                val map = hashMapOf(
                    "id" to d.id,
                    "title" to d.title, "arabic" to d.arabic, "transliteration" to d.transliteration,
                    "translation" to d.translation, "category" to d.category
                )
                firestore.collection("content_duas").document("dua_${d.id}").set(map)
            }
            SeedData.quiz.forEachIndexed { idx, q ->
                val map = hashMapOf(
                    "difficulty" to q.difficulty, "question" to q.question, "optionA" to q.optionA,
                    "optionB" to q.optionB, "optionC" to q.optionC, "optionD" to q.optionD, "correctIndex" to q.correctIndex
                )
                firestore.collection("content_quizzes").document("quiz_$idx").set(map)
            }
            com.brightnest.app.ui.StoriesData.stories.forEach { s ->
                val map = hashMapOf(
                    "id" to s.id, "emoji" to s.emoji,
                    "titleEn" to s.titleEn, "titleUr" to s.titleUr, "titleAr" to s.titleAr, "titleHi" to s.titleHi,
                    "taglineEn" to s.taglineEn, "taglineUr" to s.taglineUr, "taglineAr" to s.taglineAr, "taglineHi" to s.taglineHi,
                    "lessonEn" to s.lessonEn, "lessonUr" to s.lessonUr, "lessonAr" to s.lessonAr, "lessonHi" to s.lessonHi,
                    "contentEn" to s.contentEn, "contentUr" to s.contentUr, "contentAr" to s.contentAr, "contentHi" to s.contentHi
                )
                firestore.collection("content_stories").document("story_${s.id}").set(map)
            }
            SeedData.poems.forEachIndexed { idx, p ->
                val map = hashMapOf("title" to p.title, "color" to p.color, "linesJson" to p.linesJson)
                firestore.collection("content_poems").document("poem_$idx").set(map)
            }
            SeedData.namazPrayers.forEach { p ->
                val map = hashMapOf(
                    "name" to p.name, "arabic" to p.arabic, "urdu" to p.urdu, "time" to p.time,
                    "timeUrdu" to p.timeUrdu, "icon" to p.icon, "about" to p.about,
                    "aboutUrdu" to p.aboutUrdu, "breakdownJson" to p.breakdownJson
                )
                firestore.collection("content_namaz_prayers").document("prayer_${p.name.lowercase()}").set(map)
            }
            SeedData.namazSteps.forEach { s ->
                val map = hashMapOf(
                    "stepNumber" to s.stepNumber, "step" to s.step, "stepUrdu" to s.stepUrdu,
                    "desc" to s.desc, "descUrdu" to s.descUrdu
                )
                firestore.collection("content_namaz_steps").document("step_${s.stepNumber}").set(map)
            }
            SeedData.wuduSteps.forEach { w ->
                val map = hashMapOf(
                    "stepNumber" to w.stepNumber, "titleEn" to w.titleEn, "titleUr" to w.titleUr,
                    "descEn" to w.descEn, "descUr" to w.descUr, "icon" to w.icon
                )
                firestore.collection("content_wudu_steps").document("wudu_${w.stepNumber}").set(map)
            }
            SeedData.quranLessons.forEach { q ->
                val map = hashMapOf(
                    "id" to q.id, "icon" to q.icon, "title" to q.title,
                    "subtitle" to q.subtitle, "content" to q.content
                )
                firestore.collection("content_quran_lessons").document("quran_${q.id}").set(map)
            }

            // Direct seeding of Allah Names
            com.brightnest.app.ui.NamesFragment.allNames.forEach { n ->
                val map = hashMapOf("id" to n.n, "arabic" to n.ar, "transliteration" to n.tr, "translation" to n.en, "urdu" to n.ur)
                firestore.collection("content_allah_names").document("name_${n.n}").set(map)
            }

            // Direct seeding of Kids Screens (ABC, Animals, Colors, Shapes, etc.)
            com.brightnest.app.content.KidsContent.screens.forEach { s ->
                val id = s.title.lowercase().replace(" ", "_")
                val json = gson.toJson(s.items)
                val map = hashMapOf(
                    "id" to id, "title" to s.title, "titleUr" to s.urdu, "titleAr" to "", "titleHi" to "",
                    "icon" to (if (s.items.isNotEmpty()) s.items[0].emoji else "⭐"), "headerColor" to s.headerColor,
                    "columns" to s.columns, "variant" to s.variant.name, "itemsJson" to json
                )
                firestore.collection("content_kids_screens").document("screen_$id").set(map)

                val collName = when (s.title) {
                    "ABC" -> "content_alphabets"
                    "Animals" -> "content_animals"
                    "Colors" -> "content_colors"
                    "Shapes" -> "content_shapes"
                    "Body Parts" -> "content_body_parts"
                    "Science" -> "content_science"
                    "Fruits" -> "content_fruits"
                    "Numbers" -> "content_numbers"
                    "Urdu" -> "content_urdu_letters"
                    "Arabic" -> "content_arabic_letters"
                    else -> "content_kids_$id"
                }
                s.items.forEachIndexed { idx, item ->
                    val itemMap = hashMapOf(
                        "label" to item.label, "urdu" to item.urdu, "emoji" to item.emoji,
                        "colorHex" to item.colorHex, "detailText" to item.detailText, "speakText" to item.speakText, "category" to item.category
                    )
                    firestore.collection(collName).document("item_$idx").set(itemMap)
                }
            }

            // Direct seeding of Tenses
            com.brightnest.app.ui.TensesFragment.allGroups.forEach { g ->
                val id = g.title.lowercase().replace(" ", "_")
                val map = hashMapOf("id" to id, "title" to g.title, "urduTitle" to g.ur, "color" to g.color, "tensesJson" to gson.toJson(g.tenses))
                firestore.collection("content_tenses").document("tense_$id").set(map)
            }

            // Direct seeding of Vowels
            com.brightnest.app.ui.VowelsFragment.allVowels.forEach { v ->
                val map = hashMapOf("letter" to v.letter, "color" to v.color, "wordsJson" to gson.toJson(v.words))
                firestore.collection("content_vowels").document("vowel_${v.letter.lowercase()}").set(map)
            }

            // Direct seeding of Basics & individual sections
            com.brightnest.app.ui.BasicsFragment.allSections.forEach { s ->
                val id = s.title.lowercase().replace(" ", "_")
                val map = hashMapOf("id" to id, "title" to s.title, "urduTitle" to s.ur, "emoji" to s.emoji, "color" to s.color, "chips" to s.chips, "itemsJson" to gson.toJson(s.items))
                firestore.collection("content_basics").document("basic_$id").set(map)

                val collName = when (s.title) {
                    "Days of the Week" -> "content_days"
                    "Months" -> "content_months"
                    "Seasons" -> "content_seasons"
                    "Good Habits" -> "content_good_habits"
                    else -> "content_$id"
                }
                s.items.forEachIndexed { idx, item ->
                    val itemMap = hashMapOf("en" to item.en, "ur" to item.ur)
                    firestore.collection(collName).document("item_$idx").set(itemMap)
                }
            }

        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Direct seed error", e)
        }
    }
}
