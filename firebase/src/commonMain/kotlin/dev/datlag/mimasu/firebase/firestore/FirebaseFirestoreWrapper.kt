package dev.datlag.mimasu.firebase.firestore

import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

/**
 * Wrapper for Firebase Firestore to simplify requests and lower usage.
 */
data class FirebaseFirestoreWrapper(
    private val app: FirebaseApp = Firebase.app
) {
    /**
     * Locks online/offline usage to subsequent tasks.
     */
    private val networkMutex = Mutex()

    private val firestore: FirebaseFirestore
        get() = Firebase.firestore(app)

    private val auth: FirebaseAuth
        get() = Firebase.auth(app)

    suspend fun <T> getOfflineData(block: suspend (FirebaseFirestore) -> T): T? {
        return suspendCatching {
            networkMutex.withLock {
                firestore.disableNetwork()
                block(firestore)
            }
        }.getOrNull()
    }

    suspend fun <T> getOnlineData(block: suspend (FirebaseFirestore) -> T): T? {
        return suspendCatching {
            networkMutex.withLock {
                firestore.enableNetwork()
                block(firestore)
            }
        }.getOrNull()
    }

    suspend fun getBookmarkedMovies(): List<MovieData> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        suspend fun request(db: FirebaseFirestore): List<MovieData> {
            return db.collection(MovieData.COLLECTION).document(uid).collection(MovieData.GROUP).where {
                all(
                    MovieData.BOOKMARKED equalTo true,
                    MovieData.TMDB_ID greaterThan 0
                )
            }.orderBy(MovieData.LAST_UPDATED, Direction.DESCENDING).get().documents.map { it.data<MovieData>() }
        }

        val time = bookmarkedMoviesRequested.value
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData { db ->
                request(db)
            }?.ifEmpty { null }?.also { bookmarkedMoviesRequested.value = Clock.System.now().epochSeconds } ?: request(firestore)
        } else {
            getOfflineData { db ->
                request(db)
            }?.ifEmpty { null } ?: getOnlineData { db ->
                request(db)
            }?.ifEmpty { null }?.also { bookmarkedMoviesRequested.value = Clock.System.now().epochSeconds } ?: request(firestore)
        }
    }

    suspend fun getBookmarkedShows(): List<ShowData> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        suspend fun request(db: FirebaseFirestore): List<ShowData> {
            return db.collection(ShowData.COLLECTION).document(uid).collection(ShowData.GROUP).where {
                all(
                    ShowData.BOOKMARKED equalTo true,
                    ShowData.TMDB_ID greaterThan 0
                )
            }.orderBy(MovieData.LAST_UPDATED, Direction.DESCENDING).get().documents.map { it.data<ShowData>() }
        }

        val time = bookmarkedShowsRequested.value
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData { db ->
                request(db)
            }?.ifEmpty { null }?.also { bookmarkedShowsRequested.value = Clock.System.now().epochSeconds } ?: request(firestore)
        } else {
            getOfflineData { db ->
                request(db)
            }?.ifEmpty { null } ?: getOnlineData { db ->
                request(db)
            }?.ifEmpty { null }?.also { bookmarkedShowsRequested.value = Clock.System.now().epochSeconds } ?: request(firestore)
        }
    }

    suspend fun bookmark(movie: MovieData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(MovieData.COLLECTION)
            .document(uid)
            .collection(MovieData.GROUP)
            .document(movie.tmdbId.toString())

        getOnlineData {
            doc.set(movie, merge = true) {
                encodeDefaults = false
            }
        }
    }

    suspend fun bookmark(show: ShowData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        }
    }

    suspend fun isMovieBookmarked(tmdbId: Int): Boolean {
        return getBookmarkedMovies().any { it.tmdbId == tmdbId }
    }

    suspend fun isShowBookmarked(tmdbId: Int): Boolean {
        return getBookmarkedShows().any { it.tmdbId == tmdbId }
    }

    suspend fun selectSeason(show: ShowData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        }
    }

    suspend fun getSeason(tmdbId: Int, offlineOnly: Boolean = false): Int? {
        val uid = auth.currentUser?.uid ?: return null
        suspend fun request(db: FirebaseFirestore): Int? {
            return db.collection(ShowData.COLLECTION)
                .document(uid)
                .collection(ShowData.GROUP)
                .document(tmdbId.toString())
                .get()
                .data<ShowData>().season
        }

        if (offlineOnly) {
            return getOfflineData { db ->
                request(db)
            }
        }

        val time = seasonShowsRequested.value[tmdbId] ?: 0L
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData { db ->
                request(db)
            }?.takeIf { it >= 0 }?.also {
                val map = seasonShowsRequested.value
                map[tmdbId] = Clock.System.now().epochSeconds

                seasonShowsRequested.value = map
            } ?: request(firestore)?.takeIf { it >= 0 }
        } else {
            getOfflineData { db ->
                request(db)
            }?.takeIf { it >= 0 } ?: getOnlineData { db ->
                request(db)
            }?.takeIf { it >= 0 }?.also {
                val map = seasonShowsRequested.value
                map[tmdbId] = Clock.System.now().epochSeconds

                seasonShowsRequested.value = map
            } ?: request(firestore)?.takeIf { it >= 0 }
        }
    }

    companion object {
        private val cacheDuration = 12.hours

        private val bookmarkedMoviesRequested = atomic(0L)
        private val bookmarkedShowsRequested = atomic(0L)

        private val seasonShowsRequested = atomic(hashMapOf<Int, Long>())
    }
}
