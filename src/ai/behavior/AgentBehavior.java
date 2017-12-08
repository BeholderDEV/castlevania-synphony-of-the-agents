/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import com.badlogic.gdx.graphics.OrthographicCamera;
import core.actors.GameActor;
import core.actors.enemies.Bat;
import core.actors.enemies.Enemy;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.Random;

/**
 *
 * @author Augustop
 */
public abstract class AgentBehavior extends TickerBehaviour{
    protected Random rand;
    protected float distanceToAtkPlayer;
    protected Enemy container;
    protected GameActor player;
    
    public AgentBehavior(Enemy container, Agent a, long period, float distanceToAtkPlayer) {
        super(a, period);
        this.container = container;
        this.player = this.container.getGameScreen().getActors().get(0);
        this.distanceToAtkPlayer = distanceToAtkPlayer;
        this.rand = new Random(System.nanoTime());
    }
    
    @Override
    protected void onTick() {
        if(this.container.canDelete()){
            this.realizeAgentTakeDown();
            return;
        }
        if(!this.container.isPossibleToRender()){
            return;
        }
//        this.container.setStateTime(this.container);
        this.container.setFoundPlayer(this.checkIfFoundPlayer());
        this.defineAction();
        this.container.updatePosition(this.container.getGameScreen().getLastDelta());
        this.checkCollisions();
        this.checkStatus();
        this.container.setPossibleToRender(false);
    }
    
    protected void realizeAgentTakeDown(){
        this.myAgent.doDelete();
    }
    
    protected void updateHurted(){
        this.container.setBlinkPeriod(this.container.getBlinkPeriod() + this.container.getGameScreen().getLastDelta());
        if(this.container.getStateTime() >= Enemy.HURTED_DURATION){
            this.container.setIsBlinking(false);
        }
    }
    
    protected void updateAtk(){
        if(this.container.getAtkAnimation().isAnimationFinished(this.container.getStateTime())){
            this.container.setCurrentState(GameActor.State.STANDING);
            this.container.setStateTime(0);
        }
    }
    
    protected boolean checkIfCanAtk(){
        if(this.container.foundPlayer() && this.isPlayerOnRange()){
            this.container.setCurrentState(GameActor.State.ATTACKING);
            this.container.setStateTime(0);
            this.container.getVelocity().set(0, 0);
            return true;
        }
        return false;
    }
    
    protected boolean checkIfFoundPlayer(){
        OrthographicCamera camera = this.container.getGameScreen().getCamera();
        float dx = Math.abs(this.container.getBody().x - this.player.getBody().x);
        float dy = Math.abs(this.container.getBody().y - this.player.getBody().y);
        if(dx < camera.viewportWidth / 2f && dy < camera.viewportHeight / 2f){
            if(!(this.container instanceof Bat))
            {
                this.container.getVelocity().x = this.container.getWalkingSpeed();                
            }
            return true;
        }
        return false;
    }
    
    protected boolean isPlayerOnRange(){
        float currentDistance = this.container.getBody().x - this.player.getBody().x;
        if(Math.abs(currentDistance) <= this.distanceToAtkPlayer && (this.container.getBody().y + this.container.getBody().height) > this.player.getBody().y){
            this.container.setFacingRight(this.container.getBody().x - this.player.getBody().x < 0);
            return true;
        }
        return false;
    }
    
    public abstract void defineAction();
    
    public abstract void checkCollisions();
    
    public abstract void checkStatus();    
}
