package com.edu.unal.haspima.icfes.data.repository

import com.edu.unal.haspima.icfes.data.model.IcfesCatalog
import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.data.remote.ApiClient
import com.edu.unal.haspima.icfes.data.remote.IcfesApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Default implementation of [IcfesRepository] using the SODA2 resource endpoint.
 */
class IcfesRepositoryImpl(
    private val apiService: IcfesApiService = ApiClient.icfesApiService,
    private val datasetId: String = IcfesCatalog.DATASET_ID,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IcfesRepository {

    override suspend fun getIcfesRecords(filter: IcfesFilter): Result<List<IcfesRecord>> =
        withContext(dispatcher) {
            runCatching {
                apiService.getResourceRecords(
                    datasetId = datasetId,
                    where = filter.toSoqlWhereClause(),
                    limit = filter.safeLimit,
                    offset = filter.safeOffset,
                    order = filter.orderBy?.takeIf { it.isNotBlank() }
                )
            }
        }

    override fun getIcfesRecordsFlow(filter: IcfesFilter): Flow<Result<List<IcfesRecord>>> =
        flow {
            emit(getIcfesRecords(filter))
        }.flowOn(dispatcher)

    override suspend fun getDistinctPeriods(): Result<List<String>> =
        Result.success(IcfesCatalog.PERIODS)

    override suspend fun getDistinctDepartments(): Result<List<String>> =
        Result.success(IcfesCatalog.DEPARTMENTS)
}
