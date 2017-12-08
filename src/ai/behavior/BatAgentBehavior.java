/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import core.actors.CollisionHandler;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import jade.core.Agent;

/**
 *
 * @author Augustop
 */
public class BatAgentBehavior extends AgentBehavior{
    public static final float BAT_DISTANCE_TO_ATK = 10f;

    public BatAgentBehavior(Enemy container, Agent a, long period) {
        super(container, a, period, BAT_DISTANCE_TO_ATK);
    }
    
    @Override
    public void defineAction() {
        if(super.container.isIsBlinking()){
            super.updateHurted();
        }
        switch(super.container.getCurrentState()){
            case STANDING:
                this.defineActionStanding();
            break;
            case ATTACKING:
                this.defineActionAttacking();
            break;
            case DYING:
                this.realizeAgentTakeDown();
            break;
        }
    }
    
    private void defineActionStanding(){
        if(super.isPlayerOnRange())
        {
            super.container.setCurrentState(GameActor.State.ATTACKING);            
        }
    }
    
    private void defineActionAttacking()
    {
        float dx = Math.abs(this.player.getBody().x - super.container.getBody().x);
        float dy = Math.abs(this.player.getBody().y - super.container.getBody().y);
//        float distance = (float) Math.sqrt((dx*dx)+(dy*dy));
        float distance = 0.394f * (dx + dy) + 0.554f *  Math.max(dx, dy);
        if(distance == 0){
            distance = 0.01f;
        }
        super.container.getVelocity().x = super.container.getWalkingSpeed()*(this.player.getBody().x - this.container.getBody().x)/distance;
        super.container.getVelocity().y = super.container.getWalkingSpeed()*(this.player.getBody().y+3f - this.container.getBody().y)/distance;
        super.container.setFacingRight(super.container.getBody().x - this.player.getBody().x < 0);
        if(!super.container.isFacingRight())
        {
            super.container.getVelocity().x = super.container.getVelocity().x*-1;
        }
    }

    @Override
    public void checkCollisions() {
        if(CollisionHandler.checkCollisionBetweenTwoActorsBodies(this.player, super.container) != null)
        {
            this.player.receiveDamage(super.container.getBody(), 1);
            this.container.setCurrentState(GameActor.State.DYING);
        }
    }

    @Override
    public void checkStatus() {
        
    }
    
}
