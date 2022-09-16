package com.example.cameraxfacemlkit.face_detection

import android.graphics.*
import com.example.cameraxfacemlkit.R
import com.example.cameraxfacemlkit.camerax.GraphicOverlay
import com.google.mlkit.vision.face.Face

class FaceContourGraphic(
    overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint
    private val stickerBitmap: Bitmap
    init {
        val selectedColor = Color.CYAN

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH

        stickerBitmap = BitmapFactory.decodeResource(overlay.context.resources, R.drawable.snap)

    }

    override fun draw(canvas: Canvas?) {
        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )
        canvas?.drawRect(rect, boxPaint)

        /*op = Bitmap.createScaledBitmap(
            bitmap,
            scaleX(face.getWidth()) as Int,
            scaleY(bitmap.getHeight() * face.getWidth() / bitmap.getWidth()) as Int,
            false
        )*/

        //canvas?.drawBitmap(stickerBitmap, null, rect, boxPaint)
    }

    companion object {
        private const val BOX_STROKE_WIDTH = 2.0f
    }

}