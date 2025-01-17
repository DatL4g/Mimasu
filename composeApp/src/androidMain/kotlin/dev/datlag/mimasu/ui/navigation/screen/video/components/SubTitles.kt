package dev.datlag.mimasu.ui.navigation.screen.video.components

import android.content.Context
import android.text.Layout
import android.util.TypedValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.TypedValueCompat
import androidx.media3.common.text.Cue
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tolgee.I18N
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.no_subtitle
import org.jetbrains.compose.resources.painterResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import kotlin.time.Duration.Companion.minutes

@Composable
fun SubTitles(
    state: VideoPlayerState,
    modifier: Modifier = Modifier
) {
    val cues by state.cues.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = cues.isNotEmpty() && state.controlsAvailable,
        enter = expandIn(expandFrom = Alignment.CenterStart),
        exit = shrinkOut(shrinkTowards = Alignment.CenterEnd)
    ) {
        Text(
            text = buildAnnotatedSubTitle(LocalContext.current, cues, LocalTextStyle.current),
            color = Color.White,
            textAlign = TextAlign.Center,
            softWrap = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTitleSelector(
    state: VideoPlayerState
) {
    val subTitle by state.subTitle.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    if (subTitle.available.isNotEmpty() || subTitle.selected != null) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            IconButton(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                onClick = {
                    state.showControls(2.minutes)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Subtitles,
                    contentDescription = null
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    state.hideControls()
                },
                matchTextFieldWidth = false
            ) {
                DropdownMenuItem(
                    onClick = {
                        state.selectLanguage(null)

                        expanded = false
                        state.hideControls()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.PublicOff,
                            contentDescription = null
                        )
                    },
                    text = {
                        val i18n by localDI().instance<I18N>()

                        Text(text = i18n.stringResource(Res.string.no_subtitle))
                    },
                    trailingIcon = if (subTitle.selected == null) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null
                            )
                        }
                    } else null,
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
                subTitle.available.forEach { lang ->
                    DropdownMenuItem(
                        onClick = {
                            state.selectLanguage(lang)

                            expanded = false
                            state.hideControls()
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp).clip(CircleShape),
                                painter = lang.icon?.let {
                                    painterResource(it)
                                } ?: rememberVectorPainter(Icons.Rounded.Public),
                                contentDescription = null,
                                tint = if (lang.icon != null) {
                                    Color.Unspecified
                                } else {
                                    LocalContentColor.current
                                }
                            )
                        },
                        text = {
                            Text(
                                text = lang.name,
                                maxLines = 1,
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingIcon = if (subTitle.selected == lang) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null
                                )
                            }
                        } else null,
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

private fun buildAnnotatedSubTitle(
    context: Context,
    cues: Collection<Cue>,
    defaultStyle: TextStyle
): AnnotatedString {
    return buildAnnotatedString {
        cues.forEachIndexed { index, cue ->
            if (cue.text.isNullOrBlank()) {
                return@forEachIndexed
            }

            val textAlign = when (cue.textAlignment) {
                Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
                Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
                Layout.Alignment.ALIGN_NORMAL -> TextAlign.Start
                else -> defaultStyle.textAlign
            }

            val fontSize = when (cue.textSizeType) {
                Cue.TEXT_SIZE_TYPE_FRACTIONAL -> (cue.textSize * 16).sp
                Cue.TEXT_SIZE_TYPE_ABSOLUTE -> {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX, cue.textSize, context.resources.displayMetrics
                    ).sp
                }
                else -> defaultStyle.fontSize
            }

            withStyle(
                style = TextStyle(
                    fontSize = fontSize,
                    textAlign = textAlign
                ).toSpanStyle()
            ) {
                append(cue.text?.ifBlank { null })
            }

            if (index < cues.size - 1) {
                append("\n")
            }
        }
    }
}