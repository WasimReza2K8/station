package com.example.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiEvent : ViewEvent, UiState : ViewState> : ViewModel() {

    private val initialState: UiState by lazy { provideInitialState() }
    private val _viewState: MutableStateFlow<UiState> by lazy { MutableStateFlow(initialState) }
    val viewState: StateFlow<UiState> by lazy { _viewState }

    // Event (user actions)
    private val _event: MutableSharedFlow<UiEvent> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            _event.collect {
                handleEvent(it)
            }
        }
    }

    abstract fun provideInitialState(): UiState

    protected fun updateState(reducer: UiState.() -> UiState) {
        val newState = viewState.value.reducer()
        _viewState.value = newState
    }

    fun onEvent(event: UiEvent) {
        viewModelScope.launch { _event.emit(event) }
    }

    abstract fun handleEvent(event: UiEvent)
}

interface ViewState

interface ViewEvent
