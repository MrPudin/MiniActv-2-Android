package com.example.miniact2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.miniact2.ui.theme.MiniAct2Theme
import java.time.Year
import java.util.Locale
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniAct2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MiniActScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MiniActScreen(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    val language = remember(config) { Locale.getDefault().language }
    val currentYear = remember { Year.now().value }
    val yearsSince2000 = remember(currentYear) { (currentYear - 2000).coerceAtLeast(0) }

    // Nombre del animal según idioma
    val animalName = when (language) {
        "ca" -> stringResource(R.string.animal_ca)
        "es" -> stringResource(R.string.animal_es)
        "ro" -> stringResource(R.string.animal_ro)
        "ru" -> stringResource(R.string.animal_ru)
        else -> stringResource(R.string.animal_en)
    }

    // Población en 2000
    val base2000 = when (language) {
        "ca" -> 20000.0 // jabali
        "es" -> 900.0   // lince iberico
        "ro" -> 5000.0  // oso pardo
        "ru" -> 450.0   // tigre siberiano
        else -> 12000.0 // zorro rojo
    }

    val annualRate = when (language) {
        "ca" -> 0.020   // +2.0%
        "es" -> 0.035   // +3.5%
        "ro" -> 0.010   // +1.0%
        "ru" -> -0.008  // -0.8%
        else -> 0.015   // +1.5%
    }

    var result by rememberSaveable(language) { mutableStateOf<Double?>(null) }

    val greeting = if (isLandscape) {
        stringResource(R.string.greeting_landscape)
    } else {
        stringResource(R.string.greeting_portrait)
    }

    fun calculateCurrentPopulation(): Double {
        return base2000 * (1.0 + annualRate).pow(yearsSince2000.toDouble())
    }

    if (isLandscape) {
        LandscapeLayout(
            modifier = modifier,
            language = language,
            greeting = greeting,
            animalName = animalName,
            base2000 = base2000,
            currentYear = currentYear,
            result = result,
            onCalculate = { result = calculateCurrentPopulation() }
        )
    } else {
        PortraitLayout(
            modifier = modifier,
            language = language,
            greeting = greeting,
            animalName = animalName,
            base2000 = base2000,
            currentYear = currentYear,
            result = result,
            onCalculate = { result = calculateCurrentPopulation() }
        )
    }
}

@Composable
private fun PortraitLayout(
    modifier: Modifier,
    language: String,
    greeting: String,
    animalName: String,
    base2000: Double,
    currentYear: Int,
    result: Double?,
    onCalculate: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.current_language, language))
        Text(text = greeting, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

        Text(text = stringResource(R.string.animal_label, animalName), textAlign = TextAlign.Center)
        Text(text = stringResource(R.string.year_2000_label, base2000), textAlign = TextAlign.Center)

        Button(onClick = onCalculate, shape = RoundedCornerShape(12.dp)) {
            Text(text = stringResource(R.string.calc_button))
        }

        result?.let {
            Text(
                text = stringResource(R.string.current_year_label, currentYear, it),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(8.dp))
        ImagesBlock()
    }
}

@Composable
private fun LandscapeLayout(
    modifier: Modifier,
    language: String,
    greeting: String,
    animalName: String,
    base2000: Double,
    currentYear: Int,
    result: Double?,
    onCalculate: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.current_language, language))
            Text(text = greeting, style = MaterialTheme.typography.headlineSmall)

            Text(text = stringResource(R.string.animal_label, animalName))
            Text(text = stringResource(R.string.year_2000_label, base2000))

            Button(onClick = onCalculate, shape = RoundedCornerShape(12.dp)) {
                Text(text = stringResource(R.string.calc_button))
            }

            result?.let {
                Text(text = stringResource(R.string.current_year_label, currentYear, it))
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            ImagesBlock()
        }
    }
}

@Composable
private fun ImagesBlock() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageCard(resId = R.drawable.animal, sizeDp = 180)
        ImageCard(resId = R.drawable.food, sizeDp = 160)
    }
}

@Composable
private fun ImageCard(resId: Int, sizeDp: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}