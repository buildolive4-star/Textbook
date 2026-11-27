package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.data.firebase.signOutUser
import com.example.data.model.CurriculumCatalog
import com.example.data.model.UiState
import com.example.data.repository.AiTeacherRepository
import com.example.data.repository.TextbookRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguagePickerSheet
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiTeacherScreen
import com.example.ui.screens.CoAdminLoginDialog
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileAndManagementScreen
import com.example.ui.screens.QuizAndExamScreen
import com.example.ui.screens.SplashAndAuthScreen
import com.example.ui.screens.StudyMaterialAndVideosScreen
import com.example.ui.screens.StudyPlannerAndProgressScreen
import com.example.ui.screens.TextbookDetailScreen
import com.example.ui.screens.TextbookReaderScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.viewmodel.AiTeacherViewModel
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private object Routes {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val AI_TEACHER = "ai_teacher"
    const val PRACTICE = "practice"
    const val PROFILE = "profile"
    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val READER = "reader/{bookId}/{chapterId}"
    const val NOTES_VIDEOS = "notes_videos/{initialTab}"
    const val PLANNER_PROGRESS = "planner_progress"
    const val EDIT_PREFERENCES = "edit_preferences"
    const val ADMIN_PANEL = "admin_panel"

    fun bookDetail(bookId: String) = "book_detail/$bookId"
    fun reader(bookId: String, chapterId: String) = "reader/$bookId/$chapterId"
    fun notesVideos(initialTab: Int) = "notes_videos/$initialTab"
}

private data class NavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private val topLevelRoutes = listOf(
    Routes.HOME,
    Routes.LIBRARY,
    Routes.AI_TEACHER,
    Routes.PRACTICE,
    Routes.PROFILE
)

@Composable
fun TextbookApp(
    isDarkMode: Boolean,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleDarkMode: () -> Unit
) {
    val strings = LocalAppStrings.current
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    val user = currentUser
    if (user == null) {
        SplashAndAuthScreen(
            onOpenLanguagePicker = { showLanguagePicker = true },
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        // Key composition and ViewModels by uid so signing out or switching accounts disposes old listeners cleanly
        key(user.uid) {
            AuthenticatedTextbookShell(
                currentUserId = user.uid,
                displayName = user.displayName ?: "Student",
                email = user.email,
                isDarkMode = isDarkMode,
                currentLanguage = currentLanguage,
                onOpenLanguagePicker = { showLanguagePicker = true },
                onToggleDarkMode = onToggleDarkMode
            )
        }
    }

    if (showLanguagePicker) {
        LanguagePickerSheet(
            currentLanguage = currentLanguage,
            title = strings.chooseLanguageTitle,
            subtitle = strings.chooseLanguageSubtitle,
            onSelectLanguage = onSelectLanguage,
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedTextbookShell(
    currentUserId: String,
    displayName: String,
    email: String?,
    isDarkMode: Boolean,
    currentLanguage: AppLanguage,
    onOpenLanguagePicker: () -> Unit,
    onToggleDarkMode: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val mainViewModel: MainViewModel = viewModel(
        key = "main_$currentUserId",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                MainViewModel(
                    repository = TextbookRepository(db),
                    currentUserId = currentUserId,
                    initialDisplayName = displayName,
                    initialEmail = email
                )
            }
        }
    )

    val aiViewModel: AiTeacherViewModel = viewModel(
        key = "ai_$currentUserId",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                AiTeacherViewModel(
                    textbookRepository = TextbookRepository(db),
                    aiRepository = AiTeacherRepository(),
                    currentUserId = currentUserId
                )
            }
        }
    )

    val profileState by mainViewModel.userProfileState.collectAsStateWithLifecycle()
    val textbooksState by mainViewModel.textbooksState.collectAsStateWithLifecycle()
    val activeChapters by mainViewModel.activeBookChapters.collectAsStateWithLifecycle()
    val readingProgressList by mainViewModel.readingProgressList.collectAsStateWithLifecycle()
    val personalNotes by mainViewModel.personalNotes.collectAsStateWithLifecycle()
    val studyTasks by mainViewModel.studyTasks.collectAsStateWithLifecycle()
    val quizAttempts by mainViewModel.quizAttempts.collectAsStateWithLifecycle()
    val studyMaterials by mainViewModel.studyMaterials.collectAsStateWithLifecycle()
    val assessments by mainViewModel.assessments.collectAsStateWithLifecycle()
    val announcements by mainViewModel.announcements.collectAsStateWithLifecycle()
    val savedMaterials by mainViewModel.savedMaterials.collectAsStateWithLifecycle()
    val liveClasses by mainViewModel.liveClasses.collectAsStateWithLifecycle()
    val coAdminSession by mainViewModel.coAdminSession.collectAsStateWithLifecycle()
    val coAdmins by mainViewModel.coAdmins.collectAsStateWithLifecycle()
    val statusBanner by mainViewModel.statusBanner.collectAsStateWithLifecycle()

    val hasAdminAccess = mainViewModel.isMainAdmin || coAdminSession != null

    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showCoAdminLoginDialog by remember { mutableStateOf(false) }

    // Keep AI Teacher language synced when user changes app language
    LaunchedEffect(currentLanguage.englishName) {
        aiViewModel.setLanguage(currentLanguage.englishName)
    }

    LaunchedEffect(statusBanner) {
        if (!statusBanner.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusBanner!!)
            mainViewModel.clearBanner()
        }
    }

    val profile = (profileState as? UiState.Success)?.data
    val textbooks = (textbooksState as? UiState.Success)?.data ?: CurriculumCatalog.starterTextbooks

    // Show onboarding once when profile loads and onboardingDone == false
    if (profileState is UiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (profile != null && !profile.onboardingDone) {
        OnboardingScreen(
            existingProfile = profile,
            isEditingFromProfile = false,
            onOpenLanguagePicker = onOpenLanguagePicker,
            onSave = { name, board, cls, med, session, subjects ->
                mainViewModel.saveAcademicPreferences(name, board, cls, med, session, subjects)
            }
        )
        return
    }

    val topLevelDestinations = remember(strings) {
        listOf(
            NavDestination(Routes.HOME, strings.navHome, Icons.Filled.Home, "nav_home"),
            NavDestination(Routes.LIBRARY, strings.navBooks, Icons.AutoMirrored.Filled.MenuBook, "nav_library"),
            NavDestination(Routes.AI_TEACHER, strings.navAiTutor, Icons.Filled.AutoAwesome, "nav_ai"),
            NavDestination(Routes.PRACTICE, strings.navPractice, Icons.Filled.Quiz, "nav_practice"),
            NavDestination(Routes.PROFILE, strings.navProfile, Icons.Filled.Person, "nav_profile")
        )
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showChrome = currentRoute in topLevelRoutes

    val navigateTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 680.dp && showChrome

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavRail) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                    Spacer(modifier = Modifier.height(12.dp))
                    topLevelDestinations.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateTopLevel(dest.route) },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showChrome && !useNavRail) {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                            topLevelDestinations.forEach { dest ->
                                val selected = currentRoute == dest.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(dest.route) },
                                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                                    label = { Text(dest.label) },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(Routes.HOME) {
                        HomeDashboardScreen(
                            profile = profile,
                            textbooks = textbooks,
                            liveClasses = liveClasses,
                            readingProgressList = readingProgressList,
                            quizAttempts = quizAttempts,
                            announcements = announcements,
                            isDarkMode = isDarkMode,
                            onOpenLanguagePicker = onOpenLanguagePicker,
                            onToggleDarkMode = onToggleDarkMode,
                            onSecretAdminTrigger = {
                                if (hasAdminAccess) {
                                    navController.navigate(Routes.ADMIN_PANEL) {
                                        launchSingleTop = true
                                    }
                                } else {
                                    showCoAdminLoginDialog = true
                                }
                            },
                            onOpenNotifications = { showNotificationsSheet = true },
                            onOpenBook = { bookId ->
                                mainViewModel.selectBookForChapters(bookId)
                                navController.navigate(Routes.bookDetail(bookId))
                            },
                            onContinueReading = { bookId, chapterId ->
                                mainViewModel.selectBookForChapters(bookId)
                                navController.navigate(Routes.reader(bookId, chapterId))
                            },
                            onOpenLibrary = { navigateTopLevel(Routes.LIBRARY) },
                            onOpenAiTeacher = { navigateTopLevel(Routes.AI_TEACHER) },
                            onOpenPractice = { navigateTopLevel(Routes.PRACTICE) },
                            onOpenNotesAndVideos = { tab -> navController.navigate(Routes.notesVideos(tab)) },
                            onOpenPlannerAndProgress = { navController.navigate(Routes.PLANNER_PROGRESS) }
                        )
                    }

                    composable(Routes.LIBRARY) {
                        LibraryScreen(
                            textbooks = textbooks,
                            readingProgressList = readingProgressList,
                            onOpenBook = { bookId ->
                                mainViewModel.selectBookForChapters(bookId)
                                navController.navigate(Routes.bookDetail(bookId))
                            }
                        )
                    }

                    composable(Routes.AI_TEACHER) {
                        AiTeacherScreen(
                            viewModel = aiViewModel,
                            profile = profile,
                            onOpenLanguagePicker = onOpenLanguagePicker
                        )
                    }

                    composable(Routes.PRACTICE) {
                        QuizAndExamScreen(
                            assessments = assessments,
                            attempts = quizAttempts,
                            onRecordAttempt = { assessment, correct, incorrect, skipped, pct, secs, weak ->
                                mainViewModel.submitQuizAttempt(
                                    assessment, correct, incorrect, skipped, pct, secs, weak
                                )
                            }
                        )
                    }

                    composable(Routes.PROFILE) {
                        ProfileAndManagementScreen(
                            profile = profile,
                            personalNotes = personalNotes,
                            savedMaterials = savedMaterials,
                            isDarkMode = isDarkMode,
                            hasAdminAccess = hasAdminAccess,
                            isMainAdmin = mainViewModel.isMainAdmin,
                            onOpenLanguagePicker = onOpenLanguagePicker,
                            onOpenAdminPanel = {
                                navController.navigate(Routes.ADMIN_PANEL) {
                                    launchSingleTop = true
                                }
                            },
                            onToggleDarkMode = onToggleDarkMode,
                            onEditPreferences = { navController.navigate(Routes.EDIT_PREFERENCES) },
                            onOpenPlannerAndProgress = { navController.navigate(Routes.PLANNER_PROGRESS) },
                            onOpenNotifications = { showNotificationsSheet = true },
                            onSelectPlan = { tier -> mainViewModel.selectSubscriptionPlan(tier) },
                            onDeleteNote = { noteId -> mainViewModel.deleteNote(noteId) },
                            onSignOut = {
                                signOutUser(credentialManager, scope)
                            }
                        )
                    }

                    composable(Routes.ADMIN_PANEL) {
                        AdminPanelScreen(
                            isMainAdmin = mainViewModel.isMainAdmin,
                            coAdminSession = coAdminSession,
                            coAdmins = coAdmins,
                            liveClasses = liveClasses,
                            videoLectures = studyMaterials.filter { it.category == "Video Lecture" },
                            textbookCount = textbooks.size,
                            assessmentCount = assessments.size,
                            onBackToUserPanel = { navController.popBackStack() },
                            onLockCoAdminSession = {
                                mainViewModel.lockCoAdminSession {
                                    navController.popBackStack()
                                }
                            },
                            onCreateCoAdmin = { name, pass, done ->
                                mainViewModel.createCoAdmin(name, pass, done)
                            },
                            onDeleteCoAdmin = { id -> mainViewModel.deleteCoAdmin(id) },
                            onPublishLiveClass = { t, s, cls, tch, sch, zoom, mid, pc, liveNow, done ->
                                mainViewModel.publishLiveClass(t, s, cls, tch, sch, zoom, mid, pc, liveNow, done)
                            },
                            onToggleLiveClassStatus = { id, liveNow ->
                                mainViewModel.toggleLiveClassStatus(id, liveNow)
                            },
                            onDeleteLiveClass = { id -> mainViewModel.deleteLiveClass(id) },
                            onPublishStudyMaterial = { t, cat, s, cls, ch, d, b, url, mins, prem, done ->
                                mainViewModel.publishStudyMaterial(t, cat, s, cls, ch, d, b, url, mins, prem, done)
                            },
                            onDeleteStudyMaterial = { id -> mainViewModel.deleteStudyMaterial(id) },
                            onPublishTextbook = { title, subj, cls, cat, brd, med, desc, prem, chT, chB, done ->
                                mainViewModel.publishTextbook(title, subj, cls, cat, brd, med, desc, prem, chT, chB, done)
                            },
                            onPublishAssessment = { t, s, cls, m, dur, p, opts, ci, exp, done ->
                                mainViewModel.publishQuickAssessment(t, s, cls, m, dur, p, opts, ci, exp, done)
                            },
                            onPublishAnnouncement = { t, b, cat, aud, done ->
                                mainViewModel.publishAnnouncement(t, b, cat, aud, done)
                            },
                            onSyncCatalogToCloud = { mainViewModel.syncStarterCurriculumToCloud() }
                        )
                    }

                    composable(
                        route = Routes.BOOK_DETAIL,
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { entry ->
                        val bookId = entry.arguments?.getString("bookId").orEmpty()
                        LaunchedEffect(bookId) { mainViewModel.selectBookForChapters(bookId) }
                        val book = textbooks.firstOrNull { it.id == bookId } ?: textbooks.first()
                        val chapters = activeChapters.ifEmpty {
                            CurriculumCatalog.starterChaptersByBook[book.id].orEmpty()
                        }
                        val bookProgress = readingProgressList.firstOrNull { it.bookId == book.id }
                        val isSaved = savedMaterials.any { it.materialId == book.id }

                        TextbookDetailScreen(
                            book = book,
                            chapters = chapters,
                            progress = bookProgress,
                            isSaved = isSaved,
                            onBack = { navController.popBackStack() },
                            onToggleSaveBook = {
                                mainViewModel.toggleSavedMaterial(book.id, "BOOK", book.title, book.subject)
                            },
                            onOpenChapter = { chapterId ->
                                navController.navigate(Routes.reader(book.id, chapterId))
                            }
                        )
                    }

                    composable(
                        route = Routes.READER,
                        arguments = listOf(
                            navArgument("bookId") { type = NavType.StringType },
                            navArgument("chapterId") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val bookId = entry.arguments?.getString("bookId").orEmpty()
                        val chapterId = entry.arguments?.getString("chapterId").orEmpty()
                        LaunchedEffect(bookId) {
                            mainViewModel.selectBookForChapters(bookId)
                            mainViewModel.recordReadingSessionMinutes(3L)
                        }
                        val book = textbooks.firstOrNull { it.id == bookId } ?: textbooks.first()
                        val chapters = activeChapters.ifEmpty {
                            CurriculumCatalog.starterChaptersByBook[book.id].orEmpty()
                        }
                        val bookProgress = readingProgressList.firstOrNull { it.bookId == book.id }
                        val bookNotes = personalNotes.filter { it.bookId == book.id }

                        TextbookReaderScreen(
                            book = book,
                            chapters = chapters,
                            initialChapterId = chapterId,
                            progress = bookProgress,
                            chapterNotes = bookNotes,
                            onBack = { navController.popBackStack() },
                            onSaveProgress = { ch, pageIdx, completed, bookmarks, highlights ->
                                mainViewModel.saveReadingState(
                                    book = book,
                                    chapter = ch,
                                    pageIndex = pageIdx,
                                    completedChapters = completed,
                                    bookmarkedPages = bookmarks,
                                    highlightedParagraphs = highlights,
                                    totalChaptersInBook = chapters.size.coerceAtLeast(1)
                                )
                            },
                            onAddPersonalNote = { ch, content, excerpt ->
                                mainViewModel.addNote(
                                    bookId = book.id,
                                    bookTitle = book.title,
                                    chapterId = ch.id,
                                    chapterTitle = ch.title,
                                    content = content,
                                    excerpt = excerpt
                                )
                            },
                            onDeleteNote = { noteId -> mainViewModel.deleteNote(noteId) },
                            onExplainParagraph = { subject, paragraph ->
                                aiViewModel.attachParagraphContext(subject, paragraph)
                                navigateTopLevel(Routes.AI_TEACHER)
                            }
                        )
                    }

                    composable(
                        route = Routes.NOTES_VIDEOS,
                        arguments = listOf(navArgument("initialTab") { type = NavType.IntType })
                    ) { entry ->
                        val tab = entry.arguments?.getInt("initialTab") ?: 0
                        StudyMaterialAndVideosScreen(
                            liveClasses = liveClasses,
                            materials = studyMaterials,
                            savedMaterials = savedMaterials,
                            initialTab = tab,
                            onBack = { navController.popBackStack() },
                            onToggleSave = { mat ->
                                val itemType = if (mat.category == "Video Lecture") "VIDEO" else "NOTE"
                                mainViewModel.toggleSavedMaterial(mat.id, itemType, mat.title, mat.subject)
                            },
                            onOpenPractice = { navigateTopLevel(Routes.PRACTICE) }
                        )
                    }

                    composable(Routes.PLANNER_PROGRESS) {
                        StudyPlannerAndProgressScreen(
                            profile = profile,
                            studyTasks = studyTasks,
                            textbooks = textbooks,
                            readingProgressList = readingProgressList,
                            quizAttempts = quizAttempts,
                            onBack = { navController.popBackStack() },
                            onAddTask = { title, subj, due, mins ->
                                mainViewModel.addStudyTask(title, subj, due, mins)
                            },
                            onToggleTask = { id, done -> mainViewModel.toggleStudyTask(id, done) },
                            onDeleteTask = { id -> mainViewModel.deleteStudyTask(id) }
                        )
                    }

                    composable(Routes.EDIT_PREFERENCES) {
                        OnboardingScreen(
                            existingProfile = profile,
                            isEditingFromProfile = true,
                            onOpenLanguagePicker = onOpenLanguagePicker,
                            onSave = { name, board, cls, med, session, subjects ->
                                mainViewModel.saveAcademicPreferences(name, board, cls, med, session, subjects) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Co-Admin Name & Password Login Dialog (triggered by 5 taps on Home Textbook icon)
    if (showCoAdminLoginDialog) {
        CoAdminLoginDialog(
            onDismiss = { showCoAdminLoginDialog = false },
            onSubmit = { name, password, onError ->
                mainViewModel.loginAsCoAdmin(
                    name = name,
                    password = password,
                    onSuccess = {
                        showCoAdminLoginDialog = false
                        navController.navigate(Routes.ADMIN_PANEL) {
                            launchSingleTop = true
                        }
                    },
                    onError = onError
                )
            }
        )
    }

    // Notifications & Announcements Sheet
    if (showNotificationsSheet) {
        ModalBottomSheet(onDismissRequest = { showNotificationsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = AmberAccent)
                    Text(strings.notificationsTitle, style = MaterialTheme.typography.titleLarge)
                }
                announcements.forEach { ann ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = ann.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(
                                        text = ann.targetAudience,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(ann.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(ann.body, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
