package nz.keeleysgreenhouse.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object YouTube {

    fun videoIntents(videoId: String): List<Intent> = listOf(
        Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )

    fun searchIntent(query: String): Intent {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/results?search_query=$encoded")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun openVideo(context: Context, videoId: String): Boolean {
        for (intent in videoIntents(videoId)) {
            try {
                context.startActivity(intent)
                return true
            } catch (_: ActivityNotFoundException) {
                // try next fallback
            }
        }
        return false
    }

    fun openSearch(context: Context, query: String): Boolean = try {
        context.startActivity(searchIntent(query))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }

    fun thumbnailUrl(videoId: String): String =
        "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
}
