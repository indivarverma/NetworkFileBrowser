package com.indivar.filebrowser.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.indivar.filebrowser.common.core.imageviewer.ImageViewerViewModel
import com.indivar.filebrowser.common.core.imageviewer.ViewState
import com.indivar.filebrowser.injectScoped
import org.koin.core.parameter.parametersOf
import kotlin.math.abs

class ImageViewerActivity : ComponentActivity() {
    private val viewModel by injectScoped<ImageViewerViewModel> { parametersOf(this) }
    private val screenSize by lazy {
        resources.displayMetrics.heightPixels to resources.displayMetrics.widthPixels
    }
    val imageUrl: String
        get() =
            requireNotNull(intent.getStringExtra(EXTRAS_URL))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this) {
            it.render()
        }
    }

    private fun ViewState.render() = when (this) {
        is ViewState.NotAuthorized -> Unit
        is ViewState.Ready -> {
            setContent {
                var height by remember { mutableStateOf(0) }
                var width by remember { mutableStateOf(0) }
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                    scale *= zoomChange
                    offset += offsetChange * scale
                    val zoomedImageHeight = (scale * height)
                    val zoomedImageWidth = (scale * width)
                    val (screenHeight, screenWidth) = screenSize

                    val horizontalGap = abs(screenWidth - zoomedImageWidth) / 2
                    val verticalGap = abs(screenHeight - zoomedImageHeight) / 2
                    offset = offset.copy(
                        x = offset.x.coerceIn(-horizontalGap, horizontalGap),
                        y = offset.y.coerceIn(-verticalGap, verticalGap),
                    )
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).apply {
                        baseEncodedCredentials?.let { addHeader("Authorization", it) }
                    }.listener { _, result ->
                        height = result.drawable.intrinsicHeight
                        width = result.drawable.intrinsicWidth
                    }.data(imageUrl).build(),
                    contentDescription = "attached image",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .fillMaxSize(1.0f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = state)

                )

            }
        }
    }

    companion object {
        const val EXTRAS_URL = "EXTRAS_URL"
        fun getIntent(context: Context, url: String) =
            Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(EXTRAS_URL, url)
            }

    }
}