/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.actors.CollisionHandler;
import core.screens.GameScreen;

/**
 *
 * @author Augustop
 */
public class EnemyFactory {
    
    
    public enum enemyType{
        SWORD_SKELETON,
        ARCHER_SKELETON,
        BAT
    }
    
    public static Enemy createEnemy(enemyType type, int walkingSpeed, Vector2 position, GameScreen gameScreen, float walkingRange){
        Rectangle enemyBody = CollisionHandler.rectanglePool.obtain();
        enemyBody.setPosition(position);
        switch(type){
            case SWORD_SKELETON:
                return new SwordSkeleton(walkingSpeed, enemyBody, gameScreen);
            case ARCHER_SKELETON:
                return new ArcherSkeleton(walkingSpeed, enemyBody, gameScreen, walkingRange);
            case BAT:
                return new Bat(walkingSpeed, enemyBody, gameScreen);
        }
        return null;
    }
    
    public static Enemy createEnemy(enemyType type, int walkingSpeed, Vector2 position, GameScreen gameScreen){
        return createEnemy(type, walkingSpeed, position, gameScreen, 5f);
    }
}
