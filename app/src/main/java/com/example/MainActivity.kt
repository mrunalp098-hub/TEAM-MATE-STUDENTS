package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Student
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.TeamMateTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TeamMateTheme {
        TeamMateApp()
      }
    }
  }
}

@Composable
fun TeamMateApp(
  viewModel: MainViewModel = viewModel()
) {
  val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
  val allTeams by viewModel.allTeams.collectAsStateWithLifecycle()
  val allOpportunities by viewModel.allOpportunities.collectAsStateWithLifecycle()
  val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
  val allTeamRequests by viewModel.allTeamRequests.collectAsStateWithLifecycle()

  val studentSearchQuery by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
  val selectedSkillFilter by viewModel.selectedSkillFilter.collectAsStateWithLifecycle()
  val selectedCollegeFilter by viewModel.selectedCollegeFilter.collectAsStateWithLifecycle()
  val selectedLeaderboardCategory by viewModel.selectedLeaderboardCategory.collectAsStateWithLifecycle()

  val isNotificationSheetOpen by viewModel.isNotificationSheetOpen.collectAsStateWithLifecycle()
  val isGlobalSearchOpen by viewModel.isGlobalSearchOpen.collectAsStateWithLifecycle()
  val isCreateTeamOpen by viewModel.isCreateTeamOpen.collectAsStateWithLifecycle()
  val isAddAchievementOpen by viewModel.isAddAchievementOpen.collectAsStateWithLifecycle()
  val teamToJoin by viewModel.teamToJoin.collectAsStateWithLifecycle()

  val unreadNotifications = remember(allNotifications) {
    allNotifications.count { !it.isRead }
  }

  // Handle high-level destinations
  when (val dest = currentDestination) {
    is AppDestination.Landing -> {
      LandingScreen(
        onJoinClick = { viewModel.navigateTo(AppDestination.Onboarding) },
        onExploreClick = {
          viewModel.navigateTo(AppDestination.MainContent)
          viewModel.setTab(MainTab.DISCOVER)
        }
      )
    }

    is AppDestination.Onboarding -> {
      OnboardingScreen(
        onComplete = { name, email, phone, college, collegeCode, prn, branch,
          year, gradYear, headline, bio, skills, interests, linkedin, github, instagram,
          achTitle, achOrg, achCat, achDesc ->
          viewModel.completeOnboarding(
            name, email, phone, college, collegeCode, prn, branch,
            year, gradYear, headline, bio, skills, interests, linkedin, github, instagram,
            achTitle, achOrg, achCat, achDesc
          )
        },
        onBackToLanding = { viewModel.navigateTo(AppDestination.Landing) }
      )
    }

    is AppDestination.StudentProfileView -> {
      val studentFlow = remember(dest.studentId) { viewModel.getStudentById(dest.studentId) }
      val student by studentFlow.collectAsStateWithLifecycle(initialValue = null)

      val achievementsFlow = remember(dest.studentId) { viewModel.getAchievementsForStudent(dest.studentId) }
      val achievements by achievementsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

      val projectsFlow = remember(dest.studentId) { viewModel.getProjectsForStudent(dest.studentId) }
      val projects by projectsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

      ProfileScreen(
        student = student,
        achievements = achievements,
        projects = projects,
        isCurrentUser = student?.isCurrentUser == true,
        onBackClick = { viewModel.navigateTo(AppDestination.MainContent) },
        onConnectClick = { student?.let { viewModel.toggleConnection(it) } },
        onAddAchievementClick = { viewModel.openAddAchievement() }
      )
    }

    is AppDestination.TeamWorkspaceView -> {
      val teamFlow = remember(dest.teamId) { viewModel.getTeamWithMembers(dest.teamId) }
      val team by teamFlow.collectAsStateWithLifecycle(initialValue = null)

      val messagesFlow = remember(dest.teamId) { viewModel.getTeamChatMessages(dest.teamId) }
      val messages by messagesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

      val updatesFlow = remember(dest.teamId) { viewModel.getTeamUpdates(dest.teamId) }
      val updates by updatesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

      TeamWorkspaceScreen(
        team = team,
        messages = messages,
        updates = updates,
        currentUserId = currentUser?.id ?: "student_mrunal",
        onBackClick = { viewModel.navigateTo(AppDestination.MainContent) },
        onSendMessage = { text, attachment ->
          viewModel.sendChatMessage(dest.teamId, text, attachment)
        },
        onTogglePinMessage = { msgId, isPin ->
          viewModel.togglePinMessage(msgId, isPin)
        },
        onPostUpdate = { title, content, cat ->
          viewModel.postTeamUpdate(dest.teamId, title, content, cat)
        }
      )
    }

    is AppDestination.MainContent, AppDestination.OpportunitiesView -> {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          TeamMateTopAppBar(
            title = when (currentTab) {
              MainTab.HOME -> "TeamMate"
              MainTab.DISCOVER -> "Discover Talent"
              MainTab.TEAMS -> "Hackathon Teams"
              MainTab.LEADERBOARD -> "Leaderboard"
              MainTab.PROFILE -> "Student Journey"
            },
            unreadCount = unreadNotifications,
            userInitials = currentUser?.avatarInitials ?: "MP",
            onSearchClick = { viewModel.openGlobalSearch() },
            onNotificationsClick = { viewModel.openNotifications() },
            onProfileClick = { viewModel.setTab(MainTab.PROFILE) }
          )
        },
        bottomBar = {
          TeamMateBottomNavigation(
            currentTab = currentTab,
            onTabSelected = { viewModel.setTab(it) }
          )
        }
      ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          when (currentTab) {
            MainTab.HOME -> {
              val recentAchievementsFlow = remember(currentUser?.id) {
                viewModel.getAchievementsForStudent(currentUser?.id ?: "student_mrunal")
              }
              val recentAchievements by recentAchievementsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

              HomeScreen(
                currentUser = currentUser,
                recommendedStudents = allStudents,
                activeTeams = allTeams,
                upcomingOpportunities = allOpportunities,
                recentAchievements = recentAchievements,
                onConnectClick = { viewModel.toggleConnection(it) },
                onStudentClick = { viewModel.navigateTo(AppDestination.StudentProfileView(it.id)) },
                onTeamClick = { viewModel.navigateTo(AppDestination.TeamWorkspaceView(it.id)) },
                onRequestJoinClick = { viewModel.openJoinRequest(it) },
                onOpportunityClick = { viewModel.toggleOpportunityBookmark(it.id, !it.isBookmarked) },
                onFormTeamForOpportunity = { opp ->
                  viewModel.openCreateTeam()
                },
                onCreateTeamClick = { viewModel.openCreateTeam() },
                onAddAchievementClick = { viewModel.openAddAchievement() },
                onFindTeammatesClick = { viewModel.setTab(MainTab.DISCOVER) },
                onExploreCompetitionsClick = { viewModel.setTab(MainTab.TEAMS) },
                onLeaderboardClick = { viewModel.setTab(MainTab.LEADERBOARD) }
              )
            }

            MainTab.DISCOVER -> {
              DiscoverScreen(
                students = allStudents,
                searchQuery = studentSearchQuery,
                selectedSkillFilter = selectedSkillFilter,
                selectedCollegeFilter = selectedCollegeFilter,
                onSearchChange = { viewModel.setStudentSearchQuery(it) },
                onSkillFilterSelect = { viewModel.setSkillFilter(it) },
                onCollegeFilterSelect = { viewModel.setCollegeFilter(it) },
                onConnectClick = { viewModel.toggleConnection(it) },
                onStudentClick = { viewModel.navigateTo(AppDestination.StudentProfileView(it.id)) }
              )
            }

            MainTab.TEAMS -> {
              TeamsScreen(
                teams = allTeams,
                requests = allTeamRequests,
                onTeamClick = { viewModel.navigateTo(AppDestination.TeamWorkspaceView(it.id)) },
                onRequestJoinClick = { viewModel.openJoinRequest(it) },
                onCreateTeamClick = { viewModel.openCreateTeam() },
                onUpdateRequestStatus = { reqId, status, teamId, studentId, studentName, skills ->
                  viewModel.updateJoinRequestStatus(reqId, status, teamId, studentId, studentName, skills)
                }
              )
            }

            MainTab.LEADERBOARD -> {
              LeaderboardScreen(
                students = allStudents,
                selectedCategory = selectedLeaderboardCategory,
                onCategorySelect = { viewModel.setLeaderboardCategory(it) },
                onStudentClick = { viewModel.navigateTo(AppDestination.StudentProfileView(it.id)) }
              )
            }

            MainTab.PROFILE -> {
              val currentStudentId = currentUser?.id ?: "student_mrunal"
              val achievementsFlow = remember(currentStudentId) { viewModel.getAchievementsForStudent(currentStudentId) }
              val achievements by achievementsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

              val projectsFlow = remember(currentStudentId) { viewModel.getProjectsForStudent(currentStudentId) }
              val projects by projectsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

              ProfileScreen(
                student = currentUser,
                achievements = achievements,
                projects = projects,
                isCurrentUser = true,
                onAddAchievementClick = { viewModel.openAddAchievement() }
              )
            }
          }
        }
      }
    }
  }

  // Modals & BottomSheets
  if (isNotificationSheetOpen) {
    NotificationsSheet(
      notifications = allNotifications,
      onDismiss = { viewModel.closeNotifications() },
      onMarkAllRead = { viewModel.markAllNotificationsRead() }
    )
  }

  if (isGlobalSearchOpen) {
    GlobalSearchDialog(
      students = allStudents,
      teams = allTeams,
      opportunities = allOpportunities,
      onDismiss = { viewModel.closeGlobalSearch() },
      onStudentSelect = { viewModel.navigateTo(AppDestination.StudentProfileView(it.id)) },
      onTeamSelect = { viewModel.navigateTo(AppDestination.TeamWorkspaceView(it.id)) }
    )
  }

  if (isCreateTeamOpen) {
    CreateTeamDialog(
      onDismiss = { viewModel.closeCreateTeam() },
      onSubmit = { name, compName, compLink, compDesc, problemStatement, membersRequired, roles, skills, deadline ->
        viewModel.createTeam(
          name, compName, compLink, compDesc, problemStatement,
          membersRequired, roles, skills, deadline
        )
      }
    )
  }

  if (isAddAchievementOpen) {
    AddAchievementDialog(
      onDismiss = { viewModel.closeAddAchievement() },
      onSubmit = { title, org, date, cat, desc ->
        viewModel.addAchievement(title, org, date, cat, desc)
      }
    )
  }

  teamToJoin?.let { team ->
    RequestToJoinDialog(
      team = team,
      student = currentUser,
      onDismiss = { viewModel.closeJoinRequest() },
      onConfirm = { viewModel.submitJoinRequest(team.id) }
    )
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

