package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.wiley.fordummies.androidsdk.tictactoe.Game
import com.wiley.fordummies.androidsdk.tictactoe.GameGrid
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.Symbol
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore
import com.wiley.fordummies.androidsdk.tictactoe.ui.Board
import com.wiley.fordummies.androidsdk.tictactoe.ui.GameView
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Objects
import java.util.Random

/**
 * Fragment where user plays Tic-Tac-Toe.
 *
 * Created by adamcchampion on 2017/08/19.
 */
class GameSessionFragment : Fragment() {

	var mActiveGame: Game = Game()
	private var mIsTestMode = false
	private lateinit var mBoard: Board
	private lateinit var mTurnStatusView: TextView
	private lateinit var mScoreView: TextView
	private lateinit var mGameView: GameView
	private var mScorePlayerOne = 0
	private var mScorePlayerTwo = 0
	private var mFirstPlayerName: String = ""
	private var mSecondPlayerName: String = ""
	private val mTestMode = false
	private lateinit var mContainer: ViewGroup
	private lateinit var mSavedInstanceState: Bundle

	private val TAG = javaClass.simpleName
	private lateinit var mDataStore: SettingsDataStore

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		CoroutineScope(Dispatchers.IO).launch {
			mIsTestMode = mDataStore.getBoolean("is_test_mode", false)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		Timber.d("onCreateView()")
		val v: View = inflater.inflate(R.layout.fragment_game_session, container, false)
		val rotation = requireActivity().windowManager.defaultDisplay.rotation

		if (container != null) {
			mContainer = container
		}

		retainInstance = true

		// loadGameFromPrefs()

		setHasOptionsMenu(true)

		return v
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (savedInstanceState != null) {
			mSavedInstanceState = savedInstanceState
		}
		mDataStore = SettingsDataStore(context?.applicationContext!!)

		loadGameScores()

		setupBoard(view)
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
		mGameView.showScores(
			mActiveGame.playerOneName,
			mScorePlayerOne,
			mActiveGame.playerTwoName,
			mScorePlayerTwo
		)
		mGameView.setGameStatus(mActiveGame.currentPlayerName + " to play.")
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
		Timber.d("onStop()")
	}

	override fun onDestroyView() {
		super.onDestroyView()
		Timber.d("onDestroyView()")
	}

	override fun onDestroy() {
		super.onDestroy()
		Timber.d("onDestroy()")
	}

	/*
		fun startSession() {
			Timber.d("In startSession()")
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
		/* if (Settings.doesHumanPlayFirst(activity as Context)) {
			mFirstPlayerName = Settings.getName(activity as Context)
			mSecondPlayerName = "Android"
		} else {
			mFirstPlayerName = "Android"
			mSecondPlayerName = Settings.getName(activity as Context)
		} */
		CoroutineScope(Dispatchers.IO).launch {
			val humanPlaysFirst = mDataStore.getBoolean(Settings.Keys.OPT_PLAY_FIRST, Settings.Keys.OPT_PLAY_FIRST_DEF)

			if (humanPlaysFirst) {
				mFirstPlayerName = mDataStore.getString(Settings.Keys.OPT_NAME, Settings.Keys.OPT_NAME_DEFAULT)
				mSecondPlayerName = "Android"
			} else {
				mFirstPlayerName = "Android"
				mSecondPlayerName = mDataStore.getString(Settings.Keys.OPT_NAME, Settings.Keys.OPT_NAME_DEFAULT)
			}
			theGame.setPlayerNames(mFirstPlayerName, mSecondPlayerName)
		}
	}

	private fun loadGameScores() {
		CoroutineScope(Dispatchers.IO).launch {
			Timber.tag(TAG).d("Coroutine: Fetching player scores from DataStore")
			mScorePlayerOne = mDataStore.getInt(SCOREPLAYERONEKEY, 0)
			mScorePlayerTwo = mDataStore.getInt(SCOREPLAYERTWOKEY, 0)
			Timber.tag(TAG).d(
				"Coroutine: mScorePlayerOne = %d; mScorePlayerTwo = %d",
				mScorePlayerOne, mScorePlayerTwo
			)
		}
	}

	private fun saveGameScores() {
		val gameStr = mActiveGame.toString()

		Timber.tag(TAG)
			.d("Player 1 score: %d; player 2 score: %d", mScorePlayerOne, mScorePlayerTwo)
		Timber.tag(TAG).d("Game string: %s", gameStr)

		CoroutineScope(Dispatchers.IO).launch {
			mDataStore.putInt(SCOREPLAYERONEKEY, mScorePlayerOne)
			Timber.tag(TAG)
				.i("Coroutine: wrote Player 1 score %d successfully to DataStore", mScorePlayerOne)
			mDataStore.putInt(SCOREPLAYERTWOKEY, mScorePlayerTwo)
			Timber.tag(TAG)
				.i("Coroutine: Wrote Player 2 score %d successfully to DataStore", mScorePlayerTwo)
			mDataStore.putString(GAMEKEY, gameStr)
			Timber.tag(TAG).i("Coroutine: Wrote game string %s to DataStore", gameStr)
		}
	}


	fun scheduleAndroidsTurn() {
		Timber.d("Thread ID in scheduleAndroidsTurn: %s", Thread.currentThread().id)
		mBoard.disableInput()
		if (!mTestMode) {
			val randomNumber = Random()
			val handler = Handler(Looper.getMainLooper())
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
		Timber.d("Thread ID in androidTakesATurn: %s", Thread.currentThread().id)

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
		Timber.d("Thread ID in humanTakesATurn:%s", Thread.currentThread().id)
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

	private fun loadGameFromPrefs() {
		val appCtx = requireContext().applicationContext
		val prefs = PreferenceManager.getDefaultSharedPreferences(appCtx)
		mScorePlayerOne = prefs.getInt(SCOREPLAYERONEKEY, 0)
		mScorePlayerTwo = prefs.getInt(SCOREPLAYERTWOKEY, 0)
	}

	private fun saveScoresToPrefs() {
		val appCtx = requireContext().applicationContext
		val prefs = PreferenceManager.getDefaultSharedPreferences(appCtx)
		val gameStr = mActiveGame.toString()
		prefs.edit {
			putInt(SCOREPLAYERONEKEY, mScorePlayerOne)
			putInt(SCOREPLAYERTWOKEY, mScorePlayerTwo)
			putString(GAMEKEY, gameStr)
		}
	}



	private fun proceedToFinish() {
		val winningPlayerName: String
		val alertMessage: String
		when {
			mActiveGame.isWon -> {
				winningPlayerName = mActiveGame.winningPlayerName
				alertMessage = "$winningPlayerName Wins!"
				mGameView.setGameStatus(alertMessage)
				accumulateScores(winningPlayerName)
				// saveScoresToPrefs()
				saveGameScores()

				mGameView.showScores(
					mFirstPlayerName,
					mScorePlayerOne,
					mSecondPlayerName,
					mScorePlayerTwo
				)
			}

			mActiveGame.isDrawn -> {
				alertMessage = "DRAW!"
				mGameView.setGameStatus(alertMessage)
			}

			else -> {
				// Control flow should never reach this block, but if it does, show a default text string.
				alertMessage = "Info"
			}
		}
		AlertDialog.Builder(activity)
			.setTitle(alertMessage)
			.setMessage("Play another game?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("Yes") { _, _ ->
				run {
					// saveScoresToPrefs()
					saveGameScores()
					val inflater = LayoutInflater.from(activity)
					if (mContainer != null) {
						Timber.d("Calling setupBoard() again")

						mSavedInstanceState = Bundle()
						activity?.recreate()
						onCreateView(inflater, mContainer, mSavedInstanceState)

						val blankSymbol = Symbol.SymbolBlankCreate()
						for (x in 0 until GameGrid.SIZE) {
							for (y in 0 until GameGrid.SIZE) {
								mActiveGame.gameGrid.setValueAtLocation(x, y, blankSymbol)
							}
						}
					} else {
						Timber.d("Could not restart game. mContainer or mSavedInstanceState were null")
					}
					playNewGame()
				}
			}
			.setNegativeButton("No") { _, _ ->
				run {
					mScorePlayerOne = 0
					mScorePlayerTwo = 0
					saveGameScores()
					// saveScoresToPrefs()
					activity?.finish()
				}
			}
			.show()

	}

	private fun accumulateScores(winningPlayerName: String) {
		if (winningPlayerName == mFirstPlayerName)
			mScorePlayerOne++
		else
			mScorePlayerTwo++
	}

	private fun sendScoresViaEmail() {
		val emailText = "$mFirstPlayerName score is $mScorePlayerOne and " +
				"$mSecondPlayerName score is $mScorePlayerTwo"
		val emailIntent = Intent(Intent.ACTION_SEND)
		emailIntent.putExtra(
			Intent.EXTRA_SUBJECT,
			"Look at my AWESOME TicTacToe Score!"
		)
		emailIntent.type = "plain/text"
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText)
		startActivity(emailIntent)
	}

	private fun sendScoresViaSMS() {
		val smsText = "Look at my AWESOME Tic-Tac-Toe score! $mFirstPlayerName score is " +
				"$mScorePlayerOne and $mSecondPlayerName score is $mScorePlayerOne"
		val smsIntent = Intent(Intent.ACTION_VIEW)
		smsIntent.putExtra("sms_body", smsText)
		smsIntent.type = "vnd.android-dir/mms-sms"
		startActivity(smsIntent)
	}

	private fun callTicTacToeHelp() {
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

	fun getPlayCount(): Int {
		return Objects.requireNonNull(mActiveGame).getPlayCount()
	}

	companion object {
		private const val ANDROID_TIMEOUT_BASE = 500
		private const val ANDROID_TIMEOUT_SEED = 2000

		private const val SCOREPLAYERONEKEY = "ScorePlayerOne"
		private const val SCOREPLAYERTWOKEY = "ScorePlayerTwo"
		private const val GAMEKEY = "Game"
	}
}
