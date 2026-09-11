package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.Student
import com.example.ui.components.StudentCard
import com.example.ui.theme.CyanSubtle
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoSubtle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
  students: List<Student>,
  searchQuery: String,
  selectedSkillFilter: String,
  selectedCollegeFilter: String,
  onSearchChange: (String) -> Unit,
  onSkillFilterSelect: (String) -> Unit,
  onCollegeFilterSelect: (String) -> Unit,
  onConnectClick: (Student) -> Unit,
  onStudentClick: (Student) -> Unit,
  modifier: Modifier = Modifier
) {
  val domains = listOf("All", "AI/ML", "Android", "UI/UX", "Backend", "Web3", "IoT", "Python", "Kotlin")
  val colleges = listOf("All", "COEP", "IIT Bombay", "VJTI", "NIT Trichy", "IIT Delhi")

  val filteredStudents = remember(students, searchQuery, selectedSkillFilter, selectedCollegeFilter) {
    students.filter { student ->
      val matchesSearch = searchQuery.isBlank() ||
        student.name.contains(searchQuery, ignoreCase = true) ||
        student.college.contains(searchQuery, ignoreCase = true) ||
        student.headline.contains(searchQuery, ignoreCase = true) ||
        student.skills.any { it.contains(searchQuery, ignoreCase = true) } ||
        student.interests.any { it.contains(searchQuery, ignoreCase = true) }

      val matchesSkill = selectedSkillFilter == "All" ||
        student.skills.any { it.contains(selectedSkillFilter, ignoreCase = true) } ||
        student.headline.contains(selectedSkillFilter, ignoreCase = true)

      val matchesCollege = selectedCollegeFilter == "All" ||
        student.college.contains(selectedCollegeFilter, ignoreCase = true)

      matchesSearch && matchesSkill && matchesCollege
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("discover_screen")
  ) {
    // Search Box
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      Text(
        text = "Discover Students",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      )
      Text(
        text = "Find peers with complementary skills for your next project or hackathon.",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("student_search_input"),
        placeholder = { Text("Search by name, skill, college, or domain...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
            }
          }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )
    }

    // Domain Filters
    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(domains) { domain ->
        val isSelected = selectedSkillFilter == domain
        FilterChip(
          selected = isSelected,
          onClick = { onSkillFilterSelect(domain) },
          label = {
            Text(
              text = domain,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
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

    Spacer(modifier = Modifier.height(6.dp))

    // College Filters
    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(colleges) { college ->
        val isSelected = selectedCollegeFilter == college
        FilterChip(
          selected = isSelected,
          onClick = { onCollegeFilterSelect(college) },
          label = {
            Text(
              text = if (college == "All") "All Colleges" else college,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 12.sp
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Students Count Indicator
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Showing ${filteredStudents.size} students",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        )
      )

      Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyanSubtle
      ) {
        Text(
          text = "● Open to Teams",
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color(0xFF0E7490),
            fontWeight = FontWeight.Bold
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Student Cards List
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(filteredStudents) { student ->
        StudentCard(
          student = student,
          onConnectClick = { onConnectClick(student) },
          onCardClick = { onStudentClick(student) }
        )
      }

      if (filteredStudents.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Outlined.SearchOff,
              contentDescription = null,
              modifier = Modifier.size(48.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No students match your query",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Try clearing filters or searching for another skill",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}
