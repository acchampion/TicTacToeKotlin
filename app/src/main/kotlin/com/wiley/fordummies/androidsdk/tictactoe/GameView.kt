package com.wiley.fordummies.androidsdk.tictactoe

import android.widget.TextView
import com.wiley.fordummies.androidsdk.tictactoe.ui.Board

class GameView(board: Board, statusView: TextView, sessionScoresView: TextView) {
    private var mGameBoard: Board
    private var mStatusView: TextView
    private var mSessionScoresView: TextView

    init {
        mGameBoard = board
        mStatusView = statusView
        mSessionScoresView = sessionScoresView
    }

    fun setGameViewComponents(theBoard: Board, theStatusView: TextView, theSessionScoresView: TextView) {
        mGameBoard = theBoard
        mStatusView = theStatusView
        mSessionScoresView = theSessionScoresView
    }

    fun setGameStatus(message: String) {
        mStatusView.text = message
    }

    fun showScores(player1Name: String, player1Score: Int, player2Name: String, player2Score: Int) {
        val scoresText = "$player1Name:$player1Score....$player2Name:$player2Score"
        mSessionScoresView.text = scoresText
    }

    fun placeSymbol(x: Int, y: Int) {
        if (mGameBoard != null) {
            mGameBoard.placeSymbol(x, y)
            mGameBoard.invalidateBlock(x, y)
        }
    }
}
