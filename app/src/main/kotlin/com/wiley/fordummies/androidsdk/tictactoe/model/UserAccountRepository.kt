package com.wiley.fordummies.androidsdk.tictactoe.model

import android.app.Application
import androidx.lifecycle.LiveData

/**
 * Single point of accessing UserAccount data in the app.
 *
 * Source: https://developer.android.com/codelabs/android-room-with-a-view
 *
 * Created by acc on 2021/08/04.
 */
class UserAccountRepository internal constructor(application: Application) {
    private var mUserAccountDao: UserAccountDao

    // Room executes all queries on a separate thread.
    // Observed LiveData notify the observer upon data change.
    val allUserAccounts: LiveData<List<UserAccount>>
    private val TAG = javaClass.simpleName
    fun findUserAccountByName(userAccount: UserAccount): LiveData<UserAccount> {

//		try {
//			Future<LiveData<UserAccount>> future =
//					(Future<LiveData<UserAccount>>) UserAccountDatabase.databaseWriteExecutor.submit(() -> {
//						mUserAccountDao.findByName(userAccount.getName(), userAccount.getPassword());
//					});
//			while (!future.isDone()) {
//				Timber.d(TAG, "Waiting for query to complete");
//				Thread.sleep(100);
//			}
//			theUserAccount = future.get(2, TimeUnit.SECONDS);
//		} catch (ExecutionException e) {
//			Timber.e(TAG, "Could not find UserAccount by name");
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			Timber.e(TAG, "Database query was interrupted");
//			e.printStackTrace();
//		} catch (TimeoutException e) {
//			Timber.e(TAG, "Query task timed out");
//			e.printStackTrace();
//		}
        return mUserAccountDao.findByName(userAccount.name, userAccount.password)
    }

    // You MUST call this on a non-UI thread or the app will throw an exception.
    // I'm passing a Runnable object to the database.
    fun insert(userAccount: UserAccount) {
        UserAccountDatabase.databaseWriteExecutor.execute(Runnable {
            mUserAccountDao.insert(
                userAccount
            )
        })
    }

    init {
        val db: UserAccountDatabase = UserAccountDatabase.getDatabase(application)
        mUserAccountDao = db.userAccountDao
        allUserAccounts = mUserAccountDao.allUserAccounts
    }
}
