package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
  @Query("SELECT * FROM students ORDER BY achievementScore DESC")
  fun getAllStudents(): Flow<List<StudentEntity>>

  @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
  fun getStudentById(id: String): Flow<StudentEntity?>

  @Query("SELECT * FROM students WHERE isCurrentUser = 1 LIMIT 1")
  fun getCurrentUser(): Flow<StudentEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudents(students: List<StudentEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudent(student: StudentEntity)

  @Query("UPDATE students SET isConnected = :isConnected, connectionPending = :isPending WHERE id = :studentId")
  suspend fun updateConnectionStatus(studentId: String, isConnected: Boolean, isPending: Boolean)

  @Query("SELECT COUNT(*) FROM students")
  suspend fun getCount(): Int
}

@Dao
interface AchievementDao {
  @Query("SELECT * FROM achievements WHERE studentId = :studentId ORDER BY date DESC")
  fun getAchievementsForStudent(studentId: String): Flow<List<AchievementEntity>>

  @Query("SELECT * FROM achievements ORDER BY id DESC")
  fun getAllAchievements(): Flow<List<AchievementEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAchievements(items: List<AchievementEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAchievement(item: AchievementEntity)
}

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects WHERE studentId = :studentId")
  fun getProjectsForStudent(studentId: String): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjects(items: List<ProjectEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(item: ProjectEntity)
}

@Dao
interface TeamDao {
  @Query("SELECT * FROM teams ORDER BY id DESC")
  fun getAllTeams(): Flow<List<TeamEntity>>

  @Query("SELECT * FROM teams WHERE id = :id LIMIT 1")
  fun getTeamById(id: String): Flow<TeamEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeams(teams: List<TeamEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeam(team: TeamEntity)

  @Query("SELECT * FROM team_members WHERE teamId = :teamId")
  fun getTeamMembers(teamId: String): Flow<List<TeamMemberEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeamMembers(members: List<TeamMemberEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeamMember(member: TeamMemberEntity)

  @Query("SELECT * FROM team_requests WHERE teamId = :teamId ORDER BY appliedAt DESC")
  fun getTeamRequests(teamId: String): Flow<List<TeamJoinRequestEntity>>

  @Query("SELECT * FROM team_requests ORDER BY appliedAt DESC")
  fun getAllTeamRequests(): Flow<List<TeamJoinRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeamRequest(request: TeamJoinRequestEntity)

  @Query("UPDATE team_requests SET status = :status WHERE id = :requestId")
  suspend fun updateRequestStatus(requestId: String, status: String)

  @Query("UPDATE teams SET status = :status WHERE id = :teamId")
  suspend fun updateTeamStatus(teamId: String, status: String)
}

@Dao
interface ChatMessageDao {
  @Query("SELECT * FROM chat_messages WHERE teamId = :teamId ORDER BY timestamp ASC")
  fun getMessagesForTeam(teamId: String): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<ChatMessageEntity>)

  @Query("UPDATE chat_messages SET isPinned = :isPinned WHERE id = :messageId")
  suspend fun togglePinMessage(messageId: String, isPinned: Boolean)
}

@Dao
interface TeamUpdateDao {
  @Query("SELECT * FROM team_updates WHERE teamId = :teamId ORDER BY timestamp DESC")
  fun getUpdatesForTeam(teamId: String): Flow<List<TeamUpdateEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUpdate(update: TeamUpdateEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUpdates(updates: List<TeamUpdateEntity>)
}

@Dao
interface OpportunityDao {
  @Query("SELECT * FROM opportunities")
  fun getAllOpportunities(): Flow<List<OpportunityEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOpportunities(items: List<OpportunityEntity>)

  @Query("UPDATE opportunities SET isBookmarked = :isBookmarked WHERE id = :id")
  suspend fun toggleBookmark(id: String, isBookmarked: Boolean)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY id DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotifications(items: List<NotificationEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(item: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllAsRead()

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: String)
}
