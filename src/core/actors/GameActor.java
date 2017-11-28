/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.actors.player.PlayerBehavior;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public abstract class GameActor {
    public static final float DISTANCE_FROM_GROUND_LAYER = 0.4f;
    protected float stateTime = 0;
    protected Rectangle body;
    protected boolean facingRight = true;
    protected int walkingSpeed = 12;
    protected int jumpingSpeed = walkingSpeed * 3;
    protected Vector2 velocity = new Vector2(); 
    protected State currentState = State.STANDING;
    protected Atk_State atkState = Atk_State.STAND_ATK;
    
    public enum State {
        STANDING, WALKING, JUMPING, CROUNCHING, ON_STAIRS, DYING, ATTACKING
    }
    
    public enum Atk_State {
        STAND_ATK, CROUCH_ATK, JUMP_ATK, STAIRS_ATK
    }

    public GameActor(Rectangle body) {
        this.body = body;
    }

    public GameActor(int walkingSpeed, Rectangle body) {
        this.walkingSpeed = walkingSpeed;
        this.body = body;
    }
    
    public abstract void updateActor(float deltaTime, MapHandler map);
    
    public abstract void renderActor(SpriteBatch batch);
    
    public void updatePosition(float delta){
        this.body.x += this.velocity.x * delta * ((this.facingRight) ? 1: -1);
        this.body.y += this.velocity.y * delta;
    }
    
    public void checkGroundCollision(MapHandler map){
        if(map.checkLayerCollision(MapHandler.Layer.GROUND, Math.round(this.body.x), Math.round(this.body.y), Math.round(this.body.x + this.body.width), Math.round(this.body.y + this.body.height * 0.01f))){
            if(this.currentState == State.JUMPING|| (this.currentState == State.ATTACKING && this.atkState == Atk_State.JUMP_ATK)){
                this.currentState = State.STANDING;
                this.body.y = Math.round(this.body.y) + GameActor.DISTANCE_FROM_GROUND_LAYER;
            }
        }else if(this.currentState != State.JUMPING && this.currentState != State.ON_STAIRS){
            this.currentState = State.JUMPING;
        }
    }

    public Rectangle getBody() {
        return body;
    }

    public boolean isFacingRight() {
        return facingRight;
    }
    
    public abstract boolean isDead();

    public State getCurrentState() {
        return currentState;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getWalkingSpeed() {
        return walkingSpeed;
    }

    public int getJumpingSpeed() {
        return jumpingSpeed;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}
