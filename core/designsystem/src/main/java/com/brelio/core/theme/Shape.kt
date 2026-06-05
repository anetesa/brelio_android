package com.brelio.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import com.brelio.core.designsystem.R

@Composable
fun brelioShapes() = Shapes(
    small = RoundedCornerShape(dimensionResource(R.dimen.radius_sm)),
    medium = RoundedCornerShape(dimensionResource(R.dimen.radius_md)),
    large = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
    extraLarge = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
)
