package dev.datlag.mimasu.core

data object YouTubeUtils {

    private const val VIDEO_ID_GROUP = "videoId"
    private val URL_REGEX = "^((?:https?:)?//)?((?:www|m|music)\\.)?(youtube(?:-nocookie)?\\.com|youtu.be)(/(?:[\\w-]+\\?v=|embed/|live/|v/)?)(?<$VIDEO_ID_GROUP>[\\w-]+)(\\S+)?$".toRegex(RegexOption.IGNORE_CASE)
    private val FALLBACK_VIDEO_ID_REGEX = "^[\\w-]{11}$".toRegex(RegexOption.IGNORE_CASE)

    fun matches(url: String): Boolean {
        return URL_REGEX.matches(url)
    }

    fun videoIdFrom(url: String): String? {
        return URL_REGEX.find(url.trim())?.groups?.get(VIDEO_ID_GROUP)?.value?.trim()?.ifBlank { null }
    }

    fun videoIdAsItOrFromUrl(value: String): String? {
        return videoIdFrom(value) ?: FALLBACK_VIDEO_ID_REGEX.find(value.trim())?.value?.trim()?.ifBlank { null }
    }

    fun videoUrl(urlOrId: String): String? {
        return if (matches(urlOrId)) {
            urlOrId
        } else {
            videoIdAsItOrFromUrl(urlOrId)?.let {
                "https://youtube.com/watch?v=$it"
            }
        }
    }

    fun thumbnailsForId(videoId: String): Set<String> {
        return setOf(
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg", // highest, working for all
            "https://img.youtube.com/vi/$videoId/mqdefault.jpg", // medium, working for all
            "https://img.youtube.com/vi/$videoId/default.jpg", // lowest, working for all
            "https://img.youtube.com/vi/$videoId/maxresdefault.jpg", // highest, not working for all
            "https://img.youtube.com/vi/$videoId/sddefault.jpg" // highest, not working for all
        )
    }
}