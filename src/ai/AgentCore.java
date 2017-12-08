/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.behavior.ArcherAgentBehavior;
import ai.behavior.BatAgentBehavior;
import ai.behavior.SwordAgentBehavior;
import core.actors.enemies.ArcherSkeleton;
import core.actors.enemies.Bat;
import core.actors.enemies.SwordSkeleton;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Augustop
 */
public class AgentCore extends Agent{
    
    @Override
    protected void setup() {
        System.out.println("Iniating agent: " + getAID().getName());
        Object [] args = getArguments();
        if(args != null && args.length > 0){
            if(getAID().getName().startsWith("Sword_Skeleton")){
                addBehaviour(new SwordAgentBehavior((SwordSkeleton) args[0], this, AgentCreator.BEHAVIOR_DELAY));
                this.registerSwordService((SwordSkeleton) args[0]);
            }
            if(getAID().getName().startsWith("Archer_Skeleton")){
                addBehaviour(new ArcherAgentBehavior((ArcherSkeleton) args[0], this, AgentCreator.BEHAVIOR_DELAY));
            }
            if(getAID().getName().startsWith("Bat")){
                addBehaviour(new BatAgentBehavior((Bat) args[0], this, AgentCreator.BEHAVIOR_DELAY));
            }
        }
    }
    
    private void registerSwordService(SwordSkeleton container){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getLocalName());
        sd.setType("guard");
        sd.addProperties(new Property("x", container.getBody().x));
        sd.addProperties(new Property("y", container.getBody().y));
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            Logger.getLogger(AgentCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println("Ending agent " + getAID().getName());
    }
    
}
