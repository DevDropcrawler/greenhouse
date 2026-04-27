package nz.keeleysgreenhouse.app.util

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class YouTubeTest {

    @Test
    fun videoIntentsPreferYoutubeAppThenWatchUrl() {
        val intents = YouTube.videoIntents("dQw4w9WgXcQ")
        assertEquals(2, intents.size)
        assertEquals(Intent.ACTION_VIEW, intents[0].action)
        assertEquals("vnd.youtube:dQw4w9WgXcQ", intents[0].dataString)
        assertEquals("https://www.youtube.com/watch?v=dQw4w9WgXcQ", intents[1].dataString)
    }

    @Test
    fun searchIntentEncodesQuery() {
        val intent = YouTube.searchIntent("growing tomatoes greenhouse new zealand")
        val expected = "https://www.youtube.com/results?search_query=" +
            "growing+tomatoes+greenhouse+new+zealand"
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(expected, intent.dataString)
    }

    @Test
    fun thumbnailUrlFollowsYouTubeMqDefaultPattern() {
        assertEquals(
            "https://img.youtube.com/vi/abc123/mqdefault.jpg",
            YouTube.thumbnailUrl("abc123")
        )
    }

    @Test
    fun searchIntentHasNewTaskFlag() {
        val intent = YouTube.searchIntent("kale")
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }
}
