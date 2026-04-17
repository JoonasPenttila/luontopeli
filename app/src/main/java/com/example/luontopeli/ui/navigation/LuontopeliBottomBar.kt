package com.example.luontopeli.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun LuontopeliBottomBar(navController: NavController) {
    // Seurataan nykyistä navigointireittiä back stackista
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // Luodaan NavigationBarItem jokaiselle näkymälle (Map, Camera, Discover, Stats)
        Screen.bottomNavScreens.forEach { screen ->
            NavigationBarItem(
                // Korostetaan nykyisen sivun välilehti
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Palataan aina aloitusnäkymään asti back stackissa
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Estetään saman näkymän avaaminen uudelleen
                        launchSingleTop = true
                        // Palautetaan tallennettu tila palatessa
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) }
            )
        }
    }
}