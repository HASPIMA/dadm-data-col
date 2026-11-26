package com.edu.unal.haspima.icfes.data.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single ICFES Saber 11 record from the SODA dataset (kgxf-xxbe).
 */
@Serializable
@JsonClass(generateAdapter = true)
data class IcfesRecord(
    @SerialName("periodo")
    @field:Json(name = "periodo")
    @SerializedName("periodo")
    val periodo: String? = null,

    @SerialName("estu_consecutivo")
    @field:Json(name = "estu_consecutivo")
    @SerializedName("estu_consecutivo")
    val estuConsecutivo: String? = null,

    @SerialName("cole_depto_ubicacion")
    @field:Json(name = "cole_depto_ubicacion")
    @SerializedName("cole_depto_ubicacion")
    val coleDeptoUbicacion: String? = null,

    @SerialName("cole_mcpio_ubicacion")
    @field:Json(name = "cole_mcpio_ubicacion")
    @SerializedName("cole_mcpio_ubicacion")
    val coleMuniUbicacion: String? = null,

    @SerialName("cole_nombre_establecimiento")
    @field:Json(name = "cole_nombre_establecimiento")
    @SerializedName("cole_nombre_establecimiento")
    val coleNombreEstablecimiento: String? = null,

    @SerialName("cole_nombre_sede")
    @field:Json(name = "cole_nombre_sede")
    @SerializedName("cole_nombre_sede")
    val coleNombreSede: String? = null,

    @SerialName("cole_codigo_icfes")
    @field:Json(name = "cole_codigo_icfes")
    @SerializedName("cole_codigo_icfes")
    val coleCodigoIcfes: String? = null,

    @SerialName("cole_genero")
    @field:Json(name = "cole_genero")
    @SerializedName("cole_genero")
    val coleGenero: String? = null,

    @SerialName("cole_naturaleza")
    @field:Json(name = "cole_naturaleza")
    @SerializedName("cole_naturaleza")
    val coleNaturaleza: String? = null,

    @SerialName("cole_calendario")
    @field:Json(name = "cole_calendario")
    @SerializedName("cole_calendario")
    val coleCalendario: String? = null,

    @SerialName("cole_bilingue")
    @field:Json(name = "cole_bilingue")
    @SerializedName("cole_bilingue")
    val coleBilingue: String? = null,

    @SerialName("cole_caracter")
    @field:Json(name = "cole_caracter")
    @SerializedName("cole_caracter")
    val coleCaracter: String? = null,

    @SerialName("cole_area_ubicacion")
    @field:Json(name = "cole_area_ubicacion")
    @SerializedName("cole_area_ubicacion")
    val coleAreaUbicacion: String? = null,

    @SerialName("cole_jornada")
    @field:Json(name = "cole_jornada")
    @SerializedName("cole_jornada")
    val coleJornada: String? = null,

    @SerialName("estu_genero")
    @field:Json(name = "estu_genero")
    @SerializedName("estu_genero")
    val estuGenero: String? = null,

    @SerialName("estu_fechanacimiento")
    @field:Json(name = "estu_fechanacimiento")
    @SerializedName("estu_fechanacimiento")
    val estuFechaNacimiento: String? = null,

    @SerialName("estu_pais_reside")
    @field:Json(name = "estu_pais_reside")
    @SerializedName("estu_pais_reside")
    val estuPaisReside: String? = null,

    @SerialName("estu_depto_reside")
    @field:Json(name = "estu_depto_reside")
    @SerializedName("estu_depto_reside")
    val estuDeptoReside: String? = null,

    @SerialName("estu_mcpio_reside")
    @field:Json(name = "estu_mcpio_reside")
    @SerializedName("estu_mcpio_reside")
    val estuMuniReside: String? = null,

    @SerialName("punt_lectura_critica")
    @field:Json(name = "punt_lectura_critica")
    @SerializedName("punt_lectura_critica")
    val puntLecturaCriticaRaw: String? = null,

    @SerialName("punt_matematicas")
    @field:Json(name = "punt_matematicas")
    @SerializedName("punt_matematicas")
    val puntMatematicasRaw: String? = null,

    @SerialName("punt_c_naturales")
    @field:Json(name = "punt_c_naturales")
    @SerializedName("punt_c_naturales")
    val puntCNaturalesRaw: String? = null,

    @SerialName("punt_sociales_ciudadanas")
    @field:Json(name = "punt_sociales_ciudadanas")
    @SerializedName("punt_sociales_ciudadanas")
    val puntSocialesCiudadanasRaw: String? = null,

    @SerialName("punt_ingles")
    @field:Json(name = "punt_ingles")
    @SerializedName("punt_ingles")
    val puntInglesRaw: String? = null,

    @SerialName("desemp_ingles")
    @field:Json(name = "desemp_ingles")
    @SerializedName("desemp_ingles")
    val desempIngles: String? = null,

    @SerialName("punt_global")
    @field:Json(name = "punt_global")
    @SerializedName("punt_global")
    val puntGlobalRaw: String? = null,

    @SerialName("estu_estadoinvestigacion")
    @field:Json(name = "estu_estadoinvestigacion")
    @SerializedName("estu_estadoinvestigacion")
    val estuEstado: String? = null,

    @SerialName("fami_estratovivienda")
    @field:Json(name = "fami_estratovivienda")
    @SerializedName("fami_estratovivienda")
    val famiEstratoVivienda: String? = null,

    @SerialName("fami_tieneinternet")
    @field:Json(name = "fami_tieneinternet")
    @SerializedName("fami_tieneinternet")
    val famiTieneInternet: String? = null,

    @SerialName("fami_tienecomputador")
    @field:Json(name = "fami_tienecomputador")
    @SerializedName("fami_tienecomputador")
    val famiTieneComputador: String? = null,

    @SerialName("fami_educacionpadre")
    @field:Json(name = "fami_educacionpadre")
    @SerializedName("fami_educacionpadre")
    val famiEducacionPadre: String? = null,

    @SerialName("fami_educacionmadre")
    @field:Json(name = "fami_educacionmadre")
    @SerializedName("fami_educacionmadre")
    val famiEducacionMadre: String? = null,

    @SerialName("fami_personashogar")
    @field:Json(name = "fami_personashogar")
    @SerializedName("fami_personashogar")
    val famiNumPersonas: String? = null,

    @SerialName("fami_tieneautomovil")
    @field:Json(name = "fami_tieneautomovil")
    @SerializedName("fami_tieneautomovil")
    val famiTieneAutomovil: String? = null
) {
    val puntLecturaCritica: Int?
        get() = puntLecturaCriticaRaw?.toIntOrNull()

    val puntMatematicas: Int?
        get() = puntMatematicasRaw?.toIntOrNull()

    val puntCNaturales: Int?
        get() = puntCNaturalesRaw?.toIntOrNull()

    val puntSocialesCiudadanas: Int?
        get() = puntSocialesCiudadanasRaw?.toIntOrNull()

    val puntIngles: Int?
        get() = puntInglesRaw?.toIntOrNull()

    val puntGlobal: Int?
        get() = puntGlobalRaw?.toIntOrNull()
}
