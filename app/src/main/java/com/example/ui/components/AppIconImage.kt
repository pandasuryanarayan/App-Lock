package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.SlateSurfaceVariant

object AppIconLoader {
    private val memoryCache = LruCache<String, ImageBitmap>(200)

    fun getAppIcon(context: Context, packageName: String, sizePx: Int = 144): ImageBitmap? {
        if (packageName.isBlank()) return null

        memoryCache.get(packageName)?.let { return it }

        return try {
            val pm = context.packageManager
            val drawable: Drawable = pm.getApplicationIcon(packageName)

            val bitmap = if (drawable is BitmapDrawable && drawable.bitmap != null) {
                val bmp = drawable.bitmap
                if (bmp.width > 0 && bmp.height > 0) {
                    Bitmap.createScaledBitmap(bmp, sizePx, sizePx, true)
                } else {
                    renderDrawableToBitmap(drawable, sizePx)
                }
            } else {
                renderDrawableToBitmap(drawable, sizePx)
            }

            val imageBitmap = bitmap.asImageBitmap()
            memoryCache.put(packageName, imageBitmap)
            imageBitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun renderDrawableToBitmap(drawable: Drawable, sizePx: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

/**
 * Renders the real official application logo from PackageManager,
 * with a fallback icon if the package is not currently installed or fails to load.
 */
@Composable
fun AppIconImage(
    packageName: String,
    appName: String,
    category: String = "OTHER",
    modifier: Modifier = Modifier,
    sizeDp: Dp = 44.dp,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val context = LocalContext.current
    val iconBitmap = remember(packageName) {
        AppIconLoader.getAppIcon(context, packageName)
    }

    if (iconBitmap != null) {
        Surface(
            shape = shape,
            color = Color.Transparent,
            modifier = modifier.size(sizeDp)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = appName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        Surface(
            shape = shape,
            color = when (category) {
                "BANKING" -> CyberCyan.copy(alpha = 0.2f)
                "SOCIAL" -> ShieldGreen.copy(alpha = 0.2f)
                else -> SlateSurfaceVariant
            },
            modifier = modifier.size(sizeDp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (category) {
                        "BANKING" -> AppIcons.AccountBalance
                        "SOCIAL" -> AppIcons.ChatBubble
                        "SYSTEM" -> AppIcons.Settings
                        else -> AppIcons.Android
                    },
                    contentDescription = appName,
                    tint = when (category) {
                        "BANKING" -> CyberCyanLight
                        "SOCIAL" -> ShieldGreen
                        else -> Color.White
                    },
                    modifier = Modifier.size(sizeDp * 0.55f)
                )
            }
        }
    }
}
