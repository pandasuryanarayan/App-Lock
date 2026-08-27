package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight, high-performance standalone vector icons.
 * Replaces the massive ~18MB material-icons-extended dependency, keeping
 * the post-install app footprint under 8MB while maintaining identical visual designs.
 */
object AppIcons {
    val Lock: ImageVector = Icons.Default.Lock
    val Delete: ImageVector = Icons.Default.Delete
    val Warning: ImageVector = Icons.Default.Warning
    val Close: ImageVector = Icons.Default.Close
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val Add: ImageVector = Icons.Default.Add

    // Fingerprint Vector Icon
    val Fingerprint: ImageVector by lazy {
        ImageVector.Builder(
            name = "Fingerprint",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17.81f, 4.47f)
                curveToRelative(-0.08f, 0f, -0.16f, -0.02f, -0.23f, -0.06f)
                curveTo(15.66f, 3.42f, 14f, 3f, 12.01f, 3f)
                curveToRelative(-1.98f, 0f, -3.86f, 0.47f, -5.57f, 1.41f)
                curveToRelative(-0.24f, 0.13f, -0.54f, 0.04f, -0.68f, -0.2f)
                curveToRelative(-0.13f, -0.24f, -0.04f, -0.55f, 0.2f, -0.68f)
                curveTo(7.82f, 2.52f, 9.86f, 2f, 12.01f, 2f)
                curveToRelative(2.13f, 0f, 3.99f, 0.47f, 6.03f, 1.52f)
                curveToRelative(0.25f, 0.13f, 0.34f, 0.43f, 0.21f, 0.67f)
                curveToRelative(-0.09f, 0.18f, -0.26f, 0.28f, -0.44f, 0.28f)
                close()
                moveTo(3.51f, 9.72f)
                curveToRelative(-0.25f, -0.08f, -0.38f, -0.35f, -0.3f, -0.6f)
                curveTo(4.14f, 6.38f, 5.75f, 4.78f, 7.98f, 4.4f)
                curveToRelative(0.27f, -0.02f, 0.51f, 0.15f, 0.53f, 0.42f)
                curveToRelative(0.02f, 0.27f, -0.15f, 0.5f, -0.42f, 0.52f)
                curveToRelative(-1.97f, 0.33f, -3.38f, 1.73f, -4.32f, 4.09f)
                curveToRelative(-0.07f, 0.18f, -0.24f, 0.29f, -0.26f, 0.29f)
                close()
                moveTo(9.78f, 21.78f)
                curveToRelative(-0.13f, 0f, -0.26f, -0.05f, -0.35f, -0.15f)
                curveToRelative(-1.93f, -1.91f, -2.99f, -4.47f, -3f, -7.2f)
                curveToRelative(-0.01f, -3.14f, 2.47f, -5.74f, 5.58f, -5.85f)
                curveToRelative(3.18f, -0.12f, 5.86f, 2.37f, 5.92f, 5.51f)
                curveToRelative(0.03f, 1.66f, -0.22f, 3.68f, -0.96f, 5.79f)
                curveToRelative(-0.1f, 0.26f, -0.39f, 0.39f, -0.65f, 0.29f)
                curveToRelative(-0.26f, -0.1f, -0.39f, -0.39f, -0.29f, -0.65f)
                curveToRelative(0.69f, -1.97f, 0.93f, -3.81f, 0.9f, -5.31f)
                curveToRelative(-0.05f, -2.61f, -2.27f, -4.68f, -4.94f, -4.58f)
                curveToRelative(-2.6f, 0.09f, -4.67f, 2.27f, -4.66f, 4.89f)
                curveToRelative(0.01f, 2.45f, 0.95f, 4.75f, 2.68f, 6.47f)
                curveToRelative(0.2f, 0.2f, 0.2f, 0.51f, 0f, 0.71f)
                curveToRelative(-0.1f, 0.08f, -0.22f, 0.13f, -0.33f, 0.13f)
                close()
                moveTo(12f, 16.5f)
                curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
                curveToRelative(0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
                curveToRelative(0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                close()
            }
        }.build()
    }

    // Security / Shield Icon
    val Security: ImageVector by lazy {
        ImageVector.Builder(
            name = "Security",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 1f)
                lineTo(3f, 5f)
                verticalLineToRelative(6f)
                curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
                curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
                verticalLineTo(5f)
                lineToRelative(-9f, -4f)
                close()
                moveTo(12f, 11.99f)
                horizontalLineToRelative(7f)
                curveToRelative(-0.53f, 4.12f, -3.28f, 7.79f, -7f, 8.94f)
                verticalLineTo(12f)
                horizontalLineTo(5f)
                verticalLineTo(6.3f)
                lineToRelative(7f, -3.11f)
                verticalLineToRelative(8.8f)
                close()
            }
        }.build()
    }

    val Shield: ImageVector by lazy { Security }
    val GppMaybe: ImageVector by lazy { Security }

    // Password / Key Icon
    val Password: ImageVector by lazy {
        ImageVector.Builder(
            name = "Password",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2f, 17f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(2f)
                lineTo(2f, 19f)
                close()
                moveTo(3.15f, 12.95f)
                lineToRelative(1.41f, 1.41f)
                lineToRelative(2.47f, -2.47f)
                lineToRelative(2.47f, 2.47f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(-2.47f, -2.47f)
                lineToRelative(2.47f, -2.47f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-2.47f, 2.47f)
                lineTo(4.56f, 6.6f)
                lineTo(3.15f, 8.01f)
                lineToRelative(2.47f, 2.47f)
                close()
                moveTo(14.15f, 12.95f)
                lineToRelative(1.41f, 1.41f)
                lineToRelative(2.47f, -2.47f)
                lineToRelative(2.47f, 2.47f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(-2.47f, -2.47f)
                lineToRelative(2.47f, -2.47f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-2.47f, 2.47f)
                lineToRelative(-2.47f, -2.47f)
                lineToRelative(-1.41f, 1.41f)
                lineToRelative(2.47f, 2.47f)
                close()
            }
        }.build()
    }

    // PIN Icon
    val Pin: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                lineTo(4f, 8f)
                close()
                moveTo(10f, 4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(16f, 4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(4f, 10f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                lineTo(4f, 14f)
                close()
                moveTo(10f, 10f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(16f, 10f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(4f, 16f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                lineTo(4f, 20f)
                close()
                moveTo(10f, 16f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(16f, 16f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-4f)
                close()
            }
        }.build()
    }

    // Pattern Icon
    val Pattern: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pattern",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 6f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(12f, 6f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(18f, 6f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(6f, 12f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(12f, 12f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(18f, 12f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(6f, 18f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(12f, 18f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                moveTo(18f, 18f)
                moveToRelative(-2f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
            }
        }.build()
    }

    // Apps Icon
    val Apps: ImageVector by lazy {
        ImageVector.Builder(
            name = "Apps",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 8f)
                horizontalLineToRelative(4f)
                lineTo(8f, 4f)
                lineTo(4f, 4f)
                verticalLineToRelative(4f)
                close()
                moveTo(10f, 20f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(4f)
                close()
                moveTo(4f, 20f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                lineTo(4f, 16f)
                verticalLineToRelative(4f)
                close()
                moveTo(4f, 14f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                lineTo(4f, 10f)
                verticalLineToRelative(4f)
                close()
                moveTo(10f, 14f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(4f)
                close()
                moveTo(16f, 4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(4f)
                lineTo(20f, 4f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(10f, 8f)
                horizontalLineToRelative(4f)
                lineTo(14f, 4f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(4f)
                close()
                moveTo(16f, 14f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(4f)
                close()
                moveTo(16f, 20f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(4f)
                close()
            }
        }.build()
    }

    // Settings Icon
    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19.14f, 12.94f)
                curveToRelative(0.04f, -0.3f, 0.06f, -0.61f, 0.06f, -0.94f)
                curveToRelative(0f, -0.32f, -0.02f, -0.64f, -0.07f, -0.94f)
                lineToRelative(2.03f, -1.58f)
                curveToRelative(0.18f, -0.14f, 0.23f, -0.41f, 0.12f, -0.61f)
                lineToRelative(-1.92f, -3.32f)
                curveToRelative(-0.12f, -0.22f, -0.37f, -0.29f, -0.59f, -0.22f)
                lineToRelative(-2.39f, 0.96f)
                curveToRelative(-0.5f, -0.38f, -1.03f, -0.7f, -1.62f, -0.94f)
                lineToRelative(-0.36f, -2.54f)
                curveToRelative(-0.04f, -0.24f, -0.24f, -0.41f, -0.48f, -0.41f)
                horizontalLineToRelative(-3.84f)
                curveToRelative(-0.24f, 0f, -0.43f, 0.17f, -0.47f, 0.41f)
                lineToRelative(-0.36f, 2.54f)
                curveToRelative(-0.59f, 0.24f, -1.13f, 0.57f, -1.62f, 0.94f)
                lineToRelative(-2.39f, -0.96f)
                curveToRelative(-0.22f, -0.08f, -0.47f, 0f, -0.59f, 0.22f)
                lineTo(2.74f, 8.87f)
                curveToRelative(-0.12f, 0.21f, -0.08f, 0.47f, 0.12f, 0.61f)
                lineToRelative(2.03f, 1.58f)
                curveToRelative(-0.05f, 0.3f, -0.09f, 0.63f, -0.09f, 0.94f)
                reflectiveCurveToRelative(0.02f, 0.64f, 0.07f, 0.94f)
                lineToRelative(-2.03f, 1.58f)
                curveToRelative(-0.18f, 0.14f, -0.23f, 0.41f, -0.12f, 0.61f)
                lineToRelative(1.92f, 3.32f)
                curveToRelative(0.12f, 0.22f, 0.37f, 0.29f, 0.59f, 0.22f)
                lineToRelative(2.39f, -0.96f)
                curveToRelative(0.5f, 0.38f, 1.03f, 0.7f, 1.62f, 0.94f)
                lineToRelative(0.36f, 2.54f)
                curveToRelative(0.05f, 0.24f, 0.24f, 0.41f, 0.48f, 0.41f)
                horizontalLineToRelative(3.84f)
                curveToRelative(0.24f, 0f, 0.44f, -0.17f, 0.47f, -0.41f)
                lineToRelative(0.36f, -2.54f)
                curveToRelative(0.59f, -0.24f, 1.13f, -0.56f, 1.62f, -0.94f)
                lineToRelative(2.39f, 0.96f)
                curveToRelative(0.22f, 0.08f, 0.47f, 0f, 0.59f, -0.22f)
                lineToRelative(1.92f, -3.32f)
                curveToRelative(0.12f, -0.22f, 0.07f, -0.47f, -0.12f, -0.61f)
                lineToRelative(-2.01f, -1.58f)
                close()
                moveTo(12f, 15.6f)
                curveToRelative(-1.98f, 0f, -3.6f, -1.62f, -3.6f, -3.6f)
                reflectiveCurveToRelative(1.62f, -3.6f, 3.6f, -3.6f)
                reflectiveCurveToRelative(3.6f, 1.62f, 3.6f, 3.6f)
                reflectiveCurveToRelative(-1.62f, 3.6f, -3.6f, 3.6f)
                close()
            }
        }.build()
    }

    // VpnKey (Vault) Icon
    val VpnKey: ImageVector by lazy {
        ImageVector.Builder(
            name = "VpnKey",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.65f, 10f)
                curveTo(11.83f, 7.67f, 9.61f, 6f, 7f, 6f)
                curveToRelative(-3.31f, 0f, -6f, 2.69f, -6f, 6f)
                reflectiveCurveToRelative(2.69f, 6f, 6f, 6f)
                curveToRelative(2.61f, 0f, 4.83f, -1.67f, 5.65f, -4f)
                horizontalLineTo(17f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-4f)
                horizontalLineTo(12.65f)
                close()
                moveTo(7f, 14f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
                reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
                close()
            }
        }.build()
    }

    // AccountBalance (Banking) Icon
    val AccountBalance: ImageVector by lazy {
        ImageVector.Builder(
            name = "AccountBalance",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 10f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-7f)
                lineTo(4f, 10f)
                close()
                moveTo(10.5f, 10f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-3f)
                close()
                moveTo(2f, 22f)
                horizontalLineToRelative(19f)
                verticalLineToRelative(-3f)
                lineTo(2f, 19f)
                verticalLineToRelative(3f)
                close()
                moveTo(17f, 10f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-3f)
                close()
                moveTo(11.5f, 1f)
                lineTo(2f, 6f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(19f)
                lineTo(21f, 6f)
                lineToRelative(-9.5f, -5f)
                close()
            }
        }.build()
    }

    // ChatBubble (Social) Icon
    val ChatBubble: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChatBubble",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 2f)
                lineTo(4f, 2f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(18f)
                lineToRelative(4f, -4f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                lineTo(22f, 4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
            }
        }.build()
    }

    // Android Icon
    val Android: ImageVector by lazy {
        ImageVector.Builder(
            name = "Android",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 18f)
                curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(3.5f)
                curveToRelative(0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
                lineTo(11f, 19f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(3.5f)
                curveToRelative(0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
                lineTo(16f, 19f)
                horizontalLineToRelative(1f)
                curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                lineTo(18f, 8f)
                lineTo(6f, 8f)
                verticalLineToRelative(10f)
                close()
                moveTo(3.5f, 8f)
                curveTo(2.67f, 8f, 2f, 8.67f, 2f, 9.5f)
                verticalLineToRelative(7f)
                curveToRelative(0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveTo(5f, 17.33f, 5f, 16.5f)
                verticalLineToRelative(-7f)
                curveTo(5f, 8.67f, 4.33f, 8f, 3.5f, 8f)
                close()
                moveTo(20.5f, 8f)
                curveToRelative(-0.83f, 0f, -1.5f, 0.67f, -1.5f, 1.5f)
                verticalLineToRelative(7f)
                curveToRelative(0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
                verticalLineToRelative(-7f)
                curveToRelative(0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
                close()
                moveTo(15.53f, 2.16f)
                lineToRelative(1.3f, -1.3f)
                curveToRelative(0.2f, -0.2f, 0.2f, -0.51f, 0f, -0.71f)
                curveToRelative(-0.2f, -0.2f, -0.51f, -0.2f, -0.71f, 0f)
                lineToRelative(-1.48f, 1.48f)
                curveTo(13.62f, 1.23f, 12.83f, 1f, 12f, 1f)
                reflectiveCurveToRelative(-1.62f, 0.23f, -2.64f, 0.63f)
                lineTo(7.88f, 0.15f)
                curveToRelative(-0.2f, -0.2f, -0.51f, -0.2f, -0.71f, 0f)
                curveToRelative(-0.2f, 0.2f, -0.2f, 0.51f, 0f, 0.71f)
                lineToRelative(1.3f, 1.3f)
                curveTo(6.85f, 3.19f, 6.01f, 4.73f, 6f, 6.5f)
                horizontalLineToRelative(12f)
                curveToRelative(-0.01f, -1.77f, -0.85f, -3.31f, -2.47f, -4.34f)
                close()
                moveTo(9f, 4.5f)
                curveToRelative(-0.41f, 0f, -0.75f, -0.34f, -0.75f, -0.75f)
                reflectiveCurveTo(8.59f, 3f, 9f, 3f)
                reflectiveCurveToRelative(0.75f, 0.34f, 0.75f, 0.75f)
                reflectiveCurveTo(9.41f, 4.5f, 9f, 4.5f)
                close()
                moveTo(15f, 4.5f)
                curveToRelative(-0.41f, 0f, -0.75f, -0.34f, -0.75f, -0.75f)
                reflectiveCurveTo(14.59f, 3f, 15f, 3f)
                reflectiveCurveToRelative(0.75f, 0.34f, 0.75f, 0.75f)
                reflectiveCurveTo(15.41f, 4.5f, 15f, 4.5f)
                close()
            }
        }.build()
    }

    // Launch Icon
    val Launch: ImageVector by lazy {
        ImageVector.Builder(
            name = "Launch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19f, 19f)
                lineTo(5f, 19f)
                lineTo(5f, 5f)
                horizontalLineToRelative(7f)
                lineTo(12f, 3f)
                lineTo(5f, 3f)
                curveToRelative(-1.11f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(7f)
                close()
                moveTo(14f, 3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3.59f)
                lineToRelative(-9.83f, 9.83f)
                lineToRelative(1.41f, 1.41f)
                lineTo(19f, 6.41f)
                lineTo(19f, 10f)
                horizontalLineToRelative(2f)
                lineTo(21f, 3f)
                horizontalLineToRelative(-7f)
                close()
            }
        }.build()
    }

    // HelpOutline Icon
    val HelpOutline: ImageVector by lazy {
        ImageVector.Builder(
            name = "HelpOutline",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 18f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(2f)
                close()
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.41f, 0f, -8f, -3.59f, -8f, -8f)
                reflectiveCurveToRelative(3.59f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.59f, 8f, 8f)
                reflectiveCurveToRelative(-3.59f, 8f, -8f, 8f)
                close()
                moveTo(12f, 6f)
                curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
                curveToRelative(0f, 2f, -3f, 1.75f, -3f, 5f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -2.25f, 3f, -2.5f, 3f, -5f)
                curveToRelative(0f, -2.21f, -1.79f, -4f, -4f, -4f)
                close()
            }
        }.build()
    }

    val Info: ImageVector by lazy { HelpOutline }

    // NoAccounts Icon
    val NoAccounts: ImageVector by lazy {
        ImageVector.Builder(
            name = "NoAccounts",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveToRelative(0f, 2.39f, 0.84f, 4.59f, 2.25f, 6.33f)
                lineTo(1.39f, 21.22f)
                lineToRelative(1.41f, 1.41f)
                lineToRelative(19.8f, -19.8f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-2.87f, 2.87f)
                curveTo(16.59f, 2.84f, 14.39f, 2f, 12f, 2f)
                close()
                moveTo(12f, 6f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                curveToRelative(0f, 0.38f, -0.11f, 0.74f, -0.29f, 1.04f)
                lineToRelative(-2.75f, -2.75f)
                curveTo(11.26f, 6.11f, 11.62f, 6f, 12f, 6f)
                close()
            }
        }.build()
    }

    // ChevronRight Icon
    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 6f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12f)
                lineToRelative(-4.58f, 4.59f)
                lineTo(10f, 18f)
                lineToRelative(6f, -6f)
                close()
            }
        }.build()
    }

    // DataUsage Icon
    val DataUsage: ImageVector by lazy {
        ImageVector.Builder(
            name = "DataUsage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(13f, 2.05f)
                verticalLineToRelative(3.03f)
                curveToRelative(3.39f, 0.49f, 6f, 3.39f, 6f, 6.92f)
                curveToRelative(0f, 0.9f, -0.18f, 1.75f, -0.48f, 2.54f)
                lineToRelative(2.6f, 1.53f)
                curveToRelative(0.56f, -1.24f, 0.88f, -2.62f, 0.88f, -4.07f)
                curveToRelative(0f, -5.18f, -3.95f, -9.45f, -9f, -9.95f)
                close()
                moveTo(12f, 19f)
                curveToRelative(-3.87f, 0f, -7f, -3.13f, -7f, -7f)
                curveToRelative(0f, -3.53f, 2.61f, -6.43f, 6f, -6.92f)
                lineTo(11f, 2.05f)
                curveToRelative(-5.05f, 0.5f, -9f, 4.76f, -9f, 9.95f)
                curveToRelative(0f, 5.52f, 4.47f, 10f, 9.99f, 10f)
                curveToRelative(3.31f, 0f, 6.24f, -1.61f, 8.01f, -4.09f)
                lineToRelative(-2.45f, -1.45f)
                curveTo(16.29f, 18.06f, 14.28f, 19f, 12f, 19f)
                close()
            }
        }.build()
    }

    // Layers Icon
    val Layers: ImageVector by lazy {
        ImageVector.Builder(
            name = "Layers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11.99f, 18.54f)
                lineToRelative(-7.37f, -5.73f)
                lineTo(3f, 14.07f)
                lineToRelative(9f, 7f)
                lineToRelative(9f, -7f)
                lineToRelative(-1.63f, -1.27f)
                close()
                moveTo(12f, 16f)
                lineToRelative(7.36f, -5.73f)
                lineTo(21f, 9f)
                lineToRelative(-9f, -7f)
                lineToRelative(-9f, 7f)
                lineToRelative(1.63f, 1.27f)
                close()
            }
        }.build()
    }

    // Accessibility Icon
    val Accessibility: ImageVector by lazy {
        ImageVector.Builder(
            name = "Accessibility",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
                reflectiveCurveToRelative(-2f, -0.9f, -2f, -2f)
                reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
                close()
                moveTo(21f, 9f)
                horizontalLineToRelative(-6f)
                verticalLineToRelative(13f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(6f)
                lineTo(9f, 22f)
                lineTo(9f, 9f)
                lineTo(3f, 9f)
                lineTo(3f, 7f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(2f)
                close()
            }
        }.build()
    }

    // SearchOff Icon
    val SearchOff: ImageVector by lazy {
        ImageVector.Builder(
            name = "SearchOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.5f, 14f)
                horizontalLineToRelative(-0.79f)
                lineToRelative(-0.28f, -0.27f)
                curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
                curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
                curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f)
                curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
                curveToRelative(1.61f, 0f, 3.09f, -0.59f, 4.23f, -1.57f)
                lineToRelative(0.27f, 0.28f)
                verticalLineToRelative(0.79f)
                lineToRelative(5f, 4.99f)
                lineTo(20.49f, 19f)
                lineToRelative(-4.99f, -5f)
                close()
                moveTo(9.5f, 14f)
                curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
                reflectiveCurveTo(7.01f, 5f, 9.5f, 5f)
                reflectiveCurveTo(14f, 7.01f, 14f, 9.5f)
                reflectiveCurveTo(11.99f, 14f, 9.5f, 14f)
                close()
            }
        }.build()
    }

    // Backspace Icon
    val Backspace: ImageVector by lazy {
        ImageVector.Builder(
            name = "Backspace",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-0.69f, 0f, -1.23f, 0.35f, -1.59f, 0.88f)
                lineTo(0f, 12f)
                lineToRelative(5.41f, 8.11f)
                curveToRelative(0.36f, 0.53f, 0.9f, 0.89f, 1.59f, 0.89f)
                horizontalLineToRelative(15f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(19f, 15.59f)
                lineTo(17.59f, 17f)
                lineTo(14f, 13.41f)
                lineTo(10.41f, 17f)
                lineTo(9f, 15.59f)
                lineTo(12.59f, 12f)
                lineTo(9f, 8.41f)
                lineTo(10.41f, 7f)
                lineTo(14f, 10.59f)
                lineTo(17.59f, 7f)
                lineTo(19f, 8.41f)
                lineTo(15.41f, 12f)
                lineTo(19f, 15.59f)
                close()
            }
        }.build()
    }
}
