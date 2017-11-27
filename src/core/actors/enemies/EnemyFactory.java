/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import java.awt.Dimension;

/**
 *
 * @author Augustop
 */
public class EnemyFactory {
    private static final Pool<Rectangle> rectanglePool = Pools.get(Rectangle.class);
    
    public enum enemyType{
        SWORD_SKELETON
    }
    
    public static Enemy createEnemy(enemyType type, int walkingSpeed, Vector2 position, float width, float height){
        Rectangle enemyBody = rectanglePool.obtain();
        enemyBody.setPosition(position);
        enemyBody.setSize(width, height);
        switch(type){
            case SWORD_SKELETON:
                return new SwordSkeleton(walkingSpeed, enemyBody);
        }
        return null;
    }
    
    public static void disposeEnemy(Enemy enemy){
        rectanglePool.free(enemy.getBody());
    }
    
}
