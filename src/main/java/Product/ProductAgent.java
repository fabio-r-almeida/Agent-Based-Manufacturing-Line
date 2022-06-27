package Product;

import Utilities.Constants;
import Utilities.DFInteraction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class ProductAgent extends Agent {
    public boolean first_time = true;
    public boolean go_prev_step = false;
    public boolean go_next_step = false;
    public AID last_resource = null;
    String id;
    ArrayList<String> executionPlan = new ArrayList<>();
    public int current_state = 0;
    String location;


    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        this.id = (String) args[0];
        this.executionPlan = this.getExecutionList((String) args[1]);
        this.location = "Source";

        System.out.println("Product launched: " + this.id + " Requires: " + executionPlan + " location: " + location);
      
      //i changed this
        //SequentialBehaviour sb = new SequentialBehaviour();
        // for(String step: executionPlan){
        //     sb.addSubBehaviour(new BH1(this,step));
        // }

        // to this
        this.addBehaviour(new BH1(this,executionPlan.get(current_state)));
    }



    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    private ArrayList<String> getExecutionList(String productType) {
        switch (productType) {
            case "A":
                return Utilities.Constants.PROD_A;
            case "B":
                return Utilities.Constants.PROD_B;
            case "C":
                return Utilities.Constants.PROD_C;
        }
        return null;
    }
}
