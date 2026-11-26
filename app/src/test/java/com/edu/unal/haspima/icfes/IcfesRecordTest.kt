package com.edu.unal.haspima.icfes

import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.data.remote.ApiClient
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class IcfesRecordTest {

    private val sampleJson = """
        {
            "periodo": "20204",
            "estu_consecutivo": "SB11202040000001",
            "cole_depto_ubicacion": "BOGOTA",
            "cole_mcpio_ubicacion": "BOGOTA D.C.",
            "cole_nombre_establecimiento": "COLEGIO PRUEBA",
            "cole_naturaleza": "OFICIAL",
            "estu_genero": "M",
            "punt_lectura_critica": "65",
            "punt_matematicas": 72,
            "punt_c_naturales": "68",
            "punt_sociales_ciudadanas": "60",
            "punt_ingles": "70",
            "desemp_ingles": "B1",
            "punt_global": "335"
        }
    """.trimIndent()

    @Test
    fun testMoshiParsingWithFlexibleStringAdapter() {
        val adapter = ApiClient.moshi.adapter(IcfesRecord::class.java)
        val record = adapter.fromJson(sampleJson)

        assertNotNull(record)
        assertEquals("20204", record?.periodo)
        assertEquals("SB11202040000001", record?.estuConsecutivo)
        assertEquals("BOGOTA", record?.coleDeptoUbicacion)
        assertEquals("BOGOTA D.C.", record?.coleMuniUbicacion)
        assertEquals("COLEGIO PRUEBA", record?.coleNombreEstablecimiento)
        assertEquals("OFICIAL", record?.coleNaturaleza)
        assertEquals("M", record?.estuGenero)
        assertEquals("65", record?.puntLecturaCriticaRaw)
        assertEquals(65, record?.puntLecturaCritica)
        assertEquals("72", record?.puntMatematicasRaw)
        assertEquals(72, record?.puntMatematicas)
        assertEquals(335, record?.puntGlobal)
    }

    @Test
    fun testGsonParsing() {
        val gson = Gson()
        val record = gson.fromJson(sampleJson, IcfesRecord::class.java)

        assertNotNull(record)
        assertEquals("20204", record.periodo)
        assertEquals("BOGOTA", record.coleDeptoUbicacion)
        assertEquals(335, record.puntGlobal)
    }

    @Test
    fun testKotlinxSerializationParsing() {
        val json = Json { ignoreUnknownKeys = true }
        // For kotlinx.serialization, punt_matematicas in sampleJson is number so we test string-only json or string conversion
        val stringOnlyJson = sampleJson.replace(": 72", ": \"72\"")
        val record = json.decodeFromString<IcfesRecord>(stringOnlyJson)

        assertNotNull(record)
        assertEquals("20204", record.periodo)
        assertEquals("BOGOTA", record.coleDeptoUbicacion)
        assertEquals(72, record.puntMatematicas)
        assertEquals(335, record.puntGlobal)
    }

    @Test
    fun testNullScoreHandling() {
        val record = IcfesRecord(
            periodo = "20214",
            puntGlobalRaw = "invalid_number",
            puntMatematicasRaw = null
        )

        assertNull(record.puntGlobal)
        assertNull(record.puntMatematicas)
    }
}
