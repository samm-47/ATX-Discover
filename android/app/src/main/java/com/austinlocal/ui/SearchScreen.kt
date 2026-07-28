package com.austinlocal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.austinlocal.model.Place
import com.austinlocal.ui.theme.AustinGreen
import com.austinlocal.ui.theme.AustinRed
import com.austinlocal.ui.theme.AustinYellow
import com.austinlocal.viewmodel.SearchViewModel
import java.util.Locale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.text.input.KeyboardType

private val categories = listOf("coffee", "live_music", "food_truck", "park", "bar")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onPlaceClick: (Place) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var locationSearchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.search() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ATX DISCOVER", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            
            // Zipcode Search Bar
            OutlinedTextField(
                value = locationSearchQuery,
                onValueChange = { 
                    if (it.length <= 5) {
                        locationSearchQuery = it
                        if (it.length == 5) {
                            viewModel.updateLocation(it, context)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Enter zipcode (e.g. 78701)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (locationSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            locationSearchQuery = "" 
                            viewModel.resetLocation()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = {
                    if (locationSearchQuery.length == 5) {
                        viewModel.updateLocation(locationSearchQuery, context)
                    }
                }),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Text(
                text = "Showing results near: ${viewModel.currentLocationName}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CategoryFilterRow(
                selected = selectedCategory,
                onSelect = { category ->
                    selectedCategory = category
                    viewModel.search(category)
                }
            )

            when {
                viewModel.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                viewModel.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(viewModel.errorMessage ?: "Something went wrong")
                    }
                }
                else -> {
                    if (viewModel.places.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No places found right here",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Try a different zipcode or expand your search.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(24.dp))
                                Button(onClick = { 
                                    locationSearchQuery = ""
                                    viewModel.resetLocation() 
                                }) {
                                    Text("Show all of ATX")
                                }
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                            items(viewModel.places) { place ->
                                PlaceRow(place = place, onClick = { onPlaceClick(place) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(selected: String?, onSelect: (String?) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("All") },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null, Modifier.size(18.dp)) }
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) },
                leadingIcon = {
                    val icon = when (category) {
                        "coffee" -> Icons.Default.Coffee
                        "live_music" -> Icons.Default.MusicNote
                        "food_truck" -> Icons.Default.LocalShipping
                        "park" -> Icons.Default.Park
                        "bar" -> Icons.Default.LocalBar
                        else -> Icons.Default.Search
                    }
                    Icon(icon, contentDescription = null, Modifier.size(18.dp))
                }
            )
        }
    }
}

@Composable
private fun PlaceRow(place: Place, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(place.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(place.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Spacer(Modifier.height(8.dp))
                Text(
                    "${place.category.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} · %.1f km away".format(place.distanceKm),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AustinYellow, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${place.rating}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = if (place.currentlyOpen) AustinGreen.copy(alpha = 0.1f) else AustinRed.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (place.currentlyOpen) "OPEN" else "CLOSED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (place.currentlyOpen) AustinGreen else AustinRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
