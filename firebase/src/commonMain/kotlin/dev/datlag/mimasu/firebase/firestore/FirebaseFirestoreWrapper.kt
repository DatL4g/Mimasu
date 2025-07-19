package dev.datlag.mimasu.firebase.firestore

import co.touchlab.kermit.Logger
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.core.findAroundPositionOrNull
import dev.datlag.mimasu.core.serialization.SerializableImmutableList
import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
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
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val _bookmarkedMovies = MutableStateFlow<SerializableImmutableSet<MovieData>>(
        persistentSetOf()
    )
    private val _bookmarkedShows = MutableStateFlow<SerializableImmutableSet<ShowData>>(
        persistentSetOf()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarkedMovies = _bookmarkedMovies.mapLatest {
        it.ifEmpty { getBookmarkedMovies() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarkedShows = _bookmarkedShows.mapLatest {
        it.ifEmpty { getBookmarkedShows() }
    }

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

    private suspend fun getBookmarkedMovies(): SerializableImmutableSet<MovieData> {
        val uid = authService.currentUser?.uid ?: return persistentSetOf()
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

        return Companion.bookmarkedMovies.async(uid) {
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
        }?.ifEmpty { null }?.filter { it.bookmarked }?.toImmutableSet() ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ).orEmpty().filter { it.bookmarked }.toImmutableSet()
    }

    private suspend fun getBookmarkedShows(): SerializableImmutableSet<ShowData> {
        val uid = authService.currentUser?.uid ?: return persistentSetOf()
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

        return Companion.bookmarkedShows.async(uid) {
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
        }?.ifEmpty { null }?.filter { it.bookmarked }?.toImmutableSet() ?: getOfflineData(
            block = { db ->
                request(db)
            }
        ).orEmpty().filter { it.bookmarked }.toImmutableSet()
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

        _bookmarkedMovies.emit(
            Companion.bookmarkedMovies.asyncPutAndGet(
                key = uid,
                value = movie.mergeWithCollection(getBookmarkedMovies())
            ).toImmutableSet()
        )
    }

    suspend fun bookmark(show: ShowData, db: FirebaseFirestore = firestore): SerializableImmutableSet<ShowData> {
        val uid = authService.currentUser?.uid ?: return _bookmarkedShows.value
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(show.tmdbId.toString())

        getOnlineData(db = db, block = {
            doc.set(show, merge = true) {
                encodeDefaults = false
            }
        })

        return _bookmarkedShows.updateAndGet {
            Companion.bookmarkedShows.asyncPutAndGet(
                key = uid,
                value = show.mergeWithCollection(getBookmarkedShows())
            ).toImmutableSet()
        }
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

    suspend fun episodesFor(tmdbId: Int, seasonNumber: Int): MutableEpisodeData {
        val uid = authService.currentUser?.uid ?: return MutableEpisodeData(persistentListOf())

        if (tmdbId <= 0 || seasonNumber < 0) {
            return MutableEpisodeData(persistentListOf())
        }

        suspend fun request(db: FirebaseFirestore): SerializableImmutableList<ShowData.EpisodeData> {
            return db.collection(ShowData.COLLECTION)
                .document(uid)
                .collection(ShowData.GROUP)
                .document(tmdbId.toString())
                .collection(ShowData.EpisodeData.collectionForSeason(seasonNumber))
                .get().documents.mapNotNull {
                    scopeCatching {
                        it.data<ShowData.EpisodeData?>()
                    }.getOrNull()
                }.toImmutableList()
        }

        val value = showSeasonEpisodeKache.async(
            key = EpisodeCacheKey(
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
            ).orEmpty().let { MutableEpisodeData(it.toImmutableList()) }
        } ?: MutableEpisodeData(persistentListOf())

        return value
    }

    suspend fun updateEpisode(
        tmdbId: Int,
        seasonNumber: Int,
        data: ShowData.EpisodeData,
        db: FirebaseFirestore = firestore
    ) {
        val uid = authService.currentUser?.uid ?: return
        val doc = db.collection(ShowData.COLLECTION)
            .document(uid)
            .collection(ShowData.GROUP)
            .document(tmdbId.toString())
            .collection(ShowData.EpisodeData.collectionForSeason(seasonNumber))
            .document(ShowData.EpisodeData.documentForNumber(data.number))

        getOnlineData(db = db, block = {
            doc.set(data, merge = true) {
                encodeDefaults = false
            }
        })

        val key = EpisodeCacheKey(
            uid = uid,
            showId = tmdbId,
            seasonNumber = seasonNumber
        )
        val cached = showSeasonEpisodeKache.async(key = key)
        val updated = data.mergeWithCollection(cached ?: episodesFor(tmdbId, seasonNumber)).toImmutableList()

        showSeasonEpisodeKache.asyncPutAndGet(
            key = key,
            value = cached?.also { it.emit(updated) } ?: MutableEpisodeData(updated)
        )
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

    class MutableEpisodeData(
        collection: SerializableImmutableList<ShowData.EpisodeData>
    ) : Collection<ShowData.EpisodeData> {

        private val _flow = MutableStateFlow(collection)
        val flow = _flow.asStateFlow()

        internal suspend fun emit(values: SerializableImmutableList<ShowData.EpisodeData>) {
            _flow.emit(values)
        }

        internal fun update(values: SerializableImmutableList<ShowData.EpisodeData>) {
            _flow.update { values }
        }

        override val size: Int
            get() = flow.value.size

        override fun isEmpty(): Boolean {
            return flow.value.isEmpty()
        }

        override fun iterator(): Iterator<ShowData.EpisodeData> {
            return flow.value.iterator()
        }

        override fun contains(element: ShowData.EpisodeData): Boolean {
            return flow.value.contains(element)
        }

        override fun containsAll(elements: Collection<ShowData.EpisodeData>): Boolean {
            return flow.value.containsAll(elements)
        }
    }

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

        private val showSeasonEpisodeKache = InMemoryKache<EpisodeCacheKey, MutableEpisodeData>(
            maxSize = 5L * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = cacheDuration
        }
    }
}
