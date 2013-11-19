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
		+workspace(world, WspName, WspId);
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(environment, EnvArtID);
		
		makeArtifact(AUName, "it.unibo.sisma.hi.mas.hs.MobileUIArtifact",[], UResID);
		+artifacts(ui, UResID);
		.concat(AUName, "_interface", AUIfName);
		
		.concat("NodeWorkspace_", Name, WNName);
		createWorkspace(WNName);
		joinWorkspace(WNName, WNid);
		+workspace(node, WNName, WNid);
		
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
		+workspace(range, WRName, WRid);
		
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
		!!cleanup;
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
			.send(HoveringName, askOne, hover_name(X), hover_name(HoverName));
			-+pieces([[HTerm, HoverName] | L]);
			.send(HTerm, achieve, start);
		} else {
			.send(HTerm, tell, sorry_after_init);
			 // ?node_id(MyNodeID);
			// sendMessage(MyNodeID, HoveringName, [sorry_after_init]);
		}
		.

@askSpace[atomic] +!manage_message(Sender, SenderName, ["there_is_space", DS, HName])
	<-	?free_space(FS);
		.my_name(Name);
		if(FS >= DS) {
			obtainPosition(X, Y);
			?pieces(PL);
			if(.member([_, HName], PL)) {
				sendMessage(Name, Sender, SenderName, [reply_ok_space, X, Y, true], _);	
			} else {
				sendMessage(Name, Sender, SenderName, [reply_ok_space, X, Y, false], _);
			}				
		} else {
			sendMessage(Name, Sender, SenderName, [reply_no_space], _);
		}.

@permLanding[atomic] +!manage_message(Sender, SenderName, ["permission_to_land", DS])
	<-	allocateData(SenderName, DS, Res);
		.my_name(Name);
		if(Res) {
			sendMessage(Name, Sender, SenderName, [permission_granted], SendRes);
			if(not SendRes) {
				removeData(SenderName);
			} else {
				.at("now +20 seconds", {+!abort_landing(Sender, SenderName)})
			}
		} else {
			sendMessage(Name, Sender, SenderName, [permission_denied], _);
		}.

@abortLandingTimeour[atomic] +!abort_landing(Sender, SenderName)
	<- 	?pieces(L);
		if(.string(SenderName)) {
			.term2string(HTerm, SenderName);
		} else {
			HTerm = SenderName;
		}
		if(.member([HTerm, _], L)) {
			.fail
		}
		removeData(SenderName).

-!abort_landing(Sender, SenderName).

@abortLanding[atomic] +!manage_message(Sender, SenderName, ["landing_aborted"])
	<-	removeData(SenderName);
		.

/**
 * Landing procedure: the agent exists, it's to be awaken
 */
@land[atomic] +!manage_message(Sender, SenderName, ["land", PackedAgent])
	<-	// Check if the space is allocated
		getData(SenderName, _, Res);
		if(Res) {
			// Real world: unpack the agent and start it!
			.my_name(HostName);
			?node_id(HostID);
			?workspace(node, MobileWsp, _);
			?pieces(L);
			if(.string(SenderName)) {
				.term2string(HTerm, SenderName);
			} else {
				HTerm = SenderName;
			}
			.send(SenderName, askOne, hover_name(X), hover_name(HoverName));
			-+pieces([[HTerm, HoverName] | L]);
			.send(SenderName, tell, you_can_resume(HostID, HostName, MobileWsp));
		} else {
			// Invalid request. Sorry for the piece.
			.send(SenderName, tell, sorry_land_failed(HostID, HostName, MobileWsp));
		}
		.

/**
 * Cloning procedure: the agent must be created
 */
@clone[atomic] +!manage_message(Sender, SenderName, ["clone", AX, AY, Area, Size, HoverName, AgentName])
	<-	// Check if the space is allocated
		getData(SenderName, _, Res);
		if(Res) {
			// Real world: unpack the agent and start it!
			.my_name(HostName);
			?node_id(HostID);
			?workspace(world, WorldWsp, _);
			
			.create_agent(AgentName, "hovering.asl", [agentArchClass("c4jason.CAgentArch")]);

			.send(AgentName, tell, [worldWsp(WorldWsp), hover_name(HoverName), 
									anchor(AX, AY, Area), size(Size), host(HostID, HostName)]
									);
						
			?pieces(L);
			if(.string(AgentName)) {
				.term2string(HTerm, AgentName);
			} else {
				HTerm = AgentName;
			}
			-+pieces([[HTerm, HoverName] | L]);
			
			.send(HTerm, achieve, start);
		} else {
			// Invalid request. Sorry for the piece.
			.send(SenderName, tell, sorry_clone_failed(HostID, HostName, MobileWsp));
		}
		.

+!manage_message(Sender, SenderName, L)
	<- 	.print("UKN received: ", L, " from ", Sender, " ", SenderName);
		.

+new_neighbour(ID).
	//<- 	//println("New neighbour: ", ID);
		//sendMessage(ID, "mobile", "Hi!!")
		//.
	
+neighbour_gone(ID).
	//<- println("Neighbour gone: ", ID).

+performing_arakiri [source(Name)]
	<- 	!removeHovering(Name).
	
@remHovering[atomic] +!removeHovering(Name)
	<-	removeData(Name);
		?pieces(OldList);
		.delete([Name, _], OldList, List);
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
		!!receiveMessage;
		.

+!discoverNeighbour : not stop_discovering
	<-	?artifacts(resource, MResID);
		discoverNeighbour(_) [artifact_id(MResID)];
		.wait(1000);
		!!discoverNeighbour;
		.

+!cleanup
	<-	.wait(2000);
		?pieces(L);
		for(.member([PName, HName], L)) {
			for(.member([PName2, HName], L)) {
				if(not (PName2  == PName)) {
					// Duplicate!
					.send(PName2, tell, please_die);
				}
			}		
		}
		!!cleanup;
		.
-!cleanup <- !cleanup;.

+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & pieces(Pieces)
	<-  OccStorage = (Storage - FreeSpace); PList = Pieces.

+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & not pieces(Pieces)
	<-  .wait(200);
		?inquire(Range, Storage, OccStorage, PList).