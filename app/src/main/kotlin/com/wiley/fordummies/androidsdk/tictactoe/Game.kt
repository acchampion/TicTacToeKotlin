package com.wiley.fordummies.androidsdk.tictactoe

class Game {

    private enum class STATE { Inactive, Active, Won, Draw }

    private var gameState = STATE.Inactive

    private var currentSymbol: Symbol

    private enum class PLAYER { Player1, Player2}

    private var currentPlayer = PLAYER.Player1
    private var winningPlayer = PLAYER.Player1

    lateinit var playerOneName: String
    lateinit var playerTwoName: String

    var gameGrid: GameGrid = GameGrid()

    private var playCount = 0

    init {
        // Constructor
        gameState = STATE.Active
        currentSymbol = Symbol.SymbolXCreate()
    }

    fun setPlayerNames(FirstPlayer: String, SecondPlayer: String) {
        playerOneName = FirstPlayer
        playerTwoName = SecondPlayer
    }

    val currentPlayerName: String
        get() = if (currentPlayer == PLAYER.Player1)
            playerOneName
        else
            playerTwoName

    val winningPlayerName: String
        get() = if (winningPlayer == PLAYER.Player1)
            playerOneName
        else
            playerTwoName

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
                gameGrid.isRightToLeftDiagonalFilled) {
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
