package com.brelio.feature.clients.list

import app.cash.turbine.test
import com.brelio.domain.model.Client
import com.brelio.domain.model.Session
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
import com.brelio.domain.usecase.client.GetClientsUseCase
import com.brelio.domain.usecase.client.SearchClientsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClientsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testSession = Session("token", "refresh", "user-123", "test@test.com")

    private lateinit var getClients: GetClientsUseCase
    private lateinit var searchClients: SearchClientsUseCase
    private lateinit var observeSession: ObserveSessionUseCase

    private val sampleClients = listOf(
        Client("c-1", "Alice", "+1234", "alice@mail.com", null, null, null, emptyList(), 0, "2024-01-01"),
        Client("c-2", "Bob", null, "bob@mail.com", null, "Regular", null, listOf("vip"), 1, "2024-02-15"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getClients = mockk()
        searchClients = mockk()
        observeSession = mockk()
        every { observeSession() } returns kotlinx.coroutines.flow.flowOf(testSession)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    private fun createVm(): ClientsViewModel {
        coEvery { getClients("user-123") } returns Result.success(sampleClients)
        return ClientsViewModel(getClients, searchClients, observeSession)
    }

    @Test
    fun `initial load fetches client list`() = runTest {
        val vm = createVm()

        assertFalse(vm.state.value.isLoading)
        assertEquals(2, vm.state.value.clients.size)
        assertEquals("Alice", vm.state.value.clients[0].name)
    }

    @Test
    fun `search query triggers debounced search`() = runTest {
        val results = listOf(sampleClients[0])
        coEvery { searchClients("user-123", "ali") } returns Result.success(results)
        val vm = createVm()

        vm.onEvent(ClientsEvent.SearchQueryChanged("ali"))
        advanceTimeBy(350)

        assertEquals(1, vm.state.value.clients.size)
        assertEquals("c-1", vm.state.value.clients.first().id)
    }

    @Test
    fun `empty search reloads all clients`() = runTest {
        coEvery { searchClients("user-123", "") } returns Result.success(sampleClients)
        val vm = createVm()

        vm.onEvent(ClientsEvent.SearchQueryChanged(""))
        advanceTimeBy(350)

        // SearchClientsUseCase with blank query returns all clients
        coVerify { searchClients("user-123", "") }
    }

    @Test
    fun `client click emits navigate effect`() = runTest {
        val vm = createVm()

        vm.effect.test {
            vm.onEvent(ClientsEvent.ClientClicked("c-2"))
            val effect = awaitItem()
            assertEquals(ClientsEffect.NavigateToDetail("c-2"), effect)
        }
    }

    @Test
    fun `add client click emits navigate to add`() = runTest {
        val vm = createVm()

        vm.effect.test {
            vm.onEvent(ClientsEvent.AddClientClicked)
            assertEquals(ClientsEffect.NavigateToAddClient, awaitItem())
        }
    }

    @Test
    fun `load failure sets error message`() = runTest {
        coEvery { getClients("user-123") } returns Result.failure(RuntimeException("timeout"))
        val vm = ClientsViewModel(getClients, searchClients, observeSession)

        assertFalse(vm.state.value.isLoading)
        assertEquals("timeout", vm.state.value.error)
    }

    @Test
    fun `refresh clears error and reloads`() = runTest {
        coEvery { getClients("user-123") } returns Result.failure(RuntimeException("oops"))
        val vm = ClientsViewModel(getClients, searchClients, observeSession)

        // Now fix the mock and refresh
        coEvery { getClients("user-123") } returns Result.success(sampleClients)
        vm.onEvent(ClientsEvent.Refresh)

        assertNull(vm.state.value.error)
        assertEquals(2, vm.state.value.clients.size)
    }
}
