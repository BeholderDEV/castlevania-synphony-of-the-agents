/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.player;

import core.actors.StairHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import core.actors.CollisionHandler;
import core.util.AssetsManager;
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
            case HURTED:
                this.defineActionHurted(deltaTime, map);
            break;
        }
    }
    
    private void defineActionHurted(float deltaTime, MapHandler map){
        if(this.playerHandler.getStateTime() >= 0.2f){
            this.playerHandler.setCurrentState(GameActor.State.JUMPING);
            this.playerHandler.getVelocity().y = 1;
            CollisionHandler.checkGroundCollision(map, playerHandler);
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
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.playerHandler.getVelocity().x = this.playerHandler.getWalkingSpeed();
            this.playerHandler.setFacingRight(false);       
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.playerHandler.getVelocity().x = this.playerHandler.getWalkingSpeed();
            this.playerHandler.setFacingRight(true); 
        }
        this.playerHandler.fallFromJump();
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
    
    public void receiveDamage(Rectangle dmgReason, int dmgPoints){
        this.playerHandler.loseLifePoints(dmgPoints);
        this.playerHandler.setCurrentState(GameActor.State.HURTED);
        this.playerHandler.getVelocity().x = -this.playerHandler.getWalkingSpeed() * 2;
        this.playerHandler.getVelocity().y = 0;
        this.playerHandler.setStateTime(0);
        this.playerHandler.setFacingRight(dmgReason.x > this.playerHandler.getBody().x);
    }
        
    public void checkCollisions(float deltaTime, MapHandler map, Array<GameActor> stageActors){
        CollisionHandler.checkGroundCollision(map, this.playerHandler);
        CollisionHandler.checkWallCollision(map, this.playerHandler, deltaTime);
        this.updateCollisionWithEnemy(stageActors);
        this.updateCollisionWithStairs(map, stageActors);
    }
    
    private void updateCollisionWithEnemy(Array<GameActor> stageActors){
        if(this.playerHandler.getCurrentState() != GameActor.State.HURTED && this.playerHandler.getCurrentState() != GameActor.State.DYING){
            for (int i = 1; i < stageActors.size; i++) {
                Rectangle dmgReasonCulprit = CollisionHandler.checkCollisionBetweenTwoActorsBodies(this.playerHandler, stageActors.get(i));
                if(dmgReasonCulprit != null){
                    this.receiveDamage(dmgReasonCulprit, 1);
                    break;
                }
            }
        }
        if((this.playerHandler.getCurrentState() == GameActor.State.ATTACKING && this.playerHandler.getAtkState() != GameActor.Atk_State.JUMP_ATK) && this.playerHandler.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 2f
        || (this.playerHandler.getCurrentState() == GameActor.State.ATTACKING && this.playerHandler.getAtkState() == GameActor.Atk_State.JUMP_ATK) && this.playerHandler.getStateTime() >= 0.2){
            this.updateWeaponHit(stageActors);
        }
    }
    
    private void updateWeaponHit(Array<GameActor> stageActors){
        Rectangle weaponArea = CollisionHandler.rectanglePool.obtain();
        float w = 6f;
        float x = (this.playerHandler.isFacingRight()) 
                  ? (this.playerHandler.getBody().x + this.playerHandler.getBody().width) - this.playerHandler.getBody().width * ((this.FOOT_SIZE.width - 30f) / 100f) 
                  : this.playerHandler.getBody().x - w;
        float y;
        if(this.playerHandler.getAtkState() == GameActor.Atk_State.JUMP_ATK){
            y = (this.playerHandler.getBody().y + this.playerHandler.getBody().height) - this.playerHandler.getBody().height * 0.5f;
        }else if(this.playerHandler.getAtkState() == GameActor.Atk_State.STAIRS_ATK){
            y = (this.playerHandler.getBody().y + this.playerHandler.getBody().height) - this.playerHandler.getBody().height * 0.45f;
        }else{
            y = (this.playerHandler.getBody().y + this.playerHandler.getBody().height) - this.playerHandler.getBody().height * 0.35f;
        } 
        float h = 1;
        weaponArea.set(x, y, w, h);
        for (int i = 1; i < stageActors.size; i++) {
            if(CollisionHandler.checkCollisionBetweenBodyAndObject(stageActors.get(i), weaponArea)){
                stageActors.get(i).receiveDamage(weaponArea, 1);
                break;
            }
        }
        CollisionHandler.rectanglePool.free(weaponArea);
    }
    
    private void updateCollisionWithStairs(MapHandler map, Array<GameActor> stageActors){
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
        Rectangle b = this.playerHandler.getBody();
        if((this.playerHandler.getCurrentState() == GameActor.State.ATTACKING && this.playerHandler.getAtkState() != GameActor.Atk_State.JUMP_ATK) && this.playerHandler.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 2f 
        || (this.playerHandler.getCurrentState() == GameActor.State.ATTACKING && this.playerHandler.getAtkState() == GameActor.Atk_State.JUMP_ATK) && this.playerHandler.getStateTime() >= 0.2){
            float w = 6f;
            float x = (this.playerHandler.isFacingRight()) 
                      ? (b.x + b.width) - b.width * ((this.FOOT_SIZE.width - 30f) / 100f) 
                      : b.x - w;
            float y;
            if(this.playerHandler.getAtkState() == GameActor.Atk_State.JUMP_ATK){
                y = (b.y + b.height) - b.height * 0.5f;
            }else if(this.playerHandler.getAtkState() == GameActor.Atk_State.STAIRS_ATK){
                y = (b.y + b.height) - b.height * 0.45f;
            }else{
                y = (b.y + b.height) - b.height * 0.35f;
            } 
            float h = 1;
            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, y, w, h);
        }
        if(this.playerHandler.getCurrentState() == GameActor.State.ON_STAIRS){
            int footTileX = 0;
            int footTileY = 0;
            if((this.playerHandler.isFacingRight() && this.stairHandler.isUpstairs()) || (!this.playerHandler.isFacingRight() && !this.stairHandler.isUpstairs())){
                footTileX = Math.round((b.x + b.width) - b.width * (this.FOOT_SIZE.width / 100f));
                footTileY = Math.round(b.y + this.FOOT_SIZE.height / 100f);
            }
            if((!this.playerHandler.isFacingRight() && this.stairHandler.isUpstairs()) || (this.playerHandler.isFacingRight() && !this.stairHandler.isUpstairs())){
                footTileX = Math.round(b.x);
                footTileY = Math.round(b.y + this.FOOT_SIZE.height / 100f);
            }
            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY, 1, 1);
//            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY - 1, 1, 1);
        }
        
        int startX = Math.round(b.x + this.playerHandler.getSpriteAdjustmentForCollision()[0]), 
            startY = Math.round(b.y + this.playerHandler.getSpriteAdjustmentForCollision()[1] + (b.height + this.playerHandler.getSpriteAdjustmentForCollision()[3]) * 0.05f), 
            endX = Math.round(b.x + this.playerHandler.getSpriteAdjustmentForCollision()[0] + (b.width + this.playerHandler.getSpriteAdjustmentForCollision()[2])), 
            endY = Math.round(b.y + this.playerHandler.getSpriteAdjustmentForCollision()[1] + (b.height + this.playerHandler.getSpriteAdjustmentForCollision()[3]) * 0.95f);
        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), startX, startY, 1, 1);
        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), endX, endY, 1, 1);
        int centerX = Math.round(((b.x + this.playerHandler.getSpriteAdjustmentForCollision()[0]) +
                                  (b.width + this.playerHandler.getSpriteAdjustmentForCollision()[2])/2f));
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), centerX, startY, 1, 1);
    
    }
        
    public boolean isUpstairs(){
        return this.stairHandler.isUpstairs();
    }
}
