package com.practicum.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.data.entity.PlaylistEntity
import com.practicum.playlistmaker.data.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

data class PlaylistWithTracks(
    @androidx.room.Embedded val playlist: PlaylistEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "trackId",
        associateBy = androidx.room.Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "trackId"
        )
    )
    val tracks: List<com.practicum.playlistmaker.data.entity.TrackEntity>
)

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun removePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT * FROM playlists")
    fun fetchAllPlaylists(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun fetchPlaylistDetails(playlistId: Long): Flow<PlaylistWithTracks?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :id")
    suspend fun clearCrossRefsForPlaylist(id: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeSpecificCrossRef(playlistId: Long, trackId: Long)
}