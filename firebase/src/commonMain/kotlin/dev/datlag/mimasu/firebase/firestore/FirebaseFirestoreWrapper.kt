package dev.datlag.mimasu.firebase.firestore

import dev.datlag.tooling.async.scopeCatching
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

    suspend fun <T> getOfflineData(
        db: FirebaseFirestore = firestore,
        block: suspend (FirebaseFirestore) -> T,
        onFailure: suspend (FirebaseFirestore) -> T? = { null }
    ): T? {
        return suspendCatching {
            networkMutex.withLock {
                db.disableNetwork()
                block(db)
            }
        }.recoverCatching { onFailure(db) }.getOrNull()
    }

    suspend fun <T> getOnlineData(
        db: FirebaseFirestore = firestore,
        block: suspend (FirebaseFirestore) -> T,
        onFailure: suspend (FirebaseFirestore) -> T? = { null }
    ): T? {
        return suspendCatching {
            networkMutex.withLock {
                db.enableNetwork()
                block(db)
            }
        }.recoverCatching { onFailure(db) }.getOrNull()
    }

    suspend fun getBookmarkedMovies(): List<MovieData> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        suspend fun request(db: FirebaseFirestore): List<MovieData> {
            return db.collection(MovieData.COLLECTION).document(uid).collection(MovieData.GROUP).where {
                all(
                    MovieData.BOOKMARKED equalTo true,
                    MovieData.TMDB_ID greaterThan 0
                )
            }.orderBy(MovieData.LAST_UPDATED, Direction.DESCENDING).get().documents.mapNotNull {
                scopeCatching {
                    it.data<MovieData?>()
                }.getOrNull()
            }
        }

        val time = bookmarkedMoviesRequested.value
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData(
                block = { db ->
                    request(db).ifEmpty { null }?.also { bookmarkedMoviesRequested.value = Clock.System.now().epochSeconds }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            ).orEmpty()
        } else {
            getOfflineData(
                block = { db ->
                    request(db)
                },
                onFailure = { db ->
                    getOnlineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )?.ifEmpty { null }?.also { bookmarkedMoviesRequested.value = Clock.System.now().epochSeconds }
                }
            ).orEmpty()
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
            }.orderBy(MovieData.LAST_UPDATED, Direction.DESCENDING).get().documents.mapNotNull {
                scopeCatching {
                    it.data<ShowData?>()
                }.getOrNull()
            }
        }

        val time = bookmarkedShowsRequested.value
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData(
                block = { db ->
                    request(db).ifEmpty { null }?.also { bookmarkedShowsRequested.value = Clock.System.now().epochSeconds }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            ).orEmpty()
        } else {
            getOfflineData(
                block = { db ->
                    request(db)
                },
                onFailure = { db ->
                    getOnlineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )?.ifEmpty { null }?.also { bookmarkedShowsRequested.value = Clock.System.now().epochSeconds }
                }
            ).orEmpty()
        }
    }

    suspend fun bookmark(movie: MovieData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(MovieData.COLLECTION)
            .document(uid)
            .collection(MovieData.GROUP)
            .document(movie.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(movie, merge = true) {
                encodeDefaults = false
            }
        })
    }

    suspend fun bookmark(show: ShowData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        })
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

        getOnlineData(db = db, block = {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        })
    }

    suspend fun getSeason(tmdbId: Int, offlineOnly: Boolean = false): Int? {
        val uid = auth.currentUser?.uid ?: return null
        suspend fun request(db: FirebaseFirestore): Int? {
            val snapshot = db.collection(ShowData.COLLECTION)
                .document(uid)
                .collection(ShowData.GROUP)
                .document(tmdbId.toString())
                .get()

            return suspendCatching {
                snapshot.data<ShowData?>()?.season
            }.getOrNull()
        }

        if (offlineOnly) {
            return getOfflineData(block = { db ->
                request(db)
            })
        }

        val time = seasonShowsRequested.value[tmdbId] ?: 0L
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData(
                block = { db ->
                    request(db)?.takeIf { it >= 0 }?.also {
                        val map = seasonShowsRequested.value
                        map[tmdbId] = Clock.System.now().epochSeconds

                        seasonShowsRequested.value = map
                    }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            )?.takeIf { it >= 0 }
        } else {
            getOfflineData(
                block = { db ->
                    request(db)
                },
                onFailure = { db ->
                    getOnlineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )?.takeIf { it >= 0 }?.also {
                        val map = seasonShowsRequested.value
                        map[tmdbId] = Clock.System.now().epochSeconds

                        seasonShowsRequested.value = map
                    }
                }
            )?.takeIf { it >= 0 }
        }
    }

    suspend fun getUserData(): UserData {
        val uid = auth.currentUser?.uid ?: return UserData.Default
        suspend fun request(db: FirebaseFirestore): UserData? {
            val snapshot = db.collection(UserData.COLLECTION)
                .document(uid)
                .get()

            return suspendCatching {
                snapshot.data<UserData?>()
            }.getOrNull()
        }

        val time = userDataRequested.value
        return if (time <= 0L || Clock.System.now().minus(cacheDuration).epochSeconds > time) {
            getOnlineData(
                block = { db ->
                    request(db)?.also { userDataRequested.value = Clock.System.now().epochSeconds }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            ) ?: UserData.Default
        } else {
            getOfflineData(
                block = { db ->
                    request(db)
                },
                onFailure = { db ->
                    getOnlineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )?.also { userDataRequested.value = Clock.System.now().epochSeconds }
                }
            ) ?: UserData.Default
        }
    }

    companion object {
        private val cacheDuration = 12.hours

        private val bookmarkedMoviesRequested = atomic(0L)
        private val bookmarkedShowsRequested = atomic(0L)

        private val seasonShowsRequested = atomic(hashMapOf<Int, Long>())

        private val userDataRequested = atomic(0L)
    }
}
