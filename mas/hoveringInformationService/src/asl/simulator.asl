// Agent simulator in project hoveringInformationService

/* Initial beliefs and rules */

~inited.
~configured.

/* Initial goals */

!init.

/* Plans */

+pot(ID, X, Y)
	<- !addNewPot(ID, X, Y).

+!addNewPot(HoverID, X, Y) : artifact(sim, _, ID)
	<- addPointOfInterest(HoverID, X, Y) [artifact_id(ID)].

-!addNewPot(ID, X, Y)
	<- 	.wait(200); 
		!addNewPot(ID, X, Y).

// Init plan: create the simulation artifact
+!init: ~inited  & worldWsp(WspName) & envArt(EAName) & guiSize(GW, GH) & guiRefresh(GR)
	<-	-~inited;
		joinWorkspace(WspName, WspID);
		+workspace(world, WspID);
		cartago.set_current_wsp(WspID);
		makeArtifact("SimArtifact", "it.unibo.sisma.hi.mas.sim.SimulatorArtifact",[],SimID);
		+artifact(sim, "SimArtifact", SimID);
		focus(SimID);
		-~configured;
		+configured;
		!start;
	.

-!init : ~inited
	<- 	!init.

// Start simulation
+!start:  artifact(sim, Name, ID) & configured
	<- 	println("===>Simulator waiting for start...");
		.wait(10000);
		!start.
	// 2

// Show simulation

+!show_simulation: wait(T) & artifact(Name,ID) & started
	<- show_simulation [artifact_id(ID)].
	
-!show_simulation: wait(T) & artifact(Name,ID) & started
	<- .wait(T); 
	!show_simulation.
