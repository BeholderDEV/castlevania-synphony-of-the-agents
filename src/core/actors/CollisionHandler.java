/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public class CollisionHandler {
    public static final Pool<Rectangle> rectanglePool = Pools.get(Rectangle.class);
    private final static Rectangle collisionBody1 = new Rectangle();
    private final static Rectangle collisionBody2 = new Rectangle();
    
    public static boolean checkCollisionBetweenTwoActorsBodies(GameActor actor1, GameActor actor2){
        Rectangle body1 = actor1.getBody(), body2 = actor2.getBody();
        collisionBody1.set(body1.x + actor1.getSpriteAdjustmentForCollision()[0],
                body1.y + actor1.getSpriteAdjustmentForCollision()[1], 
                body1.width - actor1.getSpriteAdjustmentForCollision()[2], 
                body1.height - actor1.getSpriteAdjustmentForCollision()[3]);
        collisionBody2.set(body2.x + actor2.getSpriteAdjustmentForCollision()[0], 
                body2.y + actor2.getSpriteAdjustmentForCollision()[1], 
                body2.width - actor2.getSpriteAdjustmentForCollision()[2], 
                body2.height - actor2.getSpriteAdjustmentForCollision()[3]);
        return collisionBody1.overlaps(collisionBody2);
    }
    
    public static boolean checkCollisionBetweenBodyAndObject(GameActor actor, Rectangle object){
        Rectangle body = actor.getBody();
        collisionBody1.set(body.x + actor.getSpriteAdjustmentForCollision()[0],
                body.y + actor.getSpriteAdjustmentForCollision()[1], 
                body.width - actor.getSpriteAdjustmentForCollision()[2], 
                body.height - actor.getSpriteAdjustmentForCollision()[3]);
        return collisionBody1.overlaps(object);
    }
    
    public static void checkGroundCollision(MapHandler map, GameActor actor){
        int startX = Math.round(actor.getBody().x), 
            startY = Math.round(actor.getBody().y), 
            endX = Math.round(actor.getBody().x + actor.getBody().width), 
            endY = Math.round(actor.getBody().y + actor.getBody().height * 0.01f);
        if(map.checkLayerCollision(MapHandler.Layer.GROUND, startX, startY, endX, endY)){
            if(actor.getCurrentState() == GameActor.State.JUMPING || (actor.getCurrentState() == GameActor.State.ATTACKING && actor.getAtkState() == GameActor.Atk_State.JUMP_ATK)){
                if(actor.getCurrentState() != GameActor.State.HURTED){
                    actor.setCurrentState(GameActor.State.STANDING);
                }
                float newTileY = map.getUpermostGroundTile((actor.facingRight) ? endX: startX, startY);
                actor.getBody().y = newTileY + GameActor.DISTANCE_FROM_GROUND_LAYER;
            }
        }else if(actor.getCurrentState() != GameActor.State.JUMPING && actor.getCurrentState() != GameActor.State.ON_STAIRS && actor.getCurrentState() != GameActor.State.ATTACKING && actor.getCurrentState() != GameActor.State.HURTED){
            actor.setCurrentState(GameActor.State.JUMPING);
        }
    }
    
    public static boolean checkWallCollision(MapHandler map, GameActor actor, float delta){
        int startX = Math.round(actor.getBody().x), 
            startY = Math.round(actor.getBody().y + actor.getBody().height * 0.05f), 
            endX = Math.round(actor.getBody().x + actor.getBody().width), 
            endY = Math.round(actor.getBody().y + actor.getBody().height * 0.95f);
        if(map.checkLayerCollision(MapHandler.Layer.GROUND, startX, startY, endX, endY)){
            if(actor.getCurrentState() == GameActor.State.WALKING){
                actor.velocity.x *= -1;
                actor.velocity.y *= -1;
                actor.updatePosition(delta);
                actor.setCurrentState(GameActor.State.STANDING);
            }
            if(actor.getCurrentState() == GameActor.State.JUMPING){
                actor.velocity.x *= -1;
                actor.velocity.y *= -1;
                actor.updatePosition(delta);
                actor.velocity.y *= -1;
                if(actor.getVelocity().y > 0){
                    actor.getVelocity().y = 0;
                }
            }
            return true;
        }
        return false;
    }
    
}
