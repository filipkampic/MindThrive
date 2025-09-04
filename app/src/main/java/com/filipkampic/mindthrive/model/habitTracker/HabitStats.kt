package com.filipkampic.mindthrive.model.habitTracker

class HabitStats (
    val currentStreak: Int,
    val bestStreak: Int,
    val successRate: Int,
    val successCount: Int = 0,
    val totalExpected: Int = 0
)