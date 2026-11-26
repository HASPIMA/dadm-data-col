package com.edu.unal.haspima.icfes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.edu.unal.haspima.icfes.ui.navigation.IcfesNavigation
import com.edu.unal.haspima.icfes.ui.theme.ICFESResultsTheme
import com.edu.unal.haspima.icfes.ui.viewmodel.IcfesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: IcfesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICFESResultsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IcfesNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
