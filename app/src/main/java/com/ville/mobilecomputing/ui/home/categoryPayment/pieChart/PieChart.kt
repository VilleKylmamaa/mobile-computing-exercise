package com.ville.mobilecomputing.ui.home.categoryPayment.pieChart

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.systemBarsPadding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ville.mobilecomputing.Graph
import com.ville.mobilecomputing.util.viewModelProviderFactoryOf
import kotlin.math.atan2

@Composable
fun PieChart(
    onBackPress: () -> Unit,
    categoryId: Long,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val viewModel: PieChartViewModel = viewModel(
        factory = viewModelProviderFactoryOf {
            PieChartViewModel(categoryId)
        }
    )
    val viewState by viewModel.state.collectAsState()
    var categoryName by remember { mutableStateOf("") }
    var paymentCount by remember { mutableStateOf(0) }
    val listOfTitles = mutableListOf<String>()
    val listOfPayments = mutableListOf<Float>()

    LaunchedEffect(scaffoldState.snackbarHostState) {
        categoryName = Graph.categoryRepository.getCategoryWithId(categoryId)?.name.toString()
        paymentCount = Graph.paymentRepository.getPaymentCount(categoryId)
    }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar(
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red,
                            Color.Green
                        )
                    )
                ),
            ) {
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Pie Chart")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "$categoryName spending",
                    fontSize = 26.sp,
                )
                Spacer(modifier = Modifier.height(30.dp))

                LazyColumn {
                    items(viewState.payments.value.orEmpty()) { item ->
                        if (listOfTitles.size < paymentCount) {
                            listOfTitles.add(item.payment.paymentTitle)
                            listOfPayments.add(item.payment.paymentAmount.toFloat())

                            Text(item.payment.paymentTitle + " - " + item.payment.paymentAmount + "€",
                                modifier = Modifier.padding(horizontal = 30.dp))
                        }
                        // Make pie chart after all values have been collected
                        if (listOfTitles.size >= paymentCount) {
                            YummyPieChart(listOfTitles, listOfPayments)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YummyPieChart(
    listOfTitles: List<String>,
    listOfPayments: List<Float>
) {
    val context = LocalContext.current
    val color = listOf(
        Color.Blue,
        Color.Yellow,
        Color.Green,
        Color.Gray,
        Color.Red,
        Color.Cyan
    )
    val sum = listOfPayments.sum()
    var startAngle = 0f
    val radius = 350f
    val rect = Rect(Offset(-radius, -radius), Size(2 * radius, 2 * radius))
    val path = Path()
    val angles = mutableListOf<Float>()
    var start by remember { mutableStateOf(false) }
    val sweepPre by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000)
    )
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp)
            .height(350.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val x = it.x - radius
                        val y = it.y - radius
                        var touchAngle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()))
                        if (x < 0 && y < 0 || x > 0 && y < 0) {
                            touchAngle += 360
                        }
                        val position =
                            getPositionFromAngle(touchAngle = touchAngle, angles = angles)
                        Log.d("meme", position.toString())
                        Toast
                            .makeText(context, listOfTitles[position], Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
    ) {
        translate(radius, radius) {
            start = true
            for ((i, p) in listOfPayments.withIndex()) {
                val sweepAngle = p / sum * 360f
                path.moveTo(0f, 0f)
                path.arcTo(rect = rect, startAngle, sweepAngle * sweepPre, false)
                angles.add(sweepAngle)
                drawPath(path = path, color = color[i])
                path.reset()
                startAngle += sweepAngle
            }
        }
    }
}

private fun getPositionFromAngle(
    angles: List<Float>,
    touchAngle: Double
): Int {
    var totalAngle = 0f
    for ((i, angle) in angles.withIndex()) {
        totalAngle += angle
        if (touchAngle <= totalAngle) {
            return i
        }
    }
    return -1
}
