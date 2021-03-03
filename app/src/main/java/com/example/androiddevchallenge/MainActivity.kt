/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.purple500
import dev.chrisbanes.accompanist.glide.GlideImage
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val offline = true
    val loading = remember { mutableStateOf(!offline) }
    val puppies = remember { mutableStateOf(Puppy.seed(offline)) }
    val selected = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    if (puppies.value.isEmpty()) {
        scope.launch {
            val response = api.images()
            val puppiesForAdoption = mutableListOf<Puppy>()
            response.results.forEach { image ->
                val puppy = Puppy("Puppy", image.urls.small, null)
                puppiesForAdoption.add(puppy)
            }
            puppies.value = puppiesForAdoption
            loading.value = false
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
    ) {
        Surface(color = MaterialTheme.colors.background) {
            when {
                loading.value -> {
                    Loading()
                }
                selected.value -> {
                    PuppyDetailsPage(puppy = puppies.value[0], onBack = { selected.value = false })
                }
                else -> {
                    HomePage(
                        puppies.value,
                        onNext = {
                            val list = puppies.value.toMutableList()
                            val doggo = list.removeAt(0)
                            list.add(doggo)
                            puppies.value = list
                        },
                        onSelect = {
                            selected.value = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun HomePage(puppies: List<Puppy>, onNext: () -> Unit, onSelect: () -> Unit) {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    val firstPositionMod = Modifier
        .offset(x = offsetX.value.dp, y = offsetY.value.dp)
        .pointerInput(key1 = "drag") {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX.value = (offsetX.value + dragAmount.x)
                        .coerceIn(-150f, 150f)

                    offsetY.value = (offsetY.value + dragAmount.y)
                        .coerceIn(-150f, 150f)
                },
                onDragEnd = {
                    if (offsetX.value < -100f || offsetX.value > 100f) {
                        onNext()
                    }
                    offsetX.value = 0f
                    offsetY.value = 0f
                }
            )
        }

    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            title = {
                Text("The Shelter", fontWeight = FontWeight.Bold)
            }
        )
        Card(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            elevation = 4.dp,
            backgroundColor = purple500
        ) {
            Column(
                Modifier
                    .height(70.dp)
                    .padding(16.dp),
            ) {
                Text("Shelter of the month", fontSize = 10.sp, color = Color.White)
                Text(
                    "Puppy Fairy Panama",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            ListItem(puppies[2], 4.dp, Modifier.padding(top = 32.dp))
            ListItem(puppies[1], 10.dp, Modifier.padding(top = 16.dp))
            ListItem(
                puppies[0], 16.dp, firstPositionMod,
                onClick = {
                    offsetX.value = 0f
                    offsetY.value = -50f
                    onSelect()
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(32.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(130.dp, 50.dp)
            ) {
                Text("Cute", color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    offsetX.value = 0f
                    offsetY.value = -100f
                    onSelect()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = purple500),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(130.dp, 50.dp)
            ) {
                Text("So Cute!")
            }
        }
    }
}

@Composable
fun ListItem(puppy: Puppy, elevation: Dp, modifier: Modifier, onClick: (() -> Unit)? = null) {
    Card(
        elevation = elevation,
        modifier = modifier
            .clickable { onClick?.invoke() }
            .fillMaxWidth()
            .size(400.dp)
            .padding(start = 32.dp, end = 32.dp)
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (puppy.image.isNotBlank()) {
                    GlideImage(
                        data = puppy.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Image(
                        painter = painterResource(id = puppy.resImage!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
            }
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(puppy.name, fontSize = 20.sp)
                Text("I'm so cute!", fontSize = 12.sp, color = Color.Gray)
                Row {
                    puppy.tags.map { tag -> Chip(text = tag) }
                }
            }
        }
    }
}

@Composable
fun PuppyDetailsPage(puppy: Puppy, onBack: () -> Unit) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            if (puppy.image.isNotBlank()) {
                GlideImage(
                    data = puppy.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(id = puppy.resImage!!),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
            Box(
                Modifier.padding(start = 16.dp, top = 8.dp)
            ) {
                Box(
                    Modifier
                        .size(35.dp)
                        .background(color = Color.White, shape = CircleShape)
                        .wrapContentSize(Alignment.Center)
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_arrow_back_ios_24),
                            contentDescription = null,
                            tint = purple500
                        )
                    }
                }
            }
        }
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(puppy.name, fontSize = 20.sp)
            Text("I'm so cute!", fontSize = 12.sp, color = Color.Gray)
            Row {
                puppy.tags.map { tag -> Chip(text = tag) }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = purple500),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adopt Me")
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    OutlinedButton(
        onClick = {},
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .wrapContentWidth()
            .padding(top = 8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Text(text, color = purple500, modifier = Modifier.padding(0.dp))
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
