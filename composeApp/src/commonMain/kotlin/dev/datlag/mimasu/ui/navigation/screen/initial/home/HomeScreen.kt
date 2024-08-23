package dev.datlag.mimasu.ui.navigation.screen.initial.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.common.githubAuthParams
import dev.datlag.mimasu.firebase.common.githubHandle
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val trendingMovies = component.trendingMovies.collectAsLazyPagingItems()
    val trendingPeople = component.trendingPeople.collectAsLazyPagingItems()
    val authService = component.authService
    val googleProvider = component.googleAuthProvider
    val githubProvider = component.githubAuthProvider

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            Text(text = "Trending Movies")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(trendingMovies.itemCount) { index ->
                    PlatformCard(
                        modifier = Modifier
                            .width(140.dp)
                            .height(200.dp),
                        onClick = { }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = trendingMovies[index]!!.posterPicture,
                            contentDescription = trendingMovies[index]?.title,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = trendingMovies[index]!!.posterPictureW500,
                                contentScale = ContentScale.Crop
                            )
                        )
                    }
                }
            }
        }
        item {
            val user by authService.user.collectAsStateWithLifecycle(component.authService.currentUser)

            LaunchedEffect(user) {
                user?.providerData?.forEach {
                    Napier.e("Id: ${it.providerId}, User: ${it.uid}")
                }
            }

            Column {
                Text("User: ${user?.displayName?.ifBlank { null } ?: user?.email?.ifBlank { null } ?: user?.uid}")
                Text("Provider: ${user?.providerId}")
                Text("GitHub: ${user.githubHandle()}")
            }
        }
        if (googleProvider != null) {
            item {
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        scope.launch {
                            googleProvider.signIn(false)
                        }
                    }
                ) {
                    Text(
                        text = "Google"
                    )
                }
            }
        } else {
            item {
                Text("No Google Auth Provider")
            }
        }
        if (githubProvider != null) {
            item {
                val scope = rememberCoroutineScope()
                val githubAuthParams = Platform.githubAuthParams()

                Button(
                    onClick = {
                        scope.launch {
                            githubProvider.signIn(githubAuthParams).onFailure {
                                Napier.e("GitHub error", it)
                            }.onSuccess {
                                Napier.e("GitHub success")
                            }
                        }
                    }
                ) {
                    Text(text = "GitHub")
                }
            }
        } else {
            item {
                Text("No GitHub Auth Provider")
            }
        }
    }
}