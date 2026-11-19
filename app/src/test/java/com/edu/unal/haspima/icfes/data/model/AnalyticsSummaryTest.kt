package com.edu.unal.haspima.icfes.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AnalyticsSummaryTest {

    @Test
    fun testFromRecordsWithEmptyList() {
        val summary = AnalyticsSummary.fromRecords(emptyList())

        assertEquals(0, summary.totalRecords)
        assertEquals(0.0, summary.averageGlobalScore, 0.001)
        assertNull(summary.highestGlobalScore)
        assertNull(summary.lowestGlobalScore)
        assertNull(summary.topPerformingSubject)
        assertEquals(4, summary.scoreBuckets.size)
        assertEquals(0, summary.scoreBuckets.sumOf { it.count })
        assertNull(summary.maleAverageScore)
        assertNull(summary.femaleAverageScore)
        assertEquals(0, summary.topSchools.size)
    }

    @Test
    fun testFromRecordsCalculation() {
        val records = listOf(
            IcfesRecord(
                coleNombreEstablecimiento = "COLEGIO A",
                coleDeptoUbicacion = "NARIÑO",
                coleMuniUbicacion = "PASTO",
                estuGenero = "M",
                puntGlobalRaw = "380",
                puntLecturaCriticaRaw = "75",
                puntMatematicasRaw = "80",
                puntCNaturalesRaw = "70",
                puntSocialesCiudadanasRaw = "72",
                puntInglesRaw = "83",
                desempIngles = "B1"
            ),
            IcfesRecord(
                coleNombreEstablecimiento = "COLEGIO A",
                coleDeptoUbicacion = "NARIÑO",
                coleMuniUbicacion = "PASTO",
                estuGenero = "F",
                puntGlobalRaw = "300",
                puntLecturaCriticaRaw = "65",
                puntMatematicasRaw = "60",
                puntCNaturalesRaw = "58",
                puntSocialesCiudadanasRaw = "62",
                puntInglesRaw = "55",
                desempIngles = "A2"
            ),
            IcfesRecord(
                coleNombreEstablecimiento = "COLEGIO B",
                coleDeptoUbicacion = "BOGOTA",
                coleMuniUbicacion = "BOGOTA D.C.",
                estuGenero = "F",
                puntGlobalRaw = "190",
                puntLecturaCriticaRaw = "40",
                puntMatematicasRaw = "35",
                puntCNaturalesRaw = "38",
                puntSocialesCiudadanasRaw = "42",
                puntInglesRaw = "35",
                desempIngles = "A-"
            ),
            IcfesRecord(
                coleNombreEstablecimiento = "COLEGIO C",
                coleDeptoUbicacion = "ANTIOQUIA",
                coleMuniUbicacion = "MEDELLIN",
                estuGenero = "M",
                puntGlobalRaw = "250",
                puntLecturaCriticaRaw = "50",
                puntMatematicasRaw = "52",
                puntCNaturalesRaw = "48",
                puntSocialesCiudadanasRaw = "51",
                puntInglesRaw = "49",
                desempIngles = "A1"
            )
        )

        val summary = AnalyticsSummary.fromRecords(records)

        assertEquals(4, summary.totalRecords)
        assertEquals(280.0, summary.averageGlobalScore, 0.01)
        assertEquals(380, summary.highestGlobalScore)
        assertEquals(190, summary.lowestGlobalScore)

        // Male / Female averages
        // Males: 380, 250 -> Avg = 315.0
        // Females: 300, 190 -> Avg = 245.0
        assertEquals(315.0, summary.maleAverageScore!!, 0.01)
        assertEquals(245.0, summary.femaleAverageScore!!, 0.01)
        assertEquals(2, summary.maleCount)
        assertEquals(2, summary.femaleCount)

        // Score buckets:
        // <200: 190 (1)
        // 200-279: 250 (1)
        // 280-349: 300 (1)
        // 350-500: 380 (1)
        val bucketUnder200 = summary.scoreBuckets.find { it.label == "<200" }
        val bucket200To279 = summary.scoreBuckets.find { it.label == "200-279" }
        val bucket280To349 = summary.scoreBuckets.find { it.label == "280-349" }
        val bucket350To500 = summary.scoreBuckets.find { it.label == "350-500" }

        assertNotNull(bucketUnder200)
        assertEquals(1, bucketUnder200?.count)
        assertEquals(25.0f, bucketUnder200?.percentage ?: 0f, 0.01f)

        assertNotNull(bucket200To279)
        assertEquals(1, bucket200To279?.count)

        assertNotNull(bucket280To349)
        assertEquals(1, bucket280To349?.count)

        assertNotNull(bucket350To500)
        assertEquals(1, bucket350To500?.count)

        // Top Schools
        // COLEGIO A: avg (380+300)/2 = 340.0
        // COLEGIO C: 250.0
        // COLEGIO B: 190.0
        assertEquals(3, summary.topSchools.size)
        assertEquals("COLEGIO A", summary.topSchools[0].schoolName)
        assertEquals(340.0, summary.topSchools[0].averageScore, 0.01)
        assertEquals("COLEGIO C", summary.topSchools[1].schoolName)
        assertEquals("COLEGIO B", summary.topSchools[2].schoolName)

        // Top subject: Inglés (83, 55, 35, 49 -> avg 55.5), Lectura (75, 65, 40, 50 -> avg 57.5), Mates (80, 60, 35, 52 -> avg 56.75)
        // Top subject should be Lectura Crítica (57.5)
        assertNotNull(summary.topPerformingSubject)
        assertEquals("Lectura Crítica", summary.topPerformingSubject?.subjectName)
        assertEquals(57.5, summary.topPerformingSubject?.averageScore ?: 0.0, 0.01)
    }
}
