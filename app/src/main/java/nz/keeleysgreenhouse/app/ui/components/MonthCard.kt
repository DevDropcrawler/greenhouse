package nz.keeleysgreenhouse.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.Fraunces
import nz.keeleysgreenhouse.app.ui.theme.Honey
import nz.keeleysgreenhouse.app.ui.theme.Inter
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.Terracotta

private val MonthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private fun seasonalEmoji(month: Int): String = when (month) {
    9, 10, 11 -> "\uD83C\uDF31"
    12, 1, 2 -> "\u2600\uFE0F"
    3, 4, 5 -> "\uD83C\uDF42"
    else -> "\u2744\uFE0F"
}

@Composable
fun MonthCard(
    month: Int,
    seedCount: Int,
    seedlingCount: Int,
    harvestCount: Int,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = if (isCurrent) BorderStroke(1.5.dp, Forest) else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = border
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    if (isCurrent) {
                        Text(
                            text = "THIS MONTH",
                            color = Brass,
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            fontSize = 10.sp
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                    Text(
                        text = MonthNames[month - 1],
                        color = OliveMoss,
                        fontFamily = Fraunces,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        maxLines = 1
                    )
                }
                Text(
                    text = seasonalEmoji(month),
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CountBadge("S", seedCount, Terracotta)
                CountBadge("T", seedlingCount, Forest)
                CountBadge("H", harvestCount, Honey)
            }
        }
    }
}

@Composable
private fun CountBadge(letter: String, count: Int, color: Color) {
    val active = count > 0
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = if (active) 0.18f else 0.08f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = letter,
            color = color,
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
        Text(
            text = count.toString(),
            color = if (active) color else color.copy(alpha = 0.55f),
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}
