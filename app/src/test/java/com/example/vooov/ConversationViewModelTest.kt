package com.example.vooov

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.ConversationsRepository
import com.example.vooov.repositories.UserRepository
import com.example.vooov.viewModels.ConversationsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.util.*

@ExperimentalCoroutinesApi
class ConversationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: ConversationsViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var conversationsRepository: ConversationsRepository
    val randomUuidConversation = UUID.randomUUID().toString()
    val randomUuid1 = UUID.randomUUID().toString()
    val randomUuid2 = UUID.randomUUID().toString()

    private val user1: UserModel = UserModel(
        null,
        "a@gmail.com",,
        is_verified = 0,
        uuid = randomUuid1,
        pseudo = "Pierrot",
        name = "Caillou",
        firstname = "Pierre",
    )
    private val user2: UserModel = UserModel(
        null,
        "b@gmail.com",,
        is_verified = 0,
        uuid = randomUuid1,
        pseudo = "Dodo",
        name = "Ronfle",
        firstname = "Meunier",
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        userRepository = mockk()
        conversationsRepository = mockk()
        viewModel = ConversationsViewModel()
        //viewModel = ConversationsViewModel(userRepository, conversationsRepository)
    }

    @Test
    fun testCreateConversation() = testScope.runTest {
        val contactUuid = randomUuid1
        val selfUuid = randomUuid2
        val responseUser1 = Response.success(user1)
        val responseUser2 = Response.success(user2)
        val conversation = ConversationsModel(
            null,
            selfUuid,
            contactUuid,
            randomUuidConversation,
            "${user1.pseudo} ${user2.pseudo}",
            Date().toString(),
            Date().toString()
        )
        val responseConversation = Response.success(conversation)

        coEvery { userRepository.readOneUserData(selfUuid) } returns responseUser1
        coEvery { userRepository.readOneUserData(contactUuid) } returns responseUser2
        coEvery { conversationsRepository.createConversationData(conversation) } returns responseConversation

        viewModel.createConversation(contactUuid, selfUuid)

        coVerify { userRepository.readOneUserData(selfUuid) }
        coVerify { userRepository.readOneUserData(contactUuid) }
        coVerify { conversationsRepository.createConversationData(conversation) }
    }
}
