package com.example.android.politicalpreparedness.navigation

import androidx.navigation.NavController
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class NavigationDispatcher {
    private val navigationEmitter: EventEmitter<NavigationCommandWithHostId> = EventEmitter()
    val navigationCommands: EventSource<NavigationCommandWithHostId> = navigationEmitter

    fun emit(navHostId: Int? = null, navigationCommand: NavigationCommand) {
        navigationEmitter.emit(NavigationCommandWithHostId(navHostId, navigationCommand))
    }
}

data class NavigationCommandWithHostId(
    val navHostId: Int?,
    val navigationCommand: NavigationCommand
) {
    operator fun invoke(navController: NavController) {
        navigationCommand.invoke(navController)
    }
}