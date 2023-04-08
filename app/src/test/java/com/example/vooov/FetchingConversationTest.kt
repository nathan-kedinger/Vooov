package com.example.vooov
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.repositories.ConversationsRepository
import com.example.vooov.viewModels.ConversationsViewModel
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import retrofit2.Response
import java.util.*


@ExperimentalCoroutinesApi
    class FetchingConversationTest {
        @get:Rule
        val instantTaskExecutorRule = InstantTaskExecutorRule()

        private val testDispatcher = StandardTestDispatcher()
        private val testScope = TestScope(testDispatcher)

        @Mock
        private lateinit var repository: ConversationsRepository
        @InjectMockKs
        private lateinit var viewModel: ConversationsViewModel


        val randomUuid1 = UUID.randomUUID().toString()
        val randomUuid2 = UUID.randomUUID().toString()

        @Before
        fun setup() {
            repository = mockk()
            viewModel = ConversationsViewModel()
        }

        @Test
        fun fetchConversationsTest() = testScope.runTest {
            val selfUuid = randomUuid1
            val response = Response.success(mutableListOf<ConversationsModel>())

            coEvery { repository.readConversationData(selfUuid) } returns response

            viewModel.fetchConversations(selfUuid)

            coVerify { repository.readConversationData(selfUuid) }
        }

        @Test
        fun fetchOneConversationTest() = testScope.runTest {
            val contactUuid = randomUuid2
            val selfUuid = randomUuid1
            val conversationList = MutableLiveData<MutableList<ConversationsModel>>()

            viewModel.conversationList = conversationList

            coEvery { viewModel.fetchConversations(selfUuid) } coAnswers {
                conversationList.value = mutableListOf<ConversationsModel>()
            }

            val observer = mockk<Observer<ConversationsModel>>(relaxed = true)
            viewModel.conversation.observeForever(observer)

            viewModel.fetchOneConversation(contactUuid, selfUuid)

            coVerify { viewModel.fetchConversations(selfUuid) }
        }
    }

