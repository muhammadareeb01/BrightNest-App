package com.brightnest.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Kalma::class,
        Dua::class,
        QuizQuestion::class,
        StoryEntity::class,
        PoemEntity::class,
        NamazPrayerEntity::class,
        NamazStepEntity::class,
        WuduStepEntity::class,
        QuranLessonEntity::class,
        NameEntity::class,
        KidsScreenEntity::class,
        ColoringPictureEntity::class,
        TensesEntity::class,
        VowelsEntity::class,
        BasicsEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class BrightNestDatabase : RoomDatabase() {
    abstract fun kalmaDao(): KalmaDao
    abstract fun duaDao(): DuaDao
    abstract fun quizDao(): QuizDao
    abstract fun storyDao(): StoryDao
    abstract fun poemDao(): PoemDao
    abstract fun namazPrayerDao(): NamazPrayerDao
    abstract fun namazStepDao(): NamazStepDao
    abstract fun wuduStepDao(): WuduStepDao
    abstract fun quranLessonDao(): QuranLessonDao
    abstract fun nameDao(): NameDao
    abstract fun kidsScreenDao(): KidsScreenDao
    abstract fun coloringPictureDao(): ColoringPictureDao
    abstract fun tensesDao(): TensesDao
    abstract fun vowelsDao(): VowelsDao
    abstract fun basicsDao(): BasicsDao

    companion object {
        @Volatile
        private var INSTANCE: BrightNestDatabase? = null

        fun get(context: Context): BrightNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    BrightNestDatabase::class.java,
                    "brightnest.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = db
                db
            }
        }
    }
}
