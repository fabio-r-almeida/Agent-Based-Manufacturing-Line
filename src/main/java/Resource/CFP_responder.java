package Resource;

import Product.ProductAgent;
import Utilities.Constants;
import Resource.ResourceAgent;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class CFP_responder extends ContractNetResponder {
    private String location;

    public CFP_responder(Agent a, MessageTemplate mt, String locationRA) {
        super(a, mt);
        this.location = locationRA;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        System.out.println("RA_CFP : Step 2 - Recursos recebe pedido e negoceia tem um " + ((ResourceAgent) myAgent).current_product);

        ACLMessage msg = cfp.createReply();

        msg.setContent(Double.toString(Math.random()));
        msg.setOntology(Constants.ONTOLOGY_NEGOTIATE_RESOURCE);


        //a Source não tem falta de capacidade
        if (myAgent.getLocalName().equalsIgnoreCase("Source")) {
            msg.setPerformative(ACLMessage.PROPOSE);
            return msg;
        }
        System.out.println(cfp.getContent());

        //se estiver livre ou se receber um pedido do produto que tem ou se for alvo de swap, faz a proposta
        if (
                ((ResourceAgent) myAgent).current_product.equalsIgnoreCase("None") ||
                ((ResourceAgent) myAgent).current_product.equalsIgnoreCase(cfp.getSender().getLocalName()) ||
                ((ResourceAgent) myAgent).swap.equalsIgnoreCase(cfp.getSender().getLocalName())
        ) {
            msg.setPerformative(ACLMessage.PROPOSE);
        } else {
            //ao recusar, envia o produto que tem
            msg.setContent(((ResourceAgent) myAgent).current_product);
            msg.setPerformative(ACLMessage.REFUSE);
        }
        return msg;
    }

   /* protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        //se a sua proposta for rejeitada, então está vazio
        ((ResourceAgent) myAgent).current_product = backup;
    }*/

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println("RA_CFP : step 4 - Envia a aprovação da aceitação e localizacao da RA");

        //ao ser aceite o seu produto é o novo produto
        ((ResourceAgent) myAgent).current_product = cfp.getSender().getLocalName();
        ACLMessage msg = cfp.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        msg.setContent(location);
        return msg;
    }


}
