package com.example.cameraxfacemlkit.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Size
import java.io.FileDescriptor


object Util {

    var resW :Int= 720
    var resH:Int = 1280

    var defaultResW =  720
    var defaultResH =  1280

    fun getScreenSize(screenWidth:Int):Size{
        var newSize = Size(resW, resH)
        when(screenWidth){
            720->{
                newSize = Size(720, 1280)
            }
            1024 ->{
                newSize = Size(1024, 1280)
            }
            1200 ->{
                newSize = Size(1200, 1600)
            }
            1080 ->{
                newSize = Size(1080, 1920)
            }
            else->{
                newSize = Size(720, 1280)
            }
        }
        return newSize
    }


    fun mergeToPin(back: Bitmap, front: Bitmap): Bitmap? {

        val result = Bitmap.createBitmap(back.width, back.height, back.config)

      //  val result = Bitmap.createBitmap(back.width, back.height, back.config)
       /* val resalceBack = Bitmap.createScaledBitmap(back,720, 1280,false)
        val resalcefotn = Bitmap.createScaledBitmap(front,720, 1280,false)*/
        val canvas = Canvas(result)
        canvas.drawBitmap(back, 0f, 0f, null)
        canvas.drawBitmap(front, 0f, 0f, null)
        return result
    }
}

