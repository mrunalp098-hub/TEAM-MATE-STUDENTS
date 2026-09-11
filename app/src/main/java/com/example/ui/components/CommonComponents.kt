package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementItem
import com.example.data.model.OpportunityItem
import com.example.data.model.Student
import com.example.data.model.TeamItem
import com.example.ui.MainTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamMateTopAppBar(
  title: String,
  unreadCount: Int,
  userInitials: String = "MP",
  onSearchClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier.testTag("teammate_top_bar"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(listOf(IndigoPrimary, PurpleSecondary))
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "TM",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.2.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Find Your People. Build Your Future.",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            fontSize = 11.sp
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = onSearchClick,
        modifier = Modifier.testTag("global_search_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = "Search students, teams, and competitions"
        )
      }

      IconButton(
        onClick = onNotificationsClick,
        modifier = Modifier.testTag("notifications_button")
      ) {
        BadgedBox(
          badge = {
            if (unreadCount > 0) {
              Badge(
                containerColor = AmberCompetition,
                contentColor = Color.White
              ) {
                Text(unreadCount.toString())
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifications"
          )
        }
      }

      Box(
        modifier = Modifier
          .padding(end = 8.dp)
          .size(36.dp)
          .clip(CircleShape)
          .background(IndigoPrimary)
          .clickable { onProfileClick() }
          .testTag("top_bar_profile_avatar"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = userInitials,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface
    )
  )
}

@Composable
fun TeamMateBottomNavigation(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier
      .windowInsetsPadding(WindowInsets.navigationBars)
      .testTag("bottom_nav_bar"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    val items = listOf(
      Triple(MainTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
      Triple(MainTab.DISCOVER, Icons.Filled.People, Icons.Outlined.People),
      Triple(MainTab.TEAMS, Icons.Filled.GroupWork, Icons.Outlined.GroupWork),
      Triple(MainTab.LEADERBOARD, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
      Triple(MainTab.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
    )

    items.forEach { (tab, selectedIcon, unselectedIcon) ->
      val isSelected = currentTab == tab
      NavigationBarItem(
        icon = {
          Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = tab.label
          )
        },
        label = {
          Text(
            text = tab.label,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp
          )
        },
        selected = isSelected,
        onClick = { onTabSelected(tab) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Color.White,
          selectedTextColor = IndigoPrimary,
          indicatorColor = IndigoPrimary,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}

@Composable
fun StudentCard(
  student: Student,
  onConnectClick: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("student_card_${student.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Avatar with gradient halo
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(IndigoPrimary, PurpleSecondary)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = student.avatarInitials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = student.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Achievement score badge
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = AmberSubtle,
              border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AmberCompetition, AmberCompetition)))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Stars,
                  contentDescription = null,
                  tint = AmberCompetition,
                  modifier = Modifier.size(14.dp)
                )
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

          Text(
            text = "${student.college} • ${student.branch}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Text(
            text = student.yearOfStudy,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = student.headline,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Skills chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        student.skills.take(3).forEach { skill ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
          ) {
            Text(
              text = skill,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
        if (student.skills.size > 3) {
          Text(
            text = "+${student.skills.size - 3}",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          MetricItem(icon = Icons.Default.EmojiEvents, value = "${student.hackathonsCount} Hacks")
          MetricItem(icon = Icons.Default.Folder, value = "${student.projectsCount} Projs")
        }

        if (!student.isCurrentUser) {
          when {
            student.isConnected -> {
              OutlinedButton(
                onClick = onConnectClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = EmeraldSuccess
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                  brush = Brush.linearGradient(listOf(EmeraldSuccess, EmeraldSuccess))
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("connected_button_${student.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Connected", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
            student.connectionPending -> {
              OutlinedButton(
                onClick = onConnectClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = CyanAccent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                  brush = Brush.linearGradient(listOf(CyanAccent, CyanAccent))
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("pending_button_${student.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.HourglassEmpty,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pending", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
            else -> {
              Button(
                onClick = onConnectClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = IndigoPrimary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("connect_button_${student.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.PersonAdd,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Connect", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun TeamCard(
  team: TeamItem,
  onViewClick: () -> Unit,
  onRequestJoinClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onViewClick() }
      .testTag("team_card_${team.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PurpleSubtle
        ) {
          Text(
            text = team.competitionName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PurpleSecondary
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = AmberCompetition,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = team.deadline,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = team.name,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = team.problemStatement,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Open Roles
      Text(
        text = "Open Roles:",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        team.openRoles.forEach { role ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyanSubtle
          ) {
            Text(
              text = role,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF0E7490),
                fontWeight = FontWeight.SemiBold
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Members progress
      val currentCount = team.members.size.coerceAtLeast(1)
      val totalRequired = team.membersRequired
      val progress = (currentCount.toFloat() / totalRequired.toFloat()).coerceIn(0f, 1f)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (progress >= 1f) "Team Complete 🎉" else "Team Capacity",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (progress >= 1f) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
        Text(
          text = "$currentCount / $totalRequired Members",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(CircleShape),
        color = if (progress >= 1f) EmeraldSuccess else IndigoPrimary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(IndigoPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = team.leaderName.take(2).uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Led by ${team.leaderName}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = team.leaderCollege,
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            )
          }
        }

        if (team.isUserMember) {
          Button(
            onClick = onViewClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("workspace_button_${team.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Chat,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Workspace", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        } else {
          OutlinedButton(
            onClick = onRequestJoinClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
              brush = Brush.linearGradient(listOf(IndigoPrimary, PurpleSecondary))
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("request_join_button_${team.id}")
          ) {
            Text("Request to Join", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun OpportunityCard(
  opportunity: OpportunityItem,
  onBookmarkClick: () -> Unit,
  onFormTeamClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("opportunity_card_${opportunity.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when (opportunity.category) {
            "Hackathon" -> IndigoSubtle
            "Competition" -> PurpleSubtle
            "Internship" -> EmeraldSubtle
            else -> AmberSubtle
          }
        ) {
          Text(
            text = opportunity.category,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = when (opportunity.category) {
                "Hackathon" -> IndigoPrimary
                "Competition" -> PurpleSecondary
                "Internship" -> EmeraldSuccess
                else -> AmberCompetition
              }
            )
          )
        }

        IconButton(
          onClick = onBookmarkClick,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (opportunity.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (opportunity.isBookmarked) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = opportunity.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = opportunity.organization,
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        OpportunityMeta(icon = Icons.Outlined.Event, text = "Deadline: ${opportunity.deadline}")
        OpportunityMeta(icon = Icons.Outlined.Place, text = opportunity.location)
      }

      Spacer(modifier = Modifier.height(6.dp))
      OpportunityMeta(icon = Icons.Outlined.Group, text = "Team: ${opportunity.teamRequirement}")

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onFormTeamClick,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("form_team_button_${opportunity.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = IndigoPrimary
        )
      ) {
        Icon(
          imageVector = Icons.Default.GroupAdd,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Find Teammates / Create Team", fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
fun AchievementCard(
  achievement: AchievementItem,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
    )
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(
            when (achievement.category) {
              "Hackathon" -> IndigoSubtle
              "Competition" -> PurpleSubtle
              "Certificate" -> CyanSubtle
              "Award" -> AmberSubtle
              else -> EmeraldSubtle
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (achievement.category) {
            "Hackathon" -> Icons.Default.Code
            "Competition" -> Icons.Default.EmojiEvents
            "Certificate" -> Icons.Default.Verified
            "Award" -> Icons.Default.Stars
            else -> Icons.Default.WorkOutline
          },
          contentDescription = null,
          tint = when (achievement.category) {
            "Hackathon" -> IndigoPrimary
            "Competition" -> PurpleSecondary
            "Certificate" -> CyanAccent
            "Award" -> AmberCompetition
            else -> EmeraldSuccess
          },
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = achievement.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = achievement.date,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        Text(
          text = "${achievement.organization} • ${achievement.category}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = achievement.description,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
  }
}

@Composable
fun ProfileStrengthCard(
  percentage: Int = 85,
  onAddClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(IndigoPrimary.copy(alpha = 0.4f), PurpleSecondary.copy(alpha = 0.4f)))
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Profile Strength",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "A complete profile gets 4x more team invites",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        Text(
          text = "$percentage%",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = IndigoPrimary
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { percentage / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape),
        color = IndigoPrimary,
        trackColor = IndigoSubtle
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "• Add an experience or certificate",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        TextButton(
          onClick = onAddClick,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "+ Add Experience",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = IndigoPrimary
            )
          )
        }
      }
    }
  }
}

@Composable
private fun MetricItem(icon: ImageVector, value: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(15.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
private fun OpportunityMeta(icon: ImageVector, text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
