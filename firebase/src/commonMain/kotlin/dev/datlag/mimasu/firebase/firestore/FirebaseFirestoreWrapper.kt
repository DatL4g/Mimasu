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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

    suspend fun getBookmarkedMovies(db: FirebaseFirestore = firestore): List<MovieData> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return db.collection(MovieData.COLLECTION).document(uid).collection(MovieData.GROUP).where {
            all(
                MovieData.BOOKMARKED equalTo true,
                MovieData.TMDB_ID greaterThan 0
            )
        }.orderBy(MovieData.LAST_UPDATED, Direction.DESCENDING).get().documents.map { it.data<MovieData>() }
    }

    suspend fun bookmark(movie: MovieData, db: FirebaseFirestore = firestore) {
        val uid = auth.currentUser?.uid ?: return
        val doc = db.collection(MovieData.COLLECTION)
            .document(uid)
            .collection(MovieData.GROUP)
            .document(movie.tmdbId.toString())

        doc.set(movie, merge = true) {
            encodeDefaults = false
        }
    }
}
