package com.ville.mobilecomputing.ui.home.categoryPayment.pieChart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.mobilecomputing.Graph
import com.ville.mobilecomputing.Graph.categoryRepository
import com.ville.mobilecomputing.data.entity.Payment
import com.ville.mobilecomputing.data.repository.PaymentRepository
import com.ville.mobilecomputing.data.room.PaymentToCategory
import com.ville.mobilecomputing.ui.payment.PaymentViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PieChartViewModel (
    private val categoryId: Long,
    private val paymentRepository: PaymentRepository = Graph.paymentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PieChartViewState())
    val query = mutableStateOf("")

    val state: StateFlow<PieChartViewState>
    get() = _state

    init {
        viewModelScope.launch {
            paymentRepository.paymentsInCategory(categoryId, "").collect { list ->
                _state.value = PieChartViewState(
                    payments = MutableLiveData(list)
                )
            }
        }
    }
}

data class PieChartViewState(
    val payments: MutableLiveData<List<PaymentToCategory>> = MutableLiveData(emptyList()),
    val categoryName: String? = ""
)
