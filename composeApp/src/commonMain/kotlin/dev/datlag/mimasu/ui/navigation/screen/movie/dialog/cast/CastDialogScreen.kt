package dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastDialogScreen(component: CastDialogComponent) {
    ModalBottomSheet(
        onDismissRequest = component::dismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        AsyncImage(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            model = component.cast.profilePicture,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = component.cast.profilePictureW500,
                contentScale = ContentScale.Crop
            )
        )
    }
}