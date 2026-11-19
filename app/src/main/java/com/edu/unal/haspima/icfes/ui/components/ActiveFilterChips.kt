package com.edu.unal.haspima.icfes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@Composable
fun ActiveFilterChips(
    filter: IcfesFilter,
    onClearSearchText: () -> Unit,
    onClearDepartment: () -> Unit,
    onClearMunicipality: () -> Unit,
    onClearPeriod: () -> Unit,
    onClearGender: () -> Unit,
    onClearScoreRange: () -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = !filter.searchKeyword.isNullOrBlank() ||
            !filter.coleDeptoUbicacion.isNullOrBlank() ||
            !filter.coleMuniUbicacion.isNullOrBlank() ||
            !filter.periodo.isNullOrBlank() ||
            !filter.estuGenero.isNullOrBlank() ||
            filter.minPuntGlobal != null ||
            filter.maxPuntGlobal != null

    if (!hasActiveFilters) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clear All Chip
        item {
            SuggestionChip(
                onClick = onResetAll,
                label = {
                    Text(
                        text = "Limpiar Todo",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Limpiar filtros",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        // Search Keyword Chip
        filter.searchKeyword?.takeIf { it.isNotBlank() }?.let { kw ->
            item {
                ActiveChip(
                    label = "Búsqueda: \"$kw\"",
                    onClear = onClearSearchText
                )
            }
        }

        // Department Chip
        filter.coleDeptoUbicacion?.takeIf { it.isNotBlank() }?.let { depto ->
            item {
                ActiveChip(
                    label = "Depto: $depto",
                    onClear = onClearDepartment
                )
            }
        }

        // Municipality Chip
        filter.coleMuniUbicacion?.takeIf { it.isNotBlank() }?.let { muni ->
            item {
                ActiveChip(
                    label = "Muni: $muni",
                    onClear = onClearMunicipality
                )
            }
        }

        // Period Chip
        filter.periodo?.takeIf { it.isNotBlank() }?.let { per ->
            item {
                ActiveChip(
                    label = "Periodo: $per",
                    onClear = onClearPeriod
                )
            }
        }

        // Gender Chip
        filter.estuGenero?.takeIf { it.isNotBlank() }?.let { gen ->
            item {
                ActiveChip(
                    label = "Género: ${if (gen.equals("M", ignoreCase = true)) "Masculino" else "Femenino"}",
                    onClear = onClearGender
                )
            }
        }

        // Score Range Chip
        if (filter.minPuntGlobal != null || filter.maxPuntGlobal != null) {
            val minText = filter.minPuntGlobal?.toString() ?: "0"
            val maxText = filter.maxPuntGlobal?.toString() ?: "500"
            item {
                ActiveChip(
                    label = "Puntaje: $minText - $maxText",
                    onClear = onClearScoreRange
                )
            }
        }
    }
}

@Composable
private fun ActiveChip(
    label: String,
    onClear: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = onClear,
        label = { Text(text = label, style = MaterialTheme.typography.bodySmall) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Remover filtro $label",
                modifier = Modifier
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ActiveFilterChipsPreview() {
    ICFESResultsTheme {
        ActiveFilterChips(
            filter = IcfesFilter(
                searchKeyword = "Colegio Nacional",
                coleDeptoUbicacion = "BOGOTA",
                periodo = "20204",
                minPuntGlobal = 250,
                maxPuntGlobal = 450
            ),
            onClearSearchText = {},
            onClearDepartment = {},
            onClearMunicipality = {},
            onClearPeriod = {},
            onClearGender = {},
            onClearScoreRange = {},
            onResetAll = {},
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
