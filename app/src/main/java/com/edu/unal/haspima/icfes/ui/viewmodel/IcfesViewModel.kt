package com.edu.unal.haspima.icfes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.unal.haspima.icfes.data.model.IcfesCatalog
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.data.repository.IcfesRepository
import com.edu.unal.haspima.icfes.data.repository.IcfesRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing ICFES Saber 11 filter state and dataset queries.
 */
class IcfesViewModel(
    private val repository: IcfesRepository = IcfesRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(IcfesUiState())
    val uiState: StateFlow<IcfesUiState> = _uiState.asStateFlow()

    init {
        loadFilterChoices()
        applyFilters()
    }

    /**
     * Loads distinct values for filter choices (departments, periods).
     */
    fun loadFilterChoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFilters = true) }

            val deptsResult = repository.getDistinctDepartments()
            val periodsResult = repository.getDistinctPeriods()

            val depts = deptsResult.getOrDefault(DEFAULT_DEPARTMENTS)
            val periods = periodsResult.getOrDefault(DEFAULT_PERIODS)

            _uiState.update { state ->
                state.copy(
                    isLoadingFilters = false,
                    availableDepartments = if (depts.isNotEmpty()) depts else DEFAULT_DEPARTMENTS,
                    availablePeriods = if (periods.isNotEmpty()) periods else DEFAULT_PERIODS
                )
            }
        }
    }

    /**
     * Updates selected navigation tab (Results vs Analytics).
     */
    fun selectTab(tab: MainTab) {
        _uiState.update { state ->
            state.copy(selectedTab = tab)
        }
    }

    /**
     * Updates pending department filter value.
     */
    fun updateDepartment(department: String?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    coleDeptoUbicacion = department?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Updates pending municipality filter value.
     */
    fun updateMunicipality(municipality: String?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    coleMuniUbicacion = municipality?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Updates pending period filter value.
     */
    fun updatePeriod(period: String?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    periodo = period?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Updates pending gender filter value.
     */
    fun updateGender(gender: String?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    estuGenero = gender?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Updates pending global score range filter values.
     */
    fun updateScoreRange(minScore: Int?, maxScore: Int?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    minPuntGlobal = minScore,
                    maxPuntGlobal = maxScore
                )
            )
        }
    }

    /**
     * Updates pending search keyword text.
     */
    fun updateSearchText(searchText: String?) {
        _uiState.update { state ->
            state.copy(
                pendingFilter = state.pendingFilter.copy(
                    searchKeyword = searchText?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Updates page size.
     */
    fun updatePageSize(requestedPageSize: Int) {
        if (requestedPageSize <= 0) return
        val pageSize = requestedPageSize.coerceAtMost(IcfesFilter.MAX_LIMIT)
        _uiState.update { state ->
            state.copy(
                pageSize = pageSize,
                filter = state.filter.copy(limit = pageSize, offset = 0),
                pendingFilter = state.pendingFilter.copy(limit = pageSize, offset = 0),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Toggles filter panel expansion state in UI.
     */
    fun toggleFilterPanelExpanded() {
        _uiState.update { state ->
            state.copy(isFilterPanelExpanded = !state.isFilterPanelExpanded)
        }
    }

    /**
     * Applies pending filter selections and executes data query starting from page 1.
     */
    fun applyFilters() {
        _uiState.update { state ->
            val newFilter = state.pendingFilter.copy(
                limit = state.pageSize,
                offset = 0
            )
            state.copy(
                filter = newFilter,
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Resets all filters to default state and executes query.
     */
    fun resetFilters() {
        val defaultFilter = IcfesFilter(limit = _uiState.value.pageSize, offset = 0)
        _uiState.update { state ->
            state.copy(
                filter = defaultFilter,
                pendingFilter = defaultFilter,
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears department active filter and re-queries.
     */
    fun clearDepartment() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(coleDeptoUbicacion = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(coleDeptoUbicacion = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears municipality active filter and re-queries.
     */
    fun clearMunicipality() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(coleMuniUbicacion = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(coleMuniUbicacion = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears period active filter and re-queries.
     */
    fun clearPeriod() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(periodo = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(periodo = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears gender active filter and re-queries.
     */
    fun clearGender() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(estuGenero = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(estuGenero = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears score range active filter and re-queries.
     */
    fun clearScoreRange() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(minPuntGlobal = null, maxPuntGlobal = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(minPuntGlobal = null, maxPuntGlobal = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Clears search keyword active filter and re-queries.
     */
    fun clearSearchText() {
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(searchKeyword = null, offset = 0)
            state.copy(
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(searchKeyword = null),
                currentPage = 1
            )
        }
        executeRecordsQuery()
    }

    /**
     * Navigates to next page.
     */
    fun nextPage() {
        val currentState = _uiState.value
        val nextPage = currentState.currentPage + 1
        val nextOffset = (nextPage - 1) * currentState.pageSize
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(offset = nextOffset)
            state.copy(
                currentPage = nextPage,
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(offset = nextOffset)
            )
        }
        executeRecordsQuery()
    }

    /**
     * Navigates to previous page.
     */
    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage <= 1) return
        val prevPage = currentState.currentPage - 1
        val prevOffset = (prevPage - 1) * currentState.pageSize
        _uiState.update { state ->
            val updatedFilter = state.filter.copy(offset = prevOffset)
            state.copy(
                currentPage = prevPage,
                filter = updatedFilter,
                pendingFilter = state.pendingFilter.copy(offset = prevOffset)
            )
        }
        executeRecordsQuery()
    }

    /**
     * Retries current query execution.
     */
    fun retry() {
        executeRecordsQuery()
    }

    private fun executeRecordsQuery() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentFilter = _uiState.value.filter
            val result = repository.getIcfesRecords(currentFilter)
            result.onSuccess { list ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        records = list,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        records = emptyList(),
                        errorMessage = error.localizedMessage ?: "Error fetching ICFES records from API"
                    )
                }
            }
        }
    }

    companion object {
        val DEFAULT_DEPARTMENTS = IcfesCatalog.DEPARTMENTS

        val DEFAULT_PERIODS = IcfesCatalog.PERIODS
    }
}
