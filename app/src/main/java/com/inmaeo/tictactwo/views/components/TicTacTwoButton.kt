package com.inmaeo.tictactwo.views.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.domain.GamePiece
import com.inmaeo.tictactwo.domain.getTextByPiece

class TicTacTwoButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private val defaultBackground = ContextCompat.getDrawable(context, R.drawable.grid_cell_background)
    private val activeGridBackground = ContextCompat.getDrawable(context, R.drawable.active_grid_cell_background)
    private val selectedBackground = ContextCompat.getDrawable(context, R.drawable.selected_cell_background)
    private val player1Background = ContextCompat.getDrawable(context, R.drawable.player1_marker)
    private val player2Background = ContextCompat.getDrawable(context, R.drawable.player2_marker)
    private val inactivePlayer1Background = ContextCompat.getDrawable(context, R.drawable.inactive_player1_marker)
    private val inactivePlayer2Background = ContextCompat.getDrawable(context, R.drawable.inactive_player2_marker)

    var isGridButton = false
        set(value) {
            if (field != value) {
                field = value
                updateAppearance()
            }
        }

    var isButtonSelected = false
        set(value) {
            if (field != value) {
                field = value
                updateAppearance()
            }
        }

    var piece: GamePiece = GamePiece.Empty
        set(value) {
            if (field != value) {
                field = value
                updateAppearance()
            }
        }

    init {
        background = defaultBackground
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        
        val padding = resources.getDimensionPixelSize(R.dimen.default_item_padding)
        setPadding(padding, padding, padding, padding)
        
        gravity = android.view.Gravity.CENTER
        elevation = resources.getDimension(R.dimen.elevation_default)
        textAlignment = TEXT_ALIGNMENT_CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        post {
            val availableWidth = width - paddingLeft - paddingRight
            val availableHeight = height - paddingTop - paddingBottom
            val minDimension = minOf(availableWidth, availableHeight)
            val isLandscape = availableWidth > availableHeight
            
            gravity = android.view.Gravity.CENTER
            includeFontPadding = false
            textAlignment = TEXT_ALIGNMENT_CENTER

            val adjustmentFactor = if (isLandscape) 0.75f else 0.78f

            textSize = minDimension * adjustmentFactor / resources.displayMetrics.density
            
            maxLines = 1
            isSingleLine = true
            invalidate()
        }
    }

    private fun updateAppearance() {
        text = piece.getTextByPiece()
        
        if (width > 0 && height > 0) {
            onSizeChanged(width, height, width, height)
        }

        when (piece) {
            GamePiece.Player1 -> {
                background = if (isGridButton) player1Background else inactivePlayer1Background
                setTextColor(ContextCompat.getColor(context, 
                    if (isGridButton) R.color.colorPrimary else R.color.colorPrimaryVariant))
                alpha = if (isGridButton) 1.0f else 0.7f
                typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            }
            GamePiece.Player2 -> {
                background = if (isGridButton) player2Background else inactivePlayer2Background
                setTextColor(ContextCompat.getColor(context, 
                    if (isGridButton) R.color.colorSecondary else R.color.colorSecondaryVariant))
                alpha = if (isGridButton) 1.0f else 0.7f
                typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            }
            else -> {
                background = when {
                    isButtonSelected -> selectedBackground
                    isGridButton -> activeGridBackground
                    else -> defaultBackground
                }
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
                alpha = 1.0f
            }
        }

        animateAppearance()
    }

    private fun animateAppearance() {
        ValueAnimator.ofFloat(0.8f, 1.0f).apply {
            duration = 150
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                scaleX = value
                scaleY = value
            }
            start()
        }
    }
}
