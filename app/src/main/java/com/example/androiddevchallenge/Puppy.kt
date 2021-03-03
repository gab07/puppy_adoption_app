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

data class Puppy(
    val name: String,
    val image: String,
    val resImage: Int?,
    val tags: List<String> = listOf("Female", "Cute", "Intelligent", "Friendly")
) {
    companion object {
        fun seed(offline: Boolean): List<Puppy> {
            if (!offline) return emptyList()

            return listOf(
                Puppy("Rocco", "", R.drawable.img_puppy_1, listOf("Male", "Friendly", "Loyal")),
                Puppy(
                    "Lili",
                    "",
                    R.drawable.img_puppy_2,
                    listOf("Female", "Grumpy", "Intelligent")
                ),
                Puppy(
                    "Layla", "", R.drawable.img_puppy_7,
                    listOf("Female", "Friendly", "Loyal")
                ),
                Puppy(
                    "Rocky",
                    "",
                    R.drawable.img_puppy_3,
                    listOf("Male", "Friendly")
                ),
                Puppy(
                    "Pocho", "", R.drawable.img_puppy_4, listOf("Male", "Friendly", "Tiny")
                ),
                Puppy(
                    "Coco", "", R.drawable.img_puppy_5, listOf("Male", "Friendly")
                ),
                Puppy(
                    "Frida", "", R.drawable.img_puppy_6,
                    listOf("Female", "Friendly", "Tiny")
                ),

                Puppy(
                    "Silver", "", R.drawable.img_puppy_8,
                    listOf("Male", "Friendly")
                ),
            )
        }
    }
}
