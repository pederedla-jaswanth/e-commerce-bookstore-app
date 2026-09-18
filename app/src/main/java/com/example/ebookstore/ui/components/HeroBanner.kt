package com.example.ebookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ebookstore.data.mock.MockData
import com.example.ebookstore.domain.model.Banner
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import kotlinx.coroutines.delay

/**
 * Full-width auto-scrolling hero banner pager.
 *
 * Cycles through [banners] automatically every 4 seconds. Dot indicators
 * show the current position. Each banner is tappable via [onBannerClick].
 *
 * Background uses the banner's [Banner.imageUrl] loaded by Coil, with a
 * dark gradient overlay so white text remains legible on any image.
 *
 * Colors: gradient uses [MaterialTheme.colorScheme.secondary] (brand green)
 * with transparency so the brand identity shows through.
 *
 * @param banners       List of [Banner] items to cycle through.
 * @param onBannerClick Called with the banner's [Banner.actionRoute] when tapped.
 * @param modifier      Applied to the outer [Box].
 */
@Composable
fun HeroBanner(
    banners: List<Banner>,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (banners.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto-advance every 4 seconds
    LaunchedEffect(banners.size) {
        while (true) {
            delay(4_000)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }

    val banner = banners[currentIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onBannerClick(banner.actionRoute) },
    ) {
        // Background image
        AsyncImage(
            model              = banner.imageUrl,
            contentDescription = banner.title,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize(),
        )

        // Gradient overlay for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        )
                    )
                )
        )

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(EBookStoreSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
        ) {
            Text(
                text       = banner.title,
                style      = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                ),
                color      = MaterialTheme.colorScheme.onSecondary,
                textAlign  = TextAlign.Start,
            )
            Text(
                text   = banner.subtitle,
                style  = MaterialTheme.typography.bodyMedium,
                color  = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.9f),
            )
        }

        // Dot page indicators
        Row(
            modifier              = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = EBookStoreSpacing.Small),
            horizontalArrangement = Arrangement.spacedBy(EBookStoreSpacing.XSmall),
        ) {
            banners.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentIndex) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentIndex)
                                MaterialTheme.colorScheme.onSecondary
                            else
                                MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Preview(name = "Hero Banner — Light")
@Composable
private fun HeroBannerLightPreview() {
    EBookStoreTheme {
        HeroBanner(
            banners       = MockData.banners,
            onBannerClick = {},
        )
    }
}

@Preview(name = "Hero Banner — Dark")
@Composable
private fun HeroBannerDarkPreview() {
    EBookStoreTheme(themeMode = com.example.ebookstore.ui.theme.ThemeMode.DARK) {
        HeroBanner(
            banners       = MockData.banners,
            onBannerClick = {},
        )
    }
}
