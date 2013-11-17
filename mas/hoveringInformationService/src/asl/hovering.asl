// Agent hoveringAgent in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

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
		+current_zone(ok);

		+defcon(5, 1.0, 0.8);
		+defcon(4, 0.8, 0.68);
		+defcon(3, 0.68, 0.42);
		+defcon(2, 0.42, 0.35);
		+defcon(1, 0.35, 0.0);
		
		+cur_defcon(5, 0);
		
		.concat("NodeWorkspace_", DeviceName, WNName);
		joinWorkspace(WNName, WspNodeId);
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
	<- 	+pieces([]);
		!!survive;
		!!receiveMessage;
		.
		
+sorry_after_init <-
			// Uff.. so much effort for nothing
			!stop.

+!stop <- +stop_surviving;
		+stop_receiving;
		?host(HostID, HostName);
		.send(HostName, tell, performing_arakiri);
		// ARAKIRI
		.my_name(Name);
		.kill_agent(Name)
		.

+!survive : stop_surviving.
+!receiveMessage : stop_receiving.

+!getOld(_, _, _, _, OldSpeed, OldDistance, OldDirect, OldAnchorVect) : speed(OldSpeed) & distance(OldDistance) &
															direction(OldDirect) & anchor_vector(OldAnchorVect).
+!getOld(Speed, Distance, Direct, AnchorVect, Speed, Distance, Direct, AnchorVect).

+!updateZone(Distance) : zone(Zone, Min, Max) & Distance >= Min & Distance < Max
	<- -current_zone; +current_zone(Zone).

@upBelieves[atomic] +!updateBelieves([PX, PY]) : artifacts(hovering, HArtID) & anchor(AX, AY, Area)
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
		
		-+speed(Speed);
		-+distance(Distance);
		-+direction(Direct);
		-+anchor_vector(AnchorVect);
		.

// *** approach_level(AL)
// *** approach_variation(AV)
//
// If I'm leaving AL < 0. If I'm approaching, AL > 0 
// The speed of approaching or leaving is given by AV
// 
// The possibile behaviours can be:
//   - DEFCON 5: nessun bisogno di comunicare con il mondo esterno, se non per rispondere
//   - DEFCON 4: si inizia a controllare la lista di neighbor. Se sono pochi, contattarli
//				 ogni tanto per conoscere il loro stato.
//	 - DEFCON 3: controllo di tutti i neighbor e del loro stato.
//	 - DEFCON 2: Migrazione dell'hovering sul neighbor più vicino all'anchor.
//	 - DEFCON 1: Copia dell'hovering sui neighbor più vicini.

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

// I'm leaving!
@defconDec[atomic] +!decideDefcon : approach_level(AL) & approach_variation(AV) & speed(SP) & current_zone(Zone) &
									 cur_defcon(CurDef, CurPoint) & defcon(CurDef, MinThr, MaxThr) &
									 distance(Dist) & zone(death_zone, Max_Dist, _)
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
		exp(NewPoints, -Mul*(Dist / Max_Dist)) [artifact_id(HArtID)];
		!setDefcon(NewPoints);
		.

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
	<-	?artifacts(resource, MResID);
		discoverNeighbour(List) [artifact_id(MResID)];
		?size(S);
		for(.member(N, List)) {
			!send_to_neighbor(N);
		}
	.

+!send_to_neighbor(N) : ask_neighbor(N, K) & K > 0
	<-	-ask_neighbor(N, K);
		+ask_neighbor(N, K-1);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) | ask_neighbor(N, 0)) & reply_neighbot_ok(N, K) & K > 0
	<-	-reply_neighbot_ok(N, K);
		+reply_neighbot_ok(N, K-1);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) | ask_neighbor(N, 0)) & reply_neighbot_no(N, K) & K > 0
	<-	-reply_neighbot_no(N, K);
		+reply_neighbot_no(N, K-1);
		.
+!send_to_neighbor(N) :  (not ask_neighbor(N,_) &
							(	(not reply_neighbor_ok(N, _) | reply_neighbor_ok(N, 0)) &
								(not reply_neighbor_no(N, _) | reply_neighbor_no(N, 0))
							)) | ask_neighbor(N, 0)
	<- 	-ask_neighbor(N,_);
		-reply_neighbor_no(N,_);
		-reply_neighbor_no(N,_);
		+ask_neighbor(N, 10);			
		.my_name(Name);
		sendMessage(Name, N, "mobile", [there_is_space, S], Res);
		if(not Res) {
			-ask_neighbor(N, _);	
		}
		.

+!doWhatIsNecessary : cur_defcon(CurDef, CurPoints) & current_zone(Z) & distance(D) & zone(death_zone, Max_Dist, _)
	//<-	// println("Current defcon: ", CurDef, " (", CurPoints, ") in ", Z);
	.

+!survive : not stop_surviving & anchor(AX, AY, Area)
	<-	?artifacts(resource, MResID);
		obtainPosition(P) [artifact_id(MResID)];
		!updateBelieves(P);
		!decideDefcon;
		!doWhatIsNecessary;
		.wait(500);
		!!survive;
		.

+!receiveMessage : not stop_receiving
	<- 	?artifacts(resource, MResID);
		.my_name(Name);
		receiveMessage(Name, Res, Sender, SenderName, Message) [artifact_id(MResID)];
		if(Res) {
			!manage_message(Sender, SenderName, Message);	
		}
		.wait(500);
		!!receiveMessage;
		.

+!manage_message(Sender, SenderName, ["reply_no_space"])
	<- 	+reply_neighbor_no(Sender, 10);
		.
+!manage_message(Sender, SenderName, ["reply_ok_space"])
	<- 	+reply_neighbor_ok(Sender, 5);
		.

+!manage_message(Sender, SenderName, L)
	<- 	println("UKN received: ", L, " from ", Sender, " ", SenderName);
		.

+?inquire(HoverName, Size) : size(S) & hover_name(HN)
<- HoverName = HN; Size = S.