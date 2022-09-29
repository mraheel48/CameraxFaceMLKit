package com.example.cameraxfacemlkit.face_detection

import android.graphics.*
import android.util.Log
import com.example.cameraxfacemlkit.R
import com.example.cameraxfacemlkit.camerax.GraphicOverlay
import com.example.cameraxfacemlkit.utils.BitmapUtils
import com.example.cameraxfacemlkit.utils.Util
import com.google.mlkit.vision.face.Face

class FaceContourGraphic(
    private val overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    /*private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint*/
    private val stickerBitmap: Bitmap

    init {
        /*val selectedColor = Color.CYAN

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH*/

        stickerBitmap = BitmapFactory.decodeResource(overlay.context.resources, R.drawable.snap)

    }

    override fun draw(canvas: Canvas?) {

        Log.d("myTag", "${face.headEulerAngleZ}")

        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )
        overlay.rotation = face.headEulerAngleZ
        canvas?.drawBitmap(stickerBitmap, null, rect, null)
    }

    companion object {
        private const val BOX_STROKE_WIDTH = 2.0f
    }

}