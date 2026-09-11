package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamDialog(
  onDismiss: () -> Unit,
  onSubmit: (
    name: String,
    competitionName: String,
    competitionLink: String,
    competitionDescription: String,
    problemStatement: String,
    membersRequired: Int,
    openRoles: List<String>,
    requiredSkills: List<String>,
    deadline: String
  ) -> Unit
) {
  var teamName by remember { mutableStateOf("") }
  var competitionName by remember { mutableStateOf("Smart India Hackathon 2026") }
  var competitionLink by remember { mutableStateOf("https://sih.gov.in") }
  var competitionDescription by remember { mutableStateOf("National innovation challenge by AICTE & Ministry of Education.") }
  var problemStatement by remember { mutableStateOf("") }
  var membersRequired by remember { mutableIntStateOf(4) }
  var rolesInput by remember { mutableStateOf("AI/ML Developer, UI/UX Designer") }
  var skillsInput by remember { mutableStateOf("Python, PyTorch, Figma, Kotlin") }
  var deadline by remember { mutableStateOf("15 Oct 2026") }

  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("create_team_dialog"),
    title = {
      Text(
        text = "Post Team Requirement",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = teamName,
          onValueChange = { teamName = it },
          label = { Text("Team Name") },
          placeholder = { Text("e.g. NeuroVision Labs") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = competitionName,
          onValueChange = { competitionName = it },
          label = { Text("Competition Name") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = competitionLink,
          onValueChange = { competitionLink = it },
          label = { Text("Official Competition Link") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = problemStatement,
          onValueChange = { problemStatement = it },
          label = { Text("Problem Statement") },
          placeholder = { Text("Describe what challenge your team is tackling...") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Members Required:", style = MaterialTheme.typography.bodyMedium)
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { if (membersRequired > 2) membersRequired-- }
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
              text = "$membersRequired",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(
              onClick = { if (membersRequired < 8) membersRequired++ }
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase")
            }
          }
        }

        OutlinedTextField(
          value = rolesInput,
          onValueChange = { rolesInput = it },
          label = { Text("Open Roles (comma separated)") },
          placeholder = { Text("Frontend Dev, Cloud Architect, UI/UX") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = skillsInput,
          onValueChange = { skillsInput = it },
          label = { Text("Required Skills") },
          placeholder = { Text("Kotlin, React, PyTorch, Figma") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = deadline,
          onValueChange = { deadline = it },
          label = { Text("Application Deadline") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (teamName.isNotBlank() && problemStatement.isNotBlank()) {
            val roles = rolesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val skills = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            onSubmit(
              teamName, competitionName, competitionLink, competitionDescription,
              problemStatement, membersRequired, roles, skills, deadline
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
      ) {
        Text("Publish Requirement")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun RequestToJoinDialog(
  team: TeamItem,
  student: Student?,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("request_join_dialog"),
    title = {
      Text(
        text = "Join \"${team.name}\"?",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Your verified student profile summary will be sent to team leader ${team.leaderName}:",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = student?.name ?: "Student",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${student?.college} • ${student?.branch}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = student?.headline ?: "",
              style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Skills: ${student?.skills?.joinToString(", ")}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = IndigoPrimary
              )
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
      ) {
        Text("Submit Application")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun AddAchievementDialog(
  onDismiss: () -> Unit,
  onSubmit: (title: String, organization: String, date: String, category: String, description: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var organization by remember { mutableStateOf("") }
  var date by remember { mutableStateOf("Oct 2026") }
  var category by remember { mutableStateOf("Hackathon") }
  var description by remember { mutableStateOf("") }

  val categories = listOf("Hackathon", "Competition", "Certificate", "Internship", "Project", "Award", "Research")

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("add_achievement_dialog"),
    title = {
      Text(
        text = "Add to Your Journey",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          placeholder = { Text("e.g. 1st Place SIH Hackathon") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = organization,
          onValueChange = { organization = it },
          label = { Text("Organization / Institution") },
          placeholder = { Text("e.g. AICTE, Google, IEEE") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("Date / Period") },
          modifier = Modifier.fillMaxWidth()
        )

        Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(3).forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("What did you build or learn?") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSubmit(title, organization, date, category, description)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
      ) {
        Text("Save Experience")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
  notifications: List<NotificationModel>,
  onDismiss: () -> Unit,
  onMarkAllRead: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("notifications_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Notifications",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        TextButton(onClick = onMarkAllRead) {
          Text("Mark all as read")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications) { notif ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(
                    when (notif.category) {
                      "Teams" -> IndigoSubtle
                      "Networking" -> PurpleSubtle
                      "Achievement" -> AmberSubtle
                      else -> CyanSubtle
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (notif.category) {
                    "Teams" -> Icons.Default.Group
                    "Networking" -> Icons.Default.PersonAdd
                    "Achievement" -> Icons.Default.EmojiEvents
                    else -> Icons.Default.Notifications
                  },
                  contentDescription = null,
                  tint = when (notif.category) {
                    "Teams" -> IndigoPrimary
                    "Networking" -> PurpleSecondary
                    "Achievement" -> AmberCompetition
                    else -> CyanAccent
                  },
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = notif.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = notif.timeAgo,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = notif.message,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
  students: List<Student>,
  teams: List<TeamItem>,
  opportunities: List<OpportunityItem>,
  onDismiss: () -> Unit,
  onStudentSelect: (Student) -> Unit,
  onTeamSelect: (TeamItem) -> Unit
) {
  var query by remember { mutableStateOf("") }

  val matchingStudents = remember(query, students) {
    if (query.isBlank()) emptyList()
    else students.filter {
      it.name.contains(query, ignoreCase = true) ||
        it.skills.any { s -> s.contains(query, ignoreCase = true) } ||
        it.college.contains(query, ignoreCase = true)
    }
  }

  val matchingTeams = remember(query, teams) {
    if (query.isBlank()) emptyList()
    else teams.filter {
      it.name.contains(query, ignoreCase = true) ||
        it.competitionName.contains(query, ignoreCase = true) ||
        it.problemStatement.contains(query, ignoreCase = true)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("global_search_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Text(
        text = "Search TeamMate",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text("Find students, skills, teams, hackathons...") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (matchingStudents.isNotEmpty()) {
          item {
            Text(
              text = "STUDENTS (${matchingStudents.size})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary)
            )
          }
          items(matchingStudents) { student ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onStudentSelect(student)
                  onDismiss()
                },
              shape = RoundedCornerShape(10.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(IndigoPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Text(student.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text("${student.college} • ${student.headline}", fontSize = 11.sp, maxLines = 1)
                }
              }
            }
          }
        }

        if (matchingTeams.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "TEAMS (${matchingTeams.size})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PurpleSecondary)
            )
          }
          items(matchingTeams) { team ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onTeamSelect(team)
                  onDismiss()
                },
              shape = RoundedCornerShape(10.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(team.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${team.competitionName} • ${team.problemStatement}", fontSize = 11.sp, maxLines = 1)
              }
            }
          }
        }

        if (query.isNotBlank() && matchingStudents.isEmpty() && matchingTeams.isEmpty()) {
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("No results for \"$query\"", style = MaterialTheme.typography.bodyMedium)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
