package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.CreationRepository
import com.example.ui.screens.EthioCreatorApp
import com.example.ui.theme.EthioTheme
import com.example.ui.viewmodel.EthioCreatorViewModel
import com.example.ui.viewmodel.EthioCreatorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core Database & Repository Initializations
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CreationRepository(database.creationDao())

        // ViewModel compilation
        val viewModelFactory = EthioCreatorViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[EthioCreatorViewModel::class.java]

        setContent {
            EthioTheme {
                EthioCreatorApp(viewModel)
            }
        }
    }
}
