package com.edu.unal.haspima.icfes

import com.edu.unal.haspima.icfes.data.model.IcfesCatalog
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.data.remote.IcfesApiService
import com.edu.unal.haspima.icfes.data.repository.IcfesRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeIcfesApiService : IcfesApiService {

    var shouldFail = false

    var lastDatasetId: String? = null
    var lastWhere: String? = null
    var lastLimit: Int? = null
    var lastOffset: Int? = null
    var lastOrder: String? = null

    val records = listOf(
        IcfesRecord(
            periodo = "20194",
            coleDeptoUbicacion = "BOGOTA",
            coleMuniUbicacion = "BOGOTÁ D.C.",
            coleNombreEstablecimiento = "COLEGIO A",
            puntGlobalRaw = "350"
        ),
        IcfesRecord(
            periodo = "20194",
            coleDeptoUbicacion = "ANTIOQUIA",
            coleMuniUbicacion = "MEDELLÍN",
            coleNombreEstablecimiento = "COLEGIO B",
            puntGlobalRaw = "310"
        )
    )

    override suspend fun getResourceRecords(
        datasetId: String,
        where: String?,
        limit: Int,
        offset: Int?,
        order: String?,
        select: String?
    ): List<IcfesRecord> {
        if (shouldFail) throw RuntimeException("SODA2 network error")
        lastDatasetId = datasetId
        lastWhere = where
        lastLimit = limit
        lastOffset = offset
        lastOrder = order
        return records
    }
}

class IcfesRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private fun repository(api: IcfesApiService) =
        IcfesRepositoryImpl(apiService = api, dispatcher = testDispatcher)

    @Test
    fun testGetIcfesRecordsQueriesSoda2ResourceWithFilter() = runTest(testDispatcher) {
        val fakeApi = FakeIcfesApiService()

        val result = repository(fakeApi).getIcfesRecords(
            IcfesFilter(periodo = "20194", limit = 20, offset = 40)
        )

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
        assertEquals("COLEGIO A", result.getOrThrow()[0].coleNombreEstablecimiento)
        assertEquals(IcfesCatalog.DATASET_ID, fakeApi.lastDatasetId)
        assertEquals("punt_global IS NOT NULL AND periodo = '20194'", fakeApi.lastWhere)
        assertEquals(20, fakeApi.lastLimit)
        assertEquals(40, fakeApi.lastOffset)
        assertEquals(":id", fakeApi.lastOrder)
    }

    @Test
    fun testGetIcfesRecordsAlwaysSendsCappedLimit() = runTest(testDispatcher) {
        val fakeApi = FakeIcfesApiService()

        repository(fakeApi).getIcfesRecords(IcfesFilter(limit = 1_000_000))

        assertEquals(IcfesFilter.MAX_LIMIT, fakeApi.lastLimit)
    }

    @Test
    fun testGetIcfesRecordsReturnsFailureOnNetworkError() = runTest(testDispatcher) {
        val fakeApi = FakeIcfesApiService().apply { shouldFail = true }

        val result = repository(fakeApi).getIcfesRecords(IcfesFilter())

        assertTrue(result.isFailure)
        assertEquals("SODA2 network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun testGetIcfesRecordsFlowEmitsSuccess() = runTest(testDispatcher) {
        val flowResult = repository(FakeIcfesApiService()).getIcfesRecordsFlow(IcfesFilter()).first()

        assertTrue(flowResult.isSuccess)
        assertEquals(2, flowResult.getOrThrow().size)
    }

    @Test
    fun testFilterChoicesComeFromCatalog() = runTest(testDispatcher) {
        val repository = repository(FakeIcfesApiService())

        assertEquals(IcfesCatalog.PERIODS, repository.getDistinctPeriods().getOrThrow())
        assertEquals(IcfesCatalog.DEPARTMENTS, repository.getDistinctDepartments().getOrThrow())
    }
}
