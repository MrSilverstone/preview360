package com.tools.louis.preview360

import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tools.louis.previewgenerator.PreviewGenerator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generator = PreviewGenerator(this)

        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888

        val input = BitmapFactory.decodeResource(resources, R.drawable.img, opts)
        imgDst.setImageBitmap(generator.generatePreview(input))
    }
}
