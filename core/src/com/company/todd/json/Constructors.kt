package com.company.todd.json

import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.CloudyPlatform
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidPolygonPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform

val passiveConstructors = mutableMapOf(
///////////////////////////////////////// Passive objects /////////////////////////////////////////
        "solidRectangle" to JsonType("Solid Rectangle Platform") { game, json ->
            SolidRectanglePlatform(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle]
            )
        },

        "solidPolygon" to JsonType("Solid Polygon Platform") { game, json ->
            SolidPolygonPlatform(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["localVertices", vectorArray], json["worldCenter", vector]
            )
        },

        "halfCollided" to JsonType("Half Collided Platform") { game, json ->
            HalfCollidedPlatform(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle]
            )
        },

        "cloudy" to JsonType("Cloudy Platform") { game, json ->
            CloudyPlatform(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle], json["sinceContactTillInactive", float],
                    json["sinceInactiveTillActive", float]
            )
        },

        "jumper" to JsonType("Jumper") { game, json ->
            Jumper(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle], json["pushPower", float]
            )
        },

        "portal" to JsonType("Portal") { game, json ->
            Portal(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["center", vector], json["radius", float],
                    json["teleportTo", vector], json["teleportDelay", float]
            )
        },

        "trampoline" to JsonType("Trampoline") { game, json ->
            Trampoline(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle]
            )
        },

        "travolator" to JsonType("Trav0lator") { game, json ->
            Travolator(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json["xywh", rectangle], json["pushPower", float]
            )
        }
//////////////////////////////////////////// Creatures ////////////////////////////////////////////
)
