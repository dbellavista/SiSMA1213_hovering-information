// Agent hoveringAgent in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */
/****************************************************************************************
 * * INITIALIZATION, STARTING AND STOPPING PLANS
 ****************************************************************************************/
+!init : ~inited & worldWsp(WspName) & anchor(X, Y, Area) & size(S) &
		hover_name(HoverName) & host(DeviceID, DeviceName)
	<-	-~inited;
		
		joinWorkspace(WspName, WspId);
		+workspace(world, WspId);
		cartago.set_current_wsp(WspId);
		.my_name(Name);
		.concat("HoveringArtifact_", Name, ArtName);
		makeArtifact(ArtName, "it.unibo.sisma.hi.mas.hs.HoveringArtifact",[Name, S],HArtID);
		focus(HArtID);
		+artifacts(hovering, HArtID);
		
		.double_infinite(INFINITE);
		
		+zone(ok, 0, Area);
		+zone(warning, Area, Area * 2);
		+zone(violent, Area * 2, Area * 3);	
		+zone(death_zone, Area * 3, INFINITE);
		
		+approach_level(0);
		+approach_variation(0);
		+num_neighbors(0);
		+current_zone(ok);

		+defcon(5, 1.0, 0.8);
		+defcon(4, 0.8, 0.68);
		+defcon(3, 0.68, 0.42);
		+defcon(2, 0.42, 0.35);
		+defcon(1, 0.35, 0.0);
		
		+cur_defcon(5, 0);
		
		.concat("NodeWorkspace_", DeviceName, WNName);
		joinWorkspace(WNName, WspNodeId);
		cartago.set_current_wsp(WspNodeId);
		+workspace(host, WspNodeId);
		lookupArtifact("MobileResource", HostResID);
		+artifacts(resource, HostResID);
		
		-~configured;
		+configured;
		.

-!init : ~inited
	<-  .wait(200);
		!init.

+!start : ~configured | not configured
	<- 	.wait(1000);
		!start;
		.

+!start : configured
	<- 		!!survive;
		!!receiveMessage;
		.

/**
 * Stops the normal plans, tells the host about the stopping and kills itself.
 */
+!stop <- +stop_surviving;
		+stop_receiving;
		?host(HostID, HostName);
		.send(HostName, tell, performing_arakiri);
		// ARAKIRI
		!arakiri;.

-!arakiri <- .my_name(Name); .kill_agent(Name).

/**
 * Sent by the mobile node if the initial dissemination is not feasible
 */ 
+sorry_after_init <-
			// Uff.. so much effort for nothing
			!stop.

+please_die <-
			// I have been deallocated :(
			!stop.

/****************************************************************************************
 * * INQUISITION PLANS
 ****************************************************************************************/

/**
 * Asked from the simulator Agent. Replies with the hover's name and the size.
 */
+?inquire(HoverName, Size) : size(S) & hover_name(HN)
	<-	HoverName = HN; Size = S.		

/****************************************************************************************
 * * BASIC PLANS: SURVIVE AND RECEIVE MESSAGES
 ****************************************************************************************/

/**
 * Survive plan.
 */
+!survive : not stop_surviving & anchor(AX, AY, Area) & not landing(_)
	<-	?artifacts(resource, MResID);
		obtainPosition(X, Y) [artifact_id(MResID)];
		!updateBelieves(X, Y);
		!decideDefcon;
		!doWhatIsNecessary;
		.wait(500);
		!!survive;
		.
+!survive : landing(_) <- .wait(500); !!survive.
+!survive : stop_surviving.

/**
 * Receive Message Plan
 */
+!receiveMessage : not stop_receiving & not landing(_)
	<- 	?artifacts(resource, MResID);
		.my_name(Name);
		receiveMessage(Name, Res, Sender, SenderName, Message) [artifact_id(MResID)];
		if(Res) {
			!manage_message(Sender, SenderName, Message);	
		}
		!!receiveMessage;
		.

+!receiveMessage : landing(_) <- .wait(500); !!receiveMessage.

+!receiveMessage : stop_receiving.

/****************************************************************************************
 * * UPDATE BELIEVES PLANS: gather and process data about position and speed
 ****************************************************************************************/
/**
 * Gather the positional data and process them, for instance by setting the new zone.
 */
@upBelieves[atomic] +!updateBelieves(PX, PY) : artifacts(hovering, HArtID) & anchor(AX, AY, Area)
	<-	computePositionalData(PX, PY, AX, AY, Speed, Distance, Direct, AnchorVect) [artifact_id(HArtID)];
		!getOld(Speed, Distance, Direct, AnchorVect, OldSpeed, OldDistance, OldDirect, OldAnchorVect);
		Direct = [DX, DY];
		AnchorVect = [AVX, AVY];
		// TODO: what to do?
		?approach_level(AL);
		-approach_level(_);
		!updateZone(Distance);
		NAL = (DX*AVX) + (DY*AVY);
		+approach_level(NAL);
		
		?artifacts(resource, MResID);
		discoverNeighbors(List) [artifact_id(MResID)];
		.length(List, NNeigh);
		
		-+num_neighbors(NNeigh);
		-+speed(Speed);
		-+distance(Distance);
		-+direction(Direct);
		-+anchor_vector(AnchorVect);
		.
/**
 * Unifies with the previous known data if possible, otherwise with the current data
 */
+!getOld(_, _, _, _, OldSpeed, OldDistance, OldDirect, OldAnchorVect) : speed(OldSpeed) & distance(OldDistance) &
															direction(OldDirect) & anchor_vector(OldAnchorVect).
+!getOld(Speed, Distance, Direct, AnchorVect, Speed, Distance, Direct, AnchorVect).

/**
 * Update the current zone, basing on the current distance from the Hovering Anchor.
 */
+!updateZone(Distance) : zone(Zone, Min, Max) & Distance >= Min & Distance < Max
	<- -current_zone(_); +current_zone(Zone).

/****************************************************************************************
 * * DECIDE DEFCON PLANS: from the new information, to decide the new defcon level 
 ****************************************************************************************/
// 
// The possibile behaviours can be:
//   - DEFCON 5: nessun bisogno di comunicare con il mondo esterno, se non per rispondere
//   - DEFCON 4: si inizia a controllare la lista di neighbor. Se sono pochi, contattarli
//				 ogni tanto per conoscere il loro stato.
//	 - DEFCON 3: controllo di tutti i neighbor e del loro stato.
//	 - DEFCON 2: Migrazione dell'hovering sul neighbor più vicino all'anchor.
//	 - DEFCON 1: Copia dell'hovering sui neighbor più vicini.

/**
 * Decides the defcon basing on a (TODO) linear combination between exponential functions.
 */
@defconDec[atomic] +!decideDefcon : approach_level(AL) & approach_variation(AV) & speed(SP) & current_zone(Zone) &
									 cur_defcon(CurDef, CurPoint) & defcon(CurDef, MinThr, MaxThr) &
									 distance(Dist) & zone(death_zone, Max_Dist, _) & num_neighbors(NumNeigh)
	<-	if(Zone == ok) {
			Mul = 1
		} else {
		if(Zone == warning) {
			Mul = 2
		} else {
		if(Zone == violent) {
			Mul = 3
		} else {
		if(Zone == death_zone) {
			Mul = 4
		} else {
			Mul = 0.0
		}
		}	
		}
		}
	
		// TODO: use the approach level to consider the direction
		?artifacts(hovering, HArtID);
		exp(DistancePoints, -Mul*(Dist / Max_Dist)) [artifact_id(HArtID)];
		exp(NeighborPoints_tmp, NumNeigh / 10) [artifact_id(HArtID)];
		NeighborPoints = NeighborPoints_tmp - 1;
		
		!setDefcon(0.5 * DistancePoints + 0.5 * NeighborPoints);
		.

/**
 * Set the defcon, basing on the points.
 * The defcon change is always a transition and can never skip a level.
 */
+!setDefcon(NewPoints) : cur_defcon(CurDef, CurPoint) & defcon(CurDef, MaxThr, MinThr)
	<-	-cur_defcon(_, _);
		if(NewPoints > MaxThr & CurDef < 5) {
			
			+cur_defcon(CurDef + 1, NewPoints)
		} else {
			if(NewPoints < MinThr & CurDef > 1) {
				+cur_defcon(CurDef - 1, NewPoints)		
			} else {
				+cur_defcon(CurDef, NewPoints)	
			}
		}
		.

/****************************************************************************************
 * * DO WHAT IS NECESSARY basing on the current defcon level! 
 ****************************************************************************************/

+!doWhatIsNecessary : current_zone(death_zone)
	<-	!stop
		.

+!doWhatIsNecessary : cur_defcon(5, CurPoints) & current_zone(Z) & distance(D) & zone(death_zone, Max_Dist, _)
	// Do nothing for now...
	.

+!doWhatIsNecessary : cur_defcon(4, CurPoints) & current_zone(Z) & distance(D) & zone(death_zone, Max_Dist, _)
	// Do nothing for now...
	.

+!doWhatIsNecessary : cur_defcon(3, CurPoints) & current_zone(Z) & distance(D) & zone(death_zone, Max_Dist, _)
	<-	!probe_neighbor
		.

+!doWhatIsNecessary : cur_defcon(2, CurPoints) & asked_landing(N, K)
	<-	-asked_landing(N, K);
		+asked_landing(N, K-1);
		.
+!doWhatIsNecessary : cur_defcon(2, CurPoints) & (not asked_landing(_, _) | asked_landing(_, 0)) 
	<-	-asked_landing(_, _);
		!probe_neighbor;
		!land_on_best_neighbor;
		.
		
+!doWhatIsNecessary : cur_defcon(1, CurPoints) & asked_cloning(N, K)
	<-	-asked_cloning(N, K);
		+asked_cloning(N, K-1);
		.

+!doWhatIsNecessary : cur_defcon(1, CurPoints) & (not asked_cloning(_, _) | asked_landing(_, 0)) 
	<-	-asked_cloning(_, _);
		!probe_neighbor;
		!clone_on_best_neighbor;
		.
+!doWhatIsNecessary : cur_defcon(CurDef, CurPoints) & current_zone(Z) & distance(D) & zone(death_zone, Max_Dist, _)
	//<-	// println("Current defcon: ", CurDef, " (", CurPoints, ") in ", Z);
	.


/****************************************************************************************
 * * PROBE NEIGHBOR PLANS: discover and gather information on the neighbors.
 ****************************************************************************************/

/**
 * Lists the neighbors and sends an information request. 
 */
+!probe_neighbor
	<-	?artifacts(resource, MResID);
		discoverNeighbors(List) [artifact_id(MResID)];
		?size(S);
		for(.member(N, List)) {
			!send_to_neighbor(N);
		}
	.
/**
 * The information request can be sent only if there isn't a pending (not expired) request and
 * there isn't a non expired response.
 */
+!send_to_neighbor(N) : ask_neighbor(N, K) & K > 0
	<-	-ask_neighbor(N, K);
		+ask_neighbor(N, K-1);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) | ask_neighbor(N, 0)) & reply_neighbor_ok(N, K, D, A) & K > 0
	<-	-reply_neighbor_ok(N, K, D, A);
		+reply_neighbor_ok(N, K-1, D, A);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) | ask_neighbor(N, 0)) & reply_neighbor_no(N, K) & K > 0
	<-	-reply_neighbor_no(N, K);
		+reply_neighbor_no(N, K-1);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) &
							(	(not reply_neighbor_ok(N, _, _, _) | reply_neighbor_ok(N, 0, _, _)) &
								(not reply_neighbor_no(N, _) | reply_neighbor_no(N, 0))
							)) | ask_neighbor(N, 0)
	<- 	-ask_neighbor(N,_);
		-reply_neighbor_no(N,_);
		-reply_neighbor_ok(N,_,_,_);
		+ask_neighbor(N, 6);			
		.my_name(Name);
		?size(S);
		?hover_name(HName);
		sendMessage(Name, N, "mobile", [there_is_space, S, HName], Res);
		if(not Res) {
			-ask_neighbor(N, _);	
		}
		.

/**
 * This messages are the replies to the probe information
 */
+!manage_message(Sender, SenderName, ["reply_no_space"])
	<- 	+reply_neighbor_no(Sender, 1);
		.
+!manage_message(Sender, SenderName, ["reply_ok_space", X, Y, AlreadyHere])
	<- 	?anchor(AX, AY, Area);
		?artifacts(hovering, HArtID);
		distance(D, X, Y, AX, AY) [artifact_id(HArtID)];
		+reply_neighbor_ok(Sender, 2, D, AlreadyHere);
		.

/****************************************************************************************
 * * LANDING AND CLONING PRE-PROTOCOL
 ****************************************************************************************/

/**
 * Find the best neighbor to migrate to, and ask for permission.
 */
+!land_on_best_neighbor
	<-	?distance(MyDistance);
		.findall(D, reply_neighbor_ok(_,_,D, false), L);
		if(not .empty(L)) {
			.min(L, Min);
			if(Min < MyDistance) {
				?reply_neighbor_ok(N, _, Min, _);
				.my_name(Name);
				+asked_landing(N, 6);
				?size(S);
				sendMessage(Name, N, "mobile", [permission_to_land, S], Res);
				if(not Res) {
					-asked_landing(N, _);
				}
			}
		}
		.

/**
 * Find the best neighbor to copy in, and ask for permission.
 */
+!clone_on_best_neighbor
	<-	.findall(D, reply_neighbor_ok(_,_,D, false), L);
		if(not .empty(L)) {
			.min(L, Min);
			?zone(death_zone, Max, _);
			if(Min < Max) {
				?reply_neighbor_ok(N, _, Min, _);
				.my_name(Name);
				+asked_cloning(N, 10);
				?size(S);
				sendMessage(Name, N, "mobile", [permission_to_land, S], Res);
				if(not Res) {
					-asked_cloning(N, _);
				}
			}
		}
		.

/**
 * Replied by the mobile node if the space has been allocated, but the request doesn't exists
 */
+!manage_message(Sender, SenderName, ["permission_granted"]) : (not asked_landing(Sender, _) & not asked_cloning(Sender, _))
	<- 	.my_name(Name);
		sendMessage(Name, Sender, "mobile", [landing_aborted], _);
		.

/**
 * Replied by the mobile node if the space has not been allocated.
 */
+!manage_message(Sender, SenderName, ["permission_denied"])
	<- 	-asked_landing(Sender, _).


/****************************************************************************************
 * * CLONING PROCEDURE
 ****************************************************************************************/


/**
 * Replied when there's space for the migration. Clone case: send the information.
 */
+!manage_message(Sender, SenderName, ["permission_granted"]) : asked_cloning(Sender, _)
	<- 	// In real life now the agent should pack it's data.
		?host(_, Host);
		.my_name(Name);
		?artifacts(hovering, HArtID);
		generateUniqueName(AgentName, Name) [artifact_id(HArtID)];
		?anchor(X, Y, Area);
		?size(S);
		?hover_name(HoverName);
		sendMessage(Name, Sender, "mobile", [clone, X, Y, Area, S, HoverName, AgentName], Res);
		-asked_cloning(Sender, _);
		.

+sorry_clone_failed(HostID, HostName).

/****************************************************************************************
 * * LANDING PROCEDURE
 ****************************************************************************************/


/**
 * Replied when there's space for the migration. Suspends the activities, while waits for the final confirms.
 */
+!manage_message(Sender, SenderName, ["permission_granted"]) : asked_landing(Sender, _)
	<- 	// In real life now the agent should pack itself.
		?host(_, Host);
		+landing(Sender);
		.my_name(Name);
		sendMessage(Name, Sender, "mobile", [land, "packed!"], Res);
		-asked_landing(Sender, _);
		// In real life the agent should stop. For "idon'twanttoimplementit" reasons,
		// the agent communicate to the host the migration and pause itself. 
		if(Res) {
			.send(Host, tell, performing_arakiri);
			?workspace(host, WspNodeId);
			cartago.set_current_wsp(WspNodeId);
			quitWorkspace;
			?workspace(world, WspId);
			cartago.set_current_wsp(WspId);			
			// TODO: leaveWorkspace(WspNodeId);
			-workspace(host, _);
			-artifacts(resource, _);
			-host(_, Host) [source(_)];
			+i_can_resume(Sender);
		} else {
			-landing(Sender);
		}
		.

/**
 * Told by the mobile node: the migration is completed, normal activities can be resumed. 
 */
+you_can_resume(HostID, HostName, MobileWsp)
	<-  !resume(HostID, HostName, MobileWsp).

+sorry_land_failed(HostID, HostName, MobileWsp)
	<-  !abort_landing(HostID, HostName, MobileWsp).

+!abort_landing(HostID, HostName, MobileWsp) : landing(HostID) & not i_can_resume(HostID)
	<-	.wait(500);
		!abort_landing(HostID, HostName, MobileWsp).

+!abort_landing(HostID, HostName, MobileWsp) : landing(HostID) & i_can_resume(HostID)
	<- 	// Curses!
		!arakiri;.

/**
 * Internal synch: wait for the cleanup to be completed before resuming.
 */
+!resume(HostID, HostName, MobileWsp) : landing(HostID) & not i_can_resume(HostID)
	<-	.wait(500);
		!resume(HostID, HostName, MobileWsp).

/**
 * Resuming: updating host, workspace and artifact
 */
@resumeFromLanding[atomic] +!resume(HostID, HostName, MobileWsp) : landing(HostID) & i_can_resume(HostID)
	<- 	.term2string(HostName, HostNameStr);
		+host(HostID, HostNameStr);
		joinWorkspace(MobileWsp, WspNodeId);
		+workspace(host, WspNodeId);
		lookupArtifact("MobileResource", HostResID);
		+artifacts(resource, HostResID);
		-landing(_);
		-i_can_resume(_);
		-you_can_resume(_, _, _) [source(_)];
		.

/****************************************************************************************
 * * FALLBACK PLANS
 ****************************************************************************************/

/**
 * Debug/Fallback: unknown message received!
 */
+!manage_message(Sender, SenderName, L)
	<- 	.print("UKN received: ", L, " from ", Sender, " ", SenderName);
		.