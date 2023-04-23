package com.example.vooov

import android.content.ContentValues
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vooov.data.model.UserModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
   lateinit var userViewModel: UserViewModel
    val username = 254

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addition_isCorrect() {
        val latch = CountDownLatch(1)

        CoroutineScope(Dispatchers.Main).launch {
            userViewModel.fetchOneUserById(username)
        }

        val testObserver = userViewModel.user.observe( Li, androidx.lifecycle.Observer { user ->
            if (user != null) {
                Log.i(ContentValues.TAG, user.toString())
                println(user.toString())

                // Add assertions or other test logic
            }
            latch.countDown()
        })
        userViewModel.userById.observeForever(testObserver)

        // Wait for the data to be updated
        latch.await(2, TimeUnit.SECONDS)

        // Clean up
        userViewModel.userById.removeObserver(testObserver)
    }

}