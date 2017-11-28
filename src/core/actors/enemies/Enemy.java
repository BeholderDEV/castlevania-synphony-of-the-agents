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

/**
 *
 * @author Augustop
 */
public abstract class Enemy extends GameActor{
    protected TextureRegion standImg;
    protected Animation<TextureRegion> movingAnimation;
    protected Animation<TextureRegion> atkAnimation;
//    protected Agent enemyBehavior  ----- integration with JADE

    public Enemy(int walkingSpeed, Rectangle body) {
        super(walkingSpeed, body);
        super.facingRight = false;
    }
    
    @Override
    public boolean isDead() {
        return super.currentState == GameActor.State.DYING;
    }
    
    
}
