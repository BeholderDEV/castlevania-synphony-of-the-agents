/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import core.AssetsManager;
import core.actors.GameActor;
import core.map.MapHandler;
import java.awt.Dimension;

/**
 *
 * @author Alisson
 */
public class PlayerBehavior {
    
    public static final float NORMAL_WIDTH = 4f;
    public static final float NORMAL_HEIGHT = 6f;
    public static final Dimension FOOT_SIZE = new Dimension(32, 25);
    private PlayerHandler playerHandler;
    private StairHandler stairHandler;
    

    public PlayerBehavior(PlayerHandler handler) {
        this.playerHandler = handler;
        this.stairHandler = new StairHandler(this.playerHandler.getBody());
    }
    
    public void defineAction(float deltaTime, MapHandler map){
        switch(this.playerHandler.getCurrentState()){
            case WALKING:
            case STANDING:
                this.defineActionStanding(deltaTime);
            break;
            case JUMPING:
                this.defineActionJumping(deltaTime);
            break;
            case CROUNCHING:
                this.defineActionCrounching(deltaTime);
            break;
            case ON_STAIRS:
                this.defineActionOnStairs(deltaTime, map);
            break;
            case ATTACKING:
                if(this.playerHandler.getCurrentAtkState() == GameActor.Atk_State.JUMP_ATK){
                    this.defineActionJumping(deltaTime);
                }
            break;
        }
    }
    
    
    private void defineActionStanding(float deltaTime){
        this.playerHandler.setCurrentState(GameActor.State.STANDING);
        this.playerHandler.getVelocity().set(0, 0);
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.playerHandler.setCurrentState(GameActor.State.ATTACKING);
            this.playerHandler.setAtkState(GameActor.Atk_State.STAND_ATK);
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)){
            this.playerHandler.setCurrentState(GameActor.State.DYING);
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.playerHandler.setCurrentState(GameActor.State.WALKING);
            this.playerHandler.getVelocity().x = this.playerHandler.getWalkingSpeed();
            this.playerHandler.setFacingRight(false);       
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.playerHandler.setCurrentState(GameActor.State.WALKING);
            this.playerHandler.getVelocity().x = this.playerHandler.getWalkingSpeed();
            this.playerHandler.setFacingRight(true); 
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            this.playerHandler.setCurrentState(GameActor.State.CROUNCHING);
            this.playerHandler.getBody().height = Math.round(this.NORMAL_HEIGHT - this.NORMAL_HEIGHT * 0.25);
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            this.playerHandler.setCurrentState(GameActor.State.JUMPING);
            this.playerHandler.getVelocity().y = this.playerHandler.getJumpingSpeed();
        }
    }
    
    private void defineActionJumping(float deltaTime){
        this.playerHandler.getVelocity().y = (this.playerHandler.getVelocity().y < 0) ? this.playerHandler.getVelocity().y - this.playerHandler.getJumpingSpeed() / 28f : this.playerHandler.getVelocity().y - this.playerHandler.getJumpingSpeed() /21f;
        this.playerHandler.getVelocity().x = (this.playerHandler.getVelocity().x > 0) ? this.playerHandler.getVelocity().x + this.playerHandler.getWalkingSpeed() / 1.1f: 0;
        if(this.playerHandler.getVelocity().x >= this.playerHandler.getWalkingSpeed()){
            this.playerHandler.getVelocity().x = this.playerHandler.getWalkingSpeed();
        }
        if(this.playerHandler.getCurrentState() != GameActor.State.ATTACKING && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F))){
            this.playerHandler.setCurrentState(GameActor.State.ATTACKING);
            this.playerHandler.setAtkState(GameActor.Atk_State.JUMP_ATK);
            this.playerHandler.setStateTime(0);
        }
    }
    
    private void defineActionCrounching(float deltaTime){
        this.playerHandler.getVelocity().set(0, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.playerHandler.setCurrentState(GameActor.State.ATTACKING);
            this.playerHandler.setAtkState(GameActor.Atk_State.CROUCH_ATK);
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            this.playerHandler.setCurrentState(GameActor.State.CROUNCHING);
            return;
        }
        this.playerHandler.setCurrentState(GameActor.State.STANDING);
        this.playerHandler.getBody().height = this.NORMAL_HEIGHT;
    }
    
    private void defineActionOnStairs(float deltaTime, MapHandler map){
        this.playerHandler.getVelocity().set(0, 0);
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            this.playerHandler.changeStateTime(-deltaTime);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.playerHandler.setCurrentState(GameActor.State.ATTACKING);
            this.playerHandler.setAtkState(GameActor.Atk_State.STAIRS_ATK);
            this.playerHandler.setStateTime(0);
            return;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            this.playerHandler.getVelocity().set(this.playerHandler.getWalkingSpeed(), this.playerHandler.getWalkingSpeed());

            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
                this.stairHandler.updateUpstairs(this.playerHandler.isFacingRight(), true, this.playerHandler.getVelocity());
                this.playerHandler.setFacingRight(true); 
            }else{
                this.stairHandler.updateUpstairs(this.playerHandler.isFacingRight(), false, this.playerHandler.getVelocity());
                this.playerHandler.setFacingRight(false);
            }

            if(!this.stairHandler.checkValidStairStep(map, this.playerHandler.isFacingRight())){
                this.playerHandler.setCurrentState(GameActor.State.WALKING);
                this.playerHandler.getVelocity().y = 0;
                return;
            }            
            this.playerHandler.changeStateTime(deltaTime);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            this.playerHandler.setCurrentState(GameActor.State.JUMPING);
            this.playerHandler.getVelocity().y = this.playerHandler.getJumpingSpeed();
        }

        this.playerHandler.changeStateTime(-deltaTime);
    }
        
    public void checkCollisions(MapHandler map){
        this.playerHandler.checkGroundCollision(map);
        if(this.playerHandler.getCurrentState() == GameActor.State.STANDING || this.playerHandler.getCurrentState() == GameActor.State.WALKING){
            Rectangle stairBoundary = this.stairHandler.checkStairsCollision(map, this.playerHandler.isFacingRight());
            if(stairBoundary != null){
                this.stairHandler.fixPositionForStairClimbing(map, stairBoundary, this.playerHandler.isFacingRight());
                this.playerHandler.setCurrentState(GameActor.State.ON_STAIRS);
                this.playerHandler.setStateTime(0);
            }
        }
    }
                    
//    Used for debug
    public void drawRec(SpriteBatch batch){

        // When Jumping frame is faster and a little lower
        if((this.playerHandler.getCurrentState() == GameActor.State.ATTACKING) && this.playerHandler.getStateTime() >= PlayerAnimation.STANDARD_ATK_FRAME_TIME * 2f){
            float x = (this.playerHandler.isFacingRight()) 
                      ? (this.playerHandler.getBody().x + this.playerHandler.getBody().width) - this.playerHandler.getBody().width * ((this.FOOT_SIZE.width - 30f) / 100f) 
                      : this.playerHandler.getBody().x - this.playerHandler.getBody().width * ((this.FOOT_SIZE.width - 30f) / 100f);
            float y = (this.playerHandler.getBody().y + this.playerHandler.getBody().height) - this.playerHandler.getBody().height * 0.35f;
            float w = 6f;
            w *= (this.playerHandler.isFacingRight())? 1: -1;
            float h = 1;
            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, y, w, h);
            
        }
//        if(this.playerHandler.getCurrentState() == State.ON_STAIRS){
//            int footTileX = 0;
//            int footTileY = 0;
//            if((this.facesRight && this.stairHandler.isUpstairs()) || (!this.facesRight && !this.stairHandler.isUpstairs())){
//                footTileX = Math.round((this.playerHandler.getBody().x + this.playerHandler.getBody().width) - this.playerHandler.getBody().width * (this.FOOT_SIZE.width / 100f));
//                footTileY = Math.round(this.playerHandler.getBody().y + this.FOOT_SIZE.height / 100f);
//            }
//            if((!this.facesRight && this.stairHandler.isUpstairs()) || (this.facesRight && !this.stairHandler.isUpstairs())){
//                footTileX = Math.round(this.playerHandler.getBody().x);
//                footTileY = Math.round(this.playerHandler.getBody().y + this.FOOT_SIZE.height / 100f);
//            }
//            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY, 1, 1);
////            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY - 1, 1, 1);
//        }
//        batch.draw(AssetsManager.assets.get("assets/img/squarer.png", Texture.class), this.playerHandler.getBody().x, this.playerHandler.getBody().y, this.playerHandler.getBody().width + 0.3f, this.playerHandler.getBody().height + 0.2f);
//        float x = (this.facesRight) ? (this.playerHandler.getBody().x + this.playerHandler.getBody().width) - this.playerHandler.getBody().width * (this.FOOT_SIZE.width / 100f): this.playerHandler.getBody().x + this.playerHandler.getBody().width * (this.FOOT_SIZE.width / 100f);
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, this.playerHandler.getBody().y, this.playerHandler.getBody().width * (this.FOOT_SIZE.width / 8f / 100f), this.playerHandler.getBody().height * (this.FOOT_SIZE.height / 100f));
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 29, 5, 1, 1);
////        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 4, 1, 1);
////        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 4, 1, 1);
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 5, 1, 1);
    }
        
    public boolean isUpstairs(){
        return this.stairHandler.isUpstairs();
    }
}
