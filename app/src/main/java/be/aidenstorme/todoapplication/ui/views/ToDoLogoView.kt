package be.aidenstorme.todoapplication.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.aidenstorme.todoapplication.R

@Composable
fun ToDoLogoView(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.size(72.dp)
    ) {
        Column {
            Row(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF4CAF50)), // Green
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.logo_t),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFE91E63)), // Pink
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.logo_o),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
            Row(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF03A9F4)), // Cyan/Blue
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.logo_d),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFFF9800)), // Orange
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.logo_o),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}
