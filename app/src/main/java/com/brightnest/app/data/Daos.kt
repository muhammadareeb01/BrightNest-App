package com.brightnest.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface KalmaDao {
    @Query("SELECT * FROM kalmas ORDER BY number ASC")
    suspend fun all(): List<Kalma>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Kalma>)

    @Query("SELECT COUNT(*) FROM kalmas")
    suspend fun count(): Int
}

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas ORDER BY id ASC")
    suspend fun all(): List<Dua>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Dua>)

    @Query("DELETE FROM duas")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM duas")
    suspend fun count(): Int
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_questions WHERE difficulty = :level ORDER BY RANDOM() LIMIT :limit")
    suspend fun byDifficulty(level: String, limit: Int): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuizQuestion>)

    @Query("SELECT COUNT(*) FROM quiz_questions")
    suspend fun count(): Int
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    suspend fun all(): List<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StoryEntity>)

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun count(): Int
}

@Dao
interface PoemDao {
    @Query("SELECT * FROM poems")
    suspend fun all(): List<PoemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PoemEntity>)

    @Query("SELECT COUNT(*) FROM poems")
    suspend fun count(): Int
}

@Dao
interface NamazPrayerDao {
    @Query("SELECT * FROM namaz_prayers")
    suspend fun all(): List<NamazPrayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NamazPrayerEntity>)

    @Query("SELECT COUNT(*) FROM namaz_prayers")
    suspend fun count(): Int
}

@Dao
interface NamazStepDao {
    @Query("SELECT * FROM namaz_steps ORDER BY stepNumber ASC")
    suspend fun all(): List<NamazStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NamazStepEntity>)

    @Query("SELECT COUNT(*) FROM namaz_steps")
    suspend fun count(): Int
}

@Dao
interface WuduStepDao {
    @Query("SELECT * FROM wudu_steps ORDER BY stepNumber ASC")
    suspend fun all(): List<WuduStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WuduStepEntity>)

    @Query("SELECT COUNT(*) FROM wudu_steps")
    suspend fun count(): Int
}

@Dao
interface QuranLessonDao {
    @Query("SELECT * FROM quran_lessons")
    suspend fun all(): List<QuranLessonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuranLessonEntity>)

    @Query("SELECT COUNT(*) FROM quran_lessons")
    suspend fun count(): Int
}

@Dao
interface NameDao {
    @Query("SELECT * FROM allah_names ORDER BY id ASC")
    suspend fun all(): List<NameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NameEntity>)

    @Query("SELECT COUNT(*) FROM allah_names")
    suspend fun count(): Int
}

@Dao
interface KidsScreenDao {
    @Query("SELECT * FROM kids_screens")
    suspend fun all(): List<KidsScreenEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<KidsScreenEntity>)

    @Query("SELECT COUNT(*) FROM kids_screens")
    suspend fun count(): Int
}

@Dao
interface ColoringPictureDao {
    @Query("SELECT * FROM coloring_pictures")
    suspend fun all(): List<ColoringPictureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ColoringPictureEntity>)

    @Query("SELECT COUNT(*) FROM coloring_pictures")
    suspend fun count(): Int
}

@Dao
interface TensesDao {
    @Query("SELECT * FROM content_tenses")
    suspend fun all(): List<TensesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TensesEntity>)

    @Query("SELECT COUNT(*) FROM content_tenses")
    suspend fun count(): Int
}

@Dao
interface VowelsDao {
    @Query("SELECT * FROM content_vowels")
    suspend fun all(): List<VowelsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VowelsEntity>)

    @Query("SELECT COUNT(*) FROM content_vowels")
    suspend fun count(): Int
}

@Dao
interface BasicsDao {
    @Query("SELECT * FROM content_basics")
    suspend fun all(): List<BasicsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BasicsEntity>)

    @Query("SELECT COUNT(*) FROM content_basics")
    suspend fun count(): Int
}
