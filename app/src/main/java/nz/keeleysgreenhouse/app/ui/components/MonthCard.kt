package nz.keeleysgreenhouse.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.Honey
import nz.keeleysgreenhouse.app.ui.theme.Inter
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import nz.keeleysgreenhouse.app.ui.theme.Sprout

private val MonthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private fun seasonalEmoji(month: Int): String = when (month) {
    9, 10, 11 -> "\uD83C\uDF31"  // sprout — spring
    12, 1, 2 -> "\u2600\uFE0F"   // sun — summer
    3, 4, 5 -> "\uD83C\uDF42"    // leaf — autumn
    else -> "\u2744\uFE0F"        // snowflake — winter
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = seasonalEmoji(month),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = MonthNames[month - 1],
                color = if (isCurrent) Forest else OliveMoss,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            if (isCurrent) {
                Text(
                    text = "THIS MONTH",
                    color = Brass,
                    fontFamily = Inter,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CountBadge("S", seedCount, Forest)
                CountBadge("T", seedlingCount, Sprout)
                CountBadge("H", harvestCount, Honey)
            }
        }
    }
}

@Composable
private fun CountBadge(letter: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = if (count > 0) 0.18f else 0.08f))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = letter,
            color = color,
            fontFamily = Inter,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = count.toString(),
            color = if (count > 0) OnSurfaceInk else OnSurfaceInk.copy(alpha = 0.45f),
            fontFamily = Inter,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
