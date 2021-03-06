import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import jade.core.*;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Collection;

public class UserManagerAgent extends Agent{
	
	private Vector<Movement> moves;
	private int movementQuantums=0;
	//where the user is for the moment
	//used for heartbeats
	private Vector<Movement> currentStatus;
	
	protected void setup() {

		currentStatus=new Vector<Movement>();
		System.out.println("UserManagerAgent :	" + getAID().getName()
				+ " is ready.");
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			String inputFile = (String) args[0];
			System.out.println("Trying to parse " + inputFile);
			try 
			{
				parseUserRoutes(inputFile);
			} catch (FileNotFoundException e) 
			{	
				e.printStackTrace();
			}
			
			//User movement behaviour
			addBehaviour(new TickerBehaviour(this, 2000) {
				
				protected void onTick() {
					movementQuantums++;
					System.out.println("UserManagerAgent: Quantum " + movementQuantums);

					while(true)
					{
						Movement m=moves.firstElement();
						if(m.Quantum==movementQuantums)
						{
							int index=search(m.User,currentStatus);
							if(index==-1)
							{
								//new user
								currentStatus.add(m);
							}
							else 
							{
								//user has moved
								currentStatus.elementAt(index).Cell=m.Cell;
								currentStatus.elementAt(index).Quantum=m.Quantum;
							}
							

						     
						     moves.remove(0);
						}
						
						else break;
					}
					
					// System.out.println("Found movement:"+m.Cell+" "+m.User +" at quantum:"+ m.Quantum);
				    for(Movement mov:currentStatus)
				    {
					 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				     msg.setContent(mov.User);
				     msg.addReceiver( new AID( mov.Cell, AID.ISLOCALNAME) );
				     send(msg);
					}
//					DFAgentDescription template = new DFAgentDescription();
//					ServiceDescription sd = new ServiceDescription();
//					sd.setName("C1");
//					template.addServices(sd);
//					try {
//						DFAgentDescription[] result = DFService.search(ellAgent,
//								template);
//						System.out
//								.println("Found the following cell agents:");
//						AID[] cellAgents = new AID[result.length];
//
//						for (int i = 0; i < result.length; ++i) {
//							cellAgents[i] = result[i].getName();
//							System.out.println(cellAgents[i].getName());
//						}
//					} catch (FIPAException fe) {
//						fe.printStackTrace();
//					}
					

					
					
				}
			});
			
		} else {
			System.out.println("No input file specified");
			doDelete();
		}
	}
	
	//Method to parse the routes of users
	void parseUserRoutes(String inputFile) throws FileNotFoundException
	{
		Scanner sc = new Scanner(new File(inputFile));
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
		  lines.add(sc.nextLine());
		}
		moves=new Vector<Movement>();
		String[] arr = lines.toArray(new String[0]);
		for(String a:arr)
		{
			//System.out.println(a);
			String[] splitted = a.split(" ");
			int its=(splitted.length-1)/2;
			for(int i=0;i!=its;i++)
			{
				Movement m=new Movement();
				m.User=splitted[0];
				m.Quantum=Integer.parseInt(splitted[(i+1)*2]);
				m.Cell=splitted[(i+1)*2-1];
				moves.add(m);		
			}
		}
        Comparator<Movement> QUANTUM = new Comparator<Movement>() {
            public int compare(Movement m1, Movement m2) {
                return m1.Quantum - m2.Quantum;
            }
        };
        Collections.sort(moves, QUANTUM);
	}

	
	public int search(String user, Vector<Movement>cm){  
	    int place = -1;  
	    boolean found = false;  
	    for ( Iterator<Movement> iter = cm.iterator(); iter.hasNext() && found == false; ) {  
	        place++;
	    	Movement temp = (Movement) iter.next();  
	        if(temp.User.equals(user))found=true;
	    }
	if(found==true)    
		return place;
	else
		return -1;
	}
}
