package com.federico.payment_completed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.component.PrimaryButton
import com.nutrisportdemo.shared.component.card.InfoCard
import com.nutrisportdemo.shared.component.card.LoadingCard
import com.nutrisportdemo.shared.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentCompleted(
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<PaymentViewModel>()
    val screenState = viewModel.screenState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .systemBarsPadding()
            .padding(all = 24.dp)
    ) {
        screenState.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = {
                Column {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        InfoCard(
                            title = "Success!",
                            subtitle = "Your purchase is on the way.",
                            image = Resources.Image.Checkmark
                        )
                    }
                    PrimaryButton(
                        text = "Go back",
                        icon = Resources.Icon.RightArrow,
                        onClick = navigateBack
                    )
                }
            },
            onError = { message ->
                Column {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        InfoCard(
                            title = "Oops!",
                            subtitle = message,
                            image = Resources.Image.Cat
                        )
                    }
                    PrimaryButton(
                        text = "Go back",
                        icon = Resources.Icon.RightArrow,
                        onClick = navigateBack
                    )
                }
            }
        )
    }
}