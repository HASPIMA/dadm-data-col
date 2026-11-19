package com.edu.unal.haspima.icfes.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.ui.screens.IcfesDetailScreen
import com.edu.unal.haspima.icfes.ui.screens.IcfesHomeScreen
import com.edu.unal.haspima.icfes.ui.viewmodel.IcfesViewModel
import kotlinx.serialization.Serializable

/**
 * Home destination: filters, results list and analytics dashboard (tabs).
 */
@Serializable
data object HomeRoute : NavKey

/**
 * Detail destination for a single ICFES record.
 */
@Serializable
data class DetailRoute(val record: IcfesRecord) : NavKey

/**
 * Opens the detail view for [record], replacing any detail already on top so the
 * back stack never grows into a chain of details.
 */
fun NavBackStack<NavKey>.openDetail(record: IcfesRecord) {
    if (lastOrNull() is DetailRoute) removeAt(lastIndex)
    add(DetailRoute(record))
}

/**
 * Root navigation host. Uses Navigation 3 with a [ListDetailSceneStrategy] so that on
 * expanded windows (tablets, foldables, landscape) the list and detail panes are shown
 * side by side, while compact windows navigate between them as full screens.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun IcfesNavigation(
    viewModel: IcfesViewModel,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeRoute)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
        sceneStrategies = listOf(listDetailStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<HomeRoute>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { DetailPlaceholder() }
                )
            ) {
                IcfesHomeScreen(
                    viewModel = viewModel,
                    onSelectRecord = { record -> backStack.openDetail(record) }
                )
            }
            entry<DetailRoute>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                IcfesDetailScreen(
                    record = route.record,
                    onBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun DetailPlaceholder() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Selecciona un registro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "El detalle completo de puntajes y metadatos aparecerá aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
