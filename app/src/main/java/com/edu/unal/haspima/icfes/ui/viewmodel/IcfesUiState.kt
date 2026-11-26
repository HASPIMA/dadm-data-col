package com.edu.unal.haspima.icfes.ui.viewmodel

import com.edu.unal.haspima.icfes.data.model.AnalyticsSummary
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord

/**
 * Main navigation tab options.
 */
enum class MainTab {
    RESULTS,
    ANALYTICS
}

/**
 * UI State for the ICFES Saber 11 results and analytics screens.
 */
data class IcfesUiState(
    val isLoading: Boolean = false,
    val records: List<IcfesRecord> = emptyList(),
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val filter: IcfesFilter = IcfesFilter(limit = 20, offset = 0),
    val pendingFilter: IcfesFilter = IcfesFilter(limit = 20, offset = 0),
    val errorMessage: String? = null,
    val availableDepartments: List<String> = emptyList(),
    val availablePeriods: List<String> = emptyList(),
    val availableGenders: List<String> = listOf("M", "F"),
    val isLoadingFilters: Boolean = false,
    val isFilterPanelExpanded: Boolean = true,
    val selectedTab: MainTab = MainTab.RESULTS
) {
    val analyticsSummary: AnalyticsSummary
        get() = AnalyticsSummary.fromRecords(records)

    val totalRecordsCountInPage: Int
        get() = records.size

    val hasActiveFilters: Boolean
        get() = !filter.periodo.isNullOrBlank() ||
                !filter.coleDeptoUbicacion.isNullOrBlank() ||
                !filter.coleMuniUbicacion.isNullOrBlank() ||
                !filter.estuGenero.isNullOrBlank() ||
                filter.minPuntGlobal != null ||
                filter.maxPuntGlobal != null ||
                !filter.searchKeyword.isNullOrBlank()

    val activeFilterCount: Int
        get() {
            var count = 0
            if (!filter.periodo.isNullOrBlank()) count++
            if (!filter.coleDeptoUbicacion.isNullOrBlank()) count++
            if (!filter.coleMuniUbicacion.isNullOrBlank()) count++
            if (!filter.estuGenero.isNullOrBlank()) count++
            if (filter.minPuntGlobal != null || filter.maxPuntGlobal != null) count++
            if (!filter.searchKeyword.isNullOrBlank()) count++
            return count
        }
}
