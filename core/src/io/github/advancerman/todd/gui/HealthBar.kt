package io.github.advancerman.todd.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import io.github.advancerman.todd.asset.texture.*
import io.github.advancerman.todd.json.*
import io.github.advancerman.todd.objects.base.withColor
import kotlin.math.min

/**
 * Basic health bar for InGameObject.
 * @param maxHealth Maximal health for the owner of health bar.
 * @param backgroundDrawable Background drawable: `z-index` is a global health bar z-index (TODO);
 *                           `size` is used for health bar size;
 *                           `offset` is used for health bar bottom center positioning
 *                           relative to owner top center.
 * @param foregroundDrawable Foreground drawable: `z-index` is not used;
 *                           `size` is used for height and maximum width used for foreground,
 *                           zero components will be changed to health bar size;
 *                           `offset` is used for foreground positioning relative to health bar position.
 * @param animateDuration Duration in seconds of displayed value movement animation
 */
@SerializationType([HealthBar::class])
class HealthBar(
    @JsonUpdateSerializable var maxHealth: Float,
    @JsonFullSerializable private val backgroundDrawable: ToddDrawable,
    @JsonFullSerializable private val foregroundDrawable: ToddDrawable,
    @JsonFullSerializable private val animateDuration: Float,
) : Actor(), DisposableByManager {
    @JsonUpdateSerializable
    var value = maxHealth
    set(value) {
        animateFrom = visualValue
        sinceValueSet = 0f
        field = value
    }

    private var sinceValueSet = 0f
    private var animateFrom = value

    private val visualValue: Float
        get() {
            val percentAnimated = min(sinceValueSet, animateDuration) / animateDuration
            return animateFrom - (animateFrom - value) * percentAnimated
        }

    init {
        setSize(backgroundDrawable.size.x, backgroundDrawable.size.y)
        if (foregroundDrawable.size.x == 0f) {
            foregroundDrawable.size.x = backgroundDrawable.size.x
        }
        if (foregroundDrawable.size.y == 0f) {
            foregroundDrawable.size.y = backgroundDrawable.size.y
        }
    }

    fun setOwnerTopCenter(x: Float, y: Float) {
        setPosition(
            x + backgroundDrawable.offset.x,
            y + backgroundDrawable.offset.y,
            Align.center or Align.bottom
        )
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceValueSet += delta
        backgroundDrawable.update(delta)
        foregroundDrawable.update(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.withColor(parentAlpha, color) {
            backgroundDrawable.draw(it, x, y, width, height)
            val foregroundWidth = getPercentWidth(
                foregroundDrawable,
                visualValue / maxHealth
            )
            foregroundDrawable.draw(
                it,
                x + foregroundDrawable.offset.x,
                y + foregroundDrawable.offset.y,
                foregroundWidth,
                foregroundDrawable.size.y
            )
        }
    }

    private fun getPercentWidth(drawable: ToddDrawable, percent: Float): Float {
        val innerWidth = (drawable.size.x - drawable.leftWidth - drawable.rightWidth) * percent
        return innerWidth + drawable.leftWidth + drawable.rightWidth
    }

    override fun dispose(manager: TextureManager) {
        backgroundDrawable.dispose(manager)
        foregroundDrawable.dispose(manager)
    }
}
