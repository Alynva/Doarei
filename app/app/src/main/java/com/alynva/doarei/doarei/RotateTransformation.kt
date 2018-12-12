package com.alynva.doarei.doarei

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


class RotateTransformation(context: Context, rotateRotationAngle: Float) : BitmapTransformation(context) {

    private var rotateRotationAngle = 0f

    init {

        this.rotateRotationAngle = rotateRotationAngle
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val matrix = Matrix()

        matrix.postRotate(rotateRotationAngle)

        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.width, toTransform.height, matrix, true)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(("rotate" + rotateRotationAngle).toByteArray())
    }
}