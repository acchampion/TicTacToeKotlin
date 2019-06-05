package com.wiley.fordummies.androidsdk.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

@Suppress("DEPRECATION")
@SuppressLint("LogNotTimber")
class Board(context: Context, attributes: AttributeSet) : View(context, attributes) {
    private val mGameSessionActivity: GameSessionActivity = context as GameSessionActivity    // game context (parent)
    private var mBlockWidth: Float = 0.toFloat()     // mBlockWidth of one block
    private var mBlockHeight: Float = 0.toFloat()    // will be same as mBlockWidth;
    private val mStrokeWidth = 2f
    private val mLineWidth = 10f
    private var mGameGrid: GameGrid? = null
    private var isInputEnabled = true

    private val mBackgroundPaint: Paint
    private val mDarkPaint: Paint
    private val mLightPaint: Paint
    private val mLinePaint: Paint
    private val mDitherPaint: Paint

    private val TAG = javaClass.simpleName

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        // Allocate Paint objects to save memory.
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.color = resources.getColor(R.color.white)
        mDarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDarkPaint.color = resources.getColor(R.color.dark)
        mDarkPaint.strokeWidth = mStrokeWidth
        mLightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLightPaint.color = resources.getColor(R.color.light)
        mLightPaint.strokeWidth = mStrokeWidth
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.color = resources.getColor(R.color.dark)
        mLinePaint.strokeWidth = mLineWidth
        mLinePaint.strokeCap = Cap.ROUND
        mDitherPaint = Paint()
        mDitherPaint.isDither = true
    }

    fun setGrid(aGrid: GameGrid) {
        mGameGrid = aGrid
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mBlockWidth = w / 3f
        mBlockHeight = h / 3f

        if (w < h) {
            mBlockHeight = mBlockWidth
        } else {
            mBlockWidth = mBlockHeight
        }

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var canvasWidth = width.toFloat()
        var canvasHeight = height.toFloat()

        if (canvasWidth < canvasHeight)
            canvasHeight = canvasWidth
        else
            canvasWidth = canvasHeight

        canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, mBackgroundPaint)

        // Drawing lines
        for (i in 0 until GameGrid.SIZE) {
            canvas.drawLine(0f, (i + 1) * mBlockHeight, canvasWidth, (i + 1) * mBlockHeight, mDarkPaint)
            canvas.drawLine(0f, (i + 1) * mBlockHeight + 1, canvasWidth, (i + 1) * mBlockHeight + 1, mLightPaint)
            canvas.drawLine((i + 1) * mBlockHeight, 0f, (i + 1) * mBlockHeight, canvasHeight, mDarkPaint)
            canvas.drawLine((i + 1) * mBlockHeight + 1, 0f, (i + 1) * mBlockHeight + 1, canvasHeight, mLightPaint)
        }

        for (i in 0 until GameGrid.SIZE) {
            for (j in 0 until GameGrid.SIZE) {
                val symSelected = getBitmapForSymbol(mGameGrid!!.getValueAtLocation(i, j))
                val offsetX = ((mBlockWidth - symSelected!!.width) / 2 + i * mBlockWidth).toInt().toFloat()
                val offsetY = ((mBlockHeight - symSelected.height) / 2 + j * mBlockHeight).toInt().toFloat()
                canvas.drawBitmap(symSelected, offsetX, offsetY, mDitherPaint)
            }
        }
    }

    override fun performClick(): Boolean {
        Log.d(TAG, "performClick()")
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!this.isInputEnabled) {
            Log.d(TAG, "Board.onTouchEvent: Board not mIsEnabled")
            return false
        }

        var posX = 0
        var posY = 0

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                Log.d(TAG, "Coordinates: $x,$y")
                if (x > mBlockWidth && x < mBlockWidth * 2) posX = 1
                if (x > mBlockWidth * 2 && x < mBlockWidth * 3) posX = 2

                if (y > mBlockHeight && y < mBlockHeight * 2) posY = 1
                if (y > mBlockHeight * 2 && y < mBlockHeight * 3) posY = 2

                performClick()
                mGameSessionActivity.humanTakesATurn(posX, posY)
            }
        }
        return super.onTouchEvent(event)
    }

    fun placeSymbol(x: Int, y: Int): Boolean {
        Log.d(TAG, "Thread ID in Board.placeSymbol:" + Thread.currentThread().id)
        invalidateBlock(x, y)
        return true
    }

    fun invalidateBlock(x: Int, y: Int) {
        val selBlock = Rect((x * mBlockWidth).toInt(), (y * mBlockHeight).toInt(), ((x + 1) * mBlockWidth).toInt(), ((y + 1) * mBlockHeight).toInt())
        invalidate(selBlock)
    }

    fun disableInput() {
        this.isInputEnabled = false
        Log.d(TAG, "Board.disableInput(): Board not mIsEnabled")
    }

    fun enableInput() {
        this.isInputEnabled = true
        Log.d(TAG, "Board.enableInput(): Board mIsEnabled")
    }

    fun getBitmapForSymbol(aSymbol: Symbol?): Bitmap? {

        if (!sDrawablesInitialized) {
            try {
                val res = resources
                sSymX = BitmapFactory.decodeResource(res, R.drawable.x)
                sSymO = BitmapFactory.decodeResource(res, R.drawable.o)
                sSymBlank = BitmapFactory.decodeResource(res, R.drawable.blank)
                sDrawablesInitialized = true
            } catch (ome: OutOfMemoryError) {
                Log.d(TAG, "Ran out of memory decoding bitmaps")
                ome.printStackTrace()
            }

        }

        var symSelected = sSymBlank

        if (aSymbol === Symbol.SymbolXCreate())
            symSelected = sSymX
        else if (aSymbol === Symbol.SymbolOCreate())
            symSelected = sSymO
        return symSelected
    }

    companion object {
        internal var sSymX: Bitmap? = null
        internal var sSymO: Bitmap? = null
        internal var sSymBlank: Bitmap? = null
        internal var sDrawablesInitialized = false
    }
}

