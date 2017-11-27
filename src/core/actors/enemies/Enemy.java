/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import core.AssetsManager;
import core.actors.GameActor;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public abstract class Enemy extends GameActor{
    protected TextureRegion standImg = new TextureRegion(AssetsManager.assets.get("assets/img/superIV_Enemies.png", Texture.class), 155, 143, 29, 50);
//    protected Animation<TextureRegion> walkAnimation = ;
//    protected Agent enemyBehavior  ----- integration with JADE

    public Enemy(int walkingSpeed, Rectangle body) {
        super(walkingSpeed, body);
    }
    
    public Rectangle getBody() {
        return body;
    }
}
