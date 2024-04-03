package com.aioapp.nuggetmvp.service.camerapicture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.ErrorCallback
import android.hardware.Camera.PictureCallback
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraView(ctx: Context?) : SurfaceHolder.Callback, PictureCallback, ErrorCallback {
    private var context: Context? = null
    private var winMan: WindowManager? = null

    // a surface holder
    private var sHolder: SurfaceHolder? = null
    private var parameters: Camera.Parameters? = null
    private val audioMgr: AudioManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var callback: IFrontCaptureCallback? = null
    private var surfaceView: SurfaceView? = null

    init {
        context = ctx
    }

    @Deprecated("Deprecated in Java")
    override fun onError(error: Int, camera: Camera) {
        Log.d(ContentValues.TAG, "Camera Error : $error", null)
        val winMan = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winMan.removeView(surfaceView)
        callback!!.onCaptureError(-1)
    }

    @Deprecated("Deprecated in Java")
    override fun onPictureTaken(data: ByteArray, camera: Camera) {
        Toast.makeText(context, "Image Captured Successfully", Toast.LENGTH_LONG).show()
        if (data != null) {
            val winMan = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            winMan.removeView(surfaceView)
            try {
                val opts = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeByteArray(
                    data, 0,
                    data.size, opts
                )
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false)
                val width = bitmap.width
                val height = bitmap.height
                val newWidth = 300
                val newHeight = 300
                val scaleWidth = newWidth.toFloat() / width
                val scaleHeight = newHeight.toFloat() / height
                val matrix = Matrix()
                // resize the bit map
                matrix.postScale(scaleWidth, scaleHeight)
                // rotate the Bitmap
                matrix.postRotate(-90f)
                val resizedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    width, height, matrix, true
                )

                val bytes = ByteArrayOutputStream()
                resizedBitmap.compress(
                    Bitmap.CompressFormat.JPEG, 40,
                    bytes
                )

                val cacheDir = context?.cacheDir
                val f = File(cacheDir, "test.jpg")
                Log.e(ContentValues.TAG, "onPictureTaken: " + f.path)
                println("File F : $f")
                if (f.exists()) {
                    if (f.delete()) {
                        Log.d(ContentValues.TAG, "Previous file deleted successfully.")
                    } else {
                        Log.e(ContentValues.TAG, "Failed to delete previous file.")
                    }
                }
                if (f.createNewFile()) {
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    Log.d(
                        ContentValues.TAG,
                        "New file created and image data written successfully."
                    )

                    callback?.onPhotoCaptured(f.path)
                } else {
                    Log.e(ContentValues.TAG, "Failed to create new file.")
                }
                //  context.stopService(new Intent(context, GetBackCoreService.class));
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*GetBackCoreService getBackCoreService = new GetBackCoreService();
        getBackCoreService.stopSelf();*/
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
        parameters = camera?.parameters
        camera?.parameters = parameters
        camera?.startPreview()
        Log.d(ContentValues.TAG, "Taking picture")
        try {
            Thread.sleep(10000)
            camera?.takePicture(null, null, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            Log.d(ContentValues.TAG, "Camera Opened")
            camera!!.setPreviewDisplay(sHolder)
        } catch (exception: IOException) {
            camera!!.release()
            camera = null
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        Log.d(ContentValues.TAG, "Camera released")
        camera = null
    }

    fun capturePhoto(frontCaptureCb: IFrontCaptureCallback?) {
        callback = frontCaptureCb
        if (!Utils.isFrontCameraPresent(context)) callback!!.onCaptureError(-1)
        surfaceView = SurfaceView(context)
        winMan = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            1,
            1,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        winMan?.addView(surfaceView, params)
        surfaceView?.setZOrderOnTop(true)
        val holder = surfaceView?.holder
        holder?.setFormat(PixelFormat.TRANSPARENT)
        sHolder = holder
        sHolder?.addCallback(this)
        sHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        Utils.LogUtil.LogD(Constants.LOG_TAG, "Opening Camera")

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
    }

    companion object {
        private var camera: Camera? = null
    }
}