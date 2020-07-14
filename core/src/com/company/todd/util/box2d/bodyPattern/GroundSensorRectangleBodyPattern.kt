package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef

class GroundSensorRectangleBodyPattern(type: BodyDef.BodyType, size: Vector2,
                                       position: Vector2, localCenter: Vector2 = Vector2()) :
        GroundSensorPolygonBodyPattern(
                createSmoothRectangle(localCenter, size),
                type,
                position.cpy().add(size.x / 2, size.y / 2)
        )
