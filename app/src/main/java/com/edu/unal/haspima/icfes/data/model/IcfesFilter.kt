package com.edu.unal.haspima.icfes.data.model

/**
 * Filter parameters for querying ICFES Saber 11 data through the SODA2 resource endpoint.
 *
 * Every column in kgxf-xxbe is text, so score comparisons cast with `::number`.
 */
data class IcfesFilter(
    val periodo: String? = null,
    val coleDeptoUbicacion: String? = null,
    val coleMuniUbicacion: String? = null,
    val coleNaturaleza: String? = null,
    val estuGenero: String? = null,
    val minPuntGlobal: Int? = null,
    val maxPuntGlobal: Int? = null,
    val minPuntMatematicas: Int? = null,
    val maxPuntMatematicas: Int? = null,
    val minPuntLecturaCritica: Int? = null,
    val maxPuntLecturaCritica: Int? = null,
    val searchKeyword: String? = null,
    val limit: Int = 50,
    val offset: Int = 0,
    val customWhere: String? = null,
    /**
     * Sorting by score scans all 7M rows (5–15 s), so pages are ordered by the indexed
     * row id, which keeps pagination stable and fast.
     */
    val orderBy: String? = DEFAULT_ORDER
) {
    /** `$limit` sent to the API, always within 1..[MAX_LIMIT]. */
    val safeLimit: Int
        get() = limit.coerceIn(1, MAX_LIMIT)

    /** `$offset` sent to the API, never negative. */
    val safeOffset: Int
        get() = offset.coerceAtLeast(0)

    /**
     * Builds the SoQL `$where` clause for the active filter parameters. Rows without a
     * global score are always excluded.
     */
    fun toSoqlWhereClause(): String {
        val conditions = mutableListOf("punt_global IS NOT NULL")

        periodo?.takeIf { it.isNotBlank() }?.let {
            conditions.add("periodo = ${it.trim().soqlLiteral()}")
        }
        coleDeptoUbicacion?.takeIf { it.isNotBlank() }?.let { dept ->
            val aliases = IcfesCatalog.departmentAliases(dept.trim().uppercase())
            conditions.add(
                if (aliases.size == 1) "cole_depto_ubicacion = ${aliases[0].soqlLiteral()}"
                else "cole_depto_ubicacion IN (${aliases.joinToString(", ") { it.soqlLiteral() }})"
            )
        }
        coleMuniUbicacion?.takeIf { it.isNotBlank() }?.let {
            conditions.add("upper(cole_mcpio_ubicacion) LIKE ${it.trim().uppercase().soqlContains()}")
        }
        coleNaturaleza?.takeIf { it.isNotBlank() }?.let {
            conditions.add("upper(cole_naturaleza) = ${it.trim().uppercase().soqlLiteral()}")
        }
        estuGenero?.takeIf { it.isNotBlank() }?.let {
            conditions.add("estu_genero = ${it.trim().uppercase().soqlLiteral()}")
        }
        minPuntGlobal?.let { conditions.add("punt_global::number >= $it") }
        maxPuntGlobal?.let { conditions.add("punt_global::number <= $it") }
        minPuntMatematicas?.let { conditions.add("punt_matematicas::number >= $it") }
        maxPuntMatematicas?.let { conditions.add("punt_matematicas::number <= $it") }
        minPuntLecturaCritica?.let { conditions.add("punt_lectura_critica::number >= $it") }
        maxPuntLecturaCritica?.let { conditions.add("punt_lectura_critica::number <= $it") }
        searchKeyword?.takeIf { it.isNotBlank() }?.let { kw ->
            val pattern = kw.trim().uppercase().soqlContains()
            conditions.add("(upper(cole_nombre_establecimiento) LIKE $pattern OR upper(cole_mcpio_ubicacion) LIKE $pattern)")
        }
        customWhere?.takeIf { it.isNotBlank() }?.let {
            conditions.add("($it)")
        }

        return conditions.joinToString(" AND ")
    }

    companion object {
        /** Hard cap on rows per request; the dataset holds ~7.1M rows. */
        const val MAX_LIMIT = 100

        const val DEFAULT_ORDER = ":id"

        private fun String.soqlLiteral(): String = "'${replace("'", "''")}'"

        private fun String.soqlContains(): String = "%$this%".soqlLiteral()
    }
}
