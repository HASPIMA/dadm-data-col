package com.edu.unal.haspima.icfes.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcfesDetailScreen(
    record: IcfesRecord,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Registro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver a la lista"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Global Score Header Card
            GlobalScoreHeaderCard(record = record)

            // 2. English Level Badge Section
            record.desempIngles?.takeIf { it.isNotBlank() }?.let { englishLevel ->
                EnglishLevelCard(englishLevel = englishLevel, score = record.puntIngles)
            }

            // 3. Subject Score Cards/Bars
            SubjectScoresSection(record = record)

            // 4. Complete Metadata Section
            MetadataSection(record = record)
        }
    }
}

@Composable
private fun GlobalScoreHeaderCard(record: IcfesRecord) {
    val score = record.puntGlobal
    val (bgColor, textColor, levelLabel, percentileText) = when {
        score == null -> Quadruple(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurface,
            "Puntaje No Disponible",
            "Sin datos de percentil"
        )
        score >= 350 -> Quadruple(
            Color(0xFF1B5E20),
            Color.White,
            "Desempeño Superior",
            "Percentil Estimado: ~92th - 99th (Top Nacional)"
        )
        score >= 280 -> Quadruple(
            Color(0xFF0D47A1),
            Color.White,
            "Desempeño Alto",
            "Percentil Estimado: ~70th - 91st (Sobre el promedio)"
        )
        score >= 200 -> Quadruple(
            Color(0xFFE65100),
            Color.White,
            "Desempeño Medio",
            "Percentil Estimado: ~30th - 69th (Nivel Promedio)"
        )
        else -> Quadruple(
            Color(0xFFB71C1C),
            Color.White,
            "Desempeño Bajo",
            "Percentil Estimado: < 30th (Requiere Refuerzo)"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PUNTAJE GLOBAL SABER 11",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = textColor.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = score?.toString() ?: "N/A",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )

            Text(
                text = "de 500 puntos máximos",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Level rating pill badge
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = textColor.copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MilitaryTech,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = levelLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            Text(
                text = percentileText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EnglishLevelCard(
    englishLevel: String,
    score: Int?
) {
    val (badgeBgColor, description) = when (englishLevel.uppercase()) {
        "B+" -> Color(0xFF2E7D32) to "Usuario Independiente Avanzado (Nivel B+ Superior)"
        "B1" -> Color(0xFF1565C0) to "Usuario Independiente Umbral (Nivel B1 Intermedio)"
        "A2" -> Color(0xFF00838F) to "Usuario Básico Plataforma (Nivel A2 Elemental)"
        "A1" -> Color(0xFFE65100) to "Usuario Básico Acceso (Nivel A1 Principiante)"
        "A-" -> Color(0xFFC62828) to "Principiante / Por debajo de A1"
        else -> MaterialTheme.colorScheme.primary to "Nivel de desempeño en inglés"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = badgeBgColor,
                shadowElevation = 2.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "INGLÉS",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = englishLevel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nivel Marco Común Europeo (MCER)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (score != null) {
                    Text(
                        text = "Puntaje en prueba: $score / 100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectScoresSection(record: IcfesRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Desglose por Asignaturas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            SubjectScoreBarItem(
                subjectName = "Lectura Crítica",
                score = record.puntLecturaCritica
            )
            SubjectScoreBarItem(
                subjectName = "Matemáticas",
                score = record.puntMatematicas
            )
            SubjectScoreBarItem(
                subjectName = "Ciencias Naturales",
                score = record.puntCNaturales
            )
            SubjectScoreBarItem(
                subjectName = "Sociales y Ciudadanas",
                score = record.puntSocialesCiudadanas
            )
            SubjectScoreBarItem(
                subjectName = "Inglés",
                score = record.puntIngles,
                badge = record.desempIngles
            )
        }
    }
}

@Composable
private fun SubjectScoreBarItem(
    subjectName: String,
    score: Int?,
    badge: String? = null
) {
    var animateBar by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateBar = true
    }

    val progress = (score ?: 0) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = if (animateBar) progress else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "subjectScoreProgress"
    )

    val (barColor, levelText) = when {
        score == null -> MaterialTheme.colorScheme.outline to "Sin datos"
        score >= 70 -> Color(0xFF2E7D32) to "Alto (>=70)"
        score >= 55 -> Color(0xFF1565C0) to "Satisfactorio (55-69)"
        score >= 40 -> Color(0xFFE65100) to "Mínimo (40-54)"
        else -> Color(0xFFC62828) to "Insuficiente (<40)"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(barColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!badge.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = score?.toString() ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
                Text(
                    text = " / 100",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.15f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = levelText,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetadataSection(record: IcfesRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información Completa del Registro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // School & Location details
            MetadataItem(
                icon = Icons.Rounded.School,
                title = "Establecimiento Educativo",
                value = record.coleNombreEstablecimiento
                    ?: record.coleNombreSede
                    ?: "No especificado"
            )

            record.coleNombreSede?.takeIf { it.isNotBlank() && it != record.coleNombreEstablecimiento }?.let { sede ->
                MetadataItem(
                    icon = Icons.Rounded.HomeWork,
                    title = "Sede Educativa",
                    value = sede
                )
            }

            MetadataItem(
                icon = Icons.Rounded.Badge,
                title = "Código ICFES Establecimiento",
                value = record.coleCodigoIcfes ?: "No registrado"
            )

            MetadataItem(
                icon = Icons.Rounded.LocationOn,
                title = "Ubicación Geográfica",
                value = listOfNotNull(
                    record.coleMuniUbicacion?.takeIf { it.isNotBlank() },
                    record.coleDeptoUbicacion?.takeIf { it.isNotBlank() }
                ).joinToString(", ").ifEmpty { "Ubicación no especificada" }
            )

            MetadataItem(
                icon = Icons.Rounded.CalendarToday,
                title = "Periodo de Aplicación",
                value = record.periodo ?: "No especificado"
            )

            MetadataItem(
                icon = Icons.Rounded.Business,
                title = "Sector / Naturaleza del Colegio",
                value = record.coleNaturaleza ?: "No especificado"
            )

            MetadataItem(
                icon = Icons.Rounded.Public,
                title = "Área / Zona de Ubicación",
                value = record.coleAreaUbicacion ?: "No especificado"
            )

            MetadataItem(
                icon = Icons.Rounded.Schedule,
                title = "Jornada Escolar",
                value = record.coleJornada ?: "No especificada"
            )

            MetadataItem(
                icon = Icons.Rounded.Person,
                title = "Género del Estudiante",
                value = when (record.estuGenero?.uppercase()) {
                    "M" -> "Masculino (M)"
                    "F" -> "Femenino (F)"
                    else -> record.estuGenero ?: "No especificado"
                }
            )

            record.desempIngles?.takeIf { it.isNotBlank() }?.let { desemp ->
                MetadataItem(
                    icon = Icons.Rounded.Language,
                    title = "Nivel de Desempeño en Inglés",
                    value = desemp
                )
            }

            // Socio-economic metadata chips if available
            val hasSocioInfo = !record.famiEstratoVivienda.isNullOrBlank() ||
                    !record.famiTieneInternet.isNullOrBlank() ||
                    !record.famiTieneComputador.isNullOrBlank()

            if (hasSocioInfo) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Información Socioeconómica Adicional",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    record.famiEstratoVivienda?.takeIf { it.isNotBlank() }?.let { estrato ->
                        SocioChip(
                            icon = Icons.Rounded.Domain,
                            label = "Estrato $estrato"
                        )
                    }
                    record.famiTieneInternet?.takeIf { it.isNotBlank() }?.let { internet ->
                        SocioChip(
                            icon = Icons.Rounded.Wifi,
                            label = "Internet: $internet"
                        )
                    }
                    record.famiTieneComputador?.takeIf { it.isNotBlank() }?.let { pc ->
                        SocioChip(
                            icon = Icons.Rounded.Computer,
                            label = "Computador: $pc"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(6.dp)
                    .size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SocioChip(
    icon: ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Preview(showBackground = true)
@Composable
fun IcfesDetailScreenPreview() {
    ICFESResultsTheme {
        IcfesDetailScreen(
            record = IcfesRecord(
                periodo = "20204",
                coleNombreEstablecimiento = "COLEGIO NACIONAL LORENZO DE ALDANA",
                coleNombreSede = "SEDE PRINCIPAL",
                coleCodigoIcfes = "015420",
                coleDeptoUbicacion = "NARIÑO",
                coleMuniUbicacion = "PASTO",
                coleNaturaleza = "OFICIAL",
                coleAreaUbicacion = "URBANO",
                coleJornada = "COMPLETA",
                estuGenero = "M",
                puntGlobalRaw = "342",
                puntLecturaCriticaRaw = "68",
                puntMatematicasRaw = "72",
                puntCNaturalesRaw = "65",
                puntSocialesCiudadanasRaw = "64",
                puntInglesRaw = "73",
                desempIngles = "B1",
                famiEstratoVivienda = "Estrato 3",
                famiTieneInternet = "Si",
                famiTieneComputador = "Si"
            ),
            onBack = {}
        )
    }
}
