package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.TextureRegion

class StaticSprite(private val regionInfo: RegionInfo, region: TextureRegion): MySprite() {
    init {
        updateRegion(region)
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(regionInfo)
    }
}
