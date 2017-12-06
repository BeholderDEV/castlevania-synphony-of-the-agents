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
        if(super.container.isIsBlinking()){
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
                super.container.fallFromJump();
            break;
            case DYING:
                super.myAgent.doDelete();
            break;
        }
    }
    
    private void defineActionStanding(){
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = (super.container.foundPlayer()) ? super.container.getWalkingSpeed(): super.container.getWalkingSpeed() / 2f;
        super.container.setFacingRight(super.container.getBody().x - this.player.getBody().x < 0);
    }
        
    private void defineActionWalking(){
        if(super.container.getStateTime() >= 1f){
            super.container.setStateTime(0);
            super.container.setFacingRight(super.container.getBody().x - this.player.getBody().x < 0);
        }
        if(super.container.foundPlayer() && this.isPlayerOnRange()){
            super.container.setStateTime(0);
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.ATTACKING);
        }
    }

    //Later check weapon range instead of distance
    private boolean isPlayerOnRange(){
        float currentDistance = super.container.getBody().x - this.player.getBody().x;
        if(Math.abs(currentDistance) <= DISTANCE_TO_ATK_PLAYER && (super.container.getBody().y + super.container.getBody().height) > this.player.getBody().y){
            super.container.setFacingRight(super.container.getBody().x - this.player.getBody().x < 0);
            return true;
        }
        return false;
    }
    
    @Override
    public void checkCollisions(){
        this.groundBehavior();
        this.wallBehavior();
        if(super.container.getCurrentState() == GameActor.State.ATTACKING && super.container.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 2){
            this.updateWeaponHit();
        }
    }
    
    private void wallBehavior(){
        boolean wallCollision = CollisionHandler.checkWallCollision(super.container.getGameScreen().getMapHandler(), this.container, super.container.getGameScreen().getLastDelta());
        if(wallCollision){
            super.container.getVelocity().x = super.container.getWalkingSpeed();
            super.container.getVelocity().y = super.container.getJumpingSpeed();
            super.container.setCurrentState(GameActor.State.JUMPING);
        }
    }
    
    private void groundBehavior(){
        CollisionHandler.checkGroundCollision(super.container.getGameScreen().getMapHandler(), super.container);
        if(super.container.getCurrentState() == GameActor.State.JUMPING){
            if(super.container.getVelocity().y == 0){
                super.container.getVelocity().y = super.container.getJumpingSpeed();
            }
        }else{
            super.container.getVelocity().y = 0;
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
        float x = (super.container.isFacingRight()) 
                  ? super.container.getBody().x + super.container.getBody().width
                  : super.container.getBody().x - w;
        float y = (super.container.getBody().y + super.container.getBody().height) - super.container.getBody().height * 0.35f;
        float h = 1;
        weaponArea.set(x, y, w, h);
        if(CollisionHandler.checkCollisionBetweenBodyAndObject(this.player, weaponArea)){
            this.player.receiveDamage(super.container.getBody(), 1);
        }
        CollisionHandler.rectanglePool.free(weaponArea);
    }
}
