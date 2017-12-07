/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.actors.CollisionHandler;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 5674867
 */
public class SwordAgentBehavior extends AgentBehavior{
    private AID patronArcherAddress;
    private final Vector2 patronPosition = new Vector2();
    
    public SwordAgentBehavior(Enemy container, Agent a, long period) {
        super(container, a, period, 6);
    }
    
    @Override
    public void defineAction(){
        if(super.container.isIsBlinking()){
            super.updateHurted();
        }
        ACLMessage msg = super.myAgent.receive();
        if(msg != null){
            this.checkReceivedMsg(msg);
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
                this.realizeAgentTakeDown();
            break;
        }
    }
    
    @Override
    protected void realizeAgentTakeDown(){
        if(patronArcherAddress == null){
            try {
                DFService.deregister(super.myAgent);
            } catch (FIPAException ex) {
                Logger.getLogger(SwordAgentBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.myAgent.doDelete();
    }
    
    private void checkReceivedMsg(ACLMessage msg){
        if(msg.getContent().startsWith("Requesting guard")){
            this.receiveRequestForGuard(msg);
        }
        if(msg.getContent().startsWith("Guard confirmation")){
            this.confirmGuard(msg);
        }
        if(msg.getContent().startsWith("Update Guard Position")){
            String[] params = msg.getContent().split(",");
            this.patronPosition.set(Float.parseFloat(params[1]), Float.parseFloat(params[2]));
        }
    }
    
    private void receiveRequestForGuard(ACLMessage msg){
        if(this.patronArcherAddress == null){
            this.sendAcceptanceMsg(msg);
            return;
        }
    }
    
    private void sendAcceptanceMsg(ACLMessage msg){
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        reply.setContent("Accept");
        super.myAgent.send(reply);
    }
    
    private void confirmGuard(ACLMessage msg){
        try {
            this.patronArcherAddress = msg.getSender();
            String[] params = msg.getContent().split(",");
            this.patronPosition.set(Float.parseFloat(params[1]), Float.parseFloat(params[2]));
            DFService.deregister(super.myAgent);
        } catch (FIPAException ex) {
            Logger.getLogger(SwordAgentBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void defineActionStanding(){
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = (super.container.foundPlayer()) ? super.container.getWalkingSpeed(): super.container.getWalkingSpeed() / 2f;
        super.container.setFacingRight(super.container.getBody().x - super.player.getBody().x < 0);
    }
        
    private void defineActionWalking(){
        if(super.checkIfCanAtk()){
            return;
        }
        if(super.container.getStateTime() >= 1f){
            super.container.setStateTime(0);
            super.container.setFacingRight(super.container.getBody().x - super.player.getBody().x < 0);
        }
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
        float w = 5f;
        float x = (super.container.isFacingRight()) 
                  ? super.container.getBody().x + super.container.getBody().width
                  : super.container.getBody().x - w;
        float y = (super.container.getBody().y + super.container.getBody().height) - super.container.getBody().height * 0.35f;
        float h = 1;
        weaponArea.set(x, y, w, h);
        if(CollisionHandler.checkCollisionBetweenBodyAndObject(super.player, weaponArea)){
            super.player.receiveDamage(super.container.getBody(), 1);
        }
        CollisionHandler.rectanglePool.free(weaponArea);
    }

    public AID getPatronArcher() {
        return patronArcherAddress;
    }

    public void setPatronArcher(AID patronArcher) {
        this.patronArcherAddress = patronArcher;
    }
}
