package com.federico.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.federico.home.domain.BottomBarDestination
import com.nutrisportdemo.shared.IconPrimary
import com.nutrisportdemo.shared.IconSecondary
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.domain.Customer
import com.nutrisportdemo.shared.util.RequestState
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    customer: RequestState<Customer>,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit
) {
    Box(modifier = Modifier.padding(all = 12.dp)) {
        Row(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(SurfaceLighter).padding(vertical = 24.dp, horizontal = 36.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomBarDestination.entries.forEach { destination ->
                val animatedIconColor by animateColorAsState(
                    targetValue = if (selected == destination) IconSecondary else IconPrimary,
                )
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        modifier = Modifier.clickable { onSelect(destination) },
                        painter = painterResource(destination.icon),
                        contentDescription = "Bottom bar destination icon",
                        tint = animatedIconColor
                    )
                    if (destination == BottomBarDestination.Cart) {
                        AnimatedContent(
                            targetState = customer
                        ) { customerState ->
                            if (customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(IconSecondary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}