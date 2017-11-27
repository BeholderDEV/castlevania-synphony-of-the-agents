/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public class SwordSkeleton extends Enemy{
    
    
    public SwordSkeleton(int walkingSpeed, Rectangle body) {
        super(walkingSpeed, body);
    }

    @Override
    public void updateActor(float deltaTime, MapHandler map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void renderActor(SpriteBatch batch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
