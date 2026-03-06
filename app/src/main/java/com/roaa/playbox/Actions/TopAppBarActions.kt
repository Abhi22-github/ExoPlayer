package com.roaa.playbox.Actions

sealed class TopAppBarActions {

    /**
     * for More button clicked.
     */
    object MoreButtonClicked : TopAppBarActions()

    /**
     * for Back button clicked.
     */
    object BackButtonClicked : TopAppBarActions()
}