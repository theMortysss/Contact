package com.example.library.repository.alarm

import com.example.java.interfaces.ICalendarRepository
import java.util.*

class CalendarRepository : ICalendarRepository {
    override fun now(): Calendar = Calendar.getInstance()
}