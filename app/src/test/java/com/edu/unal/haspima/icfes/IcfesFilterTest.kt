package com.edu.unal.haspima.icfes

import com.edu.unal.haspima.icfes.data.model.IcfesFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IcfesFilterTest {

    @Test
    fun testToSoqlWhereClauseWithMultipleFilters() {
        val filter = IcfesFilter(
            periodo = "20194",
            coleDeptoUbicacion = "huila",
            coleMuniUbicacion = "Aipe",
            coleNaturaleza = "oficial",
            estuGenero = "m",
            minPuntGlobal = 300,
            maxPuntGlobal = 450,
            searchKeyword = "San Jose"
        )

        val whereClause = filter.toSoqlWhereClause()

        assertTrue(whereClause.contains("punt_global IS NOT NULL"))
        assertTrue(whereClause.contains("periodo = '20194'"))
        assertTrue(whereClause.contains("cole_depto_ubicacion = 'HUILA'"))
        assertTrue(whereClause.contains("upper(cole_mcpio_ubicacion) LIKE '%AIPE%'"))
        assertTrue(whereClause.contains("upper(cole_naturaleza) = 'OFICIAL'"))
        assertTrue(whereClause.contains("estu_genero = 'M'"))
        assertTrue(whereClause.contains("punt_global::number >= 300"))
        assertTrue(whereClause.contains("punt_global::number <= 450"))
        assertTrue(whereClause.contains("upper(cole_nombre_establecimiento) LIKE '%SAN JOSE%'"))
    }

    @Test
    fun testEmptyFilterOnlyExcludesRowsWithoutScore() {
        assertEquals("punt_global IS NOT NULL", IcfesFilter().toSoqlWhereClause())
    }

    @Test
    fun testBogotaMatchesBothDatasetSpellings() {
        val whereClause = IcfesFilter(coleDeptoUbicacion = "BOGOTA").toSoqlWhereClause()

        assertTrue(whereClause.contains("cole_depto_ubicacion IN ('BOGOTA', 'BOGOTÁ')"))
    }

    @Test
    fun testSingleQuotesAreEscaped() {
        val whereClause = IcfesFilter(searchKeyword = "O'Higgins").toSoqlWhereClause()

        assertTrue(whereClause.contains("LIKE '%O''HIGGINS%'"))
    }

    @Test
    fun testLimitIsCappedAndOffsetNonNegative() {
        val filter = IcfesFilter(limit = 5000, offset = -10)

        assertEquals(IcfesFilter.MAX_LIMIT, filter.safeLimit)
        assertEquals(0, filter.safeOffset)
        assertEquals(1, IcfesFilter(limit = 0).safeLimit)
        assertEquals(20, IcfesFilter(limit = 20).safeLimit)
    }

    @Test
    fun testDefaultOrderIsIndexedRowId() {
        assertEquals(":id", IcfesFilter().orderBy)
    }
}
