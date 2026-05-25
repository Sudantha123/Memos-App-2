package com.memos.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.memos.app.ui.screens.auth.LoginScreen
import com.memos.app.ui.screens.home.HomeScreen
import com.memos.app.ui.screens.memo.CreateMemoScreen
import com.memos.app.ui.screens.memo.MemoDetailScreen
import com.memos.app.ui.screens.settings.ServerSetupScreen
import com.memos.app.utils.PreferenceManager

sealed class Screen(val route: String) {
    object ServerSetup : Screen("server_setup")
    object Login       : Screen("login")
    object Home        : Screen("home")
    object CreateMemo  : Screen("create_memo?name={name}") {
        fun route(name: String? = null) =
            if (name != null) "create_memo?name=$name" else "create_memo"
    }
    object MemoDetail  : Screen("detail/{name}") {
        fun route(name: String) = "detail/$name"
    }
}

@Composable
fun AppNavigation(preferenceManager: PreferenceManager) {

    val nav = rememberNavController()

    val start = when {
        preferenceManager.getServerUrl().isNullOrEmpty() -> Screen.ServerSetup.route
        !preferenceManager.isLoggedIn()                  -> Screen.Login.route
        else                                             -> Screen.Home.route
    }

    val enter  = slideInHorizontally(tween(280)) { it / 3 } + fadeIn(tween(280))
    val exit   = slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut(tween(280))
    val pEnter = slideInHorizontally(tween(280)) { -it / 3 } + fadeIn(tween(280))
    val pExit  = slideOutHorizontally(tween(280)) { it / 3 } + fadeOut(tween(280))

    NavHost(
        navController        = nav,
        startDestination     = start,
        enterTransition      = { enter },
        exitTransition       = { exit },
        popEnterTransition   = { pEnter },
        popExitTransition    = { pExit }
    ) {

        composable(Screen.ServerSetup.route) {
            ServerSetupScreen(
                onSetupComplete = {
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.ServerSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onServerChange = {
                    nav.navigate(Screen.ServerSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCreateMemo = { nav.navigate(Screen.CreateMemo.route()) },
                onMemoClick  = { n -> nav.navigate(Screen.MemoDetail.route(n)) },
                onEditMemo   = { n -> nav.navigate(Screen.CreateMemo.route(n)) },
                onLogout     = {
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Screen.CreateMemo.route,
            arguments = listOf(navArgument("name") {
                nullable     = true
                defaultValue = null
            })
        ) { back ->
            val name = back.arguments?.getString("name")
            CreateMemoScreen(
                memoName = name,
                onBack   = { nav.popBackStack() },
                onSaved  = { nav.popBackStack() }
            )
        }

        composable(
            route     = Screen.MemoDetail.route,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { back ->
            val name = back.arguments?.getString("name") ?: ""
            MemoDetailScreen(
                memoName = name,
                onBack   = { nav.popBackStack() },
                onEdit   = { n -> nav.navigate(Screen.CreateMemo.route(n)) }
            )
        }
    }
}

