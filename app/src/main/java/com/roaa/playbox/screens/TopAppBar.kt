package com.roaa.playbox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.playbox.R
import com.roaa.playbox.actions.TopAppBarActions
import com.roaa.playbox.composition.localViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayBoxTopAppBar(
    modifier: Modifier = Modifier,
    actions: (TopAppBarActions) -> Unit,
    shouldShowBackButton: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior,
) {

    val viewModel = localViewModel.current
    val currentFolderList by viewModel.currentVideoFolder.collectAsStateWithLifecycle()

    TopAppBar(
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        title = {
            if (!shouldShowBackButton) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "App icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "PlayBox",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Text(
                    text = currentFolderList.bucketName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },

        navigationIcon = {
            if (shouldShowBackButton) {
                IconButton(onClick = { actions(TopAppBarActions.BackButtonClicked) }) {
                    Icon(
                        painter = painterResource(R.drawable.round_back),
                        contentDescription = "Back"
                    )
                }
            }
        },

        actions = {
            IconButton(onClick = { actions(TopAppBarActions.MoreButtonClicked) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }
    )
}
