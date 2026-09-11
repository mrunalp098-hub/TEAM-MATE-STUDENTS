package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamItem
import com.example.data.model.TeamJoinRequestItem
import com.example.ui.components.TeamCard
import com.example.ui.theme.*

@Composable
fun TeamsScreen(
  teams: List<TeamItem>,
  requests: List<TeamJoinRequestItem>,
  onTeamClick: (TeamItem) -> Unit,
  onRequestJoinClick: (TeamItem) -> Unit,
  onCreateTeamClick: () -> Unit,
  onUpdateRequestStatus: (requestId: String, status: String, teamId: String, studentId: String, studentName: String, skills: List<String>) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Explore Teams", "My Teams", "Applications (${requests.size})")

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("teams_screen"),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onCreateTeamClick,
        containerColor = IndigoPrimary,
        contentColor = Color.White,
        icon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
        text = { Text("Create Team Req", fontWeight = FontWeight.Bold) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("fab_create_team")
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Hackathon & Competition Teams",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Collaborate with talented peers for national & international competitions.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = IndigoPrimary
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              )
            }
          )
        }
      }

      when (selectedTab) {
        0 -> {
          // Explore Teams
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = PurpleSubtle,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PurpleSecondary
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Teams with verified problem statements and clear role openings.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = PurpleSecondary,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }
            }

            items(teams) { team ->
              TeamCard(
                team = team,
                onViewClick = { onTeamClick(team) },
                onRequestJoinClick = { onRequestJoinClick(team) }
              )
            }

            item {
              Spacer(modifier = Modifier.height(72.dp))
            }
          }
        }

        1 -> {
          // My Teams (Where user is leader or member)
          val myTeams = teams.filter { it.isUserMember || it.isUserLeader }
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            if (myTeams.isEmpty()) {
              item {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    imageVector = Icons.Outlined.GroupWork,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "No Teams Joined Yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Create a team or join an open requirement to get your private workspace.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Button(
                    onClick = onCreateTeamClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                  ) {
                    Text("Create Your First Team")
                  }
                }
              }
            } else {
              items(myTeams) { team ->
                TeamCard(
                  team = team,
                  onViewClick = { onTeamClick(team) },
                  onRequestJoinClick = {}
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(72.dp))
            }
          }
        }

        2 -> {
          // Applications / Candidate Reviews
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            if (requests.isEmpty()) {
              item {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    imageVector = Icons.Outlined.AssignmentTurnedIn,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "No Pending Applications",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "When students apply to your teams, their profiles will appear here.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
            } else {
              items(requests) { req ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = CardDefaults.outlinedCardBorder()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = req.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                      )
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (req.status) {
                          "PENDING" -> AmberSubtle
                          "SHORTLISTED" -> CyanSubtle
                          "ACCEPTED" -> EmeraldSubtle
                          else -> Color(0xFFF1F5F9)
                        }
                      ) {
                        Text(
                          text = req.status,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (req.status) {
                              "PENDING" -> AmberCompetition
                              "SHORTLISTED" -> CyanAccent
                              "ACCEPTED" -> EmeraldSuccess
                              else -> Color.Gray
                            }
                          )
                        )
                      }
                    }

                    Text(
                      text = "${req.studentCollege} • ${req.studentBranch} (${req.studentYear})",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = req.studentHeadline,
                      style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Skills
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      req.studentSkills.take(4).forEach { skill ->
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                          Text(
                            text = skill,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (req.status == "PENDING" || req.status == "SHORTLISTED") {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        OutlinedButton(
                          onClick = {
                            onUpdateRequestStatus(req.id, "SHORTLISTED", req.teamId, req.studentId, req.studentName, req.studentSkills)
                          },
                          modifier = Modifier.weight(1f),
                          shape = RoundedCornerShape(8.dp)
                        ) {
                          Text("Shortlist")
                        }

                        Button(
                          onClick = {
                            onUpdateRequestStatus(req.id, "ACCEPTED", req.teamId, req.studentId, req.studentName, req.studentSkills)
                          },
                          modifier = Modifier.weight(1f),
                          shape = RoundedCornerShape(8.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Accept")
                        }
                      }
                    }
                  }
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(72.dp))
            }
          }
        }
      }
    }
  }
}
