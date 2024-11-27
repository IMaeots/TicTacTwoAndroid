package com.inmaeo.tictactwo.views.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.getTextByPiece

class TicTacTwoButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private val gradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setStroke(10, context.getColor(R.color.colorSecondary))
        setColor(Color.TRANSPARENT)
    }

    var isGridButton = false
        set(value) {
            field = value
            updateAppearance()
        }
    var isButtonSelected = false
        set(value) {
            field = value
            updateAppearance()
        }
    var piece: GamePiece = GamePiece.Empty
        set(value) {
            field = value
            updateAppearance()
        }

    init {
        background = gradientDrawable
        setTextColor(context.getColor(R.color.colorPrimary))
        textSize = 24f
    }

    private fun updateAppearance() {
        text = piece.getTextByPiece()

        gradientDrawable.setStroke(10, context.getColor(if (isGridButton) R.color.colorPrimary else R.color.colorSecondary))
        gradientDrawable.setColor(if (isButtonSelected) Color.LTGRAY else Color.TRANSPARENT)
    }
}
