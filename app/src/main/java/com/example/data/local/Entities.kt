package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val college: String,
  val collegeCode: String,
  val prn: String,
  val branch: String,
  val yearOfStudy: String,
  val gradYear: String,
  val headline: String,
  val bio: String,
  val avatarInitials: String,
  val skills: String, // comma-separated
  val interests: String, // comma-separated
  val linkedinUrl: String,
  val githubUrl: String,
  val instagramUrl: String,
  val achievementScore: Int,
  val hackathonsCount: Int,
  val competitionsCount: Int,
  val projectsCount: Int,
  val isConnected: Boolean,
  val connectionPending: Boolean,
  val isCurrentUser: Boolean
)

@Entity(tableName = "achievements")
data class AchievementEntity(
  @PrimaryKey val id: String,
  val studentId: String,
  val title: String,
  val organization: String,
  val date: String,
  val category: String,
  val description: String
)

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val studentId: String,
  val title: String,
  val description: String,
  val technologies: String, // comma-separated
  val githubUrl: String,
  val liveUrl: String
)

@Entity(tableName = "teams")
data class TeamEntity(
  @PrimaryKey val id: String,
  val name: String,
  val competitionName: String,
  val competitionLink: String,
  val competitionDescription: String,
  val problemStatement: String,
  val leaderId: String,
  val leaderName: String,
  val leaderCollege: String,
  val membersRequired: Int,
  val openRoles: String, // comma-separated
  val requiredSkills: String, // comma-separated
  val deadline: String,
  val status: String,
  val isUserMember: Boolean,
  val isUserLeader: Boolean
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
  @PrimaryKey val id: String,
  val teamId: String,
  val studentId: String,
  val name: String,
  val role: String,
  val skills: String, // comma-separated
  val isLeader: Boolean
)

@Entity(tableName = "team_requests")
data class TeamJoinRequestEntity(
  @PrimaryKey val id: String,
  val teamId: String,
  val studentId: String,
  val studentName: String,
  val studentHeadline: String,
  val studentCollege: String,
  val studentBranch: String,
  val studentYear: String,
  val studentSkills: String, // comma-separated
  val status: String, // "PENDING", "SHORTLISTED", "ACCEPTED", "DECLINED"
  val appliedAt: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey val id: String,
  val teamId: String,
  val senderId: String,
  val senderName: String,
  val message: String,
  val timestamp: Long,
  val isPinned: Boolean,
  val attachmentName: String?
)

@Entity(tableName = "team_updates")
data class TeamUpdateEntity(
  @PrimaryKey val id: String,
  val teamId: String,
  val title: String,
  val content: String,
  val authorName: String,
  val category: String,
  val timestamp: Long
)

@Entity(tableName = "opportunities")
data class OpportunityEntity(
  @PrimaryKey val id: String,
  val title: String,
  val organization: String,
  val category: String,
  val deadline: String,
  val location: String,
  val eligibility: String,
  val teamRequirement: String,
  val link: String,
  val isBookmarked: Boolean
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val title: String,
  val message: String,
  val category: String,
  val timeAgo: String,
  val isRead: Boolean
)
