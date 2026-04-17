package com.example.luontopeli.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.AppDatabase
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.local.entity.WalkSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel tilastonäkymälle.
 * Laskee yhteenvedot kaikista kävelyistä ja löydöistä.
 */
class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val walkDao = db.walkSessionDao()
    private val spotDao = db.natureSpotDao()

    /** Kaikki kävelysessiot tietokannasta */
    val allSessions: StateFlow<List<WalkSession>> = walkDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Kaikki luontolöydöt tietokannasta */
    val allSpots: StateFlow<List<NatureSpot>> = spotDao.getAllSpots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Laskettu yhteenveto tilastoista */
    val summaryStats: StateFlow<SummaryData> = allSessions.map { sessions ->
        SummaryData(
            totalSteps = sessions.sumOf { it.stepCount },
            totalDistanceMeters = sessions.sumOf { it.distanceMeters.toDouble() }.toFloat(),
            walkCount = sessions.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SummaryData())
}

data class SummaryData(
    val totalSteps: Int = 0,
    val totalDistanceMeters: Float = 0f,
    val walkCount: Int = 0
)
