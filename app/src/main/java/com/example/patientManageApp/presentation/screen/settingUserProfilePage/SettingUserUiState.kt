package com.example.patientManageApp.presentation.screen.settingUserProfilePage

sealed interface SettingUserUiState {
    data object Idle: SettingUserUiState
    data object Loading: SettingUserUiState
    data object Success: SettingUserUiState
    data object Error: SettingUserUiState
}