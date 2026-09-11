package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    StudentEntity::class,
    AchievementEntity::class,
    ProjectEntity::class,
    TeamEntity::class,
    TeamMemberEntity::class,
    TeamJoinRequestEntity::class,
    ChatMessageEntity::class,
    TeamUpdateEntity::class,
    OpportunityEntity::class,
    NotificationEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class TeamMateDatabase : RoomDatabase() {
  abstract fun studentDao(): StudentDao
  abstract fun achievementDao(): AchievementDao
  abstract fun projectDao(): ProjectDao
  abstract fun teamDao(): TeamDao
  abstract fun chatMessageDao(): ChatMessageDao
  abstract fun teamUpdateDao(): TeamUpdateDao
  abstract fun opportunityDao(): OpportunityDao
  abstract fun notificationDao(): NotificationDao

  companion object {
    @Volatile
    private var INSTANCE: TeamMateDatabase? = null

    fun getDatabase(context: Context): TeamMateDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TeamMateDatabase::class.java,
          "teammate_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
