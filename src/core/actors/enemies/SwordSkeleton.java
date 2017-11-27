/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import core.AssetsManager;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public class SwordSkeleton extends Enemy{
    
    public SwordSkeleton(int walkingSpeed, Rectangle body) {
        super(walkingSpeed, body);
        super.standImg = standImg = new TextureRegion(AssetsManager.assets.get("assets/img/superIV_Enemies.png", Texture.class), 312, 143, 30, 49);
    }

    @Override
    public void updateActor(float deltaTime, MapHandler map) {
        
    }

    @Override
    public void renderActor(SpriteBatch batch) {
        batch.draw(super.standImg, (super.facingRight) ? super.body.x: super.body.x + super.body.width, 
                super.body.y, 
                (super.facingRight) ? super.body.width: -super.body.width, 
                super.body.height);
    }

    
}
