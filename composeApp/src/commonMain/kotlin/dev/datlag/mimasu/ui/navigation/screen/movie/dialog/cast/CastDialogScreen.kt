package dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Boy
import androidx.compose.material.icons.rounded.Girl
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Man
import androidx.compose.material.icons.rounded.Man4
import androidx.compose.material.icons.rounded.NoAdultContent
import androidx.compose.material.icons.rounded.Woman
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.common.isFullyExpandedOrTargeted
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tolgee.kodeinStringResource
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformIcon
import dev.datlag.tooling.compose.platform.PlatformIconButton
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.ProvideNonTvContentColor
import dev.datlag.tooling.compose.platform.typography
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.close
import mimasu.composeapp.generated.resources.movie_character_adult_content
import mimasu.composeapp.generated.resources.movie_character_department
import mimasu.composeapp.generated.resources.movie_character_female
import mimasu.composeapp.generated.resources.movie_character_gender
import mimasu.composeapp.generated.resources.movie_character_male
import mimasu.composeapp.generated.resources.movie_character_non_binary
import mimasu.composeapp.generated.resources.movie_character_unspecified
import mimasu.composeapp.generated.resources.no
import mimasu.composeapp.generated.resources.yes
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CastDialogScreen(component: CastDialogComponent) {
    val state = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = component::dismiss,
        sheetState = state
    ) {
        ProvideNonTvContentColor {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                this@ModalBottomSheet.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.CenterStart),
                    visible = state.isFullyExpandedOrTargeted(true),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PlatformIconButton(
                        onClick = component::dismiss
                    ) {
                        PlatformIcon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = kodeinStringResource(Res.string.close)
                        )
                    }
                }

                AsyncImage(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    model = component.cast.profilePicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = component.cast.profilePictureW500,
                        contentScale = ContentScale.Crop
                    )
                )
            }

            val charName = remember(component.cast.id) {
                component.cast.character ?: component.cast.name ?: component.cast.originalName
            }
            val realName = remember(component.cast.id, charName) {
                if (charName.equals(component.cast.name)) {
                    component.cast.originalName
                } else if (charName.equals(component.cast.originalName)) {
                    null
                } else {
                    component.cast.name
                }
            }

            PlatformText(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                text = charName ?: "",
                style = Platform.typography().headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                softWrap = true
            )
            PlatformText(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = realName ?: "",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                softWrap = true
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlatformIcon(
                        imageVector = MaterialSymbols.Rating18Up,
                        contentDescription = null
                    )
                    PlatformText(
                        text = kodeinStringResource(Res.string.movie_character_adult_content),
                        style = Platform.typography().labelSmall
                    )
                    PlatformText(
                        text = kodeinStringResource(if (component.cast.adult) Res.string.yes else Res.string.no),
                        style = Platform.typography().titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val (genderIcon, genderRes) = remember(component.cast.id) {
                        when (component.cast.gender) {
                            1 -> Icons.Rounded.Woman to Res.string.movie_character_female
                            2 -> Icons.Rounded.Man to Res.string.movie_character_male
                            3 -> Icons.Rounded.Man4 to Res.string.movie_character_non_binary
                            else -> Icons.Rounded.Man4 to Res.string.movie_character_unspecified
                        }
                    }

                    PlatformIcon(
                        imageVector = genderIcon,
                        contentDescription = null
                    )
                    PlatformText(
                        text = kodeinStringResource(Res.string.movie_character_gender),
                        style = Platform.typography().labelSmall
                    )
                    PlatformText(
                        text = kodeinStringResource(genderRes),
                        style = Platform.typography().titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                component.cast.knownForDepartment?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlatformIcon(
                            imageVector = Icons.Rounded.Work,
                            contentDescription = null
                        )
                        PlatformText(
                            text = kodeinStringResource(Res.string.movie_character_department),
                            style = Platform.typography().labelSmall
                        )
                        PlatformText(
                            text = it,
                            style = Platform.typography().titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}