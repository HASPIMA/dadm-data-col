package com.edu.unal.haspima.icfes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterControls(
    pendingFilter: IcfesFilter,
    availableDepartments: List<String>,
    availablePeriods: List<String>,
    isExpanded: Boolean,
    activeFilterCount: Int,
    onToggleExpanded: () -> Unit,
    onUpdateSearchText: (String?) -> Unit,
    onUpdateDepartment: (String?) -> Unit,
    onUpdateMunicipality: (String?) -> Unit,
    onUpdatePeriod: (String?) -> Unit,
    onUpdateGender: (String?) -> Unit,
    onUpdateScoreRange: (min: Int?, max: Int?) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Filter Title, Active Count Badge, Expand/Collapse Icon
            Surface(
                onClick = onToggleExpanded,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Filtros",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Filtros de Búsqueda",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (activeFilterCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "$activeFilterCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Colapsar filtros" else "Expandir filtros",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Filter Form Body
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Search TextField
                    OutlinedTextField(
                        value = pendingFilter.searchKeyword ?: "",
                        onValueChange = { text ->
                            onUpdateSearchText(text.takeIf { it.isNotBlank() })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Buscar Colegio o Municipio") },
                        placeholder = { Text("Ej: San José, Bogotá, etc.") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        trailingIcon = {
                            if (!pendingFilter.searchKeyword.isNullOrEmpty()) {
                                IconButton(onClick = { onUpdateSearchText(null) }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Borrar búsqueda"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 2. Department Selector & Period Dropdown in Row/FlowRow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Department Dropdown
                        DepartmentDropdown(
                            selectedDepartment = pendingFilter.coleDeptoUbicacion,
                            departments = availableDepartments,
                            onDepartmentSelected = onUpdateDepartment,
                            modifier = Modifier.weight(1f)
                        )

                        // Period Dropdown
                        PeriodDropdown(
                            selectedPeriod = pendingFilter.periodo,
                            periods = availablePeriods,
                            onPeriodSelected = onUpdatePeriod,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 3. Municipality Selector & Gender Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pendingFilter.coleMuniUbicacion ?: "",
                            onValueChange = { text ->
                                onUpdateMunicipality(text.takeIf { it.isNotBlank() })
                            },
                            modifier = Modifier.weight(1.2f),
                            label = { Text("Municipio") },
                            placeholder = { Text("Ej: Medellín") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Gender Filter Chips
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Género Estudiante",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = pendingFilter.estuGenero == null,
                                    onClick = { onUpdateGender(null) },
                                    label = { Text("Todos", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = pendingFilter.estuGenero == "M",
                                    onClick = { onUpdateGender(if (pendingFilter.estuGenero == "M") null else "M") },
                                    label = { Text("M", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = pendingFilter.estuGenero == "F",
                                    onClick = { onUpdateGender(if (pendingFilter.estuGenero == "F") null else "F") },
                                    label = { Text("F", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }

                    // 4. Min/Max Score Range Slider
                    ScoreRangeSliderSection(
                        minScore = pendingFilter.minPuntGlobal,
                        maxScore = pendingFilter.maxPuntGlobal,
                        onScoreRangeChanged = onUpdateScoreRange
                    )

                    // 5. Action Buttons (Reset & Apply)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onResetFilters,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restablecer")
                        }

                        Button(
                            onClick = onApplyFilters,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aplicar Filtros")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepartmentDropdown(
    selectedDepartment: String?,
    departments: List<String>,
    onDepartmentSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDepartment ?: "Todos",
            onValueChange = {},
            readOnly = true,
            label = { Text("Departamento") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos los departamentos", fontWeight = FontWeight.Bold) },
                onClick = {
                    onDepartmentSelected(null)
                    expanded = false
                }
            )
            departments.forEach { dept ->
                DropdownMenuItem(
                    text = { Text(dept) },
                    onClick = {
                        onDepartmentSelected(dept)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodDropdown(
    selectedPeriod: String?,
    periods: List<String>,
    onPeriodSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedPeriod ?: "Todos",
            onValueChange = {},
            readOnly = true,
            label = { Text("Periodo ICFES") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos los periodos", fontWeight = FontWeight.Bold) },
                onClick = {
                    onPeriodSelected(null)
                    expanded = false
                }
            )
            periods.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ScoreRangeSliderSection(
    minScore: Int?,
    maxScore: Int?,
    onScoreRangeChanged: (min: Int?, max: Int?) -> Unit
) {
    val currentMin = (minScore ?: 0).toFloat()
    val currentMax = (maxScore ?: 500).toFloat()
    var sliderRange by remember(minScore, maxScore) {
        mutableStateOf(currentMin..currentMax)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rango Puntaje Global",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${sliderRange.start.toInt()} - ${sliderRange.endInclusive.toInt()} / 500",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        RangeSlider(
            value = sliderRange,
            onValueChange = { range ->
                sliderRange = range
            },
            onValueChangeFinished = {
                val newMin = sliderRange.start.toInt().takeIf { it > 0 }
                val newMax = sliderRange.endInclusive.toInt().takeIf { it < 500 }
                onScoreRangeChanged(newMin, newMax)
            },
            valueRange = 0f..500f,
            steps = 49,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterControlsPreview() {
    ICFESResultsTheme {
        FilterControls(
            pendingFilter = IcfesFilter(
                searchKeyword = "Nacional",
                coleDeptoUbicacion = "BOGOTA",
                periodo = "20204",
                minPuntGlobal = 200,
                maxPuntGlobal = 450
            ),
            availableDepartments = listOf("BOGOTA", "ANTIOQUIA", "VALLE"),
            availablePeriods = listOf("20204", "20214"),
            isExpanded = true,
            activeFilterCount = 3,
            onToggleExpanded = {},
            onUpdateSearchText = {},
            onUpdateDepartment = {},
            onUpdateMunicipality = {},
            onUpdatePeriod = {},
            onUpdateGender = {},
            onUpdateScoreRange = { _, _ -> },
            onApplyFilters = {},
            onResetFilters = {}
        )
    }
}
