package be.aidenstorme.todoapplication.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import be.aidenstorme.todoapplication.R

@Composable
fun HomeView(modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.home_greeting), modifier = modifier)
}
