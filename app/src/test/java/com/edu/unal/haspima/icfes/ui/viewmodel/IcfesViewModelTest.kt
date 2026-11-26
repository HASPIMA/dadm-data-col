package com.edu.unal.haspima.icfes.ui.viewmodel

import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.data.repository.IcfesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeIcfesRepository : IcfesRepository {

    var shouldFail = false
    var failureMessage = "Network error"
    val sampleRecords = listOf(
        IcfesRecord(
            periodo = "20204",
            coleDeptoUbicacion = "BOGOTA",
            coleMuniUbicacion = "BOGOTA D.C.",
            coleNombreEstablecimiento = "COLEGIO PRUEBA A",
            puntGlobalRaw = "350"
        ),
        IcfesRecord(
            periodo = "20204",
            coleDeptoUbicacion = "ANTIOQUIA",
            coleMuniUbicacion = "MEDELLIN",
            coleNombreEstablecimiento = "COLEGIO PRUEBA B",
            puntGlobalRaw = "310"
        )
    )

    var lastQueryFilter: IcfesFilter? = null

    override suspend fun getIcfesRecords(filter: IcfesFilter): Result<List<IcfesRecord>> {
        lastQueryFilter = filter
        return if (shouldFail) {
            Result.failure(RuntimeException(failureMessage))
        } else {
            Result.success(sampleRecords)
        }
    }

    override fun getIcfesRecordsFlow(filter: IcfesFilter): Flow<Result<List<IcfesRecord>>> {
        return kotlinx.coroutines.flow.flow {
            emit(getIcfesRecords(filter))
        }
    }

    override suspend fun getDistinctPeriods(): Result<List<String>> {
        return Result.success(listOf("20214", "20204", "20194"))
    }

    override suspend fun getDistinctDepartments(): Result<List<String>> {
        return Result.success(listOf("ANTIOQUIA", "BOGOTA", "VALLE"))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class IcfesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeIcfesRepository
    private lateinit var viewModel: IcfesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeIcfesRepository()
        viewModel = IcfesViewModel(repository = fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateAndLoadFilterChoices() = runTest(testDispatcher) {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.records.size)
        assertEquals("COLEGIO PRUEBA A", state.records[0].coleNombreEstablecimiento)
        assertEquals(listOf("ANTIOQUIA", "BOGOTA", "VALLE"), state.availableDepartments)
        assertEquals(listOf("20214", "20204", "20194"), state.availablePeriods)
        assertEquals(MainTab.RESULTS, state.selectedTab)
        assertNotNull(state.analyticsSummary)
        assertEquals(2, state.analyticsSummary.totalRecords)
        assertEquals(330.0, state.analyticsSummary.averageGlobalScore, 0.01)
    }

    @Test
    fun testSelectTabNavigation() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.selectTab(MainTab.ANALYTICS)
        assertEquals(MainTab.ANALYTICS, viewModel.uiState.value.selectedTab)

        viewModel.selectTab(MainTab.RESULTS)
        assertEquals(MainTab.RESULTS, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun testUpdatePendingFilters() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.updateDepartment("BOGOTA")
        viewModel.updateMunicipality("BOGOTA D.C.")
        viewModel.updatePeriod("20204")
        viewModel.updateGender("M")
        viewModel.updateScoreRange(250, 450)
        viewModel.updateSearchText("Nacional")

        val state = viewModel.uiState.value
        assertEquals("BOGOTA", state.pendingFilter.coleDeptoUbicacion)
        assertEquals("BOGOTA D.C.", state.pendingFilter.coleMuniUbicacion)
        assertEquals("20204", state.pendingFilter.periodo)
        assertEquals("M", state.pendingFilter.estuGenero)
        assertEquals(250, state.pendingFilter.minPuntGlobal)
        assertEquals(450, state.pendingFilter.maxPuntGlobal)
        assertEquals("Nacional", state.pendingFilter.searchKeyword)

        // Verify active filter has not been updated yet before applyFilters()
        assertNull(state.filter.coleDeptoUbicacion)
    }

    @Test
    fun testApplyFiltersExecutesQuery() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.updateDepartment("BOGOTA")
        viewModel.updateSearchText("Nacional")
        viewModel.applyFilters()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("BOGOTA", state.filter.coleDeptoUbicacion)
        assertEquals("Nacional", state.filter.searchKeyword)
        assertEquals(1, state.currentPage)
        assertEquals(2, state.records.size)
        assertEquals("BOGOTA", fakeRepository.lastQueryFilter?.coleDeptoUbicacion)
    }

    @Test
    fun testResetFilters() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.updateDepartment("ANTIOQUIA")
        viewModel.applyFilters()
        advanceUntilIdle()

        assertEquals("ANTIOQUIA", viewModel.uiState.value.filter.coleDeptoUbicacion)

        viewModel.resetFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.filter.coleDeptoUbicacion)
        assertNull(state.pendingFilter.coleDeptoUbicacion)
        assertEquals(1, state.currentPage)
    }

    @Test
    fun testClearIndividualFilters() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.updateDepartment("BOGOTA")
        viewModel.updatePeriod("20204")
        viewModel.applyFilters()
        advanceUntilIdle()

        assertEquals("BOGOTA", viewModel.uiState.value.filter.coleDeptoUbicacion)
        assertEquals("20204", viewModel.uiState.value.filter.periodo)

        viewModel.clearDepartment()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.filter.coleDeptoUbicacion)
        assertEquals("20204", state.filter.periodo)
    }

    @Test
    fun testPaginationNextAndPreviousPage() = runTest(testDispatcher) {
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.currentPage)
        assertEquals(0, viewModel.uiState.value.filter.offset)

        viewModel.nextPage()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.currentPage)
        assertEquals(20, viewModel.uiState.value.filter.offset)

        viewModel.previousPage()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.currentPage)
        assertEquals(0, viewModel.uiState.value.filter.offset)
    }

    @Test
    fun testErrorHandlingOnQueryFailure() = runTest(testDispatcher) {
        advanceUntilIdle()

        fakeRepository.shouldFail = true
        fakeRepository.failureMessage = "SODA3 connection timeout"

        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.records.isEmpty())
        assertEquals("SODA3 connection timeout", state.errorMessage)
    }
}
