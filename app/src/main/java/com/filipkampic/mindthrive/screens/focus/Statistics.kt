package com.filipkampic.mindthrive.screens.focus

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.focus.StatisticCard
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.FocusViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.filipkampic.mindthrive.model.focus.FocusPeriod
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.floor
import kotlin.math.roundToInt

@SuppressLint("RememberReturnType", "SimpleDateFormat")
@Composable
fun Statistics(
    modifier: Modifier = Modifier,
    viewModel: FocusViewModel
) {
    val entries by viewModel.focusEntries
    val now = System.currentTimeMillis()

    val today = remember(entries) {
        entries.filter { entry ->
            entry.timestamp >= now - 24 * 60 * 60 * 1000
        }.sumOf { entry->
            entry.durationSeconds }
    }

    val total by viewModel.totalFocusFromPrefs.collectAsState(initial = 0)

    var selectedPeriod by remember { mutableStateOf(FocusPeriod.WEEK)}
    val data = viewModel.getFocusPerDay(selectedPeriod)

    val xLabels = data.keys.toList()
    val yValues = data.values.map { (it / 60f * 10).roundToInt() / 10f }
    val labelComponent = axisLabelComponent(
        color = Peach
    )
    val entriesList = yValues.mapIndexed { index, value ->
        FloatEntry(index.toFloat(), value)
    }.let { list ->
        if (list.size == 1) list + FloatEntry(1f, list[0].y) else list
    }
    val chartModel = entryModelOf(entriesList)

    val groupedByDay = entries.groupBy { entry ->
        SimpleDateFormat("yyyy-MM-dd").format(Date(entry.timestamp))
    }
    val dailyTotals = groupedByDay.map { (_, dayEntries) ->
        dayEntries.sumOf { it.durationSeconds }
    }
    val average = if (dailyTotals.isNotEmpty()) dailyTotals.sum() / dailyTotals.size else 0
    val averageMinutes = floor(average / 60f).toInt()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticCard("Focus Today", today, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            StatisticCard("Total Focus", total, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Focus History", color = Peach)
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp)
        ) {
            if (entriesList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available", color = Peach)
                }
            } else {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            LineSpec(
                                lineColor = Peach.toArgb(),
                                lineThicknessDp = 2f
                            )
                        )
                    ),
                    model = chartModel,
                    startAxis = startAxis(
                        label = labelComponent,
                        title = "Minutes",
                        titleComponent = axisLabelComponent(color = Peach)
                    ),
                    bottomAxis = bottomAxis(
                        valueFormatter = { i, _ -> xLabels.getOrNull(i.toInt()) ?: "" },
                        label = labelComponent
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FocusPeriod.entries.forEach { period ->
                Text(
                    text = period.label,
                    color = if (period == selectedPeriod) Peach else Color.LightGray,
                    modifier = Modifier
                        .clickable { selectedPeriod = period }
                        .padding(8.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Daily Average: ${averageMinutes}m", color = Peach)
        }
    }
}
