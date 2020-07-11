package com.company.todd.objects.passive

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.screen.GameScreen

interface PassiveObjectInfo {
    fun create(game: ToddGame): PassiveObject
}
