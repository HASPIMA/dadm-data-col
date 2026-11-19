package com.edu.unal.haspima.icfes.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Scoreboard
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.unal.haspima.icfes.data.model.AnalyticsSummary
import com.edu.unal.haspima.icfes.data.model.SchoolAverage
import com.edu.unal.haspima.icfes.data.model.ScoreBucketCount
import com.edu.unal.haspima.icfes.data.model.SubjectAverage
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@Composable
fun IcfesAnalyticsScreen(
    summary: AnalyticsSummary,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            isLoading -> {
                AnalyticsLoadingState()
            }
            errorMessage != null -> {
                AnalyticsErrorState(errorMessage = errorMessage, onRetry = onRefresh)
            }
            summary.totalRecords == 0 -> {
                AnalyticsEmptyState(onRefresh = onRefresh)
            }
            else -> {
                // 1. Summary Metric Cards Grid
                SummaryMetricCardsGrid(summary = summary)

                // 2. Subject Averages Custom Bar Chart (Canvas)
                SubjectAveragesChartCard(subjectAverages = summary.subjectAverages)

                // 3. Global Score Distribution Histogram (Canvas)
                ScoreDistributionChartCard(scoreBuckets = summary.scoreBuckets)

                // 4. Gender Score Comparison Card
                GenderComparisonCard(
                    maleAvg = summary.maleAverageScore,
                    femaleAvg = summary.femaleAverageScore,
                    maleCount = summary.maleCount,
                    femaleCount = summary.femaleCount
                )

                // 5. Top Performing Schools Table
                if (summary.topSchools.isNotEmpty()) {
                    TopSchoolsCard(topSchools = summary.topSchools)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. SUMMARY METRIC CARDS
// -----------------------------------------------------------------------------

@Composable
private fun SummaryMetricCardsGrid(
    summary: AnalyticsSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Resumen de Métricas Clave",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Row 1: Total Records & Average Global Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Registros",
                value = summary.totalRecords.toString(),
                subtitle = "en página actual",
                icon = Icons.Rounded.Numbers,
                color = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Promedio Global",
                value = String.format(java.util.Locale.US, "%.1f", summary.averageGlobalScore),
                subtitle = "máximo 500",
                icon = Icons.Rounded.Scoreboard,
                color = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Highest Score & Lowest Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Puntaje Máximo",
                value = summary.highestGlobalScore?.toString() ?: "N/A",
                subtitle = "mayor del conjunto",
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                color = Color(0xFFE8F5E9),
                iconTint = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Puntaje Mínimo",
                value = summary.lowestGlobalScore?.toString() ?: "N/A",
                subtitle = "menor del conjunto",
                icon = Icons.AutoMirrored.Rounded.TrendingDown,
                color = Color(0xFFFFEBEE),
                iconTint = Color(0xFFC62828),
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Top Performing Subject
        summary.topPerformingSubject?.let { topSubj ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ASIGNATURA DE MEJOR RENDIMIENTO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = topSubj.subjectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "Promedio: %.1f / 100 pts", topSubj.averageScore),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = iconTint.copy(alpha = 0.9f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = iconTint
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = iconTint.copy(alpha = 0.75f)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 2. SUBJECT AVERAGES CUSTOM BAR CHART (JETPACK COMPOSE CANVAS)
// -----------------------------------------------------------------------------

@Composable
private fun SubjectAveragesChartCard(subjectAverages: List<SubjectAverage>) {
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Comparativa de Promedios por Asignatura",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Escala de 0 a 100 puntos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SubjectAveragesCanvasChart(
                subjectAverages = subjectAverages,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}

@Composable
private fun SubjectAveragesCanvasChart(
    subjectAverages: List<SubjectAverage>,
    modifier: Modifier = Modifier
) {
    var animateChart by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateChart = true
    }

    val animationProgress by animateFloatAsState(
        targetValue = if (animateChart) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "subjectChartAnim"
    )

    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val defaultPrimaryColor = MaterialTheme.colorScheme.primary

    val barColors = listOf(
        Color(0xFF1E88E5), // Lectura - Blue
        Color(0xFF43A047), // Mates - Green
        Color(0xFF00ACC1), // Ciencias - Teal
        Color(0xFFFB8C00), // Sociales - Orange
        Color(0xFF8E24AA)  // Inglés - Purple
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val topPadding = 30f
        val bottomPadding = 60f
        val leftPadding = 40f
        val rightPadding = 20f

        val chartWidth = width - leftPadding - rightPadding
        val chartHeight = height - topPadding - bottomPadding

        // 1. Draw horizontal grid lines (0, 25, 50, 75, 100)
        val steps = 4
        for (i in 0..steps) {
            val scoreVal = (i * 25)
            val y = topPadding + chartHeight * (1f - (scoreVal / 100f))

            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(width - rightPadding, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Grid y-axis text
            drawContext.canvas.nativeCanvas.drawText(
                scoreVal.toString(),
                leftPadding - 8f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = labelColor
                    textSize = 10.dp.toPx()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // 2. Draw bars
        if (subjectAverages.isNotEmpty()) {
            val count = subjectAverages.size
            val slotWidth = chartWidth / count
            val barWidth = slotWidth * 0.55f

            subjectAverages.forEachIndexed { index, item ->
                val score = item.averageScore.toFloat().coerceIn(0f, 100f)
                val barHeight = (score / 100f) * chartHeight * animationProgress
                val xLeft = leftPadding + (index * slotWidth) + (slotWidth - barWidth) / 2f
                val yTop = topPadding + chartHeight - barHeight

                val color = barColors.getOrElse(index) { defaultPrimaryColor }

                // Draw bar background slot
                drawRoundRect(
                    color = color.copy(alpha = 0.1f),
                    topLeft = Offset(xLeft, topPadding),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )

                // Draw filled bar
                drawRoundRect(
                    color = color,
                    topLeft = Offset(xLeft, yTop),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )

                // Draw score value above bar
                if (animationProgress > 0.5f) {
                    val scoreText = String.format(java.util.Locale.US, "%.1f", item.averageScore)
                    drawContext.canvas.nativeCanvas.drawText(
                        scoreText,
                        xLeft + barWidth / 2f,
                        yTop - 8f,
                        android.graphics.Paint().apply {
                            this.color = color.toArgb()
                            this.textSize = 11.dp.toPx()
                            this.isFakeBoldText = true
                            this.textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                // Draw subject label below x-axis
                drawContext.canvas.nativeCanvas.drawText(
                    item.shortName,
                    xLeft + barWidth / 2f,
                    height - bottomPadding / 3f,
                    android.graphics.Paint().apply {
                        this.color = labelColor
                        this.textSize = 10.dp.toPx()
                        this.textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. GLOBAL SCORE DISTRIBUTION HISTOGRAM (JETPACK COMPOSE CANVAS)
// -----------------------------------------------------------------------------

@Composable
private fun ScoreDistributionChartCard(scoreBuckets: List<ScoreBucketCount>) {
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Equalizer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Distribución del Puntaje Global",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Estudiantes por rangos de desempeño",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ScoreDistributionCanvasChart(
                scoreBuckets = scoreBuckets,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
private fun ScoreDistributionCanvasChart(
    scoreBuckets: List<ScoreBucketCount>,
    modifier: Modifier = Modifier
) {
    var animateChart by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateChart = true
    }

    val animationProgress by animateFloatAsState(
        targetValue = if (animateChart) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "histogramAnim"
    )

    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val defaultPrimaryColor = MaterialTheme.colorScheme.primary

    val bucketColors = listOf(
        Color(0xFFD32F2F), // <200 - Red (Bajo)
        Color(0xFFF57C00), // 200-279 - Orange (Medio)
        Color(0xFF1976D2), // 280-349 - Blue (Alto)
        Color(0xFF388E3C)  // 350-500 - Green (Superior)
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val topPadding = 35f
        val bottomPadding = 55f
        val leftPadding = 45f
        val rightPadding = 20f

        val chartWidth = width - leftPadding - rightPadding
        val chartHeight = height - topPadding - bottomPadding

        val maxCount = (scoreBuckets.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)

        // Draw horizontal grid lines
        val steps = 3
        for (i in 0..steps) {
            val countVal = (maxCount * i / steps)
            val y = topPadding + chartHeight * (1f - (countVal.toFloat() / maxCount))

            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(width - rightPadding, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )

            drawContext.canvas.nativeCanvas.drawText(
                countVal.toString(),
                leftPadding - 8f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = labelColor
                    textSize = 10.dp.toPx()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Draw histogram bars
        if (scoreBuckets.isNotEmpty()) {
            val slotWidth = chartWidth / scoreBuckets.size
            val barWidth = slotWidth * 0.65f

            scoreBuckets.forEachIndexed { index, bucket ->
                val barHeight = (bucket.count.toFloat() / maxCount) * chartHeight * animationProgress
                val xLeft = leftPadding + (index * slotWidth) + (slotWidth - barWidth) / 2f
                val yTop = topPadding + chartHeight - barHeight

                val color = bucketColors.getOrElse(index) { defaultPrimaryColor }

                // Draw filled bar
                drawRoundRect(
                    color = color,
                    topLeft = Offset(xLeft, yTop),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Count & % text above bar
                if (animationProgress > 0.4f) {
                    val labelText = "${bucket.count} (${String.format(java.util.Locale.US, "%.0f%%", bucket.percentage)})"
                    drawContext.canvas.nativeCanvas.drawText(
                        labelText,
                        xLeft + barWidth / 2f,
                        yTop - 8f,
                        android.graphics.Paint().apply {
                            this.color = color.toArgb()
                            this.textSize = 10.dp.toPx()
                            this.isFakeBoldText = true
                            this.textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                // Range label below bar
                drawContext.canvas.nativeCanvas.drawText(
                    bucket.label,
                    xLeft + barWidth / 2f,
                    height - bottomPadding / 3f,
                    android.graphics.Paint().apply {
                        this.color = labelColor
                        this.textSize = 10.dp.toPx()
                        this.textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4. GENDER SCORE COMPARISON CARD
// -----------------------------------------------------------------------------

@Composable
private fun GenderComparisonCard(
    maleAvg: Double?,
    femaleAvg: Double?,
    maleCount: Int,
    femaleCount: Int
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.PieChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Comparativa por Género",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Puntaje global promedio según género del estudiante",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Male Card
                GenderScoreItem(
                    genderName = "Masculino (M)",
                    avgScore = maleAvg,
                    studentCount = maleCount,
                    icon = Icons.Rounded.Male,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )

                // Female Card
                GenderScoreItem(
                    genderName = "Femenino (F)",
                    avgScore = femaleAvg,
                    studentCount = femaleCount,
                    icon = Icons.Rounded.Female,
                    color = Color(0xFFD81B60),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GenderScoreItem(
    genderName: String,
    avgScore: Double?,
    studentCount: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = genderName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = avgScore?.let { String.format(java.util.Locale.US, "%.1f", it) } ?: "N/A",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )

            Text(
                text = "$studentCount estudiantes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { ((avgScore ?: 0.0) / 500.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 5. TOP PERFORMING SCHOOLS TABLE
// -----------------------------------------------------------------------------

@Composable
private fun TopSchoolsCard(topSchools: List<SchoolAverage>) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Top Colegios Destacados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Establecimientos con mayor promedio global",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            topSchools.forEachIndexed { index, school ->
                SchoolRowItem(rank = index + 1, school = school)
            }
        }
    }
}

@Composable
private fun SchoolRowItem(
    rank: Int,
    school: SchoolAverage
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val rankTextColor = if (rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(rankColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = rankTextColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = school.schoolName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = listOfNotNull(school.municipality, school.department).joinToString(", "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = String.format(java.util.Locale.US, "%.1f pts", school.averageScore),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// LOADING & EMPTY STATES
// -----------------------------------------------------------------------------

@Composable
private fun AnalyticsLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Calculando métricas analíticas...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnalyticsErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Error al Generar Analítica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cargar Datos")
            }
        }
    }
}

@Composable
private fun AnalyticsEmptyState(onRefresh: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.FolderOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sin Datos para Analítica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Aplique o ajuste los filtros de búsqueda para cargar registros ICFES y visualizar los gráficos y estadísticas.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refrescar Búsqueda")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IcfesAnalyticsScreenPreview() {
    ICFESResultsTheme {
        IcfesAnalyticsScreen(
            summary = AnalyticsSummary(
                totalRecords = 20,
                averageGlobalScore = 295.4,
                highestGlobalScore = 412,
                lowestGlobalScore = 185,
                topPerformingSubject = SubjectAverage("Matemáticas", "Matemáticas", 68.5),
                subjectAverages = listOf(
                    SubjectAverage("Lectura Crítica", "Lectura", 62.4),
                    SubjectAverage("Matemáticas", "Matemáticas", 68.5),
                    SubjectAverage("Ciencias Naturales", "Ciencias", 60.1),
                    SubjectAverage("Sociales y Ciudadanas", "Sociales", 58.7),
                    SubjectAverage("Inglés", "Inglés", 61.2)
                ),
                scoreBuckets = listOf(
                    ScoreBucketCount("<200", "Bajo (<200)", 2, 10f),
                    ScoreBucketCount("200-279", "Medio (200-279)", 6, 30f),
                    ScoreBucketCount("280-349", "Alto (280-349)", 9, 45f),
                    ScoreBucketCount("350-500", "Superior (350-500)", 3, 15f)
                ),
                maleAverageScore = 301.2,
                femaleAverageScore = 289.6,
                maleCount = 11,
                femaleCount = 9,
                topSchools = listOf(
                    SchoolAverage("COLEGIO LORENZO DE ALDANA", "PASTO", "NARIÑO", 5, 345.0),
                    SchoolAverage("INSTITUTO TECNICO INDUSTRIAL", "PASTO", "NARIÑO", 8, 318.5)
                )
            )
        )
    }
}
