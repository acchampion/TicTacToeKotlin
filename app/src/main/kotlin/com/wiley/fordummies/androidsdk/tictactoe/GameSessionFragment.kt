package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.Board
import timber.log.Timber
import java.util.*

/**
 * Fragment where user plays Tic-Tac-Toe.
 *
 * Created by adamcchampion on 2017/08/19.
 */
class GameSessionFragment : Fragment() {

    private lateinit var mBoard: Board
    private lateinit var mTurnStatusView: TextView
    private lateinit var mScoreView: TextView
    private var mActiveGame: Game = Game()
    private lateinit var mGameView: GameView
    private var mScorePlayerOne = 0
    private var mScorePlayerTwo = 0
    private var mFirstPlayerName: String = ""
    private var mSecondPlayerName: String = ""
    private val mTestMode = false
    private lateinit var mContainer: ViewGroup
    private lateinit var mSavedInstanceState: Bundle
    private val TAG = javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d(TAG, "onCreateView()")
        val v: View
        val rotation = activity?.windowManager?.defaultDisplay?.rotation
        v = if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            inflater.inflate(R.layout.fragment_gamesession_land, container, false)
        } else {
            inflater.inflate(R.layout.fragment_game_session, container, false)
        }

        if (container != null) {
            mContainer = container
        }

        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState
            mScorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY)
            mScorePlayerTwo = savedInstanceState.getInt(SCOREPLAYERTWOKEY)
        }
        retainInstance = true

        setupBoard(v)

        setHasOptionsMenu(true)

        return v
    }

    private fun setupBoard(v: View) {
        mBoard = v.findViewById(R.id.board)
        val turnStatusView = v.findViewById<TextView>(R.id.gameInfo)
        val scoreView = v.findViewById<TextView>(R.id.scoreboard)
        mActiveGame = Game()
        val gameGrid = mActiveGame.gameGrid

        mBoard.setGrid(gameGrid)
        mGameView = GameView(mBoard, turnStatusView, scoreView)
        mGameView.setGameViewComponents(mBoard, turnStatusView, scoreView)
        this.setPlayers(mActiveGame)
        mGameView.showScores(mActiveGame.playerOneName, mScorePlayerOne, mActiveGame.playerTwoName, mScorePlayerTwo)
        mGameView.setGameStatus(mActiveGame.currentPlayerName + " to play.")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.game)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            (activity as AppCompatActivity).setShowWhenLocked(true)
        } else {
            (activity as AppCompatActivity).window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            (activity as AppCompatActivity).window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }


        playNewGame()
    }

    override fun onStop() {
        super.onStop()
        Timber.d(TAG, "onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d(TAG, "onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d(TAG, "onDestroy()")
    }

/*
    fun startSession() {
        Timber.d(TAG, "In startSession()")
        mScorePlayerOne = 0
        mScorePlayerTwo = 0
    }

    val playCount: Int
        get() = mActiveGame.playCount
*/

    private fun playNewGame() {
        // If Android is the first player, give it its turn
        if (mActiveGame.currentPlayerName == "Android") scheduleAndroidsTurn()
    }

    private fun setPlayers(theGame: Game) {

        if (Settings.doesHumanPlayFirst(activity as Context)) {
            mFirstPlayerName = Settings.getName(activity as Context)
            mSecondPlayerName = "Android"
        } else {
            mFirstPlayerName = "Android"
            mSecondPlayerName = Settings.getName(activity as Context)
        }
        theGame.setPlayerNames(mFirstPlayerName, mSecondPlayerName)
    }

    private fun scheduleAndroidsTurn() {
        Timber.d(TAG, "Thread ID in scheduleAndroidsTurn: %s", Thread.currentThread().id)
        mBoard.disableInput()
        if (!mTestMode) {
            val randomNumber = Random()
            val handler = Handler()
            handler.postDelayed(
                    { androidTakesATurn() },
                    (ANDROID_TIMEOUT_BASE + randomNumber.nextInt(ANDROID_TIMEOUT_SEED)).toLong()
            )
        } else {
            androidTakesATurn()
        }
    }

    private fun androidTakesATurn() {
        val pickedX: Int
        val pickedY: Int
        Timber.d(TAG, "Thread ID in androidTakesATurn: %s", Thread.currentThread().id)

        val gameGrid = mActiveGame.gameGrid
        val emptyBlocks = gameGrid.emptySquares
        val n = emptyBlocks.size
        val r = Random()
        val randomIndex = r.nextInt(n)
        val picked = emptyBlocks[randomIndex]
        pickedX = picked.x
        pickedY = picked.y
        mActiveGame.play(pickedX, pickedY)
        mGameView.placeSymbol(pickedX, pickedY)
        mBoard.enableInput()
        if (mActiveGame.isActive) {
            mGameView.setGameStatus(mActiveGame.currentPlayerName + " to play.")
        } else {
            proceedToFinish()
        }
    }

    fun humanTakesATurn(x: Int, y: Int) {/* human's turn */
        Timber.d(TAG, "Thread ID in humanTakesATurn:%s", Thread.currentThread().id)
        val successfulPlay = mActiveGame.play(x, y)
        if (successfulPlay) {
            if (mGameView == null) {
                mGameView = GameView(mBoard, mTurnStatusView, mScoreView)
            }
            mGameView.placeSymbol(x, y) /* Update the display */
            if (mActiveGame.isActive) {
                mGameView.setGameStatus(mActiveGame.currentPlayerName + " to play.")
                scheduleAndroidsTurn()
            } else {
                proceedToFinish()
            }
        }
    }

    private fun quitGame() {
        val fm = activity?.supportFragmentManager
        val abandonGameDialogFragment = AbandonGameDialogFragment()
        if (fm != null) {
            abandonGameDialogFragment.show(fm, "abandon_game")
        }
    }

    private fun proceedToFinish() {
        val winningPlayerName: String
        val alertMessage: String
        if (mActiveGame.isWon) {
            winningPlayerName = mActiveGame.winningPlayerName
            alertMessage = winningPlayerName + " Wins!"
            mGameView.setGameStatus(alertMessage)
            accumulateScores(winningPlayerName)

            mGameView.showScores(mFirstPlayerName, mScorePlayerOne, mSecondPlayerName, mScorePlayerTwo)

        } else if (mActiveGame.isDrawn) {
            alertMessage = "DRAW!"
            mGameView.setGameStatus(alertMessage)
        } else {
            // Control flow should never reach this block, but if it does, show a default text string.
            alertMessage = "Info"
        }
        AlertDialog.Builder(activity)
                .setTitle(alertMessage)
                .setMessage("Play another game?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes") { _, _ -> run {
                    val inflater = LayoutInflater.from(activity)
                    if (mContainer != null) {
                        Timber.d(TAG, "Calling setupBoard() again")

                        mSavedInstanceState = Bundle()
                        onSaveInstanceState(mSavedInstanceState)
                        activity?.recreate()
                        onCreateView(inflater, mContainer, mSavedInstanceState)

                        if (mBoard != null) {
                            val blankSymbol = Symbol.SymbolBlankCreate()
                            for (x in 0 until GameGrid.SIZE) {
                                for (y in 0 until GameGrid.SIZE) {
                                    mActiveGame.gameGrid.setValueAtLocation(x, y, blankSymbol)
                                }
                            }
                        }
                    } else {
                        Timber.d(TAG, "Could not restart game. mContainer or mSavedInstanceState were null")
                    }
                    playNewGame()} }
                .setNegativeButton("No") { _, _ -> activity?.finish() }
                .show()

    }

    private fun accumulateScores(winningPlayerName: String) {
        if (winningPlayerName == mFirstPlayerName)
            mScorePlayerOne++
        else
            mScorePlayerTwo++
    }

    fun sendScoresViaEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Look at my AWESOME TicTacToe Score!")
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                mFirstPlayerName + " score is  " + mScorePlayerOne +
                        " and " +
                        mSecondPlayerName + " score is  " + mScorePlayerTwo)
        startActivity(emailIntent)
    }

    fun sendScoresViaSMS() {
        val SMSIntent = Intent(Intent.ACTION_VIEW)
        SMSIntent.putExtra("sms_body",
                "Look at my AWESOME TicTacToe Score!" +
                        mFirstPlayerName + " score is  " + mScorePlayerOne +
                        " and " +
                        mSecondPlayerName + " score is  " + mScorePlayerTwo)
        SMSIntent.type = "vnd.android-dir/mms-sms"
        startActivity(SMSIntent)
    }

    fun callTicTacToeHelp() {
        val phoneIntent = Intent(Intent.ACTION_DIAL)
        val phoneNumber = "842-822-4357" // TIC TAC HELP
        val uri = "tel:" + phoneNumber.trim { it <= ' ' }
        phoneIntent.data = Uri.parse(uri)
        startActivity(phoneIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_ingame, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_help -> {
                startActivity(Intent(activity?.applicationContext, HelpActivity::class.java))
                return true
            }
            R.id.menu_exit -> {
                quitGame()
                return true
            }
            R.id.menu_email -> {
                sendScoresViaEmail()
                return true
            }
            R.id.menu_sms -> {
                sendScoresViaSMS()
                return true
            }
            R.id.menu_call -> {
                callTicTacToeHelp()
                return true
            }
        }
        return false
    }

    @Suppress("USELESS_ELVIS")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val instanceState: Bundle = outState ?: Bundle()
        // Save session score
        instanceState.putInt(SCOREPLAYERONEKEY, mScorePlayerOne)
        instanceState.putInt(SCOREPLAYERTWOKEY, mScorePlayerTwo)
        // Save turn
        instanceState.putString(GAMEKEY, mActiveGame.toString())
    }

    companion object {
        private const val ANDROID_TIMEOUT_BASE = 500
        private const val ANDROID_TIMEOUT_SEED = 2000

        private const val SCOREPLAYERONEKEY = "ScorePlayerOne"
        private const val SCOREPLAYERTWOKEY = "ScorePlayerTwo"
        private const val GAMEKEY = "Game"
    }
}
