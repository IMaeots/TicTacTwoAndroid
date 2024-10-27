package com.inmaeo.tictactwo.views.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.getTextByPiece

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    var isGridButton = false
    var isButtonSelected = false
    var piece: GamePiece = GamePiece.Empty
        set(value) {
            field = value
            updateAppearance()
        }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        setTextColor(context.getColor(R.color.colorPrimary))
        textSize = 24f
    }

    private fun updateAppearance() {
        text = piece.getTextByPiece()

        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(10, context.getColor(if (isGridButton) R.color.colorPrimary else R.color.colorSecondary))
        }
    }
}
