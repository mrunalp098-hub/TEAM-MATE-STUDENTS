package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamWorkspaceScreen(
  team: TeamItem?,
  messages: List<ChatMessageItem>,
  updates: List<TeamUpdateItem>,
  currentUserId: String = "student_mrunal",
  onBackClick: () -> Unit,
  onSendMessage: (String, String?) -> Unit,
  onTogglePinMessage: (String, Boolean) -> Unit,
  onPostUpdate: (title: String, content: String, category: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Chat", "Members", "Competition & Tasks")

  var messageText by remember { mutableStateOf("") }
  var attachedFileName by remember { mutableStateOf<String?>(null) }

  var showNewUpdateDialog by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val chatListState = rememberLazyListState()

  // Auto scroll to bottom when new message arrives
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      chatListState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("team_workspace_screen"),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = team?.name ?: "Team Workspace",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${team?.competitionName} • Private Workspace",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Teams")
          }
        },
        actions = {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = EmeraldSubtle,
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(EmeraldSuccess)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${team?.members?.size ?: 2} Online",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = EmeraldSuccess,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }
      )
    },
    bottomBar = {
      if (selectedTab == 0) {
        // Chat input bar
        Surface(
          tonalElevation = 8.dp,
          modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
          Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // Attached file chip
            attachedFileName?.let { fileName ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyanSubtle,
                modifier = Modifier.padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.AttachFile, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(fileName, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF0E7490)))
                  Spacer(modifier = Modifier.width(6.dp))
                  IconButton(
                    onClick = { attachedFileName = null },
                    modifier = Modifier.size(16.dp)
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove attachment")
                  }
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              IconButton(
                onClick = {
                  attachedFileName = if (attachedFileName == null) "architecture_spec_v1.pdf" else null
                }
              ) {
                Icon(
                  imageVector = Icons.Outlined.AttachFile,
                  contentDescription = "Attach Document",
                  tint = if (attachedFileName != null) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Message team...") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
              )

              Spacer(modifier = Modifier.width(8.dp))

              IconButton(
                onClick = {
                  if (messageText.isNotBlank() || attachedFileName != null) {
                    onSendMessage(messageText, attachedFileName)
                    messageText = ""
                    attachedFileName = null
                  }
                },
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(IndigoPrimary)
                  .testTag("chat_send_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Send",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
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
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
              )
            }
          )
        }
      }

      when (selectedTab) {
        0 -> {
          // Tab 1: Team Chat
          Column(modifier = Modifier.fillMaxSize()) {
            // Pinned message banner if any
            val pinnedMessage = messages.firstOrNull { it.isPinned }
            pinnedMessage?.let { pin ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AmberSubtle,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.PushPin, contentDescription = null, tint = AmberCompetition, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "PINNED BY ${pin.senderName.uppercase()}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    )
                    Text(
                      text = pin.message,
                      style = MaterialTheme.typography.bodySmall,
                      maxLines = 1
                    )
                  }
                }
              }
            }

            LazyColumn(
              state = chatListState,
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
              contentPadding = PaddingValues(vertical = 12.dp)
            ) {
              items(messages) { msg ->
                val isMe = msg.senderId == currentUserId
                val isSystem = msg.senderId == "system"

                if (isSystem) {
                  Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                  ) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                      Text(
                        text = msg.message,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                      )
                    }
                  }
                } else {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                  ) {
                    if (!isMe) {
                      Box(
                        modifier = Modifier
                          .size(32.dp)
                          .clip(CircleShape)
                          .background(PurpleSecondary),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = msg.senderName.take(2).uppercase(),
                          color = Color.White,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                      horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                      modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                      if (!isMe) {
                        Text(
                          text = msg.senderName,
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                          )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                      }

                      Card(
                        shape = RoundedCornerShape(
                          topStart = 16.dp,
                          topEnd = 16.dp,
                          bottomStart = if (isMe) 16.dp else 4.dp,
                          bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                          containerColor = if (isMe) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                        )
                      ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                          Text(
                            text = msg.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                              color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                          )

                          // Attached file
                          msg.attachmentName?.let { file ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (isMe) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            ) {
                              Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Icon(
                                  Icons.Default.AttachFile,
                                  contentDescription = null,
                                  tint = if (isMe) Color.White else IndigoPrimary,
                                  modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                  text = file,
                                  style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                  )
                                )
                              }
                            }
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(2.dp))

                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp)),
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                          )
                        )
                        if (msg.isPinned) {
                          Spacer(modifier = Modifier.width(4.dp))
                          Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = AmberCompetition,
                            modifier = Modifier.size(10.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        1 -> {
          // Tab 2: Team Members
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Confirmed Members (${team?.members?.size ?: 2})",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = IndigoSubtle
                ) {
                  Text(
                    text = "${team?.membersRequired ?: 4} Total Slots",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = IndigoPrimary
                    )
                  )
                }
              }
            }

            items(team?.members ?: emptyList()) { member ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(if (member.isLeader) IndigoPrimary else PurpleSecondary),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = member.name.take(2).uppercase(),
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp
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
                        text = member.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                      )
                      if (member.isLeader) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = AmberSubtle
                        ) {
                          Text(
                            text = "Team Lead",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = AmberCompetition,
                              fontWeight = FontWeight.Bold
                            )
                          )
                        }
                      }
                    }

                    Text(
                      text = member.role,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                      )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                      text = "Skills: ${member.skills.joinToString(", ")}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Tab 3: Competition Info & Internal Updates
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Competition Info
            item {
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Competition Specifications",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = team?.competitionDescription ?: "National innovation challenge.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "Problem Statement:",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = IndigoPrimary
                    )
                  )
                  Text(
                    text = team?.problemStatement ?: "Real-time AI telemetry routing system.",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Deadline: ${team?.deadline}",
                      style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AmberCompetition
                      )
                    )
                    TextButton(
                      onClick = { /* Open official link */ }
                    ) {
                      Text("Official Portal ↗")
                    }
                  }
                }
              }
            }

            // Updates / Announcements Header
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Team Decisions & Milestones",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(onClick = { showNewUpdateDialog = true }) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Post Update")
                }
              }
            }

            items(updates) { update ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = when (update.category) {
                        "DECISION" -> PurpleSubtle
                        "TASK" -> CyanSubtle
                        else -> IndigoSubtle
                      }
                    ) {
                      Text(
                        text = update.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = when (update.category) {
                            "DECISION" -> PurpleSecondary
                            "TASK" -> CyanAccent
                            else -> IndigoPrimary
                          }
                        )
                      )
                    }

                    Text(
                      text = update.authorName,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Text(
                    text = update.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = update.content,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
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

  // Dialog to post new update
  if (showNewUpdateDialog) {
    var updateTitle by remember { mutableStateOf("") }
    var updateContent by remember { mutableStateOf("") }
    var updateCategory by remember { mutableStateOf("ANNOUNCEMENT") }

    AlertDialog(
      onDismissRequest = { showNewUpdateDialog = false },
      title = { Text("Post Team Update") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = updateTitle,
            onValueChange = { updateTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = updateContent,
            onValueChange = { updateContent = it },
            label = { Text("Details") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ANNOUNCEMENT", "DECISION", "TASK").forEach { cat ->
              FilterChip(
                selected = updateCategory == cat,
                onClick = { updateCategory = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (updateTitle.isNotBlank()) {
              onPostUpdate(updateTitle, updateContent, updateCategory)
              showNewUpdateDialog = false
            }
          }
        ) {
          Text("Publish")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewUpdateDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
