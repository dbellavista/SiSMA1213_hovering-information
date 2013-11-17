// Agent mobileNode in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

+!init : ~inited & initiator(InitName) & worldWsp(WspName) & node_id(NodeID) &
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
		.send(InitName, tell, device_done);
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
	<- 	+pieces([]);
		!!discoverNeighbour;
		!!receiveMessage;
		.

+!stop : configured
	<-	+stop_receiving;
		+stop_discovering;
		.

@allocInit[atomic] +!manage_message(Sender, SenderName, ["init_dissemination", HoveringName, DS])
	<- 	allocateData(HoveringName, DS, Res);
		if(.string(HoveringName)) {
			.term2string(HTerm, HoveringName);
		} else {
			HTerm = HoveringName;
		}
		if(Res) {
			?pieces(L);

			-+pieces([HTerm | L]);
			.send(HTerm, achieve, start);
		} else {
			.send(HTerm, tell, sorry_after_init);
			 // ?node_id(MyNodeID);
			// sendMessage(MyNodeID, HoveringName, [sorry_after_init]);
		}
		.

@askSpace[atomic] +!manage_message(Sender, SenderName, ["there_is_space", DS])
	<-	?free_space(FS);
		.my_name(Name);
		if(FS >= DS) {
			sendMessage(Name, Sender, SenderName, [reply_ok_space], _);
		} else {
			sendMessage(Name, Sender, SenderName, [reply_no_space], _);
		}.

+!manage_message(Sender, L)
	<- 	println("UKN received: ", L, " from ", Sender);
		.
+new_neighbour(ID).
	//<- 	//println("New neighbour: ", ID);
		//sendMessage(ID, "mobile", "Hi!!")
		//.
	
+neighbour_gone(ID).
	//<- println("Neighbour gone: ", ID).

+performing_arakiri [source(Name)] // TODO: deallocate hovering. Delete info about it
	<- 	!removeHovering(Name).
	
@remHovering[atomic] +!removeHovering(Name)
	<-	removeData(Name);
		?pieces(OldList);
		.delete(Name, OldList, List);
		-pieces(_);
		+pieces(List);
		.

+!receiveMessage : stop_receiving.
+!discoverNeighbour : stop_discovering.

+!receiveMessage : not stop_receiving
	<- 	?artifacts(resource, MResID);
		receiveMessage("mobile", Res, Sender, SenderName, Message) [artifact_id(MResID)];
		if(Res) {
			!manage_message(Sender, SenderName, Message);	
		}
		.wait(500);
		!!receiveMessage;
		.

+!discoverNeighbour : not stop_discovering
	<-	?artifacts(resource, MResID);
		discoverNeighbour(_) [artifact_id(MResID)];
		.wait(1000);
		!!discoverNeighbour;
		.

+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & pieces(Pieces)
	<-  OccStorage = (Storage - FreeSpace); PList = Pieces.

+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & not pieces(Pieces)
	<-  .wait(200);
		?inquire(Range, Storage, OccStorage, PList).