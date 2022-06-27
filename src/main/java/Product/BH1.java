package Product;

import Utilities.Constants;
import Utilities.DFInteraction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class BH1 extends SimpleBehaviour {
    boolean go_search = true;
    String step;

    public BH1(Agent a,String s) {

        super (a);
        this.step = s;
    }

        @Override
        public void action () {
            if(go_search){
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                DFInteraction dfinteraction = new DFInteraction();

                System.out.println(step);
                try {
                    msg.setOntology(Constants.ONTOLOGY_NEGOTIATE_RESOURCE);
                    for (DFAgentDescription result : dfinteraction.SearchInDFByName(step, this.myAgent)) {
                        msg.addReceiver(new AID(result.getName().getLocalName(), false));
                        msg.setContent(step+Constants.TOKEN+((ProductAgent)myAgent).location);
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                System.out.println("BH1 : Step 1 - Pesquisa na DF");
                myAgent.addBehaviour(new BH2(myAgent, msg, step));
                go_search = false;
            }
        }

    @Override
    public boolean done() {
        if(((ProductAgent)myAgent).go_next_step){
            //dizer que passou para o próximo passo
            ((ProductAgent)myAgent).current_state++;


            // se chegou ao fim, matar o agente
            if((((ProductAgent)myAgent).executionPlan.size()<= ((ProductAgent)myAgent).current_state)){
                myAgent.doDelete();
            }

            //clear flag
            ((ProductAgent) myAgent).go_next_step = false;

            //idk se é só isto ou se é assim... maybe?
            this.myAgent.addBehaviour(new BH1(this.myAgent, ((ProductAgent)myAgent).executionPlan.get( ((ProductAgent)myAgent).current_state)));
            return true;
        }
        if(((ProductAgent)myAgent).go_prev_step){
            //dizer que voltou atrás
            ((ProductAgent)myAgent).current_state--;

            //clear flag
            ((ProductAgent) myAgent).go_prev_step = false;

            //idk se é só isto ou se é assim... maybe?
            this.myAgent.addBehaviour(new BH1(this.myAgent, ((ProductAgent)myAgent).executionPlan.get( ((ProductAgent)myAgent).current_state)));
            return true;
        }
        
        return false;
    }
}
