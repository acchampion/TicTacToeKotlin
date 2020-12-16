package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.wiley.fordummies.androidsdk.tictactoe.GameGrid
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.Symbol
import timber.log.Timber


class Board(context: Context, attributes: AttributeSet) : View(context, attributes) {

	// game context (parent)
	private val mGameSessionActivity: GameSessionActivity = context as GameSessionActivity
	private var mBlockWidth: Float = 0.toFloat()        // mBlockWidth of one block
	private var mBlockHeight: Float = 0.toFloat()    // will be same as mBlockWidth;
	private var mGameGrid: GameGrid? = null
	private var mIsEnabled = true

	private val mGridPaint: Paint
	private val mDitherPaint: Paint

	private val TAG = javaClass.simpleName

	init {

		isFocusable = true
		isFocusableInTouchMode = true

		// Allocate Paint objects to save memory.
		mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
		mGridPaint.color = ContextCompat.getColor(context, R.color.grid)

		val strokeWidth = 10f
		mGridPaint.strokeWidth = strokeWidth

		mDitherPaint = Paint()
		mDitherPaint.isDither = true
	}

	fun setGrid(aGrid: GameGrid) {
		mGameGrid = aGrid
	}

	override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
		mBlockWidth = width / 3f
		mBlockHeight = height / 3f

		if (width < height) {
			mBlockHeight = Math.min(mBlockWidth, mBlockHeight)
		} else {
			mBlockWidth = Math.min(mBlockHeight, mBlockWidth)
		}

		super.onSizeChanged(width, height, oldWidth, oldHeight)
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		var canvasWidth = width.toFloat()
		var canvasHeight = height.toFloat()

		if (canvasWidth < canvasHeight) {
			canvasHeight = Math.min(canvasHeight, canvasWidth)
		} else {
			canvasWidth = Math.min(canvasHeight, canvasWidth)
		}

		// canvas.drawRect(0, 0, canvasWidth, canvasHeight, mBackgroundPaint);

		// Drawing lines
		for (i in 0 until GameGrid.SIZE - 1) {
			canvas.drawLine(0f, (i + 1) * mBlockHeight, canvasWidth, (i + 1) * mBlockHeight, mGridPaint)
			canvas.drawLine(0f, (i + 1) * mBlockHeight + 1, canvasWidth, (i + 1) * mBlockHeight + 1, mGridPaint)
			canvas.drawLine((i + 1) * mBlockHeight, 0f, (i + 1) * mBlockHeight, canvasHeight, mGridPaint)
			canvas.drawLine((i + 1) * mBlockHeight + 1, 0f, (i + 1) * mBlockHeight + 1, canvasHeight, mGridPaint)
		}

		var offsetX: Float
		var offsetY: Float
		for (i in 0 until GameGrid.SIZE) {
			for (j in 0 until GameGrid.SIZE) {
				val symSelected = getBitmapForSymbol(mGameGrid!!.getValueAtLocation(i, j))
				offsetX = ((mBlockWidth - symSelected!!.width) / 2 + i * mBlockWidth).toInt().toFloat()
				offsetY = ((mBlockHeight - symSelected.height) / 2 + j * mBlockHeight).toInt().toFloat()
				canvas.drawBitmap(symSelected, offsetX, offsetY, mDitherPaint)
			}
		}
	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (!this.mIsEnabled) {
			Timber.d(TAG, "Board.onTouchEvent(): Board not mIsEnabled")
			return false
		}

		var posX = 0
		var posY = 0
		val action = event.action

		if (action == MotionEvent.ACTION_DOWN) {
			val x = event.x
			val y = event.y
			Timber.d(TAG, "Coordinates: $x, $y")
			if (x > mBlockWidth && x < mBlockWidth * 2) posX = 1
			if (x > mBlockWidth * 2 && x < mBlockWidth * 3) posX = 2

			if (y > mBlockHeight && y < mBlockHeight * 2) posY = 1
			if (y > mBlockHeight * 2 && y < mBlockHeight * 3) posY = 2

			performClick()
			mGameSessionActivity.humanTakesATurn(posX, posY)
		}
		return super.onTouchEvent(event)
	}

	fun placeSymbol(x: Int, y: Int) {
		Timber.d(TAG, "Thread ID in Board.placeSymbol: %s", Thread.currentThread().id)
		invalidateBlock(x, y)
	}

	fun invalidateBlock(x: Int, y: Int) {
        val selBlock = Rect((x * mBlockWidth).toInt(), (y * mBlockHeight).toInt(),
				((x + 1) * mBlockWidth).toInt(), ((y + 1) * mBlockHeight).toInt())
		invalidate()
	}

	fun disableInput() {
		this.mIsEnabled = false
		Timber.d(TAG, "Board.disableInput(): Board not mIsEnabled")
	}

	fun enableInput() {
		this.mIsEnabled = true
		Timber.d(TAG, "Board.enableInput(): Board mIsEnabled")
	}

	fun getBitmapForSymbol(aSymbol: Symbol?): Bitmap? {
		if (!sDrawablesInitialized) {
			try {
				val res = resources
				val blankSym = BitmapFactory.decodeResource(res, R.mipmap.blank)
				val oSym = BitmapFactory.decodeResource(res, R.mipmap.o)
				val xSym = BitmapFactory.decodeResource(res, R.mipmap.x)
				val imgWidth = blankSym.width
				val imgHeight = blankSym.height
				val finalImgWidth = mBlockWidth.toInt() - INSET
				val finalImgHeight = mBlockHeight.toInt() - INSET
				val widthRatio = finalImgWidth.toFloat() / imgWidth.toFloat()
				val heightRatio = finalImgHeight.toFloat() / imgHeight.toFloat()
				val imgScaleRatio = Math.min(widthRatio, heightRatio)

				sSymX = Bitmap.createScaledBitmap(xSym, (imgWidth * imgScaleRatio).toInt(), (imgHeight * imgScaleRatio).toInt(), false)
				sSymO = Bitmap.createScaledBitmap(oSym, (imgWidth * imgScaleRatio).toInt(), (imgHeight * imgScaleRatio).toInt(), false)
				sSymBlank = Bitmap.createScaledBitmap(blankSym, (imgWidth * imgScaleRatio).toInt(), (imgHeight * imgScaleRatio).toInt(), true)
				sDrawablesInitialized = true

				// Clean up old bitmaps.
				blankSym.recycle()
				oSym.recycle()
				xSym.recycle()
			} catch (ome: OutOfMemoryError) {
				Timber.d(TAG, "Ran out of memory decoding bitmaps")
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

		private val INSET = 60
	}
}

