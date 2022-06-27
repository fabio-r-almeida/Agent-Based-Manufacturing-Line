package Product;

import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;

public class BH3 extends AchieveREInitiator {
    boolean go_next = true;
    String skill;
    public BH3(Agent a, ACLMessage msg) {
        super(a,msg);

        String[] parts = msg.getContent().split(Constants.TOKEN);
        this.skill = parts[0];

        if(parts[0].equalsIgnoreCase("free")){
            go_next = false;
        }
        if(parts[0].equalsIgnoreCase("swap")){
            System.out.println("swap request");
            go_next = false;
        }
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        System.out.println("BH3 : step 8 - Recebe confirmação da execução da skill");
        System.out.println("go next is " + go_next);
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        System.out.println("BH3 : step 9 - Confirmado");
        if(go_next) {
            ((ProductAgent) myAgent).go_next_step = true;
            if(this.skill.equalsIgnoreCase(Constants.SK_QUALITY_CHECK) && ((ProductAgent) myAgent).first_time) {
                if (!inform.getContent().equalsIgnoreCase("true")) {
                    ((ProductAgent) myAgent).first_time = false;
                    ((ProductAgent) myAgent).go_prev_step = true;
                    ((ProductAgent) myAgent).go_next_step = false;
                }
            }
        }
    }
}