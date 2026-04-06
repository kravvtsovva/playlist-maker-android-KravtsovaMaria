package com.practicum.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.data.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDataAccess {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrack(track: TrackEntity)

    @Update
    suspend fun modifyTrack(track: TrackEntity)

    @Delete
    suspend fun removeTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE isFavorite = 1")
    fun fetchFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE trackId = :trackId")
    fun fetchTrackById(trackId: Long): Flow<TrackEntity?>

    @Query("SELECT CAST(COUNT(*) AS LONG) FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getPlaylistCountForTrack(trackId: Long): Long
}