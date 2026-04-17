package com.example.luontopeli.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.AppDatabase
import com.example.luontopeli.data.local.entity.WalkSession
import com.example.luontopeli.sensor.StepCounterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel kävelyn hallintaan.
 * Hallinnoi kävelylenkin aloittamista, askelten laskentaa ja lopetusta.
 */
class WalkViewModel(application: Application) : AndroidViewModel(application) {

    /** Askelmittarin hallintapalvelu (STEP_DETECTOR-sensori) */
    private val stepManager = StepCounterManager(application)

    /** Room-tietokantainstanssi kävelykertojen tallentamiseen */
    private val db = AppDatabase.getDatabase(application)

    /** Nykyinen kävelykerta (null jos kävely ei ole käynnissä) */
    private val _currentSession = MutableStateFlow<WalkSession?>(null)
    val currentSession: StateFlow<WalkSession?> = _currentSession.asStateFlow()

    /** Onko kävely parhaillaan käynnissä */
    private val _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking.asStateFlow()

    /**
     * Aloittaa uuden kävelylenkin.
     */
    fun startWalk() {
        if (_isWalking.value) return

        val session = WalkSession()
        _currentSession.value = session
        _isWalking.value = true

        stepManager.startStepCounting {
            _currentSession.update { current ->
                current?.copy(
                    stepCount = current.stepCount + 1,
                    distanceMeters = current.distanceMeters + StepCounterManager.STEP_LENGTH_METERS
                )
            }
        }
    }

    /**
     * Lopettaa käynnissä olevan kävelylenkin.
     */
    fun stopWalk() {
        stepManager.stopStepCounting()
        _isWalking.value = false

        _currentSession.update { it?.copy(
            endTime = System.currentTimeMillis(),
            isActive = false
        )}

        viewModelScope.launch {
            _currentSession.value?.let { session ->
                db.walkSessionDao().insert(session)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepManager.stopAll()
    }
}

fun formatDistance(meters: Float): String {
    return if (meters < 1000f) {
        "${meters.toInt()} m"
    } else {
        "%.1f km".format(meters / 1000f)
    }
}

fun formatDuration(startTime: Long, endTime: Long = System.currentTimeMillis()): String {
    val seconds = (endTime - startTime) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}min"
        minutes > 0 -> "${minutes}min ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
