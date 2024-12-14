package com.example.ml_kit_example.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.ml_kit_example.ui.camera.CameraScreen
import com.example.ml_kit_example.ui.no_permission.NoPermissionScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController, title: String?, content: String?) {
    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
        navController, title, content
    )
}

@Composable
fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    navController: NavController,
    title: String?,
    content: String?
) {
    if (hasPermission) {
        CameraScreen(navController, title, content)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}