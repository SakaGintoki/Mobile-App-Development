package com.filkom.designimplementation.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.filkom.designimplementation.ui.theme.Poppins

data class OnboardPage(
    val image: Int,
    val title: AnnotatedString,
    val desc: String
)


@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardPage(
            R.drawable.onboard1,
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold) ) {
                    append("Peluk erat momen, kenang")
                }
                withStyle(style = SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) {
                    append(" selamanya.")
                }
            },
            "Kenali setiap momen berharga bersama buah hati melalui LittleSteps."
        ),
        OnboardPage(
            R.drawable.onboard2,
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                    append("Tawa anak, ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) {
                    append("lagu hati ibu.")
                }
            },
            "Dengarkan tawa yang menemani setiap langkah kecil mereka."
        ),
        OnboardPage(
            R.drawable.onboard3,
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                    append("Perjalananlah ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) {
                    append("yang membawa kita.")
                }
            },
            "Setiap langkah bersama menciptakan kenangan yang abadi."
        ),
        OnboardPage(
            R.drawable.onboard4,
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) {
                    append("Genggaman tangan, ")
                }
                withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                    append("janji abadi.")
                }
            },
            "Mulailah kisah baru bersama LittleSteps hari ini."
        )
    )


    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinish) {
                    Text("Lewati", color = Color(0xFFF987C5), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                OnboardPageContent(pages[page])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index

                        // animasi ukuran & warna
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = tween(300)
                        )
                        val color = if (isSelected)
                            Color(0xFFF987C5)
                        else
                            Color(0xFFD8D8E6)

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinish()
                            }
                        }
                    },
                    containerColor = Color(0xFFF987C5),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardPageContent(page: OnboardPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title.text,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(textAlign = TextAlign.Center,text = page.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.desc,
            fontSize = 15.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF6A6A6B),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        )
    }
}
