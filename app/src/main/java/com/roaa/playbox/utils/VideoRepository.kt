package com.roaa.playbox.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.roaa.playbox.models.VideoFolder
import com.roaa.playbox.models.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    suspend fun getAllVideo(): List<VideoFolder> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<VideoItem>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val selection = """
            ${MediaStore.Video.Media.DURATION} > 0 AND
            ${MediaStore.Video.Media.SIZE} > 102400 AND
            ${MediaStore.Video.Media.MIME_TYPE} LIKE 'video/%'
            """.trimIndent()

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val bucketId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketName =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                //val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateColumn)
                //val mimeType = cursor.getString(mimeColumn)
                val path = cursor.getString(pathColumn)
                val bucketId = cursor.getLong(bucketId)
                //val bucketName = cursor.getString(bucketName)

                val name = cursor.getString(nameColumn) ?: "Unknown"
                val mimeType = cursor.getString(mimeColumn) ?: "video/*"
                val bucketName = cursor.getString(bucketName) ?: "Internal Storage"

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val thumbnailUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10+, use the video URI directly with Coil
                    contentUri
                } else {
                    // For older versions, try to get thumbnail from MediaStore
                    ContentUris.withAppendedId(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        id
                    )
                }

                videos.add(
                    VideoItem(
                        id = id,
                        name = name,
                        uri = contentUri,
                        duration = duration,
                        size = size,
                        dateAdded = dateAdded,
                        mimeType = mimeType,
                        path = path,
                        thumbnailUri = thumbnailUri,
                        bucketId = bucketId,
                        bucketName = bucketName
                    )
                )
            }
        }

        videos.groupBy { it.bucketId }
            .map { (bucketId, videoItems) ->
                VideoFolder(
                    bucketId = bucketId,
                    bucketName = videoItems.first().bucketName,
                    videos = videoItems
                )
            }
            .sortedBy { folder ->
                folder.videos.maxOf { it.dateAdded }
            }
    }

    fun getVideoItemFromUri(uri: Uri): VideoItem? {

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        val resolver = context.contentResolver

        resolver.query(uri, projection, null, null, null)?.use { cursor ->

            if (cursor.moveToFirst()) {

                val id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))

                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))

                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))

                val duration =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))

                val dateAdded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))

                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))

                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))

                val bucketId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID))

                val bucketName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))

                return VideoItem(
                    id = id,
                    name = name,
                    uri = uri,
                    size = size,
                    duration = duration,
                    dateAdded = dateAdded,
                    mimeType = mimeType,
                    path = path,
                    bucketId = bucketId,
                    bucketName = bucketName
                )
            }
        }

        return null
    }

}