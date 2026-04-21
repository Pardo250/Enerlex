package com.example.enerlex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.LocalLaundryService
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
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.DeviceIcon
import com.example.enerlex.ui.theme.*

@Composable
fun DeviceCard(
    device: Device,
    onToggle: (String) -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = when {
        !device.isOn           -> EnerDeviceGray
        device.currentWatts > 300 -> EnerDeviceYellow
        else                   -> EnerDeviceGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick(device.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = device.iconType.toIcon(),
                contentDescription = device.name,
                tint = if (device.isOn) EnerGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (device.isOn) "${device.currentWatts}W" else "0W",
                        color = if (device.isOn) EnerGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "· ${device.todayKwh} kWh hoy",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Switch(
                checked = device.isOn,
                onCheckedChange = { onToggle(device.id) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EnerGreen,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

fun DeviceIcon.toIcon(): ImageVector = when (this) {
    DeviceIcon.TV           -> Icons.Filled.Tv
    DeviceIcon.REFRIGERATOR -> Icons.Filled.Kitchen
    DeviceIcon.COMPUTER     -> Icons.Filled.Computer
    DeviceIcon.LAMP         -> Icons.Filled.LightbulbCircle
    DeviceIcon.MICROWAVE    -> Icons.Filled.Microwave
    DeviceIcon.FAN          -> Icons.Filled.AcUnit
    DeviceIcon.WASHER       -> Icons.Outlined.LocalLaundryService
    DeviceIcon.AC           -> Icons.Filled.AcUnit
    DeviceIcon.OTHER        -> Icons.Outlined.DevicesOther
}
