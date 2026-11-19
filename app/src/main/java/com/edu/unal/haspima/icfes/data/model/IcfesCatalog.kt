package com.edu.unal.haspima.icfes.data.model

/**
 * Static filter choices for the "Resultados únicos Saber 11" dataset (kgxf-xxbe).
 *
 * The dataset has ~7.1M rows, so `$group` queries for distinct values take 30–50 s.
 * These lists were captured from the API and only change when ICFES publishes a new period.
 */
object IcfesCatalog {

    const val DATASET_ID = "kgxf-xxbe"

    val PERIODS = listOf(
        "20224", "20221", "20211", "20201", "20194", "20191", "20181", "20172",
        "20171", "20162", "20161", "20152", "20151", "20142", "20141", "20132",
        "20131", "20122", "20121", "20112", "20111", "20102", "20101"
    )

    val DEPARTMENTS = listOf(
        "AMAZONAS", "ANTIOQUIA", "ARAUCA", "ATLANTICO", "BOGOTA", "BOLIVAR",
        "BOYACA", "CALDAS", "CAQUETA", "CASANARE", "CAUCA", "CESAR",
        "CHOCO", "CORDOBA", "CUNDINAMARCA", "GUAINIA", "GUAVIARE", "HUILA",
        "LA GUAJIRA", "MAGDALENA", "META", "NARIÑO", "NORTE SANTANDER", "PUTUMAYO",
        "QUINDIO", "RISARALDA", "SAN ANDRES", "SANTANDER", "SUCRE", "TOLIMA",
        "VALLE", "VAUPES", "VICHADA"
    )

    /**
     * Raw dataset spellings for a department. Bogotá appears both with and without accent.
     */
    fun departmentAliases(department: String): List<String> =
        if (department == "BOGOTA") listOf("BOGOTA", "BOGOTÁ") else listOf(department)
}
