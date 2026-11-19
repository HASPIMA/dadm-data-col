package com.edu.unal.haspima.icfes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.unal.haspima.icfes.data.model.IcfesRecord
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IcfesRecordCard(
    record: IcfesRecord,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: School Name & Global Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = "School",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Column {
                        Text(
                            text = record.coleNombreEstablecimiento?.takeIf { it.isNotBlank() }
                                ?: record.coleNombreSede?.takeIf { it.isNotBlank() }
                                ?: "Establecimiento no especificado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.height(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = listOfNotNull(
                                    record.coleMuniUbicacion?.takeIf { it.isNotBlank() },
                                    record.coleDeptoUbicacion?.takeIf { it.isNotBlank() }
                                ).joinToString(", ").ifEmpty { "Ubicación desconocida" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Global Score Badge
                val globalScore = record.puntGlobal
                GlobalScoreBadge(score = globalScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Tags Row (Nature, Period, Gender)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                record.coleNaturaleza?.takeIf { it.isNotBlank() }?.let { naturaleza ->
                    TagChip(text = naturaleza, color = MaterialTheme.colorScheme.secondaryContainer)
                }
                record.periodo?.takeIf { it.isNotBlank() }?.let { periodo ->
                    TagChip(
                        text = "Periodo: $periodo",
                        icon = Icons.Rounded.CalendarToday,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
                record.estuGenero?.takeIf { it.isNotBlank() }?.let { genero ->
                    TagChip(
                        text = if (genero.equals("M", ignoreCase = true)) "Masculino" else "Femenino",
                        icon = Icons.Rounded.Person,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Subject Scores Grid/Row
            Text(
                text = "Puntajes por Asignatura",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SubjectScoreItem(
                    title = "Lectura",
                    score = record.puntLecturaCritica,
                    modifier = Modifier.weight(1f)
                )
                SubjectScoreItem(
                    title = "Matemáticas",
                    score = record.puntMatematicas,
                    modifier = Modifier.weight(1f)
                )
                SubjectScoreItem(
                    title = "C. Naturales",
                    score = record.puntCNaturales,
                    modifier = Modifier.weight(1f)
                )
                SubjectScoreItem(
                    title = "Sociales",
                    score = record.puntSocialesCiudadanas,
                    modifier = Modifier.weight(1f)
                )
                SubjectScoreItem(
                    title = "Inglés",
                    score = record.puntIngles,
                    subtext = record.desempIngles,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GlobalScoreBadge(score: Int?) {
    val (bgColor, textColor, label) = when {
        score == null -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "N/A")
        score >= 350 -> Triple(Color(0xFF1B5E20), Color.White, "$score / 500") // Excellent - Dark Green
        score >= 280 -> Triple(Color(0xFF0D47A1), Color.White, "$score / 500") // Good - Dark Blue
        score >= 200 -> Triple(Color(0xFFE65100), Color.White, "$score / 500") // Average - Dark Orange
        else -> Triple(Color(0xFFB71C1C), Color.White, "$score / 500")          // Low - Dark Red
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        shadowElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "GLOBAL",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.85f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

@Composable
private fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.height(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubjectScoreItem(
    title: String,
    score: Int?,
    modifier: Modifier = Modifier,
    subtext: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = score?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!subtext.isNullOrBlank()) {
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IcfesRecordCardPreview() {
    ICFESResultsTheme {
        IcfesRecordCard(
            record = IcfesRecord(
                periodo = "20204",
                coleNombreEstablecimiento = "INSTITUCION EDUCATIVA NACIONAL LORENZO DE ALDANA",
                coleDeptoUbicacion = "NARIÑO",
                coleMuniUbicacion = "PASTO",
                coleNaturaleza = "OFICIAL",
                estuGenero = "M",
                puntGlobalRaw = "342",
                puntLecturaCriticaRaw = "68",
                puntMatematicasRaw = "72",
                puntCNaturalesRaw = "65",
                puntSocialesCiudadanasRaw = "64",
                puntInglesRaw = "73",
                desempIngles = "B1"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
