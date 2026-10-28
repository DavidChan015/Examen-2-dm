package com.example.geotracker.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.geotracker.data.model.LocationItem
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Default to Guatemala City or first location position
    val defaultTarget = if (state.locations.isNotEmpty()) {
        LatLng(state.locations.first().latitude, state.locations.first().longitude)
    } else {
        LatLng(14.634915, -90.506882)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultTarget, 12f)
    }

    // Update camera position when locations are loaded
    LaunchedEffect(state.locations) {
        if (state.locations.isNotEmpty()) {
            val firstLoc = state.locations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstLoc.latitude, firstLoc.longitude),
                13f
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLocations() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true
                )
            ) {
                state.locations.forEach { location ->
                    Marker(
                        state = rememberMarkerState(
                            position = LatLng(location.latitude, location.longitude)
                        ),
                        title = location.name,
                        snippet = location.description,
                        onClick = {
                            // Suppress default Google Maps InfoWindow by returning true,
                            // and trigger Material 3 AlertDialog in ViewModel state.
                            viewModel.selectLocation(location)
                            true
                        }
                    )
                }
            }

            if (state.isLoading) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cargando marcadores...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (state.locations.isEmpty() && state.errorMessage == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "No hay ubicaciones registradas para mostrar en el mapa.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            if (!state.errorMessage.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                    shadowElevation = 6.dp
                ) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Material 3 AlertDialog shown when a marker is tapped
            state.selectedLocation?.let { selected ->
                LocationDetailDialog(
                    location = selected,
                    onDismiss = { viewModel.dismissDialog() }
                )
            }
        }
    }
}

@Composable
fun LocationDetailDialog(
    location: LocationItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Column {
                    Text(
                        text = "Coordenadas GPS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Latitud: ${location.latitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Longitud: ${location.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!location.createdAt.isNullOrEmpty()) {
                    Column {
                        Text(
                            text = "Fecha de Registro",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = location.createdAt,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
