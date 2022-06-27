package Product;

import Utilities.Constants;
import Utilities.DFInteraction;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class BH2 extends ContractNetInitiator {
    String location;
    String step;
    ACLMessage backup_msg;

    public BH2(Agent a, ACLMessage cfp, String s) {
        super(a, cfp);
        this.location = ((ProductAgent) myAgent).location;
        this.step = s;
        this.backup_msg = cfp;
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        DFInteraction dfinteraction = new DFInteraction();
        ACLMessage msg_request = new ACLMessage(ACLMessage.REQUEST);
        ACLMessage msg_do_skill= new ACLMessage(ACLMessage.REQUEST);
        ACLMessage msg_free_resource= new ACLMessage(ACLMessage.REQUEST);
        AID current_resource = new AID(inform.getSender().getLocalName(),false);

        System.out.println("BH2 : step 5 - Recebe a localização do Recurso ");

        System.out.println("------- ORIGEM : " + location);
        System.out.println("------- DESTINO : " + inform.getContent());

        //se não está no primeiro passo e se for para uma estação diferente da que está,
        // vai libertar a estação em que está
        if(
                ((ProductAgent) myAgent).current_state > 0 &&
                !((ProductAgent)myAgent).last_resource.equals(current_resource)
        ) {
            msg_free_resource.setPerformative(ACLMessage.REQUEST);
            msg_free_resource.setContent("free");
            msg_free_resource.addReceiver(((ProductAgent)myAgent).last_resource);
            myAgent.addBehaviour(new BH3(myAgent, msg_free_resource));
        }

        ((ProductAgent)myAgent).last_resource = current_resource;

        msg_do_skill.setOntology(Constants.ONTOLOGY_EXECUTE_SKILL);
        msg_do_skill.setContent(step);
        msg_do_skill.setPerformative(ACLMessage.REQUEST);
        msg_do_skill.addReceiver(current_resource);

        if (inform.getContent().equalsIgnoreCase(location)) {
            System.out.println("BH2   - step 5.1 - Ja esta na localização, execute skill");
            //All gucci, execute the skill
            myAgent.addBehaviour(new BH3(myAgent, msg_do_skill));
        }

        else {

            System.out.println("BH2 : - step 5.2 - Pesquisa na Df por transporte");
            try {
                String message_content = location + Constants.TOKEN + inform.getContent();
                msg_request.clearAllReceiver();

                for (DFAgentDescription result : dfinteraction.SearchInDFByName(Constants.SK_MOVE, this.myAgent)) {
                    msg_request.addReceiver(new AID(result.getName().getLocalName(), false));
                }
                msg_request.setContent(message_content);
                msg_request.setConversationId(((ProductAgent)myAgent).id);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
            myAgent.addBehaviour(new BH4(myAgent, msg_request, msg_do_skill));
        }

    }


    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        System.out.println("BH2 : step 3 - Recebe os Proposes, aceita 1, recusa os resto");
        ACLMessage msg_do_swap= new ACLMessage(ACLMessage.REQUEST);

        Vector proposals = new Vector<>();
        Vector swap_targets= new Vector<>();

        for (Object o : responses) {
            if (((ACLMessage)o).getPerformative() == ACLMessage.PROPOSE) {
                proposals.add(o);
            }
            else{
                swap_targets.add(((ACLMessage)o).getContent());
            }
        }
        if(proposals.size()>0) {
            ACLMessage auxBest = (ACLMessage) proposals.get(0);
            for (Object o : proposals) {
                //converter
                ACLMessage auxMsg = (ACLMessage) o;

                //só vale a pena se são diferentes
                if (auxMsg.getContent().compareTo(auxBest.getContent()) != 0) {

                    if (auxMsg.getContent().compareTo(auxBest.getContent()) < 0) {
                        // rejeitar o antigo melhor, auxBest
                        ACLMessage reject = auxBest.createReply();
                        reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.add(reject);
                        auxBest = auxMsg;
                    } else {
                        //rejeitar o auxMsg pq é pior
                        ACLMessage reject = auxMsg.createReply();
                        reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.add(reject);
                    }
                }
            }

            ACLMessage reply = auxBest.createReply();
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            acceptances.add(reply);
        }
        else{
            System.out.println("BH2 : step 3 - Loop porque não recebeu propostas");
            if(((ProductAgent) myAgent).last_resource != null){
                System.out.println("BH2 : step swap vai pedir para fazer a swap");
                msg_do_swap.setPerformative(ACLMessage.REQUEST);
                msg_do_swap.setContent("swap" + Constants.TOKEN + swap_targets.get(0));
                msg_do_swap.addReceiver(((ProductAgent)myAgent).last_resource);
                myAgent.addBehaviour(new BH3(myAgent, msg_do_swap));
            }

            try {
                //sleep numero aleatoreio entre 0.5 e 2 segundos
                TimeUnit.MILLISECONDS.sleep((int) (Math.random()*1500 + 500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.myAgent.addBehaviour(new BH2(myAgent, backup_msg, step));
        }

    }

}
