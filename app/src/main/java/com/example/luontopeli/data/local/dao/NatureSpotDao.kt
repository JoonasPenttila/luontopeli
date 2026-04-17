package com.example.luontopeli.data.local.dao

import androidx.room.*
import com.example.luontopeli.data.local.entity.NatureSpot
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) luontolöytöjen tietokantaoperaatioille.
 */
@Dao
interface NatureSpotDao {

    /** Lisää uusi löytö tai korvaa olemassa oleva samalla ID:llä */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: NatureSpot): Long

    /** Palauttaa kaikki löydöt aikajärjestyksessä */
    @Query("SELECT * FROM nature_spots ORDER BY timestamp DESC")
    fun getAllSpots(): Flow<List<NatureSpot>>

    /** Palauttaa löydöt joilla on validi GPS-sijainti */
    @Query("SELECT * FROM nature_spots WHERE latitude != 0.0")
    fun getSpotsWithLocation(): Flow<List<NatureSpot>>

    /** Hakee synkronoimattomat löydöt */
    @Query("SELECT * FROM nature_spots WHERE synced = 0")
    suspend fun getUnsyncedSpots(): List<NatureSpot>

    /** Merkitsee löydön synkronoiduksi */
    @Query("UPDATE nature_spots SET synced = 1, imageFirebaseUrl = :url WHERE id = :id")
    suspend fun markSynced(id: String, url: String)

    /** Poistaa löydön tietokannasta */
    @Delete
    suspend fun delete(spot: NatureSpot)
}
