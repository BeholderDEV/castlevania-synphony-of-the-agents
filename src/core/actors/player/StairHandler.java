/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.map.MapHandler;
import java.awt.Dimension;

/**
 *
 * @author Augustop
 */
public class StairHandler {
    public static final Dimension STAIR_TO_GROUND_DISTANCE = new Dimension(2, 1);
    private Rectangle objectBody;
    private boolean upstairs = false;
    
    public StairHandler(Rectangle objectBody) {
        this.objectBody = objectBody;
    }
    
    public void updateUpstairs(boolean facesRight, boolean rightAction, Vector2 velocity){
        if(rightAction){
            if((this.upstairs && !facesRight) || (!this.upstairs && facesRight)){
                this.upstairs = false;
                velocity.y *= -1;
            }else if(!this.upstairs && !facesRight){
                this.upstairs = true;
            }
            return;
        }
        if((this.upstairs && facesRight) || (!this.upstairs && !facesRight)){
            this.upstairs = false;
            velocity.y *= -1;
        }else if(!this.upstairs && facesRight){
            this.upstairs = true;
        }
    }
    
    public boolean checkValidStairStep(MapHandler map, boolean facesRight){
        int footTileX = 0;
        int footTileY = 0;
        if((facesRight && this.upstairs) || (!facesRight && !this.upstairs)){
            footTileX = Math.round((this.objectBody.x + this.objectBody.width) - this.objectBody.width * (PlayerBehavior.FOOT_SIZE.width / 100f));
            footTileY = Math.round(this.objectBody.y + PlayerBehavior.FOOT_SIZE.height / 100f);
        }
        if((!facesRight && this.upstairs) || (facesRight && !this.upstairs)){
            footTileX = Math.round(this.objectBody.x);
            footTileY = Math.round(this.objectBody.y + PlayerBehavior.FOOT_SIZE.height / 100f);
        }
        if(!map.checkValidLayerMove(MapHandler.Layer.STAIR, footTileX, footTileY) && !map.checkValidLayerMove(MapHandler.Layer.STAIR, footTileX, footTileY - 1)){
            return false;
        }
        if(this.checkIfReachedGroundFromStairs(map, footTileX, footTileY, facesRight)){
            return false;
        }
        return true;
    }
    
    public void fixPositionForStairClimbing(MapHandler map, Rectangle stairBoundary, boolean facesRight){
        if(facesRight && this.upstairs){
            this.objectBody.setPosition(Math.round(stairBoundary.x) - 2, Math.round(stairBoundary.y));
        }
        if(!facesRight && this.upstairs){
            this.objectBody.setPosition(Math.round(stairBoundary.x) , Math.round(stairBoundary.y));
        }
        if(facesRight && !this.upstairs){
            this.objectBody.setPosition(Math.round(stairBoundary.x) + 0.3f, Math.round(stairBoundary.y));
        }
        if(!facesRight && !this.upstairs){
            this.objectBody.setPosition(Math.round(stairBoundary.x) - 2.5f, Math.round(stairBoundary.y));
        }
    }
    
    private boolean checkIfReachedGroundFromStairs(MapHandler map, int footTileX, int footTileY, boolean facesRight){
        Vector2 ground = map.getCloseTileFromLayer(MapHandler.Layer.GROUND, footTileX, footTileY, this.upstairs, facesRight, STAIR_TO_GROUND_DISTANCE);
        if(ground != null){
            this.objectBody.y = (map.checkValidLayerMove(MapHandler.Layer.GROUND, Math.round(ground.x), Math.round(ground.y + 1))) 
                                ? ground.y + 1 + PlayerBehavior.DISTANCE_FROM_GROUND_LAYER
                                : ground.y + PlayerBehavior.DISTANCE_FROM_GROUND_LAYER;
            if(this.upstairs && facesRight){
                this.objectBody.x = ground.x - 2f;
            }
            if(this.upstairs && !facesRight){
                this.objectBody.x = ground.x - 1.2f;
            }
            return true;
        }
        return false;
    }

    public Rectangle checkStairsCollision(MapHandler map, boolean facesRight){
        float x = (facesRight) ? (this.objectBody.x + this.objectBody.width) - this.objectBody.width * (PlayerBehavior.FOOT_SIZE.width / 100f): 
                                       this.objectBody.x + this.objectBody.width * (PlayerBehavior.FOOT_SIZE.width / 100f);            
        Rectangle stairBoundary = map.checkCollisionWithStairBoundary(x, this.objectBody.y, this.objectBody.width * (PlayerBehavior.FOOT_SIZE.width / 8f / 100f), this.objectBody.height * (PlayerBehavior.FOOT_SIZE.height / 100f));
        if(stairBoundary == null){
            return null;
        }
//        System.out.println("Bound " + (stairBoundary.y + stairBoundary.height));
        String stairDirection = map.checkStairsDirection(Math.round(stairBoundary.x), Math.round(stairBoundary.y), Math.round(stairBoundary.x + stairBoundary.width), Math.round(stairBoundary.y + stairBoundary.height));
        if(stairDirection.equals("Failed")){
            System.out.println(stairDirection);
            return null;
        }
//            System.out.println(stairDirection);
        if((stairDirection.equals("rightUp") && facesRight) || (stairDirection.equals("leftUp") && !facesRight)){
            if(!(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))){
                return null;
            }
            this.upstairs = true;
        }else if((stairDirection.equals("rightDown") && facesRight) || (stairDirection.equals("leftDown") && !facesRight)){
            this.upstairs = false;
        }else{
            return null;
        }
        
        return stairBoundary;
    }

    
    public boolean isUpstairs() {
        return upstairs;
    }

    public void setUpstairs(boolean upstairs) {
        this.upstairs = upstairs;
    }
    
}
