package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.AchievementCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  student: Student?,
  achievements: List<AchievementItem>,
  projects: List<ProjectItem>,
  isCurrentUser: Boolean = true,
  onBackClick: (() -> Unit)? = null,
  onConnectClick: (() -> Unit)? = null,
  onAddAchievementClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  if (student == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
    return
  }

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Overview", "Journey", "Projects", "Wins (${achievements.size})")

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("profile_screen"),
    topBar = {
      if (onBackClick != null) {
        TopAppBar(
          title = { Text(student.name, fontWeight = FontWeight.Bold) },
          navigationIcon = {
            IconButton(onClick = onBackClick) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        )
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. Cover Banner & Avatar
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
              Brush.horizontalGradient(
                listOf(IndigoPrimary, PurpleSecondary, CyanAccent)
              )
            )
        )
      }

      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
        ) {
          // Negative offset avatar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .offset(y = (-40).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                .background(IndigoPrimary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = student.avatarInitials,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
              )
            }

            if (isCurrentUser) {
              Button(
                onClick = onAddAchievementClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Win", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            } else if (onConnectClick != null) {
              Button(
                onClick = onConnectClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (student.isConnected) EmeraldSuccess else IndigoPrimary
                )
              ) {
                Text(if (student.isConnected) "Connected" else if (student.connectionPending) "Pending" else "Connect")
              }
            }
          }

          // Student Bio & Header Info
          Column(modifier = Modifier.offset(y = (-24).dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = student.name,
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  Icons.Default.Verified,
                  contentDescription = "Verified Student",
                  tint = IndigoPrimary,
                  modifier = Modifier.size(18.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(20.dp),
                color = AmberSubtle
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Stars, contentDescription = null, tint = AmberCompetition, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${student.achievementScore} pts",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFB45309)
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = student.headline,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "${student.college} (${student.collegeCode})",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )

            Text(
              text = "${student.branch} • ${student.yearOfStudy} (Class of ${student.gradYear})",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Social links
            Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (student.githubUrl.isNotBlank()) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GitHub", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }

              if (student.linkedinUrl.isNotBlank()) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LinkedIn", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }
      }

      // Tabs
      item {
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
      }

      // Tab Content
      when (selectedTab) {
        0 -> {
          // Overview
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // Bio
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = student.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Skills
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Technical Skills & Competencies",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    student.skills.forEach { skill ->
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IndigoSubtle
                      ) {
                        Text(
                          text = skill,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                    }
                  }
                }
              }

              // Interests
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Areas of Interest & Hackathon Domains",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    student.interests.forEach { interest ->
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PurpleSubtle
                      ) {
                        Text(
                          text = interest,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = PurpleSecondary,
                            fontWeight = FontWeight.SemiBold
                          )
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        1 -> {
          // Journey / Timeline
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text(
                text = "Chronological Growth Journey",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "A verifiable timeline of competitions, certifications, and projects.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )

              achievements.forEach { ach ->
                AchievementCard(achievement = ach)
              }
            }
          }
        }

        2 -> {
          // Projects
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text(
                text = "Built & Shipped Projects",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              if (projects.isEmpty()) {
                Text(
                  text = "No projects documented yet.",
                  style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              } else {
                projects.forEach { proj ->
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                  ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                      Text(
                        text = proj.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = proj.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Spacer(modifier = Modifier.height(10.dp))
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        proj.technologies.forEach { tech ->
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                          ) {
                            Text(
                              text = tech,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                              style = MaterialTheme.typography.labelSmall
                            )
                          }
                        }
                      }
                      if (proj.githubUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                          text = "Repository: ${proj.githubUrl}",
                          style = MaterialTheme.typography.labelSmall.copy(color = IndigoPrimary)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        3 -> {
          // Wins / Achievements
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Verified Awards & Recognitions",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (isCurrentUser) {
                  TextButton(onClick = onAddAchievementClick) {
                    Text("+ Add")
                  }
                }
              }

              achievements.forEach { ach ->
                AchievementCard(achievement = ach)
              }
            }
          }
        }
      }
    }
  }
}
