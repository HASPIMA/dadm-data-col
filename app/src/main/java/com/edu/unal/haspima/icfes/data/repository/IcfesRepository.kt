package com.edu.unal.haspima.icfes.data.repository

import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for querying ICFES Saber 11 data from SODA / datos.gov.co.
 */
interface IcfesRepository {

    /**
     * Queries one page of ICFES records using [IcfesFilter] parameter object.
     * Returns a [Result] containing a list of [IcfesRecord].
     */
    suspend fun getIcfesRecords(filter: IcfesFilter): Result<List<IcfesRecord>>

    /**
     * Returns a [Flow] emitting [Result<List<IcfesRecord>>] for reactive stream subscriptions.
     */
    fun getIcfesRecordsFlow(filter: IcfesFilter): Flow<Result<List<IcfesRecord>>>

    /**
     * Available exam periods (e.g., "20224", "20194").
     */
    suspend fun getDistinctPeriods(): Result<List<String>>

    /**
     * Available school departments (e.g., "BOGOTA", "ANTIOQUIA").
     */
    suspend fun getDistinctDepartments(): Result<List<String>>
}
