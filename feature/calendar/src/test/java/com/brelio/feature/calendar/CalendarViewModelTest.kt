package com.brelio.feature.calendar

import app.cash.turbine.test
import com.brelio.domain.model.Appointment
import com.brelio.domain.model.AppointmentStatus
import com.brelio.domain.model.PaymentStatus
import com.brelio.domain.model.Session
import com.brelio.domain.usecase.appointment.GetAppointmentsByDateUseCase
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testSession = Session("token", "refresh", "user-123", "test@test.com")

    private lateinit var getAppointmentsByDate: GetAppointmentsByDateUseCase
    private lateinit var observeSession: ObserveSessionUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAppointmentsByDate = mockk()
        observeSession = mockk()
        every { observeSession() } returns flowOf(testSession)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    private fun buildAppointment(id: String, hour: Int = 10) = Appointment(
        id = id,
        clientId = "c-1",
        clientName = "Jane Doe",
        stylistId = null,
        stylistName = null,
        serviceName = "Haircut",
        startAt = LocalDateTime.of(2025, 3, 15, hour, 0),
        endAt = LocalDateTime.of(2025, 3, 15, hour + 1, 0),
        status = AppointmentStatus.Confirmed,
        notes = null,
        paymentStatus = PaymentStatus.Unpaid,
        paymentMethod = null,
        services = emptyList(),
    )

    @Test
    fun `init loads appointments for today`() = runTest {
        val appointments = listOf(buildAppointment("a-1"))
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(appointments)

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)

        assertFalse(vm.state.value.isLoading)
        assertEquals(1, vm.state.value.appointments.size)
    }

    @Test
    fun `nextDay advances selected date by one`() = runTest {
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(emptyList())

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)
        val initialDate = vm.state.value.selectedDate

        vm.onEvent(CalendarEvent.NextDay)

        assertEquals(initialDate.plusDays(1), vm.state.value.selectedDate)
    }

    @Test
    fun `previousDay goes back one day`() = runTest {
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(emptyList())

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)
        val startDate = vm.state.value.selectedDate

        vm.onEvent(CalendarEvent.PreviousDay)

        val expected = startDate.minusDays(1)
        assertEquals(expected, vm.state.value.selectedDate)
    }

    @Test
    fun `selecting same date does not reload`() = runTest {
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(emptyList())

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)
        val date = vm.state.value.selectedDate

        // init triggers one load; selecting the same date should be a no-op
        vm.onEvent(CalendarEvent.DateSelected(date))

        // once for init only
        coVerify(exactly = 1) { getAppointmentsByDate("user-123", date) }
    }

    @Test
    fun `appointments are sorted by start time`() = runTest {
        val late = buildAppointment("late", hour = 16)
        val early = buildAppointment("early", hour = 9)
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(listOf(late, early))

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)

        assertEquals("early", vm.state.value.appointments.first().id)
        assertEquals("late", vm.state.value.appointments.last().id)
    }

    @Test
    fun `appointment click emits navigation effect`() = runTest {
        coEvery { getAppointmentsByDate("user-123", any()) } returns Result.success(emptyList())

        val vm = CalendarViewModel(getAppointmentsByDate, observeSession)

        vm.effect.test {
            vm.onEvent(CalendarEvent.AppointmentClicked("appt-42"))
            assertEquals(CalendarEffect.NavigateToAppointment("appt-42"), awaitItem())
        }
    }
}
