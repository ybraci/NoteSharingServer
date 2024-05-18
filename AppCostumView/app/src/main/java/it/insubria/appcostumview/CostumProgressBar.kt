package it.insubria.appcostumview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CostumProgressBar (context: Context, attrs: AttributeSet): View(context, attrs){
    val rect = RectF(0f,0f,0f,0f) //1 rettangolo
    val paint = Paint()
    //property:
    var progress:Int = 75
        get() =field
        set(value){
            field = if(value>100) 100 else value
            invalidate()
            requestLayout()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pad = paint.strokeWidth * 0.6f
        rect.set(0f+pad, 0f+pad, width.toFloat()-pad, height.toFloat()-pad)
        val maxSize = min(width, height)

        //draw background
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = maxSize*0.25f
        paint.color = Color.GRAY
        canvas.drawArc(rect, 0f, 360f, false, paint)

        //draw progress
        paint.color = Color.BLUE
        canvas.drawArc(rect, 0f, (progress.toFloat()/100f*360f), false, paint)
    }
}