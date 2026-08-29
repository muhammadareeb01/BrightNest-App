package com.brightnest.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kalmas")
data class Kalma(
    @PrimaryKey val id: Int,
    val number: Int,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String
)

@Entity(tableName = "duas")
data class Dua(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val category: String
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val difficulty: String, // easy | medium | hard
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val emoji: String,
    val titleEn: String,
    val titleUr: String,
    val titleAr: String = "",
    val titleHi: String = "",
    val taglineEn: String,
    val taglineUr: String,
    val taglineAr: String = "",
    val taglineHi: String = "",
    val lessonEn: String,
    val lessonUr: String,
    val lessonAr: String = "",
    val lessonHi: String = "",
    val contentEn: String,
    val contentUr: String,
    val contentAr: String = "",
    val contentHi: String = ""
)

@Entity(tableName = "poems")
data class PoemEntity(
    @PrimaryKey val title: String,
    val color: String,
    val linesJson: String // JSON array of lines
)

@Entity(tableName = "namaz_prayers")
data class NamazPrayerEntity(
    @PrimaryKey val name: String,
    val arabic: String,
    val urdu: String,
    val time: String,
    val timeUrdu: String,
    val icon: String,
    val about: String,
    val aboutUrdu: String,
    val breakdownJson: String // JSON representation of breakdown list
)

@Entity(tableName = "namaz_steps")
data class NamazStepEntity(
    @PrimaryKey val stepNumber: Int,
    val step: String,
    val stepUrdu: String,
    val desc: String,
    val descUrdu: String
)

@Entity(tableName = "wudu_steps")
data class WuduStepEntity(
    @PrimaryKey val stepNumber: Int,
    val titleEn: String,
    val titleUr: String,
    val descEn: String,
    val descUr: String,
    val icon: String
)

@Entity(tableName = "quran_lessons")
data class QuranLessonEntity(
    @PrimaryKey val id: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val content: String
)

@Entity(tableName = "allah_names")
data class NameEntity(
    @PrimaryKey val id: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val urdu: String
)

@Entity(tableName = "kids_screens")
data class KidsScreenEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleUr: String,
    val titleAr: String = "",
    val titleHi: String = "",
    val icon: String,
    val headerColor: String,
    val columns: Int,
    val variant: String,
    val itemsJson: String // JSON array of items
)

@Entity(tableName = "coloring_pictures")
data class ColoringPictureEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val regionsJson: String
)

@Entity(tableName = "content_tenses")
data class TensesEntity(
    @PrimaryKey val id: String,
    val title: String,
    val urduTitle: String,
    val color: String,
    val tensesJson: String
)

@Entity(tableName = "content_vowels")
data class VowelsEntity(
    @PrimaryKey val letter: String,
    val color: String,
    val wordsJson: String
)

@Entity(tableName = "content_basics")
data class BasicsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val urduTitle: String,
    val emoji: String,
    val color: String,
    val chips: Boolean,
    val itemsJson: String
)
