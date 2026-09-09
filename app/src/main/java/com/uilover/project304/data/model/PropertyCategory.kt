package com.uilover.project304.data.model

import androidx.annotation.StringRes
import com.uilover.project304.R

enum class PropertyCategory(@StringRes val labelRes: Int) {
    ALL(R.string.category_all),
    HOUSE(R.string.category_house),
    VILLA(R.string.category_villa),
    APARTMENT(R.string.category_apartment),
    PENTHOUSE(R.string.category_penthouse),
    TOWNHOUSE(R.string.category_townhouse)
}
