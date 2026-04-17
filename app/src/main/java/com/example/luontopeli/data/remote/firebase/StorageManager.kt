package com.example.luontopeli.data.remote.firebase

/**
 * Offline-tilassa toimiva tallennushallinta (no-op).
 */
class StorageManager {
    suspend fun uploadImage(localFilePath: String, spotId: String): Result<String> {
        return Result.success(localFilePath)
    }
    suspend fun deleteImage(spotId: String): Result<Unit> = Result.success(Unit)
}
