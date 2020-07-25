package com.company.todd.util.asset.texture.sprite

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.util.asset.texture.RegionInfo
import com.company.todd.util.asset.texture.TextureManager

class StaticSprite(private val regionInfo: RegionInfo, region: TextureRegion): MySprite() {
    init {
        updateRegion(region)
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(regionInfo)
    }
}
