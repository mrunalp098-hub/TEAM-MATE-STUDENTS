package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
  students: List<Student>,
  selectedCategory: String,
  onCategorySelect: (String) -> Unit,
  onStudentClick: (Student) -> Unit,
  modifier: Modifier = Modifier
) {
  val categories = listOf("Overall", "Hackathons", "Projects", "College")

  val sortedStudents = when (selectedCategory) {
    "Hackathons" -> students.sortedByDescending { it.hackathonsCount * 300 + it.achievementScore }
    "Projects" -> students.sortedByDescending { it.projectsCount * 250 + it.achievementScore }
    "College" -> students.sortedByDescending { it.achievementScore }
    else -> students.sortedByDescending { it.achievementScore }
  }

  val top3 = sortedStudents.take(3)
  val rest = sortedStudents.drop(3)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("leaderboard_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Merit Leaderboard",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Celebrating learning, active builders, and verified competition finishes.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { onCategorySelect(cat) },
              label = {
                Text(
                  text = cat,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IndigoPrimary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    // Top 3 Podium
    if (top3.size >= 3) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
              listOf(AmberCompetition.copy(alpha = 0.5f), PurpleSecondary.copy(alpha = 0.5f))
            )
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "TOP BUILDERS OF THE SEASON",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = AmberCompetition,
                letterSpacing = 1.sp
              )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Podium arrangement: Rank 2 (Silver, left), Rank 1 (Gold, center & taller), Rank 3 (Bronze, right)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.Bottom
            ) {
              // 2nd Place
              PodiumColumn(
                student = top3[1],
                rank = 2,
                podiumHeight = 90.dp,
                medalColor = Color(0xFF94A3B8),
                badgeText = "🥈 Silver",
                onStudentClick = { onStudentClick(top3[1]) }
              )

              // 1st Place (Gold)
              PodiumColumn(
                student = top3[0],
                rank = 1,
                podiumHeight = 125.dp,
                medalColor = AmberCompetition,
                badgeText = "🥇 Gold",
                onStudentClick = { onStudentClick(top3[0]) }
              )

              // 3rd Place
              PodiumColumn(
                student = top3[2],
                rank = 3,
                podiumHeight = 70.dp,
                medalColor = Color(0xFFB45309),
                badgeText = "🥉 Bronze",
                onStudentClick = { onStudentClick(top3[2]) }
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Overall Rankings",
        modifier = Modifier.padding(horizontal = 20.dp),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    // Leaderboard list
    itemsIndexed(sortedStudents) { index, student ->
      val rank = index + 1
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 5.dp)
          .clickable { onStudentClick(student) }
          .testTag("leaderboard_item_$rank"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (student.isCurrentUser) IndigoSubtle.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Rank Pill
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(
                when (rank) {
                  1 -> AmberCompetition
                  2 -> Color(0xFF94A3B8)
                  3 -> Color(0xFFB45309)
                  else -> MaterialTheme.colorScheme.surfaceVariant
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$rank",
              color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          // Avatar
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(if (student.isCurrentUser) IndigoPrimary else PurpleSecondary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = student.avatarInitials,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = student.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              if (student.isCurrentUser) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = IndigoPrimary
                ) {
                  Text(
                    text = "YOU",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    )
                  )
                }
              }
            }

            Text(
              text = "${student.college} • ${student.branch}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${student.achievementScore}",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = IndigoPrimary
              )
            )
            Text(
              text = "pts",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PodiumColumn(
  student: Student,
  rank: Int,
  podiumHeight: androidx.compose.ui.unit.Dp,
  medalColor: Color,
  badgeText: String,
  onStudentClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(96.dp)
      .clickable { onStudentClick() }
  ) {
    // Avatar
    Box(
      modifier = Modifier
        .size(if (rank == 1) 56.dp else 46.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(listOf(medalColor, medalColor.copy(alpha = 0.6f)))
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = student.avatarInitials,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = if (rank == 1) 18.sp else 14.sp
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = student.name.split(" ").firstOrNull() ?: student.name,
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      textAlign = TextAlign.Center,
      maxLines = 1
    )

    Text(
      text = "${student.achievementScore} pts",
      style = MaterialTheme.typography.labelSmall.copy(
        color = medalColor,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp
      )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Podium Block
    Box(
      modifier = Modifier
        .width(86.dp)
        .height(podiumHeight)
        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .background(medalColor.copy(alpha = 0.18f)),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "#$rank",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            color = medalColor
          )
        )
        Text(
          text = badgeText,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        )
      }
    }
  }
}
