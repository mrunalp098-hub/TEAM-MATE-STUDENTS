package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TeamMateDatabase
import com.example.data.model.*
import com.example.data.repository.TeamMateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab(val label: String) {
  HOME("Home"),
  DISCOVER("Discover"),
  TEAMS("Teams"),
  LEADERBOARD("Leaderboard"),
  PROFILE("Profile")
}

sealed class AppDestination {
  object Landing : AppDestination()
  object Onboarding : AppDestination()
  object MainContent : AppDestination()
  data class StudentProfileView(val studentId: String) : AppDestination()
  data class TeamWorkspaceView(val teamId: String) : AppDestination()
  object OpportunitiesView : AppDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = TeamMateDatabase.getDatabase(application)
  private val repository = TeamMateRepository(database)

  init {
    viewModelScope.launch {
      repository.seedInitialDataIfNeeded()
    }
  }

  // App Navigation
  private val _currentDestination = MutableStateFlow<AppDestination>(AppDestination.MainContent)
  val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

  private val _currentTab = MutableStateFlow(MainTab.HOME)
  val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

  // Modals & Sheets
  private val _isNotificationSheetOpen = MutableStateFlow(false)
  val isNotificationSheetOpen: StateFlow<Boolean> = _isNotificationSheetOpen.asStateFlow()

  private val _isGlobalSearchOpen = MutableStateFlow(false)
  val isGlobalSearchOpen: StateFlow<Boolean> = _isGlobalSearchOpen.asStateFlow()

  private val _isCreateTeamOpen = MutableStateFlow(false)
  val isCreateTeamOpen: StateFlow<Boolean> = _isCreateTeamOpen.asStateFlow()

  private val _isAddAchievementOpen = MutableStateFlow(false)
  val isAddAchievementOpen: StateFlow<Boolean> = _isAddAchievementOpen.asStateFlow()

  private val _teamToJoin = MutableStateFlow<TeamItem?>(null)
  val teamToJoin: StateFlow<TeamItem?> = _teamToJoin.asStateFlow()

  // Filters & Search
  private val _studentSearchQuery = MutableStateFlow("")
  val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

  private val _selectedSkillFilter = MutableStateFlow("All")
  val selectedSkillFilter: StateFlow<String> = _selectedSkillFilter.asStateFlow()

  private val _selectedCollegeFilter = MutableStateFlow("All")
  val selectedCollegeFilter: StateFlow<String> = _selectedCollegeFilter.asStateFlow()

  private val _selectedLeaderboardCategory = MutableStateFlow("Overall")
  val selectedLeaderboardCategory: StateFlow<String> = _selectedLeaderboardCategory.asStateFlow()

  // Data streams from repository
  val currentUser: StateFlow<Student?> = repository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allStudents: StateFlow<List<Student>> = repository.allStudents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTeams: StateFlow<List<TeamItem>> = repository.allTeams
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOpportunities: StateFlow<List<OpportunityItem>> = repository.allOpportunities
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allNotifications: StateFlow<List<NotificationModel>> = repository.allNotifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTeamRequests: StateFlow<List<TeamJoinRequestItem>> = repository.getAllTeamRequests()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic state queries
  fun getStudentById(id: String): Flow<Student?> = repository.getStudentById(id)
  fun getAchievementsForStudent(id: String): Flow<List<AchievementItem>> = repository.getAchievementsForStudent(id)
  fun getProjectsForStudent(id: String): Flow<List<ProjectItem>> = repository.getProjectsForStudent(id)
  fun getTeamWithMembers(teamId: String): Flow<TeamItem?> = repository.getTeamWithMembers(teamId)
  fun getTeamRequests(teamId: String): Flow<List<TeamJoinRequestItem>> = repository.getTeamRequests(teamId)
  fun getTeamChatMessages(teamId: String): Flow<List<ChatMessageItem>> = repository.getTeamChatMessages(teamId)
  fun getTeamUpdates(teamId: String): Flow<List<TeamUpdateItem>> = repository.getTeamUpdates(teamId)

  // Navigation handlers
  fun navigateTo(destination: AppDestination) {
    _currentDestination.value = destination
  }

  fun setTab(tab: MainTab) {
    _currentTab.value = tab
    _currentDestination.value = AppDestination.MainContent
  }

  fun openNotifications() { _isNotificationSheetOpen.value = true }
  fun closeNotifications() { _isNotificationSheetOpen.value = false }

  fun openGlobalSearch() { _isGlobalSearchOpen.value = true }
  fun closeGlobalSearch() { _isGlobalSearchOpen.value = false }

  fun openCreateTeam() { _isCreateTeamOpen.value = true }
  fun closeCreateTeam() { _isCreateTeamOpen.value = false }

  fun openAddAchievement() { _isAddAchievementOpen.value = true }
  fun closeAddAchievement() { _isAddAchievementOpen.value = false }

  fun openJoinRequest(team: TeamItem) { _teamToJoin.value = team }
  fun closeJoinRequest() { _teamToJoin.value = null }

  fun setStudentSearchQuery(query: String) { _studentSearchQuery.value = query }
  fun setSkillFilter(filter: String) { _selectedSkillFilter.value = filter }
  fun setCollegeFilter(filter: String) { _selectedCollegeFilter.value = filter }
  fun setLeaderboardCategory(cat: String) { _selectedLeaderboardCategory.value = cat }

  // Action methods
  fun toggleConnection(student: Student) {
    viewModelScope.launch {
      if (student.isConnected) {
        repository.updateConnectionStatus(student.id, isConnected = false, isPending = false)
      } else if (student.connectionPending) {
        repository.updateConnectionStatus(student.id, isConnected = false, isPending = false)
      } else {
        repository.updateConnectionStatus(student.id, isConnected = false, isPending = true)
      }
    }
  }

  fun createTeam(
    name: String,
    competitionName: String,
    competitionLink: String,
    competitionDescription: String,
    problemStatement: String,
    membersRequired: Int,
    openRoles: List<String>,
    requiredSkills: List<String>,
    deadline: String
  ) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      val team = TeamItem(
        id = "team_${System.currentTimeMillis()}",
        name = name,
        competitionName = competitionName,
        competitionLink = competitionLink,
        competitionDescription = competitionDescription,
        problemStatement = problemStatement,
        leaderId = user.id,
        leaderName = user.name,
        leaderCollege = user.college,
        membersRequired = membersRequired,
        openRoles = openRoles,
        requiredSkills = requiredSkills,
        deadline = deadline,
        status = "OPEN",
        isUserMember = true,
        isUserLeader = true
      )
      repository.createTeam(team)
      closeCreateTeam()
    }
  }

  fun submitJoinRequest(teamId: String) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      val request = TeamJoinRequestItem(
        id = "req_${System.currentTimeMillis()}",
        teamId = teamId,
        studentId = user.id,
        studentName = user.name,
        studentHeadline = user.headline,
        studentCollege = user.college,
        studentBranch = user.branch,
        studentYear = user.yearOfStudy,
        studentSkills = user.skills,
        status = "PENDING",
        appliedAt = "Just now"
      )
      repository.submitJoinRequest(request)
      closeJoinRequest()
    }
  }

  fun updateJoinRequestStatus(
    requestId: String,
    status: String,
    teamId: String,
    studentId: String,
    studentName: String,
    studentSkills: List<String>
  ) {
    viewModelScope.launch {
      repository.updateJoinRequestStatus(
        requestId,
        status,
        teamId,
        studentId,
        studentName,
        studentSkills
      )
    }
  }

  fun sendChatMessage(teamId: String, message: String, attachmentName: String? = null) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      repository.sendChatMessage(
        teamId = teamId,
        senderId = user.id,
        senderName = user.name,
        message = message,
        attachmentName = attachmentName
      )
    }
  }

  fun togglePinMessage(messageId: String, isPinned: Boolean) {
    viewModelScope.launch {
      repository.togglePinMessage(messageId, isPinned)
    }
  }

  fun postTeamUpdate(teamId: String, title: String, content: String, category: String) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      val update = TeamUpdateItem(
        id = "up_${System.currentTimeMillis()}",
        teamId = teamId,
        title = title,
        content = content,
        authorName = user.name,
        category = category,
        timestamp = System.currentTimeMillis()
      )
      repository.addTeamUpdate(update)
    }
  }

  fun addAchievement(
    title: String,
    organization: String,
    date: String,
    category: String,
    description: String
  ) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      val achievement = AchievementItem(
        id = "ach_${System.currentTimeMillis()}",
        studentId = user.id,
        title = title,
        organization = organization,
        date = date,
        category = category,
        description = description
      )
      repository.addAchievement(achievement)
      closeAddAchievement()
    }
  }

  fun toggleOpportunityBookmark(id: String, isBookmarked: Boolean) {
    viewModelScope.launch {
      repository.toggleOpportunityBookmark(id, isBookmarked)
    }
  }

  fun markAllNotificationsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsRead()
    }
  }

  fun completeOnboarding(
    name: String,
    email: String,
    phone: String,
    college: String,
    collegeCode: String,
    prn: String,
    branch: String,
    yearOfStudy: String,
    gradYear: String,
    headline: String,
    bio: String,
    skills: List<String>,
    interests: List<String>,
    linkedinUrl: String,
    githubUrl: String,
    instagramUrl: String,
    firstAchievementTitle: String,
    firstAchievementOrg: String,
    firstAchievementCat: String,
    firstAchievementDesc: String
  ) {
    viewModelScope.launch {
      val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "TM" }

      val newStudent = Student(
        id = "student_user",
        name = name,
        email = email,
        phone = phone,
        college = college,
        collegeCode = collegeCode,
        prn = prn,
        branch = branch,
        yearOfStudy = yearOfStudy,
        gradYear = gradYear,
        headline = headline,
        bio = bio,
        avatarInitials = initials,
        skills = skills,
        interests = interests,
        linkedinUrl = linkedinUrl,
        githubUrl = githubUrl,
        instagramUrl = instagramUrl,
        achievementScore = 1500,
        hackathonsCount = if (firstAchievementCat == "Hackathon") 1 else 0,
        competitionsCount = if (firstAchievementCat == "Competition") 1 else 0,
        projectsCount = 1,
        isCurrentUser = true
      )

      repository.insertStudent(newStudent)

      if (firstAchievementTitle.isNotBlank()) {
        val achievement = AchievementItem(
          id = "ach_first_${System.currentTimeMillis()}",
          studentId = newStudent.id,
          title = firstAchievementTitle,
          organization = firstAchievementOrg,
          date = "Recent",
          category = firstAchievementCat.ifBlank { "Project" },
          description = firstAchievementDesc
        )
        repository.addAchievement(achievement)
      }

      _currentDestination.value = AppDestination.MainContent
      _currentTab.value = MainTab.HOME
    }
  }
}
