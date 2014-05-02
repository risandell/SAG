import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class CellAgent extends Agent{
	
	private String cellName;
	private String[] neighbourCellsStrings;
	private Vector<String> users;
	private Vector<newUser> newUsers;
	//private AID[] neighbourCellsAIDs;
	
	private class newUserBehaviour  extends Behaviour
	{
		
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) 
			{
				String content=msg.getContent();
				//System.out.println(cellName+":Obtained message: "+ msg.toString());
				System.out.println(cellName+":Obtained message: "+ content);
//				if()
				{
					
				}
//				else if()
//				{
					
//				}
			}
			else
				block();
		}
		
		public boolean done() {
			return false;
		}
	};
	//Method to parse the routes of users
	
	protected void setup() {
//		System.out.println("CellAgent :	" + getAID().getName()
//				+ " is ready");
		
		users=new Vector<String>();
		newUsers=new Vector<newUser>();
		cellName=getAID().getLocalName();
		System.out.println("CellAgent with local name:" + cellName + " is ready");
		System.out.println("Working directory: "+ System.getProperty("user.dir"));
		
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			String inputFile = (String) args[0];
			//System.out.println("Trying to parse " + inputFile);
			try
			{
				parseNeighbours(inputFile);
			}
			catch(FileNotFoundException Exc)
			{
				Exc.printStackTrace();
				//System.out.println("No such file: " + inputFile);
			}
		
			addBehaviour(new newUserBehaviour());
			
		} else {
			System.out.println(cellName+ ": No input file specified");
			doDelete();
		}
	}
	
	void parseNeighbours(String inputFile) throws FileNotFoundException
	{
		Scanner sc = new Scanner(new File(inputFile));
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
		  lines.add(sc.nextLine());
		}

		String[] arr = lines.toArray(new String[0]);
		for(String a:arr)
		{
			//System.out.println(a);
			String[] splitted = a.split(" ");
			//System.out.println(splitted[0]);
			
			if(splitted[0].equals(cellName))
			{
				//the neighbours list for a cell was found
				//copying neighbour list
				neighbourCellsStrings=new String[splitted.length-1];
//				
//				for(int i=1; i!=splitted.length;i++)
//				{
//					neighbourCellsStrings[i-1]=splitted[i];
//				}
				System.arraycopy(splitted, 1, neighbourCellsStrings, 0, splitted.length-1);
				
				//neighbourCellsStrings=cellName
				break;
			}
		}
		for(String a:neighbourCellsStrings)
		{
			System.out.println("Neighbours of cell " + cellName+ ": "+a);
		}
	}

}
