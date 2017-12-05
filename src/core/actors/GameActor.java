/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import core.util.AssetsManager;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public abstract class GameActor {
    public static final float STANDARD_ATK_FRAME_TIME = 0.15f;
    public static final float DISTANCE_FROM_GROUND_LAYER = 0.4f;
    protected float stateTime = 0;
    protected Rectangle body;
    protected boolean facingRight = true;
    protected int walkingSpeed = 12;
    protected int jumpingSpeed = walkingSpeed * 3;
    protected Vector2 velocity = new Vector2(); 
    protected State currentState = State.STANDING;
    protected Atk_State atkState = Atk_State.STAND_ATK;
    protected Array<Rectangle> collidableObject = new Array<>();
    protected float spriteAdjustmentForCollision[] = {0, 0, 0, 0};
    protected final Vector2 renderCorrection = new Vector2(0, 0);
    protected int lifePoints = 3;
    protected boolean possibleToRender = false;
    
    
    public enum State {
        STANDING, WALKING, JUMPING, CROUNCHING, ON_STAIRS, DYING, ATTACKING, HURTED
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
    
    public void updatePosition(float delta){
        this.body.x += this.velocity.x * delta * ((this.facingRight) ? 1: -1);
        this.body.y += this.velocity.y * delta;
    }
    
    public float[] getSpriteRenderValues(TextureRegion currentFrame){
        float x = this.body.x, y = this.body.y, w = this.body.width, h = this.body.height; 
        this.renderCorrection.set(0, 0);
        if(!this.facingRight){
            x += w;
        }
//        if(this.currentState == State.ATTACKING){
            this.adjustRenderCorrections(currentFrame);
            w = currentFrame.getRegionWidth() * MapHandler.unitScale;
            h = currentFrame.getRegionHeight() * MapHandler.unitScale;
            if(this.atkState == GameActor.Atk_State.CROUCH_ATK){
                h += 1.2f;
            }
//        }
        if(this.facingRight){
            x -= this.renderCorrection.x;
        }else{
            x += this.renderCorrection.x;
            w *= -1;
        }
        y += this.renderCorrection.y;
        return new float[]{x, y, w, h};
    }
    
    
    
    public void drawRecOverBody(SpriteBatch batch){
        float ad = 1.6f;
        batch.draw(AssetsManager.assets.get("assets/img/squarer.png", Texture.class),(this.facingRight) ?this.body.x + 0.4f: this.body.x + 0.4f, 
                (this.getCurrentState() == State.ON_STAIRS) ? this.body.y + 0.4f: this.body.y, 
                this.body.width - ad, 
                this.body.height - 0.9f);
    }
    
    public abstract void updateActor(float deltaTime, MapHandler map, Array<GameActor> stageActors);
    
    public abstract void renderActor(SpriteBatch batch);
    
    public abstract void receiveDamage(Rectangle dmgReason, int dmgPoints);
    
    public abstract TextureRegion getCurrentFrame();
    
    public abstract void drawDebugRec(SpriteBatch batch);
    
    protected abstract void adjustRenderCorrections(TextureRegion currentFrame);
    
    
    public Rectangle getBody() {
        return body;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public boolean isFacingRight() {
        return facingRight;
    }
    
    public abstract boolean isDead();

    public State getCurrentState() {
        return currentState;
    }

    public Atk_State getAtkState() {
        return atkState;
    }

    public Array<Rectangle> getCollidableObject() {
        return collidableObject;
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

    public float[] getSpriteAdjustmentForCollision() {
        return spriteAdjustmentForCollision;
    }

    public float getStateTime() {
        return stateTime;
    }

    public boolean isPossibleToRender() {
        return possibleToRender;
    }
    
    public void loseLifePoints(int points){
        this.lifePoints -= points;
    }
    
    public void gainLifePoints(int points){
        this.lifePoints += points;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
    
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void setPossibleToRender(boolean possibleToRender) {
        this.possibleToRender = possibleToRender;
    }

    public void setAtkState(Atk_State atkState) {
        this.atkState = atkState;
    }
}
