package com.tools.louis.previewgenerator

import android.content.Context
import android.graphics.Bitmap
import android.support.v8.renderscript.*
import android.util.Log

/**
 * Created by loulo on 9/21/2017.
 */
class PreviewGenerator(private val context: Context) {

    private val rs = RenderScript.create(context)
    private val script = ScriptC_preview(rs)

    fun generatePreview(input: Bitmap) : Bitmap {

        val output = Bitmap.createBitmap(input.width, input.height, input.config)

        script._Size = Float2(input.width.toFloat(), input.height.toFloat())
        script._sampler = Sampler.CLAMP_NEAREST(rs)

        val inputAllocation = Allocation.createFromBitmap(rs, input, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED or Allocation.USAGE_GRAPHICS_TEXTURE or Allocation.USAGE_SCRIPT)
        val outputAllocation = Allocation.createFromBitmap(rs, output, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SHARED or Allocation.USAGE_SCRIPT)

        script._texture = inputAllocation

        script.forEach_preview360(inputAllocation, outputAllocation)
        outputAllocation.copyTo(output)

        inputAllocation.destroy()
        outputAllocation.destroy()
        rs.destroy()

        return output
    }

    companion object {
        val LOGTAG = "PreviewGenerator"
    }
}