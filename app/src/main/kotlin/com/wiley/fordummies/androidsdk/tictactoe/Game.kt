package com.wiley.fordummies.androidsdk.tictactoe

import android.text.TextUtils
import androidx.annotation.Keep
import androidx.core.text.TextUtilsCompat
import timber.log.Timber

@Keep
class Game {

    private enum class STATE { Inactive, Active, Won, Draw }

    private var gameState = STATE.Inactive

    private var currentSymbol: Symbol

    enum class PLAYER { Player1, Player2 }

    var currentPlayer = PLAYER.Player1
    var winningPlayer = PLAYER.Player1

    var playerOneName: String = ""
    var playerTwoName: String = ""

    var gameGrid: GameGrid = GameGrid()

    private var playCount = 0

    init {
        // Constructor
        gameState = STATE.Active
        currentSymbol = Symbol.SymbolXCreate()
    }

    fun setPlayerNames(firstPlayer: String, secondPlayer: String) {
        Timber.tag("Game").d("Setting first player name to $firstPlayer, second player name to $secondPlayer")
        playerOneName = firstPlayer
        playerTwoName = secondPlayer
    }

    fun getCurrentPlayerName(): String {
        if (currentPlayer === PLAYER.Player1) {
            return playerOneName
        } else {
            return playerTwoName
        }
    }

    fun getOtherPlayerName(): String {
        if (currentPlayer === PLAYER.Player1) {
            return playerTwoName
        } else {
            return playerOneName
        }
    }

    fun play(x: Int, y: Int): Boolean {
        var successfulPlay = false
        if (gameGrid.getValueAtLocation(x, y) === Symbol.SymbolBlankCreate()) {
            successfulPlay = true
            playCount += 1
            gameGrid.setValueAtLocation(x, y, currentSymbol)
            checkResultAndSetState()
            if (gameState == STATE.Active) { // if the game is still active
                // Swap symbols and players
                currentSymbol = if (currentSymbol === Symbol.SymbolXCreate())
                    Symbol.SymbolOCreate()
                else
                    Symbol.SymbolXCreate()
                currentPlayer = if (currentPlayer === PLAYER.Player1)
                    PLAYER.Player2
                else
                    PLAYER.Player1
            }
        }
        return successfulPlay
    }

    private fun checkResultAndSetState() {
        if (gameGrid.isRowFilled(0) ||
            gameGrid.isRowFilled(1) ||
            gameGrid.isRowFilled(2) ||
            gameGrid.isColumnFilled(0) ||
            gameGrid.isColumnFilled(1) ||
            gameGrid.isColumnFilled(2) ||
            gameGrid.isLeftToRightDiagonalFilled ||
            gameGrid.isRightToLeftDiagonalFilled
        ) {
            winningPlayer = currentPlayer
            gameState = STATE.Won
        } else if (playCount == 9) {
            gameState = STATE.Draw
        } /* else, leave state as is */
    }

    val isActive: Boolean
        get() = gameState == STATE.Active

    val isWon: Boolean
        get() = gameState == STATE.Won

    val isDrawn: Boolean
        get() = gameState == STATE.Draw

    fun getPlayCount(): Int {
        return playCount
    }
}
