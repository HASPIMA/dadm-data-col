package com.edu.unal.haspima.icfes.data.remote

import com.edu.unal.haspima.icfes.data.model.IcfesCatalog
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for the SODA2 resource endpoint on datos.gov.co.
 * SODA2 needs no app token (the SODA3 query endpoint answers 403 without one).
 */
interface IcfesApiService {

    /**
     * SODA2 resource endpoint: `resource/{datasetId}.json`.
     */
    @GET("resource/{datasetId}.json")
    suspend fun getResourceRecords(
        @Path("datasetId") datasetId: String = IcfesCatalog.DATASET_ID,
        @Query("\$where") where: String? = null,
        @Query("\$limit") limit: Int,
        @Query("\$offset") offset: Int? = null,
        @Query("\$order") order: String? = null,
        @Query("\$select") select: String? = null
    ): List<IcfesRecord>
}
