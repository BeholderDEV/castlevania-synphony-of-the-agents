/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import com.badlogic.gdx.math.Rectangle;
import core.actors.CollisionHandler;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import jade.core.Agent;

/**
 *
 * @author 5674867
 */
public class SwordAgentBehavior extends AgentBehavior{
    private final GameActor player;
    public static final float DISTANCE_TO_ATK_PLAYER = 6f;
    
    public SwordAgentBehavior(Enemy container, Agent a, long period) {
        super(container, a, period);
        this.player = super.container.getGameScreen().getActors().get(0);
    }
    
    @Override
    public void defineAction(){
        if(this.container.isIsBlinking()){
            super.updateHurted();
        }
        switch(super.container.getCurrentState()){
            case STANDING:
                this.defineActionStanding();
            break;
            case WALKING:
                this.defineActionWalking();
            break;
            case ATTACKING:
                super.updateAtk();
            break;
            case JUMPING:
                this.container.fallFromJump();
            break;
            case DYING:
                super.myAgent.doDelete();
            break;
        }
    }
    
    private void defineActionStanding(){
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = (super.container.foundPlayer()) ? super.container.getWalkingSpeed(): super.container.getWalkingSpeed() / 2f;
        super.container.setFacingRight(this.container.getBody().x - this.player.getBody().x < 0);
    }
        
    private void defineActionWalking(){
        if(this.container.getStateTime() >= 1f){
            this.container.setStateTime(0);
            super.container.setFacingRight(this.container.getBody().x - this.player.getBody().x < 0);
        }
        if(this.container.foundPlayer() && this.isPlayerOnRange()){
            super.container.setStateTime(0);
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.ATTACKING);
        }
    }

    //Later check weapon range instead of distance
    private boolean isPlayerOnRange(){
        float currentDistance = this.container.getBody().x - this.player.getBody().x;
        if(Math.abs(currentDistance) <= DISTANCE_TO_ATK_PLAYER && (this.container.getBody().y + this.container.getBody().height) > this.player.getBody().y){
            this.container.setFacingRight(this.container.getBody().x - this.player.getBody().x < 0);
            return true;
        }
        return false;
    }
    
    @Override
    public void checkCollisions(){
        CollisionHandler.checkGroundCollision(this.container.getGameScreen().getMapHandler(), this.container);
        if(this.container.getCurrentState() == GameActor.State.JUMPING){
            if(this.container.getVelocity().y == 0){
                this.container.getVelocity().y = this.container.getJumpingSpeed();
            }
        }else{
            this.container.getVelocity().y = 0;
        }
        if(this.container.getCurrentState() == GameActor.State.ATTACKING && this.container.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 2){
            this.updateWeaponHit();
        }
    }
    
    @Override
    public void checkStatus(){
        if(super.container.getLifePoints() <= 0 || super.container.getBody().y < 0){
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.DYING);
        }
    }

    private void updateWeaponHit(){
        Rectangle weaponArea = CollisionHandler.rectanglePool.obtain();
        float w = 3.5f;
        float x = (this.container.isFacingRight()) 
                  ? this.container.getBody().x + this.container.getBody().width
                  : this.container.getBody().x - w;
        float y = (this.container.getBody().y + this.container.getBody().height) - this.container.getBody().height * 0.35f;
        float h = 1;
        weaponArea.set(x, y, w, h);
        if(CollisionHandler.checkCollisionBetweenBodyAndObject(this.player, weaponArea)){
            this.player.receiveDamage(this.container.getBody(), 1);
        }
        CollisionHandler.rectanglePool.free(weaponArea);
    }
}
