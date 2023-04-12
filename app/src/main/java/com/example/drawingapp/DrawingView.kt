package com.example.drawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context :Context, `attr-set` :AttributeSet) : View(context,`attr-set`) {
    private var mcanvas :Canvas? = null
    private var mcanvasBitmap :Bitmap? = null
    private var mDrawPath :CustomPath? = null
private var mDrawPaint :Paint? = null
  private var mcanvasPaint : Paint? = null
    private var mBrushSize = 0.toFloat()

 var color = Color.BLUE
    private var mPath = ArrayList<CustomPath>()
    private var mundoPath =  ArrayList<CustomPath>()

    init {
        setupDrawingView()
    }
    fun undoPath(){
        if (mPath.size>0){
            mundoPath.add(mPath.removeAt(mPath.size-1))
            invalidate()
        }

    }

    private fun setupDrawingView() {

        mDrawPath = CustomPath(mBrushSize,color)
      // mBrushSize = 20.toFloat()
        mDrawPaint = Paint()
       mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.color=color
        mDrawPaint?.strokeCap = Paint.Cap.SQUARE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mcanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBitmap = Bitmap.createBitmap(0,0,Bitmap.Config.ARGB_8888)
//        mcanvas = mBitmap?.let { Canvas(it) }

    //mDrawPath = CustomPath()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mcanvasBitmap = Bitmap.createBitmap(w, h,Bitmap.Config.ARGB_8888)
        mcanvas = Canvas(mcanvasBitmap!!)

    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(m: Canvas) {
        mcanvasBitmap?.let {
            m.drawBitmap(it,0f,0f,mcanvasPaint)
        }

        for (i in mPath){
            mDrawPaint?.strokeWidth = i.BrushThicness
            mDrawPaint?.color = i.color
            m.drawPath(i,mDrawPaint!!)
        }
       if(!mDrawPath!!.isEmpty){
           mDrawPaint?.strokeWidth = mDrawPath!!.BrushThicness
           mDrawPaint?.color = mDrawPath!!.color
           m.drawPath(mDrawPath!!,mDrawPaint!!)
           val rectf  = RectF(12f,34f,32f,110f)
           m.drawOval(rectf, mDrawPaint!!)
           mDrawPaint!!.color = Color.DKGRAY

           m.drawText("ANKIT",12f,50f, mDrawPaint!!)
       }



    }
    fun setBrushSize(BrushId :Int){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            BrushId.toFloat(),resources.displayMetrics)
        mDrawPaint?.strokeWidth = mBrushSize

    }


    internal inner class CustomPath(var BrushThicness:Float, var color: Int)
      :Path() {
      }

    @SuppressLint("ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        val tochx : Float? = e!!.x
        val tochy :Float? = e.y
            when(e.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDrawPath!!.BrushThicness = mBrushSize
                    mDrawPath!!.color = color
                    mDrawPath!!.reset()
                    if (tochx != null) {
                        if (tochy != null) {
                            mDrawPath!!.moveTo(tochx,tochy)
                        }
                    }

                }
                MotionEvent.ACTION_MOVE ->{
                    if (tochx != null) {
                        if (tochy != null) {
                            mDrawPath!!.lineTo(tochx,tochy)
                        }
                    }
                }
                MotionEvent.ACTION_UP ->{
                    mPath.add(mDrawPath!!)
                    mDrawPath = CustomPath(mBrushSize,color)
                }
                else -> return false
            }

invalidate()
        return true

    }





}