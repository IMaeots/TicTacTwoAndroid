package com.inmaeo.tictactwo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.inmaeo.tictactwo.domain.GamePiece

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    var piece: GamePiece = GamePiece.Empty
        set(value) {
            field = value
            updateAppearance()
        }

    var borderColor: Int = Color.BLACK
        set(value) {
            field = value
            updateAppearance()
        }

    private fun updateAppearance() {
        text = when (piece) {
            GamePiece.Player1 -> "X"
            GamePiece.Player2 -> "O"
            GamePiece.Empty -> ""
        }

        setTextColor(Color.WHITE)

        val drawable = GradientDrawable()
        drawable.setStroke(5, borderColor)
        drawable.setColor(Color.TRANSPARENT)
        background = drawable
    }
}
