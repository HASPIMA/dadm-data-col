package com.edu.unal.haspima.icfes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.ui.components.ActiveFilterChips
import com.edu.unal.haspima.icfes.ui.components.FilterControls
import com.edu.unal.haspima.icfes.ui.components.IcfesRecordCard
import com.edu.unal.haspima.icfes.ui.components.PaginationFooter
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme
import com.edu.unal.haspima.icfes.ui.viewmodel.IcfesUiState
import com.edu.unal.haspima.icfes.ui.viewmodel.IcfesViewModel
import com.edu.unal.haspima.icfes.ui.viewmodel.MainTab

@Composable
fun IcfesHomeScreen(
    viewModel: IcfesViewModel,
    onSelectRecord: (IcfesRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IcfesHomeScreenContent(
        uiState = uiState,
        onSelectTab = viewModel::selectTab,
        onSelectRecord = onSelectRecord,
        onToggleFilterExpanded = viewModel::toggleFilterPanelExpanded,
        onUpdateSearchText = viewModel::updateSearchText,
        onUpdateDepartment = viewModel::updateDepartment,
        onUpdateMunicipality = viewModel::updateMunicipality,
        onUpdatePeriod = viewModel::updatePeriod,
        onUpdateGender = viewModel::updateGender,
        onUpdateScoreRange = viewModel::updateScoreRange,
        onApplyFilters = viewModel::applyFilters,
        onResetFilters = viewModel::resetFilters,
        onClearSearchText = viewModel::clearSearchText,
        onClearDepartment = viewModel::clearDepartment,
        onClearMunicipality = viewModel::clearMunicipality,
        onClearPeriod = viewModel::clearPeriod,
        onClearGender = viewModel::clearGender,
        onClearScoreRange = viewModel::clearScoreRange,
        onPreviousPage = viewModel::previousPage,
        onNextPage = viewModel::nextPage,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcfesHomeScreenContent(
    uiState: IcfesUiState,
    onSelectTab: (MainTab) -> Unit,
    onSelectRecord: (IcfesRecord) -> Unit,
    onToggleFilterExpanded: () -> Unit,
    onUpdateSearchText: (String?) -> Unit,
    onUpdateDepartment: (String?) -> Unit,
    onUpdateMunicipality: (String?) -> Unit,
    onUpdatePeriod: (String?) -> Unit,
    onUpdateGender: (String?) -> Unit,
    onUpdateScoreRange: (min: Int?, max: Int?) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit,
    onClearSearchText: () -> Unit,
    onClearDepartment: () -> Unit,
    onClearMunicipality: () -> Unit,
    onClearPeriod: () -> Unit,
    onClearGender: () -> Unit,
    onClearScoreRange: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Resultados ICFES Saber 11",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Datos Abiertos Colombia (SODA2)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )

                // Tab View Switcher (Resultados vs Estadísticas / Analítica)
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Tab(
                        selected = uiState.selectedTab == MainTab.RESULTS,
                        onClick = { onSelectTab(MainTab.RESULTS) },
                        text = {
                            Text(
                                text = "Resultados",
                                fontWeight = if (uiState.selectedTab == MainTab.RESULTS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.List,
                                contentDescription = "Lista de Resultados"
                            )
                        }
                    )
                    Tab(
                        selected = uiState.selectedTab == MainTab.ANALYTICS,
                        onClick = { onSelectTab(MainTab.ANALYTICS) },
                        text = {
                            Text(
                                text = "Estadísticas / Analítica",
                                fontWeight = if (uiState.selectedTab == MainTab.ANALYTICS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.BarChart,
                                contentDescription = "Dashboard de Analítica"
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState.selectedTab) {
            MainTab.RESULTS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Filter Controls Section
                    item {
                        FilterControls(
                            pendingFilter = uiState.pendingFilter,
                            availableDepartments = uiState.availableDepartments,
                            availablePeriods = uiState.availablePeriods,
                            isExpanded = uiState.isFilterPanelExpanded,
                            activeFilterCount = uiState.activeFilterCount,
                            onToggleExpanded = onToggleFilterExpanded,
                            onUpdateSearchText = onUpdateSearchText,
                            onUpdateDepartment = onUpdateDepartment,
                            onUpdateMunicipality = onUpdateMunicipality,
                            onUpdatePeriod = onUpdatePeriod,
                            onUpdateGender = onUpdateGender,
                            onUpdateScoreRange = onUpdateScoreRange,
                            onApplyFilters = onApplyFilters,
                            onResetFilters = onResetFilters
                        )
                    }

                    // 2. Active Filter Chips Row
                    item {
                        ActiveFilterChips(
                            filter = uiState.filter,
                            onClearSearchText = onClearSearchText,
                            onClearDepartment = onClearDepartment,
                            onClearMunicipality = onClearMunicipality,
                            onClearPeriod = onClearPeriod,
                            onClearGender = onClearGender,
                            onClearScoreRange = onClearScoreRange,
                            onResetAll = onResetFilters
                        )
                    }

                    // 3. Results Section (Loading, Error, Empty, or List of Cards)
                    when {
                        uiState.isLoading -> {
                            item {
                                LoadingStateView()
                            }
                        }
                        uiState.errorMessage != null -> {
                            item {
                                ErrorStateView(
                                    errorMessage = uiState.errorMessage,
                                    onRetry = onRetry
                                )
                            }
                        }
                        uiState.records.isEmpty() -> {
                            item {
                                EmptyStateView(onResetFilters = onResetFilters)
                            }
                        }
                        else -> {
                            // The dataset contains duplicated rows, so the position keeps keys unique.
                            itemsIndexed(
                                items = uiState.records,
                                key = { index, record -> "${index}_${record.estuConsecutivo}" }
                            ) { _, record ->
                                IcfesRecordCard(
                                    record = record,
                                    onClick = { onSelectRecord(record) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }

                            // 4. Pagination Footer
                            item {
                                PaginationFooter(
                                    currentPage = uiState.currentPage,
                                    pageSize = uiState.pageSize,
                                    currentRecordsCount = uiState.records.size,
                                    isLoading = uiState.isLoading,
                                    onPreviousPage = onPreviousPage,
                                    onNextPage = onNextPage,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            MainTab.ANALYTICS -> {
                IcfesAnalyticsScreen(
                    summary = uiState.analyticsSummary,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onRefresh = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LoadingStateView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Consultando datos de ICFES Saber 11...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Conectando con el API SODA2 de datos.gov.co",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = "Error de red",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Error al obtener datos de ICFES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    onResetFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.FolderOff,
                contentDescription = "Sin resultados",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sin Resultados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "No se encontraron registros de ICFES que coincidan con los filtros seleccionados.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onResetFilters,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restablecer Filtros")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IcfesHomeScreenPreview() {
    ICFESResultsTheme {
        IcfesHomeScreenContent(
            uiState = IcfesUiState(
                isLoading = false,
                records = listOf(
                    IcfesRecord(
                        periodo = "20204",
                        coleNombreEstablecimiento = "COLEGIO NACIONAL LORENZO DE ALDANA",
                        coleDeptoUbicacion = "NARIÑO",
                        coleMuniUbicacion = "PASTO",
                        puntGlobalRaw = "345",
                        puntLecturaCriticaRaw = "70",
                        puntMatematicasRaw = "74"
                    )
                ),
                currentPage = 1,
                pageSize = 20,
                availableDepartments = listOf("BOGOTA", "NARIÑO"),
                availablePeriods = listOf("20204", "20214")
            ),
            onSelectTab = {},
            onSelectRecord = {},
            onToggleFilterExpanded = {},
            onUpdateSearchText = {},
            onUpdateDepartment = {},
            onUpdateMunicipality = {},
            onUpdatePeriod = {},
            onUpdateGender = {},
            onUpdateScoreRange = { _, _ -> },
            onApplyFilters = {},
            onResetFilters = {},
            onClearSearchText = {},
            onClearDepartment = {},
            onClearMunicipality = {},
            onClearPeriod = {},
            onClearGender = {},
            onClearScoreRange = {},
            onPreviousPage = {},
            onNextPage = {},
            onRetry = {}
        )
    }
}
