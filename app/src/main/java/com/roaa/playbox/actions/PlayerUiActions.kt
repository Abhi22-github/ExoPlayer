package com.roaa.playbox.actions

sealed class PlayerUiActions {
    object LockRotation : PlayerUiActions()
    object PlayPauseClicked : PlayerUiActions()
    data class OnSeekPositionChange(val position: Long) : PlayerUiActions()
    object OnSeekPositionChangeFinished : PlayerUiActions()
    object ChangeContentScale : PlayerUiActions()
}