/*
 * This file is part of Pollinator.
 *
 * Pollinator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pollinator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pollinator. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gauravaggarwal.pollinator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gauravaggarwal.pollinator.R
import com.gauravaggarwal.pollinator.data.PollinationsAiRepository
import com.gauravaggarwal.pollinator.model.Model
import com.gauravaggarwal.pollinator.model.Models
import com.gauravaggarwal.pollinator.model.Sizes

@Composable
fun AdvancedOptions(
    pollinatorViewModel: PollinatorViewModel,
    modifier: Modifier = Modifier
) {
    val imageGeneratorUiState by pollinatorViewModel.uiState.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Model Selection with Dropdown
        ModelDropdown(
            selectedModel = imageGeneratorUiState.model,
            onModelSelected = { pollinatorViewModel.onModelChanged(it) },
            label = stringResource(R.string.label_model)
        )

        // Size Selection
        Row {
            SizeDropdown(
                modifier.weight(1f).padding(horizontal = 4.dp),
                selectedSize = imageGeneratorUiState.width,
                onSizeSelected = { pollinatorViewModel.onWidthChanged(it) },
                label = stringResource(R.string.label_width)
            )
            SizeDropdown(
                modifier.weight(1f),
                selectedSize = imageGeneratorUiState.height,
                onSizeSelected = { pollinatorViewModel.onHeightChanged(it) },
                label = stringResource(R.string.label_height)
            )
        }

        // Wallpaper Presets
        WallpaperPresetDropdown(
            onPresetSelected = { preset ->
                pollinatorViewModel.onWidthChanged(preset.width)
                pollinatorViewModel.onHeightChanged(preset.height)
            }
        )

        SeedTextField(
            value = imageGeneratorUiState.seed?.toString() ?: "",
            label = stringResource(R.string.label_seed),
            onValueChange = { newValue ->
                    pollinatorViewModel.onSeedChanged(newValue.toIntOrNull())
            }
        )

        // Removed No Logo and Private checkboxes as requested
    }
}

// TODO : Move out and reuse
@Composable
fun ModelDropdown(
    modifier: Modifier = Modifier,
    selectedModel: Model,
    onModelSelected: (Model) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val models = Models.getModelList()

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.Black)
                .border(
                    1.dp,
                    Color.LightGray,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(16.dp)
        ) {
            Text(
                text = selectedModel.displayName,
                modifier = Modifier.align(Alignment.Center)
            )
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                onDismissRequest = { expanded = false }
            ) {
                Models.getModelList().forEach { model ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(
                                text = model.displayName,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            ) },
                        onClick = {
                            onModelSelected(model)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// TODO : Move out and reuse
@Composable
fun SizeDropdown(
    modifier: Modifier = Modifier,
    selectedSize: Int,
    onSizeSelected: (Int) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val sizes = Sizes.getLabeledSizeList()
    val currentLabel = sizes.firstOrNull { it.value == selectedSize }?.label ?: selectedSize.toString()

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.Black)
                .border(
                    1.dp,
                    Color.LightGray,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(vertical = 16.dp)
        ) {
            Text(
                text =  currentLabel,
                modifier = Modifier.align(Alignment.Center)
            )
            androidx.compose.material3.DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sizes.forEach { sizeOption ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(
                                text = sizeOption.label,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            ) },
                        onClick = {
                            onSizeSelected(sizeOption.value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WallpaperPresetDropdown(
    modifier: Modifier = Modifier,
    onPresetSelected: (Sizes.Companion.WallpaperPreset) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val presets = Sizes.getWallpaperPresets()

    Column(modifier = modifier) {
        Text(
            text = "Wallpaper Preset",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.Black)
                .border(
                    1.dp,
                    Color.LightGray,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Select",
                modifier = Modifier.align(Alignment.Center)
            )
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                onDismissRequest = { expanded = false }
            ) {
                presets.forEach { preset ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(
                                text = preset.label,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            ) },
                        onClick = {
                            onPresetSelected(preset)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Seed Selection
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Preview
@Composable
fun AdvancedOptionsPreview() {
    val pollinatorViewModel = PollinatorViewModel(PollinationsAiRepository())
    AdvancedOptions(pollinatorViewModel = pollinatorViewModel)
}