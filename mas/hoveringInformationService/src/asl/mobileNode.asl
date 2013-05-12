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
		!start.
		

-!init : ~inited
	<- 	!init.
		
+!start : configured
	<- 	!!discoverNeighbour;
//		!!receiveMessage;
		.

+message(Sender, Message)
	<- println("Received: ", message, " from ", sender).

+new_neighbour(ID)
	<- println("New neighbour: ", ID).
	
+neighbour_gone(ID)
	<- println("Neighbour gone: ", ID).

+!discoverNeighbour
	<-	?artifacts(resource, MResID);
		discoverNeighbour(_) [artifact_id(MResID)];
		.wait(5000);
		!discoverNeighbour;
		.
		
+?inquire(Range, Storage, OccStorage) [source(self)] : range(DR) & storage(DS)
	<- Range = DR; Storage = DS; OccStorage = 0.