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
import com.badlogic.gdx.math.Vector2;
import core.AssetsManager;
import core.map.MapHandler;
import java.awt.Dimension;

/**
 *
 * @author Alisson
 */
public class PlayerBehavior {
    
    public enum State {
        STANDING, WALKING, JUMPING, CROUNCHING, ON_STAIRS, DYING, ATTACKING
    }
    
    public enum Atk_State {
        STAND_ATK, CROUCH_ATK, JUMP_ATK, STAIRS_ATK
    }
    
    public static final float DISTANCE_FROM_GROUND_LAYER = 0.4f;
    public static final float NORMAL_WIDTH = 4f;
    public static final float NORMAL_HEIGHT = 6f;
    public static final int WALKING_SPEED = 12;
    public static final int JUMPING_SPEED = WALKING_SPEED * 3;
    public static final Dimension FOOT_SIZE = new Dimension(32, 25);
    private boolean facesRight = true;
    private PlayerHandler playerHandler;
    private StairHandler stairHandler;
    private State currentState = State.STANDING;
    private Atk_State atkState = Atk_State.STAND_ATK;
    private Vector2 velocity = new Vector2(); 
    private Rectangle playerBody;

    public PlayerBehavior(PlayerHandler handler) {
        this.playerBody = new Rectangle(0, 0, this.NORMAL_WIDTH, this.NORMAL_HEIGHT);
        this.playerHandler = handler;
        this.stairHandler = new StairHandler(this.playerBody);
    }
    
    public void defineAction(float deltaTime, MapHandler map){
        switch(this.currentState){
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
                if(this.atkState == Atk_State.JUMP_ATK){
                    this.defineActionJumping(deltaTime);
                }
            break;
        }
    }
    
    
    private void defineActionStanding(float deltaTime){
        this.currentState = State.STANDING;
        this.velocity.set(0, 0);
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.currentState = State.ATTACKING;
            this.atkState = Atk_State.STAND_ATK;
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)){
            this.currentState = State.DYING;
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.currentState = State.WALKING;
            this.velocity.x = WALKING_SPEED;
            this.facesRight = false;        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.currentState = State.WALKING;
            this.velocity.x = WALKING_SPEED;
            this.facesRight = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            this.currentState = State.CROUNCHING;
            this.playerBody.height = Math.round(this.NORMAL_HEIGHT - this.NORMAL_HEIGHT * 0.25);
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            this.currentState = State.JUMPING;
            this.velocity.y = JUMPING_SPEED;
        }
    }
    
    private void defineActionJumping(float deltaTime){
        this.velocity.y = (this.velocity.y < 0) ? this.velocity.y - JUMPING_SPEED / 28f : this.velocity.y - JUMPING_SPEED /21f;
        this.velocity.x = (this.velocity.x > 0) ? this.velocity.x + WALKING_SPEED / 1.1f: 0;
        if(this.velocity.x >= WALKING_SPEED){
            this.velocity.x = WALKING_SPEED;
        }
        if(this.currentState != State.ATTACKING && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F))){
            this.currentState = State.ATTACKING;
            this.atkState = Atk_State.JUMP_ATK;
            this.playerHandler.setStateTime(0);
        }
    }
    
    private void defineActionCrounching(float deltaTime){
        this.velocity.set(0, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.currentState = State.ATTACKING;
            this.atkState = atkState.CROUCH_ATK;
            this.playerHandler.setStateTime(0);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            this.currentState = State.CROUNCHING;
            return;
        }
        this.currentState = State.STANDING;
        this.playerBody.height = this.NORMAL_HEIGHT;
    }
    
    private void defineActionOnStairs(float deltaTime, MapHandler map){
        this.velocity.set(0, 0);
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            this.playerHandler.changeStateTime(-deltaTime);
            return;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.F)){
            this.currentState = State.ATTACKING;
            this.atkState = Atk_State.STAIRS_ATK;
            this.playerHandler.setStateTime(0);
            return;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            this.velocity.set(WALKING_SPEED, WALKING_SPEED);

            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
                this.stairHandler.updateUpstairs(this.facesRight, true, this.velocity);
                this.facesRight = true;
            }else{
                this.stairHandler.updateUpstairs(this.facesRight, false, this.velocity);
                this.facesRight = false;
            }

            if(!this.stairHandler.checkValidStairStep(map, this.facesRight)){
                this.currentState = State.WALKING;
                this.velocity.y = 0;
                return;
            }            
            this.playerHandler.changeStateTime(deltaTime);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            this.currentState = State.JUMPING;
            this.velocity.y = JUMPING_SPEED;
        }

        this.playerHandler.changeStateTime(-deltaTime);
    }
        
    public void checkCollisions(MapHandler map){
        this.checkGroundCollision(map);
        if(this.currentState == State.STANDING || this.currentState == State.WALKING){
            Rectangle stairBoundary = this.stairHandler.checkStairsCollision(map, this.facesRight);
            if(stairBoundary != null){
                this.stairHandler.fixPositionForStairClimbing(map, stairBoundary, this.facesRight);
                this.currentState = PlayerBehavior.State.ON_STAIRS;
                this.playerHandler.setStateTime(0);
            }
        }
    }
    
    private void checkGroundCollision(MapHandler map){
        if(map.checkLayerCollision(MapHandler.Layer.GROUND, Math.round(this.playerBody.x), Math.round(this.playerBody.y), Math.round(this.playerBody.x + this.playerBody.width), Math.round(this.playerBody.y + this.playerBody.height * 0.01f))){
            if(this.currentState == State.JUMPING || (this.currentState == State.ATTACKING && this.atkState == Atk_State.JUMP_ATK)){
                this.currentState = State.STANDING;
                this.playerBody.y = Math.round(this.playerBody.y) + this.DISTANCE_FROM_GROUND_LAYER;
            }
        }else if(this.currentState != State.JUMPING && this.currentState != State.ON_STAIRS && this.currentState != State.ATTACKING){
            this.currentState = State.JUMPING;
        }
    }
            
    public void updatePosition(float delta){
        this.playerBody.x += this.velocity.x * delta * ((this.facesRight) ? 1: -1);
        this.playerBody.y += this.velocity.y * delta;
    }
    
//    Used for debug
    public void drawRec(SpriteBatch batch){
        // When Jumping frame is faster and a little lower
        
        if((this.currentState == State.ATTACKING) && this.playerHandler.getStateTime() >= PlayerAnimation.STANDARD_ATK_FRAME_TIME * 2f){
            float x = (this.facesRight) 
                      ? (this.playerBody.x + this.playerBody.width) - this.playerBody.width * ((this.FOOT_SIZE.width - 30f) / 100f) 
                      : this.playerBody.x - this.playerBody.width * ((this.FOOT_SIZE.width - 30f) / 100f);
            float y = (this.playerBody.y + this.playerBody.height) - this.playerBody.height * 0.35f;
            float w = 6f;
            w *= (this.facesRight)? 1: -1;
            float h = 1;
            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, y, w, h);
            
        }
//        if(this.currentState == State.ON_STAIRS){
//            int footTileX = 0;
//            int footTileY = 0;
//            if((this.facesRight && this.stairHandler.isUpstairs()) || (!this.facesRight && !this.stairHandler.isUpstairs())){
//                footTileX = Math.round((this.playerBody.x + this.playerBody.width) - this.playerBody.width * (this.FOOT_SIZE.width / 100f));
//                footTileY = Math.round(this.playerBody.y + this.FOOT_SIZE.height / 100f);
//            }
//            if((!this.facesRight && this.stairHandler.isUpstairs()) || (this.facesRight && !this.stairHandler.isUpstairs())){
//                footTileX = Math.round(this.playerBody.x);
//                footTileY = Math.round(this.playerBody.y + this.FOOT_SIZE.height / 100f);
//            }
//            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY, 1, 1);
////            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), footTileX, footTileY - 1, 1, 1);
//        }
//        batch.draw(AssetsManager.assets.get("assets/img/squarer.png", Texture.class), this.playerBody.x, this.playerBody.y, this.playerBody.width + 0.3f, this.playerBody.height + 0.2f);
//        float x = (this.facesRight) ? (this.playerBody.x + this.playerBody.width) - this.playerBody.width * (this.FOOT_SIZE.width / 100f): this.playerBody.x + this.playerBody.width * (this.FOOT_SIZE.width / 100f);
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, this.playerBody.y, this.playerBody.width * (this.FOOT_SIZE.width / 8f / 100f), this.playerBody.height * (this.FOOT_SIZE.height / 100f));
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 29, 5, 1, 1);
////        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 4, 1, 1);
////        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 4, 1, 1);
//        batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), 63, 5, 1, 1);
    }
    
    public Rectangle getPlayerBody() {
        return playerBody;
    }
    
    public State getCurrentState() {
        return currentState;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
    
    public Atk_State getAtkState() {
        return atkState;
    }
    
    public boolean isUpstairs(){
        return this.stairHandler.isUpstairs();
    }

    public boolean isFacingRight() {
        return facesRight;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void setFacesRight(boolean facesRight) {
        this.facesRight = facesRight;
    }


}
