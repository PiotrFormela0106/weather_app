package com.example.base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@FlowPreview
class UiEvents<T : Any> {
    private val channel = BroadcastChannel<T>(1)

    fun post(event: T) = runBlocking { channel.send(event) }

    fun events(): Flow<T> = channel.asFlow()
}
