package com.edu.unal.haspima.icfes.data.model

/**
 * Metric representation for a score range bucket.
 */
data class ScoreBucketCount(
    val label: String,
    val rangeDescription: String,
    val count: Int,
    val percentage: Float
)

/**
 * Metric representation for school performance.
 */
data class SchoolAverage(
    val schoolName: String,
    val municipality: String?,
    val department: String?,
    val recordCount: Int,
    val averageScore: Double
)

/**
 * Metric representation for subject performance average.
 */
data class SubjectAverage(
    val subjectName: String,
    val shortName: String,
    val averageScore: Double
)

/**
 * Data class representing analytics and summary metrics calculated from a set of ICFES records.
 */
data class AnalyticsSummary(
    val totalRecords: Int = 0,
    val averageGlobalScore: Double = 0.0,
    val highestGlobalScore: Int? = null,
    val lowestGlobalScore: Int? = null,
    val topPerformingSubject: SubjectAverage? = null,
    val subjectAverages: List<SubjectAverage> = emptyList(),
    val scoreBuckets: List<ScoreBucketCount> = emptyList(),
    val maleAverageScore: Double? = null,
    val femaleAverageScore: Double? = null,
    val maleCount: Int = 0,
    val femaleCount: Int = 0,
    val topSchools: List<SchoolAverage> = emptyList()
) {
    companion object {
        fun fromRecords(records: List<IcfesRecord>): AnalyticsSummary {
            if (records.isEmpty()) {
                return AnalyticsSummary(
                    scoreBuckets = listOf(
                        ScoreBucketCount("<200", "Bajo (<200)", 0, 0f),
                        ScoreBucketCount("200-279", "Medio (200-279)", 0, 0f),
                        ScoreBucketCount("280-349", "Alto (280-349)", 0, 0f),
                        ScoreBucketCount("350-500", "Superior (350-500)", 0, 0f)
                    )
                )
            }

            val validGlobalScores = records.mapNotNull { it.puntGlobal }
            val totalRecords = records.size
            val avgGlobal = if (validGlobalScores.isNotEmpty()) validGlobalScores.average() else 0.0
            val maxGlobal = validGlobalScores.maxOrNull()
            val minGlobal = validGlobalScores.minOrNull()

            // Subject averages calculation
            val lecturaAvg = records.mapNotNull { it.puntLecturaCritica }.let { if (it.isNotEmpty()) it.average() else 0.0 }
            val matesAvg = records.mapNotNull { it.puntMatematicas }.let { if (it.isNotEmpty()) it.average() else 0.0 }
            val cienciasAvg = records.mapNotNull { it.puntCNaturales }.let { if (it.isNotEmpty()) it.average() else 0.0 }
            val socialesAvg = records.mapNotNull { it.puntSocialesCiudadanas }.let { if (it.isNotEmpty()) it.average() else 0.0 }
            val inglesAvg = records.mapNotNull { it.puntIngles }.let { if (it.isNotEmpty()) it.average() else 0.0 }

            val subjects = listOf(
                SubjectAverage("Lectura Crítica", "Lectura", lecturaAvg),
                SubjectAverage("Matemáticas", "Matemáticas", matesAvg),
                SubjectAverage("Ciencias Naturales", "Ciencias", cienciasAvg),
                SubjectAverage("Sociales y Ciudadanas", "Sociales", socialesAvg),
                SubjectAverage("Inglés", "Inglés", inglesAvg)
            )

            val topSubject = subjects.filter { it.averageScore > 0 }.maxByOrNull { it.averageScore }

            // Score Distribution Buckets (<200, 200-279, 280-349, 350-500)
            val bUnder200 = validGlobalScores.count { it < 200 }
            val b200To279 = validGlobalScores.count { it in 200..279 }
            val b280To349 = validGlobalScores.count { it in 280..349 }
            val b350To500 = validGlobalScores.count { it in 350..500 }
            val validCount = validGlobalScores.size.coerceAtLeast(1).toFloat()

            val buckets = listOf(
                ScoreBucketCount("<200", "Bajo (<200)", bUnder200, (bUnder200 / validCount) * 100f),
                ScoreBucketCount("200-279", "Medio (200-279)", b200To279, (b200To279 / validCount) * 100f),
                ScoreBucketCount("280-349", "Alto (280-349)", b280To349, (b280To349 / validCount) * 100f),
                ScoreBucketCount("350-500", "Superior (350-500)", b350To500, (b350To500 / validCount) * 100f)
            )

            // Gender averages
            val maleRecords = records.filter { it.estuGenero.equals("M", ignoreCase = true) }
            val femaleRecords = records.filter { it.estuGenero.equals("F", ignoreCase = true) }

            val maleScores = maleRecords.mapNotNull { it.puntGlobal }
            val femaleScores = femaleRecords.mapNotNull { it.puntGlobal }

            val maleAvg = if (maleScores.isNotEmpty()) maleScores.average() else null
            val femaleAvg = if (femaleScores.isNotEmpty()) femaleScores.average() else null

            // Top Schools (grouped by school name)
            val topSchools = records
                .groupBy {
                    it.coleNombreEstablecimiento?.takeIf { name -> name.isNotBlank() }
                        ?: it.coleNombreSede?.takeIf { name -> name.isNotBlank() }
                        ?: "Desconocido"
                }
                .filterKeys { it != "Desconocido" }
                .map { (schoolName, schoolRecords) ->
                    val scores = schoolRecords.mapNotNull { it.puntGlobal }
                    val avg = if (scores.isNotEmpty()) scores.average() else 0.0
                    val firstRec = schoolRecords.firstOrNull()
                    SchoolAverage(
                        schoolName = schoolName,
                        municipality = firstRec?.coleMuniUbicacion,
                        department = firstRec?.coleDeptoUbicacion,
                        recordCount = schoolRecords.size,
                        averageScore = avg
                    )
                }
                .sortedByDescending { it.averageScore }
                .take(5)

            return AnalyticsSummary(
                totalRecords = totalRecords,
                averageGlobalScore = avgGlobal,
                highestGlobalScore = maxGlobal,
                lowestGlobalScore = minGlobal,
                topPerformingSubject = topSubject,
                subjectAverages = subjects,
                scoreBuckets = buckets,
                maleAverageScore = maleAvg,
                femaleAverageScore = femaleAvg,
                maleCount = maleRecords.size,
                femaleCount = femaleRecords.size,
                topSchools = topSchools
            )
        }
    }
}
