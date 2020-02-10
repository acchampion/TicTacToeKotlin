package com.wiley.fordummies.androidsdk.tictactoe

import timber.log.Timber
import java.util.*

class GameGrid {
    companion object {
        const val SIZE = 3
    }
    private val mGrid: Array<Array<Symbol?>>

    private val TAG = javaClass.simpleName

    init {
        // Constructor. Initializes the mGrid to blanks
        mGrid = array2d(3, 3) { null }
        for (i in 0 until SIZE)
            for (j in 0 until SIZE)
                mGrid[i][j] = Symbol.SymbolBlankCreate()
    }

    // Source: https://stackoverflow.com/questions/27512636/two-dimensional-int-array-in-kotlin
    private inline fun <reified INNER> array2d(sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int)->INNER): Array<Array<INNER>>
            = Array(sizeOuter) { Array(sizeInner, innerInit) }

    fun setValueAtLocation(x: Int, y: Int, value: Symbol) {
        if (x in 0 until SIZE && y in 0 until SIZE)
            mGrid[x][y] = value
    }

    fun getValueAtLocation(x: Int, y: Int): Symbol? {
        var returnValue: Symbol? = null
        if (x in 0 until SIZE && y in 0 until SIZE)
            returnValue = mGrid[x][y]
        return returnValue
    }

    fun isRowFilled(row: Int): Boolean {
        // Entire row has the same symbol
        var foundMismatch = false
        var col = 0
        while (col < SIZE && !foundMismatch) {
            if (mGrid[row][0] !== mGrid[row][col])
                foundMismatch = true
            col++
        }
        return !foundMismatch && mGrid[row][0] !== Symbol.SymbolBlankCreate()
    }

    fun isColumnFilled(column: Int): Boolean {
        // Entire column has the same symbol
        var foundMismatch = false
        var row = 0
        while (row < SIZE && !foundMismatch) {
            if (mGrid[0][column] !== mGrid[row][column]) foundMismatch = true
            row++
        }
        return !foundMismatch && mGrid[0][column] !== Symbol.SymbolBlankCreate()
    }

    // Left diagonal has the same symbol
    val isLeftToRightDiagonalFilled: Boolean
        get() {
            var foundMismatch = false
            var index = 0
            while (index < SIZE && !foundMismatch) {
                if (mGrid[0][0] !== mGrid[index][index]) foundMismatch = true
                index++
            }
            return !foundMismatch && mGrid[0][0] !== Symbol.SymbolBlankCreate()
        }

    // Right diagonal has the same symbol
    val isRightToLeftDiagonalFilled: Boolean
        get() {
            var foundIndex = -1
            var foundMismatch = false
            Timber.d(TAG, "Entering isRightToLeftDiagonalFilled")
            var index = SIZE - 1
            while (index >= 0 && !foundMismatch) {
                val logStr = ">" + mGrid[0][SIZE - 1].toString() + "<   >" + mGrid[index][index].toString() + "<"
                Timber.d(logStr)
                if (mGrid[0][SIZE - 1] !== mGrid[SIZE - 1 - index][index]) {
                    foundMismatch = true
                    foundIndex = index
                }
                index--
            }
            val finalLogStr = "Leaving isRightToLeftDiagonalFilled; " + foundMismatch + "; index: " + foundIndex + "; " + mGrid[0][SIZE - 1].toString()
            Timber.d(TAG, finalLogStr)
            return !foundMismatch && mGrid[0][SIZE - 1] !== Symbol.SymbolBlankCreate()
        }

    // Get the unfilled squares
    val emptySquares: ArrayList<Square>
        get() {
            val list = ArrayList<Square>()
            for (i in 0 until SIZE) {
                (0 until SIZE)
                        .asSequence()
                        .filter { mGrid[i][it] === Symbol.SymbolBlankCreate() }
                        .mapTo(list) { Square(i, it) }
            }
            return list
        }
}
