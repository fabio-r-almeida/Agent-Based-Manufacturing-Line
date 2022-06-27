package Resource;

import Utilities.Constants;
import Utilities.DFInteraction;
import jade.core.Agent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import Libraries.IResource;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class ResourceAgent extends Agent {

    public  String current_product = "None";
    public String  swap = "None";

    String id;
    IResource myLib;
    String description;
    String[] associatedSkills;
    String location;

    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        this.id = (String) args[0];
        this.description = (String) args[1];

        //Load hw lib
        try {
            String className = "Libraries." + (String) args[2];
            Class cls = Class.forName(className);
            Object instance;
            instance = cls.newInstance();
            myLib = (IResource) instance;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ResourceAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.location = (String) args[3];

        myLib.init(this);

        this.associatedSkills = myLib.getSkills();

        System.out.println("Resource Deployed: " + this.id + " Executes: " + Arrays.toString(associatedSkills));

        //TO DO: Register in DF with the corresponding skills as services
        DFInteraction dfinteraction = new DFInteraction();
        try {
            dfinteraction.RegisterInDF(this, this.associatedSkills, Constants.DFSERVICE_RESOURCE);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        // TO DO: Add responder behaviour/s

        this.addBehaviour(new CFP_responder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP), location));
        this.addBehaviour(new REQUEST_responder(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));




    }


    @Override
    protected void takeDown() {
        super.takeDown();
    }
}



