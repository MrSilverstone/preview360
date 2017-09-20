package com.tools.louis.preview360

import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v8.renderscript.Float2
import android.support.v8.renderscript.Sampler
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val RS = RenderScript.create(this)
        val script = ScriptC_preview(RS)

        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888



        val input = BitmapFactory.decodeResource(resources, R.drawable.img, opts)

        val output = Bitmap.createBitmap(input.width, input.height, input.config)


        script._Size = Float2(input.width.toFloat(), input.height.toFloat())
        script._sampler = Sampler.CLAMP_NEAREST(RS)

        val inputAllocation = Allocation.createFromBitmap(RS, input, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED or Allocation.USAGE_GRAPHICS_TEXTURE or Allocation.USAGE_SCRIPT)
        val outputAllocation = Allocation.createFromBitmap(RS, output, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED or Allocation.USAGE_SCRIPT)

        script._texture = inputAllocation

        val t = System.currentTimeMillis()

        Log.d("MainActivity", "Starting : $t")

        script.forEach_grayscale(inputAllocation, outputAllocation)
        outputAllocation.copyTo(output)

        Log.d("MainActivity", "Processing time : ${System.currentTimeMillis() - t} : $t")

        imgDst.setImageBitmap(output)
    }
}
