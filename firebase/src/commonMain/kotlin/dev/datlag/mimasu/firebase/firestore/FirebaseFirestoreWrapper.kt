package dev.datlag.mimasu.firebase.firestore

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.kache.asyncDelete
import dev.datlag.mimasu.kache.asyncPutAndGet
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Source
import dev.gitlive.firebase.firestore.firestore
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.hours

/**
 * Wrapper for Firebase Firestore to simplify requests and lower usage.
 */
data class FirebaseFirestoreWrapper(
    private val app: FirebaseApp = Firebase.app,
    private val authService: FirebaseAuthService
) {
    /**
     * Locks online/offline usage to subsequent tasks.
     */
    private val networkMutex = Mutex()

    private val firestore: FirebaseFirestore
        get() = Firebase.firestore(app)

    private suspend fun <T> getOfflineData(
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

    private suspend fun <T> getOnlineData(
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

    suspend fun getBookmarkedMovies(): Collection<MovieData> {
        val uid = authService.currentUser?.uid ?: return emptyList()
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

        return bookmarkedMovies.async(uid) {
            getOnlineData(
                block = { db ->
                    request(db).ifEmpty { null }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            )
        }?.ifEmpty { null }?.filter { it.bookmarked } ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ).orEmpty().filter { it.bookmarked }
    }

    suspend fun getBookmarkedShows(): Collection<ShowData> {
        val uid = authService.currentUser?.uid ?: return emptyList()
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

        return bookmarkedShows.async(uid) {
            getOnlineData(
                block = { db ->
                    request(db).ifEmpty { null }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            )
        }?.ifEmpty { null }?.filter { it.bookmarked } ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ).orEmpty().filter { it.bookmarked }
    }

    suspend fun bookmark(movie: MovieData, db: FirebaseFirestore = firestore) {
        val uid = authService.currentUser?.uid ?: return
        val doc = db.collection(MovieData.COLLECTION)
            .document(uid)
            .collection(MovieData.GROUP)
            .document(movie.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(movie, merge = true) {
                encodeDefaults = false
            }
        })

        bookmarkedMovies.asyncPutAndGet(uid, movie.mergeWithCollection(getBookmarkedMovies()))
    }

    suspend fun bookmark(show: ShowData, db: FirebaseFirestore = firestore) {
        val uid = authService.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        })

        bookmarkedShows.asyncPutAndGet(uid, show.mergeWithCollection(getBookmarkedShows()))
    }

    suspend fun isMovieBookmarked(tmdbId: Int): Boolean {
        return getBookmarkedMovies().any { it.tmdbId == tmdbId && it.bookmarked }
    }

    suspend fun isShowBookmarked(tmdbId: Int): Boolean {
        return getBookmarkedShows().any { it.tmdbId == tmdbId && it.bookmarked }
    }

    suspend fun selectSeason(show: ShowData, db: FirebaseFirestore = firestore) {
        val uid = authService.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        })

        val seasonNumber = show.season
        val key = SeasonCacheKey(
            uid = uid,
            showId = show.tmdbId
        )
        if (seasonNumber == null) {
            showSeasonKache.asyncDelete(key)
        } else {
            showSeasonKache.asyncPutAndGet(key, seasonNumber)
        }
    }

    suspend fun getSeason(tmdbId: Int, offlineOnly: Boolean = false): Int? {
        val uid = authService.currentUser?.uid ?: return null
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
            return showSeasonKache.async(
                SeasonCacheKey(
                    uid = uid,
                    showId = tmdbId
                )
            ) {
                getOfflineData(block = { db ->
                    request(db)
                })
            }?.takeIf { it >= 0 }
        }

        return showSeasonKache.async(
            SeasonCacheKey(
                uid = uid,
                showId = tmdbId
            )
        ) {
            getOnlineData(
                block = { db ->
                    request(db)?.takeIf { it >= 0 }
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
        } ?: getOfflineData(
            block = { db ->
                request(db)
            }
        )?.takeIf { it >= 0 }
    }

    suspend fun getUserData(): UserData {
        val uid = authService.currentUser?.uid ?: return UserData.Default
        suspend fun request(db: FirebaseFirestore): UserData? {
            val snapshot = db.collection(UserData.COLLECTION)
                .document(uid)
                .get()

            return suspendCatching {
                snapshot.data<UserData?>()
            }.getOrNull()
        }

        return userDataKache.async(uid) {
            getOnlineData(
                block = { db ->
                    request(db)
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
        } ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ) ?: UserData.Default
    }

    suspend fun deleteUserData(user: User) {
        val uid = user.uid.ifBlank { null } ?: authService.currentUser?.uid ?: return

        getOnlineData(
            block = { db ->
                suspendCatching { db.collection(UserData.COLLECTION).document(uid).delete() }
                suspendCatching { db.collection(ShowData.COLLECTION).document(uid).delete() }
                suspendCatching { db.collection(MovieData.COLLECTION).document(uid).delete() }
            }
        )
        userDataKache.asyncDelete(uid)
    }

    suspend fun episodesFor(tmdbId: Int, seasonNumber: Int): Collection<ShowData.EpisodeData> {
        val uid = authService.currentUser?.uid ?: return emptyList()

        if (tmdbId <= 0 || seasonNumber < 0) {
            return emptyList()
        }

        suspend fun request(db: FirebaseFirestore): List<ShowData.EpisodeData> {
            return db.collection(ShowData.COLLECTION)
                .document(uid)
                .collection(ShowData.GROUP)
                .document(tmdbId.toString())
                .collection(ShowData.EpisodeData.collectionForSeason(seasonNumber))
                .get().documents.mapNotNull {
                    scopeCatching {
                        it.data<ShowData.EpisodeData?>()
                    }.getOrNull()
                }
        }

        return showSeasonEpisodeKache.async(
            EpisodeCacheKey(
                uid = uid,
                showId = tmdbId,
                seasonNumber = seasonNumber
            )
        ) {
            getOnlineData(
                block = { db ->
                    request(db).ifEmpty { null }
                },
                onFailure = { db ->
                    getOfflineData(
                        db = db,
                        block = {
                            request(it)
                        }
                    )
                }
            )?.ifEmpty { null }
        }?.ifEmpty { null } ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ).orEmpty()
    }

    @Serializable
    data class SeasonCacheKey(
        val uid: String,
        val showId: Int
    )

    @Serializable
    data class EpisodeCacheKey(
        val uid: String,
        val showId: Int,
        val seasonNumber: Int
    )

    companion object {
        private val cacheDuration = 12.hours

        private val bookmarkedMovies = InMemoryKache<String, Collection<MovieData>>(
            maxSize = 5L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }

        private val bookmarkedShows = InMemoryKache<String, Collection<ShowData>>(
            maxSize = 5L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }

        private val showSeasonKache = InMemoryKache<SeasonCacheKey, Int>(
            maxSize = 2L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }

        private val userDataKache = InMemoryKache<String, UserData>(
            maxSize = 1L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }

        private val showSeasonEpisodeKache = InMemoryKache<EpisodeCacheKey, Collection<ShowData.EpisodeData>>(
            maxSize = 5L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }
    }
}
