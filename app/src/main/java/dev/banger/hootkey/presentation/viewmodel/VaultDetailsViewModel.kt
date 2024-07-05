package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.entity.UiField
import dev.banger.hootkey.presentation.intent.VaultDetailsIntent
import dev.banger.hootkey.presentation.state.vault_details.VaultDetailsState
import dev.banger.hootkey.presentation.ui.utils.formatDate
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VaultDetailsViewModel @AssistedInject constructor(
    @Assisted private val vaultId: String,
    private val vaultRepository: VaultRepository,
    @IoDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow<VaultDetailsState>(VaultDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        loadVault()
    }

    fun dispatch(intent: VaultDetailsIntent) {
        when (intent) {
            is VaultDetailsIntent.LoadVault -> loadVault()
            is VaultDetailsIntent.FavoriteVault -> favoriteVault()
            is VaultDetailsIntent.ChangeFieldVisibility -> changeFieldVisibility(intent.fieldIndex)
        }
    }

    private fun changeFieldVisibility(fieldIndex: Int) {
        _state.update {
            when (val currentState = it) {
                is VaultDetailsState.Content -> {
                    val field = currentState.fields[fieldIndex]
                    val newFields = currentState.fields.toMutableList()
                    newFields[fieldIndex] = field.copy(isHidden = !field.isHidden)
                    currentState.copy(fields = newFields)
                }

                else -> currentState
            }
        }
    }

    private fun favoriteVault() {
        if (_state.value !is VaultDetailsState.Content) return
        if ((_state.value as? VaultDetailsState.Content)?.canFavorite != true) return
        viewModelScope.launch(defaultDispatcher) {
            when (val currentState = _state.value) {
                is VaultDetailsState.Content -> {
                    _state.update { currentState.copy(canFavorite = false) }
                    val isFavorite = !currentState.isFavorite
                    runCatching {
                        if (isFavorite) vaultRepository.addToFavourites(vaultId)
                        else vaultRepository.removeFromFavourites(vaultId)
                    }.onSuccess {
                        _state.update {
                            currentState.copy(
                                isFavorite = isFavorite,
                                canFavorite = true
                            )
                        }
                    }.onFailure {
                        if (it is CancellationException) throw it
                        _state.update { currentState.copy(canFavorite = true) }
                    }
                }

                else -> Unit
            }
        }
    }

    private fun loadVault() {
        viewModelScope.launch(defaultDispatcher) {
            _state.update { VaultDetailsState.Loading }
            runCatching {
                vaultRepository.getById(vaultId)
            }.onSuccess { vault ->
                _state.update {
                    VaultDetailsState.Content(name = vault.name,
                        categoryName = vault.category.name,
                        fields = vault.fieldValues.map { (index, field) ->
                            index to UiField(
                                value = if (field.type == FieldType.DATE) field.value.toLongOrNull()?.let { formatDate(it) } ?: "" else field.value,
                                name = field.name,
                                type = field.type.toUi(),
                                valueMillis = if (field.type == FieldType.DATE) field.value.toLongOrNull() else null
                            )
                        }.sortedBy { it.first }.map { it.second },
                        isFavorite = vault.isFavourite,
                        canFavorite = true
                    )
                }
                runCatching {
                    vaultRepository.notifyViewed(vaultId)
                }.onFailure {
                    if (it is CancellationException) throw it
                }
            }.onFailure {
                if (it is CancellationException) throw it
                _state.update { VaultDetailsState.Error }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(vaultId: String): VaultDetailsViewModel
    }

    companion object {
        fun factory(
            factory: Factory,
            vaultId: String
        ) : ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(vaultId) as T
                }
            }
        }
    }

}