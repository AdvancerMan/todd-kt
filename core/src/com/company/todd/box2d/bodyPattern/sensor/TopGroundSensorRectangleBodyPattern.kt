package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.bodyPattern.base.*

class TopGroundSensorRectangleBodyPattern(type: BodyDef.BodyType, worldPosition: Vector2,
                                          size: Vector2, localCenter: Vector2 = Vector2()) :
        TopGroundSensorPolygonBodyPattern(
                type,
                worldPosition.cpy().add(size.x / 2, size.y / 2),
                com.company.todd.box2d.bodyPattern.base.createSmoothRectangle(localCenter, size)
        )
