package com.example.data.model

data class Student(
  val id: String,
  val name: String,
  val email: String = "",
  val phone: String = "",
  val college: String,
  val collegeCode: String = "",
  val prn: String = "",
  val branch: String,
  val yearOfStudy: String,
  val gradYear: String = "2027",
  val headline: String,
  val bio: String,
  val avatarInitials: String,
  val skills: List<String>,
  val interests: List<String>,
  val linkedinUrl: String = "",
  val githubUrl: String = "",
  val instagramUrl: String = "",
  val achievementScore: Int = 1200,
  val hackathonsCount: Int = 0,
  val competitionsCount: Int = 0,
  val projectsCount: Int = 0,
  val isConnected: Boolean = false,
  val connectionPending: Boolean = false,
  val isCurrentUser: Boolean = false
)

data class AchievementItem(
  val id: String,
  val studentId: String,
  val title: String,
  val organization: String,
  val date: String,
  val category: String, // Hackathon, Competition, Certificate, Internship, Project, Workshop, Award, Research
  val description: String
)

data class ProjectItem(
  val id: String,
  val studentId: String,
  val title: String,
  val description: String,
  val technologies: List<String>,
  val githubUrl: String = "",
  val liveUrl: String = ""
)

data class TeamItem(
  val id: String,
  val name: String,
  val competitionName: String,
  val competitionLink: String,
  val competitionDescription: String,
  val problemStatement: String,
  val leaderId: String,
  val leaderName: String,
  val leaderCollege: String,
  val membersRequired: Int,
  val openRoles: List<String>,
  val requiredSkills: List<String>,
  val deadline: String,
  val status: String, // "OPEN", "COMPLETE"
  val isUserMember: Boolean = false,
  val isUserLeader: Boolean = false,
  val members: List<TeamMemberInfo> = emptyList()
)

data class TeamMemberInfo(
  val id: String,
  val teamId: String,
  val studentId: String,
  val name: String,
  val role: String,
  val skills: List<String>,
  val isLeader: Boolean = false
)

data class TeamJoinRequestItem(
  val id: String,
  val teamId: String,
  val studentId: String,
  val studentName: String,
  val studentHeadline: String,
  val studentCollege: String,
  val studentBranch: String,
  val studentYear: String,
  val studentSkills: List<String>,
  val status: String, // "PENDING", "SHORTLISTED", "ACCEPTED", "DECLINED"
  val appliedAt: String
)

data class ChatMessageItem(
  val id: String,
  val teamId: String,
  val senderId: String,
  val senderName: String,
  val message: String,
  val timestamp: Long,
  val isPinned: Boolean = false,
  val attachmentName: String? = null
)

data class TeamUpdateItem(
  val id: String,
  val teamId: String,
  val title: String,
  val content: String,
  val authorName: String,
  val category: String, // "ANNOUNCEMENT", "DECISION", "TASK"
  val timestamp: Long
)

data class OpportunityItem(
  val id: String,
  val title: String,
  val organization: String,
  val category: String, // "Hackathon", "Competition", "Internship", "Workshop"
  val deadline: String,
  val location: String,
  val eligibility: String,
  val teamRequirement: String,
  val link: String,
  val isBookmarked: Boolean = false
)

data class NotificationModel(
  val id: String,
  val title: String,
  val message: String,
  val category: String,
  val timeAgo: String,
  val isRead: Boolean = false
)
