package com.ville.mobilecomputing.ui.home.categoryPayment

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.mobilecomputing.Graph
import com.ville.mobilecomputing.data.repository.PaymentRepository
import com.ville.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CategoryPaymentViewModel(
    private val categoryId: Long,
    private val paymentRepository: PaymentRepository = Graph.paymentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryPaymentViewState())
    val query = mutableStateOf("")

    val state: StateFlow<CategoryPaymentViewState>
        get() = _state

    fun newSearch(categoryId: Long, query: String){
        viewModelScope.launch {
            paymentRepository.paymentsInCategory(categoryId, query).collect { list ->
                _state.value = CategoryPaymentViewState(
                    payments = list
                )
            }
        }
    }
    fun onQueryChanged(query: String){
        this.query.value = query
    }

    init {
        newSearch(categoryId, query.value)
    }
}

data class CategoryPaymentViewState(
    val payments: List<PaymentToCategory> = emptyList()
)