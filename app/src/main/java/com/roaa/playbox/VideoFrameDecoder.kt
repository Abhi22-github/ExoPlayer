package com.roaa.playbox

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.decode.ImageSource
import coil.fetch.SourceResult
import coil.request.Options

class VideoFrameDecoder(
    private val source: ImageSource,
    private val options: Options
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(options.context, source.metadata as Uri)

            // Get frame at 1 second (or first frame)
            val bitmap = retriever.getFrameAtTime(
                1_000_000, // 1 second in microseconds
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: retriever.frameAtTime

            if (bitmap != null) {
                DecodeResult(
                    drawable = bitmap.toDrawable(options.context.resources),
                    isSampled = false
                )
            } else {
                throw IllegalStateException("Failed to decode video frame")
            }
        } finally {
            retriever.release()
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            // Only handle video URIs
            val uri = result.source.metadata as? Uri ?: return null
            val mimeType = options.context.contentResolver.getType(uri)

            return if (mimeType?.startsWith("video/") == true) {
                VideoFrameDecoder(result.source, options)
            } else {
                null
            }
        }
    }
}

private fun Bitmap.toDrawable(resources: android.content.res.Resources): android.graphics.drawable.BitmapDrawable {
    return android.graphics.drawable.BitmapDrawable(resources, this)
}