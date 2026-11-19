package com.edu.unal.haspima.icfes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme

@Composable
fun PaginationFooter(
    currentPage: Int,
    pageSize: Int,
    currentRecordsCount: Int,
    isLoading: Boolean,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Page Button
            OutlinedButton(
                onClick = onPreviousPage,
                enabled = currentPage > 1 && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Página anterior"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Anterior")
            }

            // Page Information
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Página $currentPage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (currentRecordsCount > 0) {
                    val start = (currentPage - 1) * pageSize + 1
                    val end = start + currentRecordsCount - 1
                    Text(
                        text = "Registros $start - $end",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Next Page Button
            OutlinedButton(
                onClick = onNextPage,
                enabled = currentRecordsCount >= pageSize && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Siguiente")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Página siguiente"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaginationFooterPreview() {
    ICFESResultsTheme {
        PaginationFooter(
            currentPage = 2,
            pageSize = 20,
            currentRecordsCount = 20,
            isLoading = false,
            onPreviousPage = {},
            onNextPage = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
