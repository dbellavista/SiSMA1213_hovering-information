// Agent mobileNode in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */
/****************************************************************************************
 * * INITIALIZATION, STARTING AND STOPPING PLANS
 ****************************************************************************************/
 
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

+start_command <- !start; -start_comand [source(_)];.
+stop_command <- !stop; -stop_command [source(_)];.

+!start : ~configured | not configured
	<- 	.wait(1000);
		!start;
		.

+!start : configured
	<- 	+pieces([]);
		!!discoverNeighbors;
		!!receiveMessage;
		!!cleanup;
		!!startObtainingPosition;
		.

+!stop : configured
	<-	!stopObtainingPosition;	
		+stop_receiving;
		+stop_discovering;
		.
		
/****************************************************************************************
 * * POSITION MANAGEMENT
 ****************************************************************************************/
+!startObtainingPosition
	<-	?artifacts(resource, MResID);
		startObtainingPosition [artifact_id(MResID)];
		.

+!stopObtainingPosition
	<-	?artifacts(resource, MResID);
		stopObtainingPosition [artifact_id(MResID)];
		.

/****************************************************************************************
 * * PIECES IN-OUT MANAGEMENT
 ****************************************************************************************/

@allocInit[atomic] +!manage_message(Sender, SenderName, ["init_dissemination", HoveringName, DS])
	<- 	allocateData(HoveringName, DS, Res);
		if(Res) {
			.send(HoveringName, askOne, hover_name(_), hover_name(HoverName));
			.send(HoveringName, askOne, anchor(_, _, _), anchor(AX, AY, Area));
			
			!newPiece(HoveringName, HoverName, AX, AY, Area);
			.send(HoveringName, achieve, start);
		} else {
			.send(HoveringName, tell, sorry_after_init);
			 // ?node_id(MyNodeID);
			// sendMessage(MyNodeID, HoveringName, [sorry_after_init]);
		}
		.

@remHovering[atomic] +!removeHovering(Name)
	<-	removeData(Name);
		?pieces(OldList);
		.delete([Name, _, _], OldList, List);
		-+pieces(List);
		!send_info_to_all;
		.

+!newPiece(AgentName, HoverName, AX, AY, Area)
	<-	?pieces(L);
		if(.string(AgentName)) {
			.term2string(HTerm, AgentName);
		} else {
			HTerm = AgentName;
		}
		-+pieces([[HTerm, HoverName, anchor(AX, AY, Area)] | L]);
		
		//.send(HTerm, askOne, data(_), data(Data));
		//editData(HTerm, Data);
		!send_info_to_all;
		.

+performing_arakiri(Name)
	<- 	!manage_arakiri(Name);
		.
		
+!manage_arakiri(Name)
	<- 	!removeHovering(Name);
		+managed_arakiri(Name);
		-performing_arakiri(Name) [source(_)].

//+position([X, Y]) <- .print("Pos: ", X, Y).

/****************************************************************************************
 * * PRE-PROTOCOL FOR LANDING OR CLONING 
 ****************************************************************************************/
@askSpace[atomic] +!manage_message(Sender, SenderName, ["there_is_space", DS, HName])
	<-	?free_space(FS);
		.my_name(Name);
		if(FS >= DS) {
			?position([X, Y]);
			?pieces(PL);
			if(.member([_, HName, _], PL)) {
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
		if(.member([HTerm, _, _], L)) {
			.fail
		}
		removeData(SenderName).

-!abort_landing(Sender, SenderName).

@abortLanding[atomic] +!manage_message(Sender, SenderName, ["landing_aborted"])
	<-	removeData(SenderName);
		.

/****************************************************************************************
 * * LANDING PROCEDURE
 ****************************************************************************************/
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
			
			.send(SenderName, askOne, hover_name(_), hover_name(HoverName));
			.send(SenderName, askOne, anchor(_, _, _), anchor(AX, AY, Area));
			
			!newPiece(SenderName, HoverName, AX, AY, Area);
			
			.send(SenderName, tell, you_can_resume(HostID, HostName, MobileWsp));
		} else {
			// Invalid request. Sorry for the piece.
			.send(SenderName, tell, sorry_land_failed(HostID, HostName, MobileWsp));
		}
		.
		
/****************************************************************************************
 * * CLONE PROCEDURE
 ****************************************************************************************/
/**
 * Cloning procedure: the agent must be created
 */
@clone[atomic] +!manage_message(Sender, SenderName, ["clone", AX, AY, Area, Size, HoverName, AgentName, Data])
	<-	// Check if the space is allocated
		getData(SenderName, _, Res);
		if(Res) {
			// Real world: unpack the agent and start it!
			.my_name(HostName);
			?node_id(HostID);
			?workspace(world, WorldWsp, _);
			
			.create_agent(AgentName, "hovering.asl", [agentArchClass("c4jason.CAgentArch")]);

			.send(AgentName, tell, [worldWsp(WorldWsp), hover_name(HoverName), 
									anchor(AX, AY, Area), size(Size), host(HostID, HostName), data(Data)]);
			
			!newPiece(AgentName, HoverName, AX, AY, Area);
			.send(AgentName, achieve, start);
		} else {
			// Invalid request. Sorry for the piece.
			.send(SenderName, tell, sorry_clone_failed(HostID, HostName, MobileWsp));
		}
		.


/****************************************************************************************
 * * NEIGHBOR MANAGEMENT
 ****************************************************************************************/
+new_neighbor(ID) <-
		!send_information(ID); 
		-new_neighbor(ID) [source(_)];.

+neighbor_gone(ID) <- 
		-database(ID, _);
		-neighbor_gone(ID) [source(_)].

+!discoverNeighbors : stop_discovering.

+!discoverNeighbors : not stop_discovering
	<-	?artifacts(resource, MResID);
		discoverNeighbors(_) [artifact_id(MResID)];
		.wait(500);
		!!discoverNeighbors;
		.

/****************************************************************************************
 * * DATABASE DISTRIBUTION
 ****************************************************************************************/

+!send_info_to_all
	<-	?neighbors(NList);
		?pieces(PList);
		!prepare_info(PList, ResList);
		!update_my_info(ResList);
		!send_info_to_all(NList, ResList);
		.

+!send_info_to_all([], _).
+!send_info_to_all([ID | Tail], ResList)
	<-	!send_information(ID, ResList);
		!send_info_to_all(Tail, ResList);
		.

+!send_information(ID)
	<- 	?pieces(List);
		!prepare_info(List, ResList);
		!send_information(ID, ResList);
	.

@send_info[atomic] +!send_information(ID, List)
	<-	?id(NodeID);
		if(.ground(Res)) {
			sendMessage(NodeID, ID, "mobile", [my_info, List], Res2);	
		} else {
			sendMessage(NodeID, ID, "mobile", [my_info, List], Res);
		}
		.

+!prepare_info([], []).
+!prepare_info([[_, HName, anchor(AX, AY, Area)] | T1], [[HName, AX, AY, Area] | T2]) <- !prepare_info(T1, T2).


@my_info[atomic] +!manage_message(Sender, SenderName, ["my_info", List])
	<-	!add_db(Sender, List, DB);
		-database(Sender, _);
		+database(Sender, DB);
	.

+!update_my_info(ResList)
	<-	?node_id(ID);
		!add_db(ID, ResList, DB);
		-database(ID, _);
		+database(ID, DB);
	.

+!add_db(Sender, [], []).
+!add_db(Sender, [[HName, AX, AY, Area] | T1], [[HName, ImIn] | T2])
	<-  ?artifacts(resource, MResID);
		?position([X, Y]);
		imIn(X, Y, AX, AY, Area, ImIn) [artifact_id(MResID)];
		!add_db(Sender, T1, T2);
		.

/****************************************************************************************
 * * DATABASE ELABORATION
 ****************************************************************************************/
//+position([X,Y]) <- .print(X, Y).
//+database(_, []).
//+database(ID, DB) <- !elaborate_db(ID, DB).

+!elaborate_db(ID, []).
+!elaborate_db(ID, [[_, False] | T]) <- !elaborate_db(ID, T).

// Mien DB
+!elaborate_db(ID, [[HName, True] | T]) : node_id(ID)
	<- 	?pieces(L);
		if(.member([Hover, HName, _], L)) {
			//getData(Hover, Data, Res);
			//if(Res) {
				.print("I HAVE THE DATA: ");
			//}
		}
		!elaborate_db(ID, T);
		.

+!elaborate_db(ID, [[HName, True] | T]) <- !elaborate_db(ID, T);.

/****************************************************************************************
 * * RECEIVE MESSAGE LOOP
 ****************************************************************************************/
+!receiveMessage : stop_receiving.

+!receiveMessage : not stop_receiving
	<- 	?artifacts(resource, MResID);
		receiveMessage("mobile", Res, Sender, SenderName, Message) [artifact_id(MResID)];
		if(Res) {
			!manage_message(Sender, SenderName, Message);	
		}
		!!receiveMessage;
		.

/****************************************************************************************
 * * CLEANUP PLANS (for removing duplicate pieces)
 ****************************************************************************************/

+!cleanup
	<-	.wait(500);
		?pieces(L);
		for(.member([PName, HName, _], L)) {
			for(.member([PName2, HName, _], L)) {
				if(not (PName2  == PName)) {
					// Duplicate!
					.send(PName2, tell, please_die);
					.fail;
				}
			}		
		}
		!!cleanup;
		.
-!cleanup <- !cleanup;.

/****************************************************************************************
 * * INQUISITION PLANS
 ****************************************************************************************/
 
+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & pieces(Pieces)
	<-  OccStorage = (Storage - FreeSpace); PList = Pieces.

+?inquire(Range, Storage, OccStorage, PList) [source(self)] : range(Range) & storage(Storage) & free_space(FreeSpace) & not pieces(Pieces)
	<-  .wait(200);
		?inquire(Range, Storage, OccStorage, PList).
		

/****************************************************************************************
 * * FALLBACK PLANS
 ****************************************************************************************/
 
+!manage_message(Sender, SenderName, L)
	<- 	.print("UKN received: ", L, " from ", Sender, " ", SenderName);
		.