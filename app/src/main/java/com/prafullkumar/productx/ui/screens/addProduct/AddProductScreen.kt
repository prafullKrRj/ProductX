package com.prafullkumar.productx.ui.screens.addProduct

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.prafullkumar.productx.domain.model.Product
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

// Function to create an Intent for UCrop to crop an image with a 1:1 aspect ratio and a maximum size of 1080x1080 pixels.
private fun createUCropIntent(context: Context, sourceUri: Uri, destinationUri: Uri): Intent {
    return UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(1f, 1f) // Force 1:1 aspect ratio
        .withMaxResultSize(1080, 1080)
        .getIntent(context)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel, onDismiss: () -> Unit, onProductAdded: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val context = LocalContext.current
    val selectedImageUris by viewModel.selectedImageUris.collectAsState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onProductAdded()
        }
    }

    val cropImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val croppedUri = result.data?.let { UCrop.getOutput(it) }
                croppedUri?.let {
                    viewModel.addImageUri(it)
                }
            } else {
                Toast.makeText(context, "Failed to crop image", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher for picking an image from the device's storage.
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "croppedImage.jpg"))
            cropImageLauncher.launch(createUCropIntent(context, uri, destinationUri))
        } else {
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    // State for managing the bottom sheet scaffold.
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded, skipHiddenState = false
        )
    )
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AddProductBottomSheet(
            scrollState = scrollState,
            product = product,
            viewModel = viewModel,
            scope = scope,
            photoPickerLauncher = photoPickerLauncher,
            errorMessage = errorMessage,
            selectedImageUris = selectedImageUris,
            isLoading = isLoading,
            sheetState = sheetState,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    scrollState: ScrollState,
    product: Product,
    viewModel: AddProductViewModel,
    scope: CoroutineScope,
    photoPickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    errorMessage: String?,
    selectedImageUris: Uri?,
    isLoading: Boolean,
    sheetState: BottomSheetScaffoldState,
    onDismiss: () -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProductDetailsFields(product = product, viewModel = viewModel)
                UploadImageButton(scope = scope, photoPickerLauncher = photoPickerLauncher)
                ErrorMessageText(errorMessage = errorMessage)
                ProductImage(selectedImageUris = selectedImageUris)
                AddProductButton(
                    isLoading = isLoading, product = product, viewModel = viewModel
                )
            }
        },
        scaffoldState = sheetState,
        sheetSwipeEnabled = false,
        sheetShape = BottomSheetDefaults.ExpandedShape,
        sheetDragHandle = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(enabled = !isLoading, onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close, contentDescription = null
                    )
                }
                Text(
                    text = "Add New Product",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }) {}
}

@Composable
fun ProductDetailsFields(product: Product, viewModel: AddProductViewModel) {
    ProductDetailsTextField(
        value = product.productName,
        label = "Product Name",
        onValueChange = viewModel::updateProductName
    )

    ProductDetailsTextField(
        value = product.productType,
        label = "Product Type",
        onValueChange = viewModel::updateProductType
    )

    ProductDetailsTextField(
        value = viewModel.price,
        label = "Price",
        onValueChange = viewModel::updatePrice,
        prefix = "₹",
        keyboardType = KeyboardType.Decimal
    )

    ProductDetailsTextField(
        value = viewModel.tax,
        label = "Tax",
        onValueChange = viewModel::updateTax,
        prefix = "₹",
        keyboardType = KeyboardType.Decimal
    )
}

@Composable
fun ProductDetailsTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    suffix: String = "",
    prefix: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(label = { Text(label) },
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(30),
        colors = OutlinedTextFieldDefaults.addProductDetailsFieldColors(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        suffix = { Text(suffix) },
        prefix = { Text(prefix) })
}

@Composable
fun UploadImageButton(
    scope: CoroutineScope, photoPickerLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    Button(
        onClick = {
            scope.launch {
                photoPickerLauncher.launch("image/*")
            }
        }, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Upload Images",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Upload Images")
    }
}

@Composable
fun ErrorMessageText(errorMessage: String?) {
    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProductImage(selectedImageUris: Uri?) {
    if (selectedImageUris != null && selectedImageUris != Uri.EMPTY) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                contentScale = ContentScale.FillHeight,
                contentDescription = "Product Image",
                painter = rememberImagePainter(selectedImageUris),
            )
        }
    }
}

@Composable
fun AddProductButton(isLoading: Boolean, product: Product, viewModel: AddProductViewModel) {
    Button(
        onClick = { viewModel.addProduct() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading && product.productName.isNotBlank() && product.productType.isNotBlank() && product.price.toString()
            .isNotBlank() && product.tax.toString().isNotBlank()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Add Product")
        }
    }
}

@Composable
fun OutlinedTextFieldDefaults.addProductDetailsFieldColors(): TextFieldColors {
    return colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}