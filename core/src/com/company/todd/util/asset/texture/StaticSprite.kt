package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.TextureRegion

class StaticSprite private constructor(private val regionInfo: RegionInfo, region: TextureRegion): MySprite() {
    constructor(manager: TextureManager, regionInfo: RegionInfo):
            this(regionInfo, manager.loadTextureRegion(regionInfo))

    init {
        updateRegion(region)
    }

    override fun dispose(manager: TextureManager) {
        manager.unloadTextureRegion(regionInfo)
    }
}
