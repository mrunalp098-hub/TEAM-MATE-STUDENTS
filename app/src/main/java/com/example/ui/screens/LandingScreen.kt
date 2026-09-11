package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun LandingScreen(
  onJoinClick: () -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("landing_screen"),
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState)
        .padding(bottom = 32.dp)
    ) {
      // Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
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
              fontSize = 17.sp
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "TeamMate",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        }

        Button(
          onClick = onJoinClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text("Get Started", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Hero Banner Image
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.teammate_hero),
            contentDescription = "TeamMate student collaboration hero",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, Color(0xCC0F172A))
                )
              )
          )
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = IndigoPrimary
            ) {
              Text(
                text = "STUDENT COLLABORATION ECOSYSTEM",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Learn • Build • Compete • Connect • Grow",
              color = Color.White,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Hero Headline & Subheadline
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Find Your People.\nBuild Your Future.",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            lineHeight = 36.sp
          ),
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "TeamMate helps ambitious students showcase their journey, discover talented peers, form high-performance teams for hackathons, and build lifelong professional connections.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CTAs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = onJoinClick,
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("landing_join_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
          ) {
            Text("Join TeamMate", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          }

          OutlinedButton(
            onClick = onExploreClick,
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("landing_explore_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
              brush = Brush.linearGradient(listOf(IndigoPrimary, PurpleSecondary))
            )
          ) {
            Text("Explore Students", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Stats Section
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          LandingStat(number = "10K+", label = "Students")
          LandingStat(number = "450+", label = "Teams Formed")
          LandingStat(number = "85+", label = "Institutes")
          LandingStat(number = "₹1.2Cr+", label = "Prizes Won")
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Core Pillars
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
      ) {
        Text(
          text = "Everything You Need to Win",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Built specifically for hackathons, engineering summits, and collegiate innovation.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        PillarItem(
          icon = Icons.Default.Badge,
          iconColor = IndigoPrimary,
          title = "Living Student Portfolio",
          description = "Showcase hackathons, certifications, internships, GitHub repositories, and verifiable achievements."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarItem(
          icon = Icons.Default.GroupAdd,
          iconColor = PurpleSecondary,
          title = "Smart Teammate Discovery",
          description = "Find peers matching complementary skill gaps (e.g. AI/ML + Full-Stack + UI/UX Designer)."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarItem(
          icon = Icons.Default.Forum,
          iconColor = CyanAccent,
          title = "Private Team Workspaces",
          description = "Centralized chat, task updates, file exchange, and competition specs without scattered WhatsApp groups."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarItem(
          icon = Icons.Default.EmojiEvents,
          iconColor = AmberCompetition,
          title = "Merit-Based Leaderboards",
          description = "Celebrate learning, consistent participation, project building, and verified podium finishes."
        )
      }
    }
  }
}

@Composable
private fun LandingStat(number: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = number,
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.ExtraBold,
        color = IndigoPrimary
      )
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
  }
}

@Composable
private fun PillarItem(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  description: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
    )
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(iconColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
  }
}
