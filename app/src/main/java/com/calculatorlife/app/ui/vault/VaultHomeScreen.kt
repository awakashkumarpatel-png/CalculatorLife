package com.calculatorlife.app.ui.vault

import android.graphics.Bitmap
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.calculatorlife.app.R
import com.calculatorlife.app.data.VaultAlbumEntity
import com.calculatorlife.app.data.VaultMediaEntity
import com.calculatorlife.app.data.VaultMediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultHomeScreen(onOpenMenu: () -> Unit, viewModel: VaultViewModel) {
    // Real auto-lock: leaving this screen (back button, drawer navigation,
    // process death) always re-locks — there is no "stay unlocked" path.
    DisposableEffect(Unit) {
        onDispose { viewModel.lock() }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var openAlbum by remember { mutableStateOf<VaultAlbumEntity?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val allMedia by viewModel.vaultDao.observeAllMedia().collectAsState(initial = emptyList())
    val albums by viewModel.vaultDao.observeAlbums().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (openAlbum != null) openAlbum!!.name else stringResource(R.string.vault_title)) },
                navigationIcon = {
                    IconButton(onClick = { if (openAlbum != null) openAlbum = null else onOpenMenu() }) {
                        Icon(
                            if (openAlbum != null) Icons.Filled.Close else Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.action_menu)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (openAlbum == null) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(R.string.vault_tab_photos)) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(R.string.vault_tab_videos)) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResource(R.string.vault_tab_albums)) })
                }
                when (selectedTab) {
                    0 -> MediaGridTab(
                        media = allMedia.filter { it.type == VaultMediaType.PHOTO },
                        albumId = null,
                        emptyMessage = stringResource(R.string.vault_empty_photos),
                        mediaType = VaultMediaType.PHOTO,
                        viewModel = viewModel
                    )
                    1 -> MediaGridTab(
                        media = allMedia.filter { it.type == VaultMediaType.VIDEO },
                        albumId = null,
                        emptyMessage = stringResource(R.string.vault_empty_videos),
                        mediaType = VaultMediaType.VIDEO,
                        viewModel = viewModel
                    )
                    2 -> AlbumsTab(albums = albums, viewModel = viewModel, onOpenAlbum = { openAlbum = it })
                }
            } else {
                MediaGridTab(
                    media = allMedia.filter { it.albumId == openAlbum!!.id },
                    albumId = openAlbum!!.id,
                    emptyMessage = stringResource(R.string.vault_empty_photos),
                    mediaType = null,
                    viewModel = viewModel
                )
            }
        }
    }

    if (showSettings) {
        VaultSettingsDialog(viewModel = viewModel, onDismiss = { showSettings = false })
    }
}

@Composable
private fun MediaGridTab(
    media: List<VaultMediaEntity>,
    albumId: Long?,
    emptyMessage: String,
    mediaType: VaultMediaType?,
    viewModel: VaultViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScopeSafe()
    var playingFile by remember { mutableStateOf<File?>(null) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    val pickMediaType = when (mediaType) {
        VaultMediaType.PHOTO -> ActivityResultContracts.PickVisualMedia.ImageOnly
        VaultMediaType.VIDEO -> ActivityResultContracts.PickVisualMedia.VideoOnly
        null -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris: List<Uri> ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            for (uri in uris) {
                val resolvedMimeIsVideo = context.contentResolver.getType(uri)?.startsWith("video/") == true
                val type = if (resolvedMimeIsVideo) VaultMediaType.VIDEO else VaultMediaType.PHOTO
                val ext = guessExtension(context, uri, if (type == VaultMediaType.VIDEO) "mp4" else "jpg")
                val storedName = withContext(Dispatchers.IO) { viewModel.fileManager.importMedia(uri, ext) }
                if (storedName != null) {
                    viewModel.vaultDao.insertMedia(
                        VaultMediaEntity(
                            albumId = albumId,
                            type = type,
                            encryptedFileName = storedName,
                            originalFileName = null,
                            addedAtMillis = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (media.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(emptyMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(media, key = { it.id }) { entity ->
                    MediaGridCell(
                        entity = entity,
                        viewModel = viewModel,
                        onPlayVideo = { file -> playingFile = file },
                        onDelete = { pendingDeleteId = entity.id }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { launcher.launch(androidx.activity.result.PickVisualMediaRequest(pickMediaType)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.vault_import))
        }
    }

    playingFile?.let { file ->
        VideoPlaybackDialog(file = file, onDismiss = {
            file.delete()
            playingFile = null
        })
    }

    pendingDeleteId?.let { id ->
        val entity = media.firstOrNull { it.id == id }
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.vault_delete_confirm_title)) },
            text = { Text(stringResource(R.string.vault_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        entity?.let {
                            viewModel.fileManager.deleteMedia(it.encryptedFileName)
                            viewModel.vaultDao.deleteMedia(it.id)
                        }
                    }
                    pendingDeleteId = null
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun MediaGridCell(
    entity: VaultMediaEntity,
    viewModel: VaultViewModel,
    onPlayVideo: (File) -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScopeSafe()
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
    ) {
        if (entity.type == VaultMediaType.PHOTO) {
            var bitmap by remember(entity.encryptedFileName) { mutableStateOf<Bitmap?>(null) }
            LaunchedEffect(entity.encryptedFileName) {
                bitmap = withContext(Dispatchers.IO) { viewModel.fileManager.decryptPhoto(entity.encryptedFileName) }
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        scope.launch {
                            val file = withContext(Dispatchers.IO) { viewModel.fileManager.decryptVideoToCache(entity.encryptedFileName) }
                            file?.let(onPlayVideo)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.VideoFile, contentDescription = null, modifier = Modifier.size(36.dp))
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).align(Alignment.BottomEnd).padding(4.dp)
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.vault_delete_media),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AlbumsTab(albums: List<VaultAlbumEntity>, viewModel: VaultViewModel, onOpenAlbum: (VaultAlbumEntity) -> Unit) {
    val scope = rememberCoroutineScopeSafe()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (albums.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.vault_empty_albums), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                listItems(albums, key = { it.id }) { album ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onOpenAlbum(album) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(album.name, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.vault_new_album))
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.vault_new_album)) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.vault_album_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        scope.launch {
                            viewModel.vaultDao.insertAlbum(VaultAlbumEntity(name = name.trim(), createdAtMillis = System.currentTimeMillis()))
                        }
                    }
                    showCreateDialog = false
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun VideoPlaybackDialog(file: File, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black)) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoPath(file.absolutePath)
                        setMediaController(MediaController(ctx).also { it.setAnchorView(this) })
                        setOnPreparedListener { start() }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VaultSettingsDialog(viewModel: VaultViewModel, onDismiss: () -> Unit) {
    val activity = LocalContext.current as? FragmentActivity
    var biometricEnabled by remember { mutableStateOf(viewModel.isBiometricEnabled()) }
    val biometricCanBeOffered = activity != null && isBiometricAvailable(activity)

    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinChangeMessage by remember { mutableStateOf<String?>(null) }
    val wrongPinMessage = stringResource(R.string.vault_wrong_pin)
    val pinTooShortMessage = stringResource(R.string.vault_pin_too_short)
    val pinChangedMessage = stringResource(R.string.vault_pin_changed)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.vault_change_pin)) },
        text = {
            Column {
                if (biometricCanBeOffered) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.vault_biometric_setting))
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                viewModel.setBiometricEnabled(it)
                            }
                        )
                    }
                }
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) currentPin = it },
                    label = { Text(stringResource(R.string.vault_current_pin)) },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) newPin = it },
                    label = { Text(stringResource(R.string.vault_new_pin)) },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                pinChangeMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    newPin.length < 4 -> pinChangeMessage = pinTooShortMessage
                    !viewModel.changePin(currentPin, newPin) -> pinChangeMessage = wrongPinMessage
                    else -> {
                        pinChangeMessage = pinChangedMessage
                        currentPin = ""
                        newPin = ""
                    }
                }
            }) { Text(stringResource(R.string.vault_change_pin)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
private fun rememberCoroutineScopeSafe() = androidx.compose.runtime.rememberCoroutineScope()
