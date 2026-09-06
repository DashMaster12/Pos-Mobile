package pe.edu.upeu.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import pe.edu.upeu.presentation.components.PosDropDownMenu
import pe.edu.upeu.presentation.theme.CyanContainer
import pe.edu.upeu.presentation.theme.PrimaryCyan
import pe.edu.upeu.presentation.theme.TextLight

enum class AuthSheetState { LOGIN, REGISTER }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(onLogout: () -> Unit) {
    var currentRoute by remember { mutableStateOf<PosRoute>(PosRoute.POS) }
    var selectedPos by remember { mutableStateOf("POS QSR") }
    val posOptions = listOf("POS QSR", "POS Sede Central", "POS Express")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados de usuario
    var isLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    // 2. ESTADOS DE AUTENTICACIÓN (Van junto a las variables de estado al inicio)
    var showAuthSheet by remember { mutableStateOf(false) }
    var currentAuthSheet by remember { mutableStateOf(AuthSheetState.LOGIN) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                PosDropDownMenu(
                    selectedOption = selectedPos,
                    options = posOptions,
                    onOptionSelected = { selectedPos = it }
                )

                HorizontalDivider()

                val items =
                    listOf(PosRoute.Dashboard, PosRoute.POS, PosRoute.Ordenes, PosRoute.Caja)
                items.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = currentRoute == screen,
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        onClick = {
                            currentRoute = screen
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // SECCIÓN INFERIOR DE USUARIO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryCyan) // 🎨 Fondo verde/azulado principal
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(60.dp),
                            tint = TextLight.copy(alpha = 0.9f) // 🎨 Blanco brillante
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoggedIn) {
                            Text(
                                text = "Hola, $userName",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    isLoggedIn = false
                                    userName = ""
                                }
                            ) {
                                Text(
                                    "Cerrar Sesión",
                                    color = CyanContainer
                                ) // 🎨 Verde/celeste claro
                            }
                        } else {
                            Button(
                                onClick = {
                                    currentAuthSheet = AuthSheetState.LOGIN
                                    showAuthSheet = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanContainer, // 🎨 Fondo claro desplegable
                                    contentColor = PrimaryCyan       // 🎨 Texto e iconos principales
                                )
                            ) {
                                Text("iniciar sesión", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentRoute.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentRoute) {
                    is PosRoute.Dashboard -> DashboardContent()
                    is PosRoute.POS -> PosContent()
                    is PosRoute.Ordenes -> OrdenesContent()
                    is PosRoute.Caja -> CajaContent()
                }
            }
        }
    }


    if (showAuthSheet) {
        Dialog(
            onDismissRequest = { showAuthSheet = false }
        ) {
            // Envolvemos el contenido en un Surface para darle fondo y esquinas redondeadas
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (currentAuthSheet) {
                    AuthSheetState.LOGIN -> {
                        LoginSheetContent(
                            onLoginSuccess = { name ->
                                userName = name
                                isLoggedIn = true
                                showAuthSheet = false
                                scope.launch { drawerState.close() }
                            },
                            onGoToRegister = {
                                currentAuthSheet = AuthSheetState.REGISTER
                            }
                        )
                    }

                    AuthSheetState.REGISTER -> {
                        RegisterSheetContent(
                            onRegisterSuccess = { name ->
                                userName = name
                                isLoggedIn = true
                                showAuthSheet = false
                                scope.launch { drawerState.close() }
                            },
                            onGoToLogin = {
                                currentAuthSheet = AuthSheetState.LOGIN
                            }
                        )
                    }
                }
            }
        }
    }
}