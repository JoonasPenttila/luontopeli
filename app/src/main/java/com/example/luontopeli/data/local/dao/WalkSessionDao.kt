package com.example.luontopeli.data.local.dao

import androidx.room.*
import com.example.luontopeli.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO (Data Access Object) kävelykertojen tietokantaoperaatioille.
 */
@Dao
interface WalkSessionDao {

    /**
     * Lisää uuden kävelykerran tietokantaan.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WalkSession): Long

    /**
     * Hakee kaikki kävelykerrat aloitusajan mukaan laskevassa järjestyksessä.
     */
    @Query("SELECT * FROM walk_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WalkSession>>

    /**
     * Hakee parhaillaan aktiivisen kävelykerran.
     */
    @Query("SELECT * FROM walk_sessions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSession(): WalkSession?

    /**
     * Päivittää olemassa olevan kävelykerran tiedot.
     */
    @Update
    suspend fun update(session: WalkSession)

    /**
     * Poistaa kävelykerran tietokannasta.
     */
    @Delete
    suspend fun delete(session: WalkSession)
}
