package com.roaa.playbox.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.playbox.R
import com.roaa.playbox.composition.localViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    onMoreClick: () -> Unit,
    shouldShowBackButton: Boolean = false,
    modifier: Modifier = Modifier,
) {

    val viewModel = localViewModel.current

    val currentFolderList by viewModel.currentVideoFolder.collectAsStateWithLifecycle()


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Top row: app icon + title OR search field, plus actions
        AnimatedContent(targetState = shouldShowBackButton) { showBackButton ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!showBackButton) {
                    // App icon
                    Image(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "App icon",
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "PlayBox",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.round_back),
                        contentDescription = "App icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = currentFolderList.bucketName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .size(24.dp)
                    )
                }
                // More / overflow icon
                IconButton(onClick = onMoreClick) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        }
    }
}
