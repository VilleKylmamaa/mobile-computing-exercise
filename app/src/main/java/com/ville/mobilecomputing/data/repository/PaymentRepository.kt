package com.ville.mobilecomputing.data.repository

import com.ville.mobilecomputing.data.entity.Payment
import com.ville.mobilecomputing.data.room.PaymentDao
import com.ville.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Payment] instances
 */
class PaymentRepository(
    private val paymentDao: PaymentDao
) {
    fun paymentsInCategory(categoryId: Long, query: String) : Flow<List<PaymentToCategory>> {
        return paymentDao.paymentsFromCategory(categoryId, query)
    }
    suspend fun addPayment(payment: Payment) = paymentDao.insert(payment)
    suspend fun getPaymentCount(categoryId: Long): Int = paymentDao.getPaymentCount(categoryId)
    suspend fun deletePayment(payment: Payment) = paymentDao.delete(payment)
}