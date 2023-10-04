package com.changrojasalex1168165.memoramapokemon

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val pokemonImages = listOf(
    "articuno", "charizard", "electabuzz",
    "entei", "gengar", "chimi","lucario",
    "gyarados", "hooh", "lapras",
    "suicune", "typloshion", "zapdos"
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GameScreen() {
    val context = LocalContext.current

    //Game Variables
    var resetGame by remember { mutableStateOf(true) }
    val rows = 4
    val cols = 3
    var selectedImages: List<String> by remember { mutableStateOf(emptyList()) }
    var cards: List<Card> by remember { mutableStateOf(emptyList()) }
    var cardImages: List<MutableState<String>> by remember { mutableStateOf(emptyList()) }
    val clickedCards: MutableList<Card> by remember { mutableStateOf(mutableListOf()) }
    var attempts by remember { mutableStateOf(0) }
    var matches by remember { mutableStateOf(0) }



    // Timer variables
    var startTime by remember { mutableStateOf(0L) }
    var timerRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf("00:00:00") }


    //Reset
    if (resetGame) {
        selectedImages = pokemonImages.shuffled().take(rows * cols / 2)
        cards = generateCards(selectedImages, rows * cols)
        cardImages = List(rows * cols) {
            mutableStateOf("pokeball_classic")
        }
        matches = 0
        attempts = 0
        startTime = System.currentTimeMillis()
        timerRunning = true
        resetGame = false

    }



    //BottomSheet Variables
    var openBottomSheet by remember { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )




    //Timer display
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning) {
                val currentTime = System.currentTimeMillis()
                val timeInSeconds = (currentTime - startTime) / 1000
                val hours = timeInSeconds / 3600
                val minutes = (timeInSeconds % 3600) / 60
                val seconds = timeInSeconds % 60
                elapsedTime = "%02d:%02d:%02d".format(hours, minutes, seconds)
                delay(1000) // Update the timer every second
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.pokemonwallpaper2),
            contentDescription = "wallpaper for game",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), // Adjust the corner radius as needed
                color = Color.Green, // Change the background color as needed
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), // Adjust padding as needed
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Matches: $matches", fontSize = 16.sp, color = Color.White) // Adjust text color as needed
                    Text(text = "Time: $elapsedTime", fontSize = 16.sp, color = Color.White) // Adjust text color as needed
                    Text(text = "Tries: $attempts", fontSize = 16.sp, color = Color.White) // Adjust text color as needed
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            for (i in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until cols) {
                        val cardIndex = i * cols + j
                        val card = cards[cardIndex]

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {

                                    if (cards[cardIndex].isFaceUp || clickedCards.size >= 2) return@clickable
                                    cards[cardIndex].isFaceUp = true
                                    clickedCards.add(cards[cardIndex])

                                    if (clickedCards.size == 2) {
                                        val areEqual =
                                            clickedCards[0].imageResName == clickedCards[1].imageResName
                                        if (areEqual) {

//                                        showToast(context, "¡Son iguales!")
                                            matches++
                                            attempts++
                                            clickedCards[0].isMatched = true
                                            clickedCards[1].isMatched = true
                                            clickedCards.clear()
                                        } else {
                                            attempts++
                                            // Use a Handler to delay the clearing of cards
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                clickedCards.forEach { it.isFaceUp = false }
//                                            showToast(context, "No son iguales")
                                                clickedCards.clear()
                                                // Reset images creo
                                                cardImages.forEachIndexed { index, imageState ->
                                                    if (!cards[index].isMatched) {
                                                        imageState.value = "pokeball_classic"
                                                    }
                                                }
                                            }, 500)
                                        }


                                    }


                                    cardImages[cardIndex].value = card.imageResName


                                    if (matches == rows * cols / 2) {
//                                    showToast(context, "¡Ganaste!")
                                        timerRunning = false
                                        scope.launch {
                                            openBottomSheet = true
                                        }
                                    }
                                }
                        ) {
                            val imageResName = cardImages[cardIndex].value
                            val imageResId =
                                context.resources.getIdentifier(imageResName, "drawable", context.packageName)

                            AnimatedContent(targetState = matches,
                                transitionSpec = {fadeIn() + slideInVertically(animationSpec = tween(400),
                                    initialOffsetY = { fullHeight -> fullHeight }) with
                                        fadeOut(animationSpec = tween(1000))}
                            ) { targetMatches ->
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        //Bottom Sheet Container
        if(openBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "You Won!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Time Reached: $elapsedTime")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Reset the game
                        resetGame = true
                        openBottomSheet = false



                        Log.d("Cards after", cards.toString())
                    }) {
                        Text(text = "Reset")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(context, MenuActivity::class.java)
                        context.startActivity(intent)
                        openBottomSheet = false
                    }) {
                        Text(text = "Go Back")
                    }
                }
            }
        }


    }


}



data class Card(var imageResName: String, var isFaceUp: Boolean = false, var isMatched: Boolean = false)


fun generateCards(selectedImages: List<String>, count: Int): List<Card> {
    val pairs = selectedImages + selectedImages
    val shuffledPairs = pairs.shuffled()
    Log.d("Generated Cards", pairs.toString())
    return List(count) { Card(shuffledPairs[it], isFaceUp = false, isMatched = false) }
}


//
//fun showToast(context: android.content.Context, message: String) {
//    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//}
//
