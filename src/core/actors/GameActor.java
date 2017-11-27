/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public abstract class GameActor {
    protected float stateTime = 0;
    protected Rectangle body;
    protected boolean facingRight = true;
    protected int walkingSpeed = 12;
    protected int jumpingSpeed = walkingSpeed * 3;
    protected Vector2 velocity = new Vector2(); 
    protected State currentState = State.STANDING;
    public static final float DISTANCE_FROM_GROUND_LAYER = 0.4f;
    
    public enum State {
        STANDING, WALKING, JUMPING, CROUNCHING, ON_STAIRS, DYING, ATTACKING
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
