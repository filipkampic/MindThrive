import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.ui.habitTracker.HabitItem
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun HabitSection(
    habits: List<Habit>,
    onToggle: (Habit) -> Unit,
    onClick: (Habit) -> Unit,
    onEnterAmount: (Habit) -> Unit,
    onMove: (List<Habit>) -> Unit,
    getChecks: @Composable (Int) -> List<HabitCheck>,
    maxHeight: Dp = 550.dp
) {
    val habitList = remember { mutableStateListOf<Habit>() }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        if (from.index in habitList.indices && to.index in 0..habitList.size) {
            val item = habitList.removeAt(from.index)
            habitList.add(to.index, item)
            onMove(habitList.mapIndexed { index, habit -> habit.copy(position = index) })
        }
    })

    val listState = reorderState.listState

    val showTopFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showBottomFade by remember {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo
            if (visible.isEmpty()) false
            else visible.last().index < listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(habits) {
        habitList.clear()
        habitList.addAll(habits)
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        if (habits.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Peach),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "No habits",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                    color = DarkBlue
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                ) {
                    items(items = habitList, key = { habit -> habit.id }) { habit ->
                        ReorderableItem(reorderState, key = habit.id) {
                            HabitItem(
                                habit = habit,
                                onToggle = { onToggle(habit) },
                                onClick = { onClick(habit) },
                                onEnterAmount = onEnterAmount,
                                checks = getChecks(habit.id)
                            )
                        }
                    }
                }

                if (showTopFade) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(DarkBlue.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                    )
                }

                if (showBottomFade) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBlue.copy(alpha = 0.6f))
                                )
                            )
                    )
                }
            }
        }
    }
}
