// Agent mobileNode in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

+!init : ~inited & worldWsp(WspName) & node_id(NodeID) &
		 range(DR) & storage(DS) & envArt(EAName) & ui_name(AUName)
	<-	-~inited;
		.my_name(Name);
		+id(NodeID);
		joinWorkspace(WspName, WspId);
		cartago.set_current_wsp(WspId);
		+workspace(world, WspId);
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(environment, EnvArtID);
		
		makeArtifact(AUName, "it.unibo.sisma.hi.mas.hs.MobileUIArtifact",[], UResID);
		+artifacts(ui, UResID);
		.concat(AUName, "_interface", AUIfName);
		
		.concat("NodeWorkspace_", Name, WNName);
		createWorkspace(WNName);
		joinWorkspace(WNName, WNid);
		+workspace(node, WNid);
		
		makeArtifact("MobileResource", "it.unibo.sisma.hi.mas.hs.MobileResourceArtifact",[NodeID, DR, DS],MResID);
		+artifacts(resource, MResID);
		focus(MResID);
		linkArtifacts(MResID, "env-link", EnvArtID);
		makeArtifact(AUIfName, "it.unibo.sisma.hi.mas.hs.MobileUIInterfaceArtifact",[], UIfResID);
		+artifacts(ui_if, UIfResID);
		focus(UIfResID);
		
		// Linking UIInterface --> UI
		linkArtifacts(UIfResID, "to-ui", UResID);
		
		// Linking UIInterface <-- UI
		cartago.set_current_wsp(WspId);
		linkArtifacts(UResID, "to-device", UIfResID);
		
		.concat("RangeWorkspace_", Name, WRName);
		createWorkspace(WRName);
		joinWorkspace(WRName, WRid);
		+workspace(range, WRid);
		
		cartago.set_current_wsp(WNid);
		-~configured;
		+configured;
		.
		

-!init : ~inited
	<- 	.wait(500);
		!init.

+start_command <- !start.
+stop_command <- !stop.		

+!start : ~configured | not configured
	<- 	.wait(1000);
		!start;
		.

+!start : configured
	<- 	!!discoverNeighbour;
		!!receiveMessage;
		.

+!stop : configured
	<-	+stop_receiving;
		+stop_discovering;
		.

+message(Receiver, Sender, Message)
	<- println("Received: ", Message, " from ", Sender, " for ", Receiver).

//+message("mobile", Sender, canICome(Size))
//	<- 	allocateData(Sender, Size, Res);
//				
//	.

+new_neighbour(ID)
	<- 	println("New neighbour: ", ID);
		sendMessage(ID, "mobile", "Hi!!")
		.
	
+neighbour_gone(ID)
	<- println("Neighbour gone: ", ID).

+!receiveMessage : stop_receiving.
+!discoverNeighbour : stop_discovering.

+!receiveMessage : not stop_receiving
	<- 	?artifacts(resource, MResID);
		receiveMessage("mobile", _, _, _) [artifact_id(MResID)];
		.wait(500);
		!receiveMessage;
		.

+!discoverNeighbour : not stop_discovering
	<-	?artifacts(resource, MResID);
		discoverNeighbour(_) [artifact_id(MResID)];
		.wait(1000);
		!discoverNeighbour;
		.
		
+?inquire(Range, Storage, OccStorage) [source(self)] : range(DR) & storage(DS)
	<- Range = DR; Storage = DS; OccStorage = 0.