package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
  onComplete: (
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
  ) -> Unit,
  onBackToLanding: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(1) }

  // Step 1 - Basic Info
  var fullName by remember { mutableStateOf("Mrunal Pawar") }
  var email by remember { mutableStateOf("mrunalpawar073@gmail.com") }
  var phone by remember { mutableStateOf("+91 98765 43210") }
  var password by remember { mutableStateOf("••••••••••••") }

  // Step 2 - Academic Info
  var collegeName by remember { mutableStateOf("COEP Technological University") }
  var collegeCode by remember { mutableStateOf("COEP-1002") }
  var prn by remember { mutableStateOf("112303045") }
  var branch by remember { mutableStateOf("Computer Engineering") }
  var yearOfStudy by remember { mutableStateOf("3rd Year") }
  var gradYear by remember { mutableStateOf("2027") }

  // Step 3 - Professional Links
  var linkedinUrl by remember { mutableStateOf("https://linkedin.com/in/mrunal-pawar") }
  var githubUrl by remember { mutableStateOf("https://github.com/mrunalpawar") }
  var instagramUrl by remember { mutableStateOf("https://instagram.com/mrunal.builds") }

  // Step 4 - Profile Setup
  var headline by remember { mutableStateOf("Full-Stack & Android Builder | AI Systems Enthusiast") }
  var bio by remember { mutableStateOf("Passionate about building resilient mobile architectures and AI agents. SIH hackathon finalist.") }
  var skillsInput by remember { mutableStateOf("Kotlin, Compose, Python, Machine Learning, UI/UX, Room DB") }
  var interestsInput by remember { mutableStateOf("Smart India Hackathon, AI/ML, Mobile Dev, Web3") }

  // Step 5 - First Achievement
  var achievementTitle by remember { mutableStateOf("SIH Zonal Hackathon Winner") }
  var achievementOrg by remember { mutableStateOf("Ministry of Education") }
  var achievementCat by remember { mutableStateOf("Hackathon") }
  var achievementDesc by remember { mutableStateOf("Built an offline-first disaster response emergency coordination app.") }

  val scrollState = rememberScrollState()

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("onboarding_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Create Student Profile",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(
            onClick = {
              if (currentStep > 1) currentStep-- else onBackToLanding()
            }
          ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = IndigoSubtle,
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Text(
              text = "Step $currentStep of 5",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
              )
            )
          }
        }
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (currentStep > 1) {
            OutlinedButton(
              onClick = { currentStep-- },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Previous")
            }
          } else {
            Spacer(modifier = Modifier.width(8.dp))
          }

          Button(
            onClick = {
              if (currentStep < 5) {
                currentStep++
              } else {
                val skills = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val interests = interestsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                onComplete(
                  fullName, email, phone, collegeName, collegeCode, prn, branch,
                  yearOfStudy, gradYear, headline, bio, skills, interests,
                  linkedinUrl, githubUrl, instagramUrl,
                  achievementTitle, achievementOrg, achievementCat, achievementDesc
                )
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            modifier = Modifier.testTag("onboarding_next_button")
          ) {
            Text(
              text = if (currentStep == 5) "Launch Profile 🚀" else "Continue",
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = if (currentStep == 5) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState)
        .padding(20.dp)
    ) {
      // Progress Bar
      LinearProgressIndicator(
        progress = { currentStep / 5f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(CircleShape),
        color = IndigoPrimary,
        trackColor = IndigoSubtle
      )

      Spacer(modifier = Modifier.height(20.dp))

      when (currentStep) {
        1 -> {
          // Step 1: Basic Information
          Text(
            text = "Basic Information",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Let's establish your student credentials on TeamMate.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("College / Personal Email") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        2 -> {
          // Step 2: Academic Information
          Text(
            text = "Academic Information",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Helps match you with teammates from your campus or inter-college hackathons.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = collegeName,
            onValueChange = { collegeName = it },
            label = { Text("College / University Name") },
            leadingIcon = { Icon(Icons.Outlined.School, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = collegeCode,
              onValueChange = { collegeCode = it },
              label = { Text("College Code") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = prn,
              onValueChange = { prn = it },
              label = { Text("PRN / Student ID") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = branch,
            onValueChange = { branch = it },
            label = { Text("Branch / Major") },
            leadingIcon = { Icon(Icons.Outlined.Computer, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = yearOfStudy,
              onValueChange = { yearOfStudy = it },
              label = { Text("Year of Study") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = gradYear,
              onValueChange = { gradYear = it },
              label = { Text("Graduation Year") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        3 -> {
          // Step 3: Professional Links
          Text(
            text = "Professional Links",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Showcase your real repositories and public contributions.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = linkedinUrl,
            onValueChange = { linkedinUrl = it },
            label = { Text("LinkedIn URL (Required)") },
            leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = githubUrl,
            onValueChange = { githubUrl = it },
            label = { Text("GitHub URL (Required)") },
            leadingIcon = { Icon(Icons.Outlined.Code, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = instagramUrl,
            onValueChange = { instagramUrl = it },
            label = { Text("Instagram URL (Optional)") },
            leadingIcon = { Icon(Icons.Outlined.CameraAlt, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyanSubtle)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Outlined.Security, contentDescription = null, tint = CyanAccent)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "We never require or publicly expose personal WhatsApp numbers.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0E7490))
              )
            }
          }
        }

        4 -> {
          // Step 4: Profile Setup
          Text(
            text = "Profile Setup",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Summarize who you are and what skills you bring to a hackathon team.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = headline,
            onValueChange = { headline = it },
            label = { Text("Professional Headline") },
            placeholder = { Text("e.g. AI/ML Developer | SIH Finalist") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Short Bio") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = skillsInput,
            onValueChange = { skillsInput = it },
            label = { Text("Skills (comma separated)") },
            placeholder = { Text("Kotlin, Python, Figma, React, PyTorch") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = interestsInput,
            onValueChange = { interestsInput = it },
            label = { Text("Areas of Interest") },
            placeholder = { Text("Hackathons, AI/ML, Clean Energy, Mobile") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        5 -> {
          // Step 5: First Achievement
          Text(
            text = "Add Your First Experience",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AmberSubtle),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = Brush.linearGradient(listOf(AmberCompetition, AmberCompetition))
            )
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Stars, contentDescription = null, tint = AmberCompetition)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Every experience matters. Showcase your journey, not just your victories.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = achievementTitle,
            onValueChange = { achievementTitle = it },
            label = { Text("Title") },
            placeholder = { Text("e.g. SIH Zonal Participant / College Techfest Winner") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = achievementOrg,
            onValueChange = { achievementOrg = it },
            label = { Text("Organization / Host") },
            placeholder = { Text("e.g. AICTE, Google DSC, IEEE") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = achievementCat,
            onValueChange = { achievementCat = it },
            label = { Text("Category (Hackathon, Project, Certificate, Internship)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = achievementDesc,
            onValueChange = { achievementDesc = it },
            label = { Text("Description & Learnings") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }
    }
  }
}
