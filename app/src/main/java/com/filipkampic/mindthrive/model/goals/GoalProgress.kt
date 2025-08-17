package com.filipkampic.mindthrive.model.goals

data class GoalProgress(
    val done: Int,
    val total: Int,
    val ratio: Float
)
