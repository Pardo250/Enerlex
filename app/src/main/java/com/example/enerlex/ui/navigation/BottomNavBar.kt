package com.example.enerlex.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.ui.theme.*

private data class NavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    NavItem(Screen.Dashboard, "Inicio",       Icons.Filled.Home,          Icons.Outlined.Home),
    NavItem(Screen.Devices,   "Dispositivos", Icons.Filled.Devices,       Icons.Outlined.Devices),
    NavItem(Screen.Alerts,    "Alertas",      Icons.Filled.Notifications, Icons.Outlined.Notifications),
    NavItem(Screen.Settings,  "Config",       Icons.Filled.Settings,      Icons.Outlined.Settings)
)

/**
 * Barra de navegación inferior con diseño oscuro que sigue la paleta EnerFlow.
 */
@Composable
fun EnerBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(EnerSurface)
    ) {
        // Línea superior sutil
        Divider(
            color = EnerDivider,
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                EnerNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.screen.route) }
                )
            }
        }
    }
}

@Composable
private fun EnerNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = if (isSelected) EnerGreen else EnerTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) EnerGreen else EnerTextSecondary
        )
    }
}
