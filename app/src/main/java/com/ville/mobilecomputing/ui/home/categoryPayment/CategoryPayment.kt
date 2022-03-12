package com.ville.mobilecomputing.ui.home.categoryPayment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ville.mobilecomputing.Graph.paymentRepository
import com.ville.mobilecomputing.R
import com.ville.mobilecomputing.data.entity.Category
import com.ville.mobilecomputing.data.entity.Payment
import com.ville.mobilecomputing.data.room.PaymentToCategory
import com.ville.mobilecomputing.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryPayment(
    categoryId: Long,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: CategoryPaymentViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { CategoryPaymentViewModel(categoryId) }
    )
    val query = viewModel.query.value
    val viewState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    Surface(
        elevation = 8.dp,
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            TextField(
                value = query,
                shape = RectangleShape,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onValueChange = {
                    viewModel.onQueryChanged(query = it)
                    // viewModel.newSearch(categoryId, query)
                },
                //label = { Text("Search") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                leadingIcon = { Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(24.dp)
                )},
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.newSearch(categoryId, query)
                        focusManager.clearFocus()
                    }
                )
            )
        }
    }

    Column(modifier = modifier) {
        PaymentList(
            payments = viewState.payments
        )
        OpenPieChartButton(navController, categoryId)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PaymentList(
    payments: List<PaymentToCategory>
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(
            items = payments,
            key = {_, item ->
                item.hashCode()
            }
        ) { index, item ->
            val state = rememberDismissState(
                confirmStateChange = {
                    Log.d("meme", it.toString())
                    if (it == DismissValue.DismissedToStart) {
                        Log.d("meme", "remove ${item.payment}")
                        coroutineScope.launch {
                            paymentRepository.deletePayment(item.payment)
                        }
                    }
                    true
                }
            )

            SwipeToDismiss(
                state = state,
                background = {
                    val color = when (state.dismissDirection) {
                        DismissDirection.StartToEnd -> Color.Transparent
                        DismissDirection.EndToStart -> Color.LightGray
                        null -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = color)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                },

                dismissContent = {
                    PaymentListItem(
                        payment = item.payment,
                        category = item.category,
                        onClick = {},
                        modifier = Modifier.fillParentMaxWidth(),
                    )
                },
                directions = setOf(DismissDirection.EndToStart)
            )
        }
    }
}

@Composable
private fun PaymentListItem(
    payment: Payment,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier.clickable { onClick() }) {
        val (paymentTitle, paymentCategory, icon, date, divider) = createRefs()

        // title
        Text(
            text = payment.paymentTitle,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(paymentTitle) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f // float this towards the start. this was is the fix we needed
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        // category
        Text(
            text = payment.paymentAmount.toString() + "€",
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(paymentCategory) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 8.dp,
                    bias = 0f // float this towards the start. this was is the fix we needed
                )
                top.linkTo(paymentTitle.bottom, margin = 6.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        // date
        Text(
            text = payment.paymentDate.toDateString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date) {
                linkTo(
                    start = paymentCategory.end,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp,
                    bias = 0f // float this towards the start. this was is the fix we needed
                )
                centerVerticallyTo(paymentCategory)
                top.linkTo(paymentTitle.bottom, 6.dp)
                bottom.linkTo(parent.bottom, 10.dp)
            }
        )

        // icon
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .size(50.dp)
                .padding(6.dp)
                .constrainAs(icon) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = stringResource(R.string.check_mark)
            )
        }

        Divider(
            Modifier.constrainAs(divider) {
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
private fun OpenPieChartButton(
    navController: NavController,
    categoryId: Long
) {
    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = { navController.navigate(route = "pieChart/$categoryId") },
        enabled = true,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF9DFFB9),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .width(140.dp)
            .size(45.dp)
            .padding(horizontal = 14.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Pie Chart",
            color = MaterialTheme.colors.onPrimary
        )
    }
}

private fun Date.formatToString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(this)
}

fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}