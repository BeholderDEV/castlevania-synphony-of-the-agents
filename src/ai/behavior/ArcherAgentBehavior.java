/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import static ai.behavior.SwordAgentBehavior.DISTANCE_TO_ATK_PLAYER;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import jade.core.Agent;

/**
 *
 * @author Adson Esteves
 */
public class ArcherAgentBehavior extends AgentBehavior{
    
    private GameActor player;
    public static final float DISTANCE_TO_ATK_PLAYER = 7f;
    
    public ArcherAgentBehavior(Enemy container, Agent a, long period) {
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
            case DYING:
                super.myAgent.doDelete();
            break;
        }
    }
    
    private void defineActionStanding(){
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = super.container.getWalkingSpeed();
        super.container.setFacingRight((int) super.container.getStateTime() % 2 == 1);
    }
        
    private void defineActionWalking(){
        this.defineActionStanding();
        if(this.isPlayerOnRange()){
            super.container.setStateTime(0);
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.ATTACKING);
        }
    }

    //Later check weapon range instead of distance
    private boolean isPlayerOnRange(){
        float currentDistance = this.container.getBody().x - this.player.getBody().x;
        if(Math.abs(currentDistance) <= DISTANCE_TO_ATK_PLAYER && this.container.getBody().y == this.player.getBody().y){
            this.container.setFacingRight(currentDistance < 0);
            return true;
        }
        return false;
    }
    
    @Override
    public void checkCollisions(){
        
    }
    
    @Override
    public void checkStatus(){
        if(super.container.getLifePoints() <= 0){
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.DYING);
        }
    }
}
