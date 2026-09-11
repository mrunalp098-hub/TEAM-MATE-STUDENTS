package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  currentUser: Student?,
  recommendedStudents: List<Student>,
  activeTeams: List<TeamItem>,
  upcomingOpportunities: List<OpportunityItem>,
  recentAchievements: List<AchievementItem>,
  onConnectClick: (Student) -> Unit,
  onStudentClick: (Student) -> Unit,
  onTeamClick: (TeamItem) -> Unit,
  onRequestJoinClick: (TeamItem) -> Unit,
  onOpportunityClick: (OpportunityItem) -> Unit,
  onFormTeamForOpportunity: (OpportunityItem) -> Unit,
  onCreateTeamClick: () -> Unit,
  onAddAchievementClick: () -> Unit,
  onFindTeammatesClick: () -> Unit,
  onExploreCompetitionsClick: () -> Unit,
  onLeaderboardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val studentName = currentUser?.name ?: "Mrunal"
  val firstName = studentName.split(" ").firstOrNull() ?: "Mrunal"

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // 1. Personalized Header Greeting
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Good morning, $firstName 👋",
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Your next great team might be one connection away.",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }
    }

    // 2. Quick Actions Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionButton(
          icon = Icons.Default.GroupAdd,
          label = "Find Teammates",
          color = IndigoPrimary,
          onClick = onFindTeammatesClick,
          modifier = Modifier.weight(1f)
        )
        QuickActionButton(
          icon = Icons.Default.AddCircleOutline,
          label = "Post Team Req",
          color = PurpleSecondary,
          onClick = onCreateTeamClick,
          modifier = Modifier.weight(1f)
        )
        QuickActionButton(
          icon = Icons.Default.EmojiEvents,
          label = "Competitions",
          color = AmberCompetition,
          onClick = onExploreCompetitionsClick,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Profile Strength Widget
    item {
      Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        ProfileStrengthCard(
          percentage = 85,
          onAddClick = onAddAchievementClick
        )
      }
    }

    // 4. Recommended Teammates Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Recommended Teammates",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Matched by complementary skills & hackathons",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          TextButton(onClick = onFindTeammatesClick) {
            Text(
              text = "See All",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(recommendedStudents.filter { !it.isCurrentUser }) { student ->
            Box(modifier = Modifier.width(290.dp)) {
              StudentCard(
                student = student,
                onConnectClick = { onConnectClick(student) },
                onCardClick = { onStudentClick(student) }
              )
            }
          }
        }
      }
    }

    // 5. Teams Looking for Members
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Teams Looking for Members",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Active requirements for upcoming competitions",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          IconButton(onClick = onCreateTeamClick) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Create Team",
              tint = IndigoPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
          modifier = Modifier.padding(horizontal = 20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          activeTeams.take(3).forEach { team ->
            TeamCard(
              team = team,
              onViewClick = { onTeamClick(team) },
              onRequestJoinClick = { onRequestJoinClick(team) }
            )
          }
        }
      }
    }

    // 6. Upcoming Competitions Highlights
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Upcoming Competitions",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Official deadlines, hackathons and grants",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          TextButton(onClick = onExploreCompetitionsClick) {
            Text(
              text = "View All",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          upcomingOpportunities.take(2).forEach { opp ->
            OpportunityCard(
              opportunity = opp,
              onBookmarkClick = { onOpportunityClick(opp) },
              onFormTeamClick = { onFormTeamForOpportunity(opp) }
            )
          }
        }
      }
    }

    // 7. Recent Achievements Feed
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
      ) {
        Text(
          text = "Peer Achievements",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Recent milestones celebrated in student community",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          recentAchievements.take(3).forEach { ach ->
            AchievementCard(achievement = ach)
          }
        }
      }
    }
  }
}

@Composable
private fun QuickActionButton(
  icon: ImageVector,
  label: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .height(72.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f)))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
      )
    }
  }
}
