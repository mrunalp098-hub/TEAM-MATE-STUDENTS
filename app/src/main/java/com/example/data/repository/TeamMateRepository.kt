package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TeamMateRepository(private val database: TeamMateDatabase) {
  private val studentDao = database.studentDao()
  private val achievementDao = database.achievementDao()
  private val projectDao = database.projectDao()
  private val teamDao = database.teamDao()
  private val chatMessageDao = database.chatMessageDao()
  private val teamUpdateDao = database.teamUpdateDao()
  private val opportunityDao = database.opportunityDao()
  private val notificationDao = database.notificationDao()

  val allStudents: Flow<List<Student>> = studentDao.getAllStudents().map { entities ->
    entities.map { it.toDomain() }
  }

  val currentUser: Flow<Student?> = studentDao.getCurrentUser().map { it?.toDomain() }

  val allTeams: Flow<List<TeamItem>> = teamDao.getAllTeams().map { entities ->
    entities.map { it.toDomain() }
  }

  val allOpportunities: Flow<List<OpportunityItem>> = opportunityDao.getAllOpportunities().map { entities ->
    entities.map { it.toDomain() }
  }

  val allNotifications: Flow<List<NotificationModel>> = notificationDao.getAllNotifications().map { entities ->
    entities.map { it.toDomain() }
  }

  fun getStudentById(id: String): Flow<Student?> = studentDao.getStudentById(id).map { it?.toDomain() }

  fun getAchievementsForStudent(studentId: String): Flow<List<AchievementItem>> =
    achievementDao.getAchievementsForStudent(studentId).map { entities ->
      entities.map { it.toDomain() }
    }

  fun getProjectsForStudent(studentId: String): Flow<List<ProjectItem>> =
    projectDao.getProjectsForStudent(studentId).map { entities ->
      entities.map { it.toDomain() }
    }

  fun getTeamWithMembers(teamId: String): Flow<TeamItem?> {
    return combine(
      teamDao.getTeamById(teamId),
      teamDao.getTeamMembers(teamId)
    ) { teamEntity, memberEntities ->
      teamEntity?.let {
        it.toDomain().copy(
          members = memberEntities.map { member -> member.toDomain() }
        )
      }
    }
  }

  fun getTeamRequests(teamId: String): Flow<List<TeamJoinRequestItem>> =
    teamDao.getTeamRequests(teamId).map { entities ->
      entities.map { it.toDomain() }
    }

  fun getAllTeamRequests(): Flow<List<TeamJoinRequestItem>> =
    teamDao.getAllTeamRequests().map { entities ->
      entities.map { it.toDomain() }
    }

  fun getTeamChatMessages(teamId: String): Flow<List<ChatMessageItem>> =
    chatMessageDao.getMessagesForTeam(teamId).map { entities ->
      entities.map { it.toDomain() }
    }

  fun getTeamUpdates(teamId: String): Flow<List<TeamUpdateItem>> =
    teamUpdateDao.getUpdatesForTeam(teamId).map { entities ->
      entities.map { it.toDomain() }
    }

  suspend fun insertStudent(student: Student) {
    studentDao.insertStudent(student.toEntity())
  }

  suspend fun updateConnectionStatus(studentId: String, isConnected: Boolean, isPending: Boolean) {
    studentDao.updateConnectionStatus(studentId, isConnected, isPending)
    if (isPending) {
      notificationDao.insertNotification(
        NotificationEntity(
          id = "notif_${System.currentTimeMillis()}",
          title = "Connection Request Sent",
          message = "Your connection invitation has been sent.",
          category = "Networking",
          timeAgo = "Just now",
          isRead = false
        )
      )
    }
  }

  suspend fun addAchievement(achievement: AchievementItem) {
    achievementDao.insertAchievement(achievement.toEntity())
    notificationDao.insertNotification(
      NotificationEntity(
        id = "notif_ach_${System.currentTimeMillis()}",
        title = "Achievement Added! 🏆",
        message = "Added \"${achievement.title}\" to your professional journey.",
        category = "Achievement",
        timeAgo = "Just now",
        isRead = false
      )
    )
  }

  suspend fun addProject(project: ProjectItem) {
    projectDao.insertProject(project.toEntity())
  }

  suspend fun createTeam(team: TeamItem, leaderRole: String = "Team Lead & Architect") {
    val teamEntity = team.toEntity().copy(isUserLeader = true, isUserMember = true)
    teamDao.insertTeam(teamEntity)

    // Add leader as first member
    teamDao.insertTeamMember(
      TeamMemberEntity(
        id = "member_${System.currentTimeMillis()}",
        teamId = team.id,
        studentId = team.leaderId,
        name = team.leaderName,
        role = leaderRole,
        skills = team.requiredSkills.take(2).joinToString(", "),
        isLeader = true
      )
    )

    // Add initial welcome update
    teamUpdateDao.insertUpdate(
      TeamUpdateEntity(
        id = "update_${System.currentTimeMillis()}",
        teamId = team.id,
        title = "Team Workspace Created! 🚀",
        content = "Welcome to team ${team.name}! We're preparing for ${team.competitionName}. Check our problem statement and milestones.",
        authorName = team.leaderName,
        category = "ANNOUNCEMENT",
        timestamp = System.currentTimeMillis()
      )
    )

    // Add initial welcome message
    chatMessageDao.insertMessage(
      ChatMessageEntity(
        id = "msg_${System.currentTimeMillis()}",
        teamId = team.id,
        senderId = team.leaderId,
        senderName = team.leaderName,
        message = "Hey team! Excited to work together on ${team.competitionName}. Feel free to drop your introduction and availability here!",
        timestamp = System.currentTimeMillis(),
        isPinned = true,
        attachmentName = null
      )
    )

    notificationDao.insertNotification(
      NotificationEntity(
        id = "notif_team_${System.currentTimeMillis()}",
        title = "Team Created! 🎉",
        message = "Team \"${team.name}\" created for ${team.competitionName}.",
        category = "Teams",
        timeAgo = "Just now",
        isRead = false
      )
    )
  }

  suspend fun submitJoinRequest(request: TeamJoinRequestItem) {
    teamDao.insertTeamRequest(request.toEntity())
    notificationDao.insertNotification(
      NotificationEntity(
        id = "notif_req_${System.currentTimeMillis()}",
        title = "Team Request Submitted",
        message = "You applied to join team. The leader will review your profile.",
        category = "Teams",
        timeAgo = "Just now",
        isRead = false
      )
    )
  }

  suspend fun updateJoinRequestStatus(
    requestId: String,
    newStatus: String,
    teamId: String,
    studentId: String,
    studentName: String,
    studentSkills: List<String>
  ) {
    teamDao.updateRequestStatus(requestId, newStatus)
    if (newStatus == "ACCEPTED") {
      // Add student to team members
      teamDao.insertTeamMember(
        TeamMemberEntity(
          id = "member_${System.currentTimeMillis()}",
          teamId = teamId,
          studentId = studentId,
          name = studentName,
          role = "Team Member",
          skills = studentSkills.take(3).joinToString(", "),
          isLeader = false
        )
      )

      chatMessageDao.insertMessage(
        ChatMessageEntity(
          id = "msg_${System.currentTimeMillis()}",
          teamId = teamId,
          senderId = "system",
          senderName = "TeamMate System",
          message = "🎉 $studentName was welcomed to the team!",
          timestamp = System.currentTimeMillis(),
          isPinned = false,
          attachmentName = null
        )
      )

      notificationDao.insertNotification(
        NotificationEntity(
          id = "notif_accepted_${System.currentTimeMillis()}",
          title = "New Member Accepted! 🤝",
          message = "$studentName has joined your team workspace.",
          category = "Teams",
          timeAgo = "Just now",
          isRead = false
        )
      )
    }
  }

  suspend fun sendChatMessage(teamId: String, senderId: String, senderName: String, message: String, attachmentName: String? = null) {
    chatMessageDao.insertMessage(
      ChatMessageEntity(
        id = "msg_${System.currentTimeMillis()}",
        teamId = teamId,
        senderId = senderId,
        senderName = senderName,
        message = message,
        timestamp = System.currentTimeMillis(),
        isPinned = false,
        attachmentName = attachmentName
      )
    )
  }

  suspend fun togglePinMessage(messageId: String, isPinned: Boolean) {
    chatMessageDao.togglePinMessage(messageId, isPinned)
  }

  suspend fun addTeamUpdate(update: TeamUpdateItem) {
    teamUpdateDao.insertUpdate(update.toEntity())
  }

  suspend fun toggleOpportunityBookmark(id: String, isBookmarked: Boolean) {
    opportunityDao.toggleBookmark(id, isBookmarked)
  }

  suspend fun markAllNotificationsRead() {
    notificationDao.markAllAsRead()
  }

  suspend fun seedInitialDataIfNeeded() {
    val count = studentDao.getCount()
    if (count > 0) return

    val currentStudent = StudentEntity(
      id = "student_mrunal",
      name = "Mrunal Pawar",
      email = "mrunalpawar073@gmail.com",
      phone = "+91 98765 43210",
      college = "COEP Technological University",
      collegeCode = "COEP-1002",
      prn = "112303045",
      branch = "Computer Engineering",
      yearOfStudy = "3rd Year",
      gradYear = "2027",
      headline = "Full-Stack & Android Builder | AI Systems Enthusiast | Hackathon Competitor",
      bio = "Passionate about designing intuitive mobile architectures and scalable AI products. Winner of 3 national college hackathons. Always eager to team up for impactful engineering challenges.",
      avatarInitials = "MP",
      skills = "Kotlin, Jetpack Compose, Python, Machine Learning, UI/UX, Room DB, PyTorch, Git",
      interests = "Smart India Hackathon, AI/ML, Mobile Dev, Web3, Clean Energy, Open Source",
      linkedinUrl = "https://linkedin.com/in/mrunal-pawar",
      githubUrl = "https://github.com/mrunalpawar",
      instagramUrl = "https://instagram.com/mrunal.builds",
      achievementScore = 2150,
      hackathonsCount = 4,
      competitionsCount = 6,
      projectsCount = 5,
      isConnected = false,
      connectionPending = false,
      isCurrentUser = true
    )

    val otherStudents = listOf(
      StudentEntity(
        id = "student_aarav",
        name = "Aarav Sharma",
        email = "aarav.sharma@iitb.ac.in",
        phone = "+91 98234 11223",
        college = "IIT Bombay",
        collegeCode = "IITB-01",
        prn = "210050012",
        branch = "Computer Science & Eng",
        yearOfStudy = "4th Year",
        gradYear = "2026",
        headline = "AI Researcher & PyTorch Contributor | SIH Winner | Kaggle Master",
        bio = "Focusing on multimodal models and edge inference. Seeking UI/UX designers and mobile developers for international innovation summits.",
        avatarInitials = "AS",
        skills = "Python, PyTorch, Transformers, Computer Vision, Docker, C++",
        interests = "Generative AI, Autonomous Systems, SIH 2026, HackMIT",
        linkedinUrl = "https://linkedin.com/in/aaravsharma-ai",
        githubUrl = "https://github.com/aaravsharma",
        instagramUrl = "",
        achievementScore = 2480,
        hackathonsCount = 7,
        competitionsCount = 8,
        projectsCount = 6,
        isConnected = true,
        connectionPending = false,
        isCurrentUser = false
      ),
      StudentEntity(
        id = "student_shreya",
        name = "Shreya Kulkarni",
        email = "shreya.k@vjti.ac.in",
        phone = "+91 98111 22334",
        college = "VJTI Mumbai",
        collegeCode = "VJTI-05",
        prn = "221080034",
        branch = "Information Technology",
        yearOfStudy = "3rd Year",
        gradYear = "2027",
        headline = "Product Designer & Frontend Engineer | Design Systems | Figma Pro",
        bio = "Translating complex algorithms into elegant, accessible human experiences. Led design for 4 hackathon-winning MVPs.",
        avatarInitials = "SK",
        skills = "Figma, UI/UX Design, Design Systems, React, Tailwind, Prototyping",
        interests = "Product Design, FinTech, Accessibility, Google Solution Challenge",
        linkedinUrl = "https://linkedin.com/in/shreyakulkarni-design",
        githubUrl = "https://github.com/shreyadesign",
        instagramUrl = "",
        achievementScore = 2050,
        hackathonsCount = 5,
        competitionsCount = 4,
        projectsCount = 8,
        isConnected = false,
        connectionPending = true,
        isCurrentUser = false
      ),
      StudentEntity(
        id = "student_rohan",
        name = "Rohan Deshmukh",
        email = "rohan.d@coep.ac.in",
        phone = "+91 97666 54321",
        college = "COEP Technological University",
        collegeCode = "COEP-1002",
        prn = "112303088",
        branch = "Computer Engineering",
        yearOfStudy = "3rd Year",
        gradYear = "2027",
        headline = "Backend Architect | Go, Rust & Distributed Systems | Cloud Native",
        bio = "Building high-throughput microservices and resilient cloud architectures. Looking to team up for backend-heavy competitions.",
        avatarInitials = "RD",
        skills = "Go, Kubernetes, PostgreSQL, gRPC, Docker, Redis, Rust",
        interests = "Cloud Native, Smart Cities, SIH, NASA Space Apps",
        linkedinUrl = "https://linkedin.com/in/rohandeshmukh",
        githubUrl = "https://github.com/rohandesh",
        instagramUrl = "",
        achievementScore = 1920,
        hackathonsCount = 3,
        competitionsCount = 5,
        projectsCount = 4,
        isConnected = true,
        connectionPending = false,
        isCurrentUser = false
      ),
      StudentEntity(
        id = "student_ananya",
        name = "Ananya Iyer",
        email = "ananya.i@nitt.edu",
        phone = "+91 94455 66778",
        college = "NIT Trichy",
        collegeCode = "NITT-12",
        prn = "106122019",
        branch = "Electronics & Comm",
        yearOfStudy = "3rd Year",
        gradYear = "2027",
        headline = "Edge AI & IoT Specialist | TinyML | Smart Agriculture Innovations",
        bio = "Hardware-software integration enthusiast. Built drone surveillance systems and smart soil monitors.",
        avatarInitials = "AI",
        skills = "Embedded C, TinyML, Arduino, ESP32, Python, OpenCV",
        interests = "AgriTech, IoT, Hardware Hackathons, Imagine Cup",
        linkedinUrl = "https://linkedin.com/in/ananyaiyer-edge",
        githubUrl = "https://github.com/ananyaiyer",
        instagramUrl = "",
        achievementScore = 1780,
        hackathonsCount = 4,
        competitionsCount = 3,
        projectsCount = 4,
        isConnected = false,
        connectionPending = false,
        isCurrentUser = false
      ),
      StudentEntity(
        id = "student_vikram",
        name = "Vikram Malhotra",
        email = "vikram.m@iitd.ac.in",
        phone = "+91 98888 12345",
        college = "IIT Delhi",
        collegeCode = "IITD-02",
        prn = "2021CS1045",
        branch = "Computer Science",
        yearOfStudy = "4th Year",
        gradYear = "2026",
        headline = "Systems & Web3 Developer | Solidity, Rust & Distributed Storage",
        bio = "Focused on zero-knowledge proofs and decentralised data structures. Winner at ETHIndia 2025.",
        avatarInitials = "VM",
        skills = "Solidity, Rust, Ethereum, Smart Contracts, TypeScript, Cryptography",
        interests = "Web3, Privacy, Decentralization, FinTech",
        linkedinUrl = "https://linkedin.com/in/vikram-malhotra",
        githubUrl = "https://github.com/vikramm",
        instagramUrl = "",
        achievementScore = 2310,
        hackathonsCount = 6,
        competitionsCount = 7,
        projectsCount = 7,
        isConnected = false,
        connectionPending = false,
        isCurrentUser = false
      )
    )

    studentDao.insertStudents(listOf(currentStudent) + otherStudents)

    // Initial achievements for Mrunal
    val achievements = listOf(
      AchievementEntity(
        id = "ach_1",
        studentId = "student_mrunal",
        title = "1st Place Winner — SIH Zonal Hackathon",
        organization = "Ministry of Education & AICTE",
        date = "Nov 2025",
        category = "Hackathon",
        description = "Developed an offline-first disaster coordination mobile app with peer-to-peer Bluetooth mesh networking."
      ),
      AchievementEntity(
        id = "ach_2",
        studentId = "student_mrunal",
        title = "Finalist — Google Solution Challenge",
        organization = "Google Developer Student Clubs",
        date = "Aug 2025",
        category = "Competition",
        description = "Created an eco-routing carbon footprint analyzer using Android Jetpack Compose and Gemini AI."
      ),
      AchievementEntity(
        id = "ach_3",
        studentId = "student_mrunal",
        title = "Android Associate Developer Certified",
        organization = "Google Developers",
        date = "May 2025",
        category = "Certificate",
        description = "Demonstrated proficiency in modern Kotlin, Jetpack libraries, Room, Coroutines, and MVVM."
      ),
      AchievementEntity(
        id = "ach_4",
        studentId = "student_mrunal",
        title = "Mobile Engineering Intern",
        organization = "NextGen Tech Labs",
        date = "June - Aug 2025",
        category = "Internship",
        description = "Shipped 3 production features for a student mentorship platform with 50K+ MAU."
      ),
      AchievementEntity(
        id = "ach_5",
        studentId = "student_aarav",
        title = "National Gold Medalist — AI Innovation Summit",
        organization = "NASSCOM",
        date = "Jan 2026",
        category = "Award",
        description = "Pioneered lightweight attention mechanism for real-time edge processing."
      ),
      AchievementEntity(
        id = "ach_6",
        studentId = "student_shreya",
        title = "Best UI/UX Award — HackDesign 2025",
        organization = "Interaction Design Association",
        date = "Dec 2025",
        category = "Competition",
        description = "Designed intuitive cognitive therapy mobile interface with high accessibility scores."
      )
    )
    achievementDao.insertAchievements(achievements)

    // Initial projects
    val projects = listOf(
      ProjectEntity(
        id = "proj_1",
        studentId = "student_mrunal",
        title = "MeshConnect — Disaster Relief Mesh Network",
        description = "Android app enabling zero-internet peer-to-peer crisis communications across local emergency responders.",
        technologies = "Kotlin, Jetpack Compose, Room, Bluetooth Low Energy, Coroutines",
        githubUrl = "https://github.com/mrunalpawar/mesh-connect",
        liveUrl = ""
      ),
      ProjectEntity(
        id = "proj_2",
        studentId = "student_mrunal",
        title = "NeuroSense AI — Student Mental Wellness",
        description = "Privacy-focused mobile assistant that provides cognitive micro-interventions and reflection journals.",
        technologies = "Android, Gemini API, Jetpack Compose, KSP, SQLite",
        githubUrl = "https://github.com/mrunalpawar/neurosense",
        liveUrl = "https://neurosense.dev"
      ),
      ProjectEntity(
        id = "proj_3",
        studentId = "student_aarav",
        title = "VisionEdge — Sub-10ms Object Tracker",
        description = "Quantized CNN framework optimized for mobile Snapdragon and Tensor processors.",
        technologies = "Python, C++, ONNX, TensorRT, PyTorch",
        githubUrl = "https://github.com/aaravsharma/visionedge",
        liveUrl = ""
      )
    )
    projectDao.insertProjects(projects)

    // Initial teams
    val teams = listOf(
      TeamEntity(
        id = "team_sih_traffic",
        name = "NeuroVision SIH",
        competitionName = "Smart India Hackathon 2026",
        competitionLink = "https://sih.gov.in",
        competitionDescription = "National competition by Ministry of Education for innovative software & hardware solutions.",
        problemStatement = "Smart Traffic Management & Emergency Vehicle Corridor Automation using Real-Time Computer Vision and Edge Telemetry.",
        leaderId = "student_aarav",
        leaderName = "Aarav Sharma",
        leaderCollege = "IIT Bombay",
        membersRequired = 4,
        openRoles = "Android Developer, UI/UX Designer",
        requiredSkills = "Kotlin, Jetpack Compose, Figma, REST APIs",
        deadline = "15 Oct 2026",
        status = "OPEN",
        isUserMember = false,
        isUserLeader = false
      ),
      TeamEntity(
        id = "team_google_solution",
        name = "EcoTrack Labs",
        competitionName = "Google Solution Challenge 2026",
        competitionLink = "https://developers.google.com/community/gdsc-solution-challenge",
        competitionDescription = "Global university hackathon solving 1 of 17 UN Sustainable Development Goals using Google tech.",
        problemStatement = "Urban Food Waste Redistribution & Cold-Chain Tracking for Neighborhood Communities.",
        leaderId = "student_mrunal",
        leaderName = "Mrunal Pawar",
        leaderCollege = "COEP Tech",
        membersRequired = 4,
        openRoles = "Cloud/Backend Engineer, ML Specialist",
        requiredSkills = "Go / Python, Cloud SQL, Firebase, Gemini API",
        deadline = "25 Nov 2026",
        status = "OPEN",
        isUserMember = true,
        isUserLeader = true
      ),
      TeamEntity(
        id = "team_nasa_space",
        name = "AeroBytes Orbit",
        competitionName = "NASA Space Apps Challenge 2026",
        competitionLink = "https://www.spaceappschallenge.org",
        competitionDescription = "The world's largest annual global hackathon using open NASA science and satellite datasets.",
        problemStatement = "Near-Earth Asteroid Trajectory Visualizer & Atmospheric Impact Predictor.",
        leaderId = "student_rohan",
        leaderName = "Rohan Deshmukh",
        leaderCollege = "COEP Tech",
        membersRequired = 5,
        openRoles = "Data Scientist, 3D Web/Mobile Visualizer",
        requiredSkills = "Python, WebGL / OpenGL, Three.js, Mathematics",
        deadline = "04 Oct 2026",
        status = "OPEN",
        isUserMember = false,
        isUserLeader = false
      )
    )
    teamDao.insertTeams(teams)

    // Team members
    val teamMembers = listOf(
      TeamMemberEntity(
        id = "tm_1",
        teamId = "team_sih_traffic",
        studentId = "student_aarav",
        name = "Aarav Sharma",
        role = "Team Lead & AI Architect",
        skills = "Python, PyTorch, Computer Vision",
        isLeader = true
      ),
      TeamMemberEntity(
        id = "tm_2",
        teamId = "team_sih_traffic",
        studentId = "student_rohan",
        name = "Rohan Deshmukh",
        role = "Backend Systems Engineer",
        skills = "Go, Kubernetes, gRPC",
        isLeader = false
      ),
      TeamMemberEntity(
        id = "tm_3",
        teamId = "team_google_solution",
        studentId = "student_mrunal",
        name = "Mrunal Pawar",
        role = "Team Lead & Android Architect",
        skills = "Kotlin, Jetpack Compose, Room",
        isLeader = true
      ),
      TeamMemberEntity(
        id = "tm_4",
        teamId = "team_google_solution",
        studentId = "student_shreya",
        name = "Shreya Kulkarni",
        role = "UI/UX Product Designer",
        skills = "Figma, User Research, Prototyping",
        isLeader = false
      )
    )
    teamDao.insertTeamMembers(teamMembers)

    // Team Requests
    val requests = listOf(
      TeamJoinRequestEntity(
        id = "req_1",
        teamId = "team_google_solution",
        studentId = "student_ananya",
        studentName = "Ananya Iyer",
        studentHeadline = "Edge AI & IoT Specialist | NIT Trichy",
        studentCollege = "NIT Trichy",
        studentBranch = "ECE",
        studentYear = "3rd Year",
        studentSkills = "TinyML, Embedded C, Python, IoT",
        status = "PENDING",
        appliedAt = "Yesterday"
      ),
      TeamJoinRequestEntity(
        id = "req_2",
        teamId = "team_google_solution",
        studentId = "student_vikram",
        studentName = "Vikram Malhotra",
        studentHeadline = "Systems & Web3 Developer | IIT Delhi",
        studentCollege = "IIT Delhi",
        studentBranch = "CSE",
        studentYear = "4th Year",
        studentSkills = "Rust, Solidity, Distributed Systems",
        status = "SHORTLISTED",
        appliedAt = "2 days ago"
      )
    )
    for (req in requests) {
      teamDao.insertTeamRequest(req)
    }

    // Chat messages for EcoTrack Labs (user's team)
    val chatMessages = listOf(
      ChatMessageEntity(
        id = "chat_1",
        teamId = "team_google_solution",
        senderId = "student_mrunal",
        senderName = "Mrunal Pawar",
        message = "Hey Shreya! Welcome to the EcoTrack team for the Google Solution Challenge 2026. Let's build something game-changing.",
        timestamp = System.currentTimeMillis() - 86400000 * 2,
        isPinned = true,
        attachmentName = null
      ),
      ChatMessageEntity(
        id = "chat_2",
        teamId = "team_google_solution",
        senderId = "student_shreya",
        senderName = "Shreya Kulkarni",
        message = "Super excited! I've started putting together wireframes in Figma for the food rescue donation map and donor flow.",
        timestamp = System.currentTimeMillis() - 86400000 + 3600000,
        isPinned = false,
        attachmentName = "wireframes_v1_figma.pdf"
      ),
      ChatMessageEntity(
        id = "chat_3",
        teamId = "team_google_solution",
        senderId = "student_mrunal",
        senderName = "Mrunal Pawar",
        message = "Awesome! I reviewed Ananya's application for IoT temperature monitoring. Thinking of shortlisting her for hardware telemetry.",
        timestamp = System.currentTimeMillis() - 7200000,
        isPinned = false,
        attachmentName = null
      )
    )
    chatMessageDao.insertMessages(chatMessages)

    // Team Updates
    val updates = listOf(
      TeamUpdateEntity(
        id = "up_1",
        teamId = "team_google_solution",
        title = "Sprint 1: Architecture & UI System Finalized",
        content = "We have mapped out the core entity schema and completed initial low-fidelity mobile flows.",
        authorName = "Mrunal Pawar",
        category = "ANNOUNCEMENT",
        timestamp = System.currentTimeMillis() - 86400000
      ),
      TeamUpdateEntity(
        id = "up_2",
        teamId = "team_google_solution",
        title = "Decided on Gemini 1.5 Flash for Food Spoilage Classifier",
        content = "Agreed to leverage multimodal vision API for camera-based produce fresh-index verification.",
        authorName = "Mrunal Pawar",
        category = "DECISION",
        timestamp = System.currentTimeMillis() - 43200000
      )
    )
    teamUpdateDao.insertUpdates(updates)

    // Opportunities
    val opportunities = listOf(
      OpportunityEntity(
        id = "opp_1",
        title = "Smart India Hackathon 2026",
        organization = "Ministry of Education & AICTE",
        category = "Hackathon",
        deadline = "15 Oct 2026",
        location = "Pan-India Nodal Centers",
        eligibility = "College Undergrad & Postgrad Students",
        teamRequirement = "6 Members (Min 1 Female Member)",
        link = "https://sih.gov.in",
        isBookmarked = true
      ),
      OpportunityEntity(
        id = "opp_2",
        title = "Google Solution Challenge 2026",
        organization = "Google Developers",
        category = "Competition",
        deadline = "25 Nov 2026",
        location = "Global / Virtual",
        eligibility = "GDSC / University Students Worldwide",
        teamRequirement = "Max 4 Members",
        link = "https://developers.google.com",
        isBookmarked = true
      ),
      OpportunityEntity(
        id = "opp_3",
        title = "NASA Space Apps Challenge 2026",
        organization = "NASA & International Space Agencies",
        category = "Hackathon",
        deadline = "04 Oct 2026",
        location = "Hybrid (Over 300 Global Cities)",
        eligibility = "Open to All Passionate Innovators",
        teamRequirement = "2 to 6 Members",
        link = "https://spaceappschallenge.org",
        isBookmarked = false
      ),
      OpportunityEntity(
        id = "opp_4",
        title = "Microsoft Imagine Cup 2026",
        organization = "Microsoft",
        category = "Competition",
        deadline = "10 Jan 2027",
        location = "Global Online & Finals at Redmond",
        eligibility = "Full-time University Students",
        teamRequirement = "1 to 4 Members",
        link = "https://imaginecup.microsoft.com",
        isBookmarked = false
      ),
      OpportunityEntity(
        id = "opp_5",
        title = "Open Source Summer Research Fellowship",
        organization = "Linux Foundation & Academic Labs",
        category = "Internship",
        deadline = "30 Oct 2026",
        location = "Remote (Stipend ₹75,000/mo)",
        eligibility = "Pre-final & Final Year CS/IT Students",
        teamRequirement = "Individual Application",
        link = "https://linuxfoundation.org",
        isBookmarked = false
      )
    )
    opportunityDao.insertOpportunities(opportunities)

    // Notifications
    val notifications = listOf(
      NotificationEntity(
        id = "notif_1",
        title = "New Team Application! 📩",
        message = "Ananya Iyer requested to join team \"EcoTrack Labs\".",
        category = "Teams",
        timeAgo = "10m ago",
        isRead = false
      ),
      NotificationEntity(
        id = "notif_2",
        title = "Connection Request Accepted",
        message = "Aarav Sharma accepted your connection request.",
        category = "Networking",
        timeAgo = "2h ago",
        isRead = false
      ),
      NotificationEntity(
        id = "notif_3",
        title = "SIH 2026 Problem Statements Released",
        message = "240+ problem statements are now live. Find teammates to apply!",
        category = "Competitions",
        timeAgo = "5h ago",
        isRead = true
      ),
      NotificationEntity(
        id = "notif_4",
        title = "Profile Viewed 👀",
        message = "3 students and 1 team leader viewed your portfolio journey today.",
        category = "Profile",
        timeAgo = "1d ago",
        isRead = true
      )
    )
    notificationDao.insertNotifications(notifications)
  }

  // Conversion extension functions
  private fun StudentEntity.toDomain() = Student(
    id = id,
    name = name,
    email = email,
    phone = phone,
    college = college,
    collegeCode = collegeCode,
    prn = prn,
    branch = branch,
    yearOfStudy = yearOfStudy,
    gradYear = gradYear,
    headline = headline,
    bio = bio,
    avatarInitials = avatarInitials,
    skills = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    interests = interests.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    linkedinUrl = linkedinUrl,
    githubUrl = githubUrl,
    instagramUrl = instagramUrl,
    achievementScore = achievementScore,
    hackathonsCount = hackathonsCount,
    competitionsCount = competitionsCount,
    projectsCount = projectsCount,
    isConnected = isConnected,
    connectionPending = connectionPending,
    isCurrentUser = isCurrentUser
  )

  private fun Student.toEntity() = StudentEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    college = college,
    collegeCode = collegeCode,
    prn = prn,
    branch = branch,
    yearOfStudy = yearOfStudy,
    gradYear = gradYear,
    headline = headline,
    bio = bio,
    avatarInitials = avatarInitials,
    skills = skills.joinToString(", "),
    interests = interests.joinToString(", "),
    linkedinUrl = linkedinUrl,
    githubUrl = githubUrl,
    instagramUrl = instagramUrl,
    achievementScore = achievementScore,
    hackathonsCount = hackathonsCount,
    competitionsCount = competitionsCount,
    projectsCount = projectsCount,
    isConnected = isConnected,
    connectionPending = connectionPending,
    isCurrentUser = isCurrentUser
  )

  private fun AchievementEntity.toDomain() = AchievementItem(
    id = id,
    studentId = studentId,
    title = title,
    organization = organization,
    date = date,
    category = category,
    description = description
  )

  private fun AchievementItem.toEntity() = AchievementEntity(
    id = id,
    studentId = studentId,
    title = title,
    organization = organization,
    date = date,
    category = category,
    description = description
  )

  private fun ProjectEntity.toDomain() = ProjectItem(
    id = id,
    studentId = studentId,
    title = title,
    description = description,
    technologies = technologies.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    githubUrl = githubUrl,
    liveUrl = liveUrl
  )

  private fun ProjectItem.toEntity() = ProjectEntity(
    id = id,
    studentId = studentId,
    title = title,
    description = description,
    technologies = technologies.joinToString(", "),
    githubUrl = githubUrl,
    liveUrl = liveUrl
  )

  private fun TeamEntity.toDomain() = TeamItem(
    id = id,
    name = name,
    competitionName = competitionName,
    competitionLink = competitionLink,
    competitionDescription = competitionDescription,
    problemStatement = problemStatement,
    leaderId = leaderId,
    leaderName = leaderName,
    leaderCollege = leaderCollege,
    membersRequired = membersRequired,
    openRoles = openRoles.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    requiredSkills = requiredSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    deadline = deadline,
    status = status,
    isUserMember = isUserMember,
    isUserLeader = isUserLeader
  )

  private fun TeamItem.toEntity() = TeamEntity(
    id = id,
    name = name,
    competitionName = competitionName,
    competitionLink = competitionLink,
    competitionDescription = competitionDescription,
    problemStatement = problemStatement,
    leaderId = leaderId,
    leaderName = leaderName,
    leaderCollege = leaderCollege,
    membersRequired = membersRequired,
    openRoles = openRoles.joinToString(", "),
    requiredSkills = requiredSkills.joinToString(", "),
    deadline = deadline,
    status = status,
    isUserMember = isUserMember,
    isUserLeader = isUserLeader
  )

  private fun TeamMemberEntity.toDomain() = TeamMemberInfo(
    id = id,
    teamId = teamId,
    studentId = studentId,
    name = name,
    role = role,
    skills = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    isLeader = isLeader
  )

  private fun TeamJoinRequestEntity.toDomain() = TeamJoinRequestItem(
    id = id,
    teamId = teamId,
    studentId = studentId,
    studentName = studentName,
    studentHeadline = studentHeadline,
    studentCollege = studentCollege,
    studentBranch = studentBranch,
    studentYear = studentYear,
    studentSkills = studentSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    status = status,
    appliedAt = appliedAt
  )

  private fun TeamJoinRequestItem.toEntity() = TeamJoinRequestEntity(
    id = id,
    teamId = teamId,
    studentId = studentId,
    studentName = studentName,
    studentHeadline = studentHeadline,
    studentCollege = studentCollege,
    studentBranch = studentBranch,
    studentYear = studentYear,
    studentSkills = studentSkills.joinToString(", "),
    status = status,
    appliedAt = appliedAt
  )

  private fun ChatMessageEntity.toDomain() = ChatMessageItem(
    id = id,
    teamId = teamId,
    senderId = senderId,
    senderName = senderName,
    message = message,
    timestamp = timestamp,
    isPinned = isPinned,
    attachmentName = attachmentName
  )

  private fun TeamUpdateEntity.toDomain() = TeamUpdateItem(
    id = id,
    teamId = teamId,
    title = title,
    content = content,
    authorName = authorName,
    category = category,
    timestamp = timestamp
  )

  private fun TeamUpdateItem.toEntity() = TeamUpdateEntity(
    id = id,
    teamId = teamId,
    title = title,
    content = content,
    authorName = authorName,
    category = category,
    timestamp = timestamp
  )

  private fun OpportunityEntity.toDomain() = OpportunityItem(
    id = id,
    title = title,
    organization = organization,
    category = category,
    deadline = deadline,
    location = location,
    eligibility = eligibility,
    teamRequirement = teamRequirement,
    link = link,
    isBookmarked = isBookmarked
  )

  private fun NotificationEntity.toDomain() = NotificationModel(
    id = id,
    title = title,
    message = message,
    category = category,
    timeAgo = timeAgo,
    isRead = isRead
  )
}
