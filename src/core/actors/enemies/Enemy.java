/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import core.actors.GameActor;
import core.screens.GameScreen;
/**
 *
 * @author Augustop
 */
public abstract class Enemy extends GameActor{
    public static final float BLINK_INTERVAL = 0.05f;
    public static final float HURTED_DURATION = 0.3f;
    protected float blinkPeriod;
    protected TextureRegion standImg;
    protected Animation<TextureRegion> movingAnimation;
    protected Animation<TextureRegion> atkAnimation;
    protected Animation<TextureRegion> deathAnimation;
    protected GameScreen gameScreen;
    protected boolean blinking = false;
    protected boolean canDelete = false;
    protected boolean foundPlayer = false;
    
    public Enemy(int walkingSpeed, Rectangle body, GameScreen gameScreen) {
        super(walkingSpeed, body);
        super.facingRight = false;
        this.gameScreen = gameScreen;
    }
    
    @Override
    public void receiveDamage(Rectangle dmgReason, int dmgPoints) {
        if(this.blinking || super.currentState == State.DYING){
            return;
        }
        super.stateTime = 0;
        super.loseLifePoints(dmgPoints);
        this.blinkPeriod = 0;
        this.blinking = true;
        if(super.lifePoints <= 0){
            super.currentState = State.DYING;
        }
    }
    
    @Override
    public TextureRegion getCurrentFrame(){
        switch(super.currentState){
            case WALKING:
                return this.movingAnimation.getKeyFrame(super.stateTime);
            case ATTACKING:
                return this.atkAnimation.getKeyFrame(super.stateTime);
//                return this.atkAnimation.getKeyFrame(0.10f);
            default:
                return this.standImg;
        }
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public Animation<TextureRegion> getAtkAnimation() {
        return atkAnimation;
    }
    
    @Override
    public boolean isDead() {
        return super.currentState == GameActor.State.DYING;
    }

    public boolean isIsBlinking() {
        return blinking;
    }

    public boolean foundPlayer() {
        return foundPlayer;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public float getBlinkPeriod() {
        return blinkPeriod;
    }

    public void setBlinkPeriod(float blinkPeriod) {
        this.blinkPeriod = blinkPeriod;
    }

    public void setIsBlinking(boolean isBlinking) {
        this.blinking = isBlinking;
    }
    
    public void setForDelete(){
        this.canDelete = true;
    }

    public void setFoundPlayer(boolean foundPlayer) {
        this.foundPlayer = foundPlayer;
    }
}
