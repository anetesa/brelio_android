package com.brelio.feature.home

import app.cash.turbine.test
import com.brelio.domain.model.Appointment
import com.brelio.domain.model.AppointmentService
import com.brelio.domain.model.AppointmentStatus
import com.brelio.domain.model.PaymentStatus
import com.brelio.domain.model.SalonContext
import com.brelio.domain.model.SalonRole
import com.brelio.domain.model.Session
import com.brelio.domain.usecase.appointment.GetTodayAppointmentsUseCase
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
import com.brelio.domain.usecase.salon.GetSalonContextUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testSession = Session("token", "refresh", "user-123", "test@test.com")

    private lateinit var getTodayAppointments: GetTodayAppointmentsUseCase
    private lateinit var getSalonContext: GetSalonContextUseCase
    private lateinit var observeSession: ObserveSessionUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTodayAppointments = mockk()
        getSalonContext = mockk()
        observeSession = mockk()
        every { observeSession() } returns flowOf(testSession)
        coEvery { getSalonContext("user-123") } returns Result.success(
            SalonContext("s1", "user-123", "Europe/Prague", SalonRole.Owner, "test-salon", "CZ")
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    private fun appt(
        id: String,
        status: AppointmentStatus,
        clientId: String = "c-1",
        services: List<AppointmentService> = emptyList(),
        hour: Int = 10,
    ) = Appointment(
        id = id,
        clientId = clientId,
        clientName = "Test Client",
        stylistId = null,
        stylistName = null,
        serviceName = "Cut",
        startAt = LocalDateTime.of(2025, 6, 1, hour, 0),
        endAt = LocalDateTime.of(2025, 6, 1, hour + 1, 0),
        status = status,
        notes = null,
        paymentStatus = PaymentStatus.Unpaid,
        paymentMethod = null,
        services = services,
    )

    @Test
    fun `loads dashboard on init`() = runTest {
        coEvery { getTodayAppointments("user-123") } returns Result.success(emptyList())

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        assertFalse(vm.state.value.isLoading)
        assertNotNull(vm.state.value.salonContext)
    }

    @Test
    fun `calculates revenue from confirmed and completed appointments`() = runTest {
        val svc1 = AppointmentService("s1", "Haircut", 30, 25.0)
        val svc2 = AppointmentService("s2", "Color", 60, 75.0)

        val appointments = listOf(
            appt("a1", AppointmentStatus.Confirmed, services = listOf(svc1)),
            appt("a2", AppointmentStatus.Completed, services = listOf(svc2)),
            appt("a3", AppointmentStatus.Cancelled, services = listOf(svc1)), // excluded
            appt("a4", AppointmentStatus.Pending, services = listOf(svc2)),   // excluded
        )
        coEvery { getTodayAppointments("user-123") } returns Result.success(appointments)

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        // only confirmed + completed: 25 + 75 = 100
        assertEquals(100.0, vm.state.value.stats.todayRevenue, 0.001)
        assertEquals(4, vm.state.value.stats.todayAppointments)
    }

    @Test
    fun `counts no-shows correctly`() = runTest {
        val appointments = listOf(
            appt("a1", AppointmentStatus.NoShow),
            appt("a2", AppointmentStatus.NoShow),
            appt("a3", AppointmentStatus.Confirmed),
        )
        coEvery { getTodayAppointments("user-123") } returns Result.success(appointments)

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        assertEquals(2, vm.state.value.stats.noShowCount)
    }

    @Test
    fun `counts unique clients`() = runTest {
        val appointments = listOf(
            appt("a1", AppointmentStatus.Confirmed, clientId = "c-1"),
            appt("a2", AppointmentStatus.Confirmed, clientId = "c-1"),
            appt("a3", AppointmentStatus.Confirmed, clientId = "c-2"),
        )
        coEvery { getTodayAppointments("user-123") } returns Result.success(appointments)

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        assertEquals(2, vm.state.value.stats.totalClients)
    }

    @Test
    fun `refresh reloads data`() = runTest {
        coEvery { getTodayAppointments("user-123") } returns Result.success(emptyList())

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)
        vm.onEvent(HomeEvent.Refresh)

        // init + refresh = 2 calls
        coVerify(exactly = 2) { getTodayAppointments("user-123") }
    }

    @Test
    fun `appointment click emits navigation effect`() = runTest {
        coEvery { getTodayAppointments("user-123") } returns Result.success(emptyList())

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        vm.effect.test {
            vm.onEvent(HomeEvent.AppointmentClicked("appt-7"))
            assertEquals(HomeEffect.NavigateToAppointment("appt-7"), awaitItem())
        }
    }

    @Test
    fun `failure sets error in state`() = runTest {
        coEvery { getTodayAppointments("user-123") } returns Result.failure(Exception("server down"))

        val vm = HomeViewModel(getTodayAppointments, getSalonContext, observeSession)

        assertFalse(vm.state.value.isLoading)
        assertEquals("server down", vm.state.value.error)
    }
}
