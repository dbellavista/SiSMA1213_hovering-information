// Agent simulator in project hoveringInformationService

/* Initial beliefs and rules */

~inited.
~configured.

/* Initial goals */

!init.

/* Plans */

// Init plan: create the simulation artifact
+!init: ~inited  & worldWsp(WspName) & envArt(EAName) & guiSize(GW, GH) & guiRefresh(GR) & analysisRate(AR)
	<-	-~inited;
		joinWorkspace(WspName, WspID);
		+workspace(world, WspID);
		cartago.set_current_wsp(WspID);
		makeArtifact("SimArtifact", "it.unibo.sisma.hi.mas.sim.SimulatorArtifact",[GR],SimArtID);
		+artifact(sim, "SimArtifact", SimArtID);
		focus(SimArtID);
		makeArtifact("UIArtifact", "it.unibo.sisma.hi.mas.sim.UIArtifact",[GW,GH],UIArtID);
		+artifact(ui, "UIArtifact", UIArtID);
		
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(envSocial, EnvArtID);		
		linkArtifacts(SimArtID, "inq-env-port", EnvArtID);
		linkArtifacts(SimArtID, "ui-port", UIArtID);
		
		-~configured;
		+configured;
	.

-!init : ~inited
	<- 	!init.

// Start simulation

-!start : ~configured
	<- .wait(200);
		!start.

+!start:  configured & artifact(sim, _, SimArtID)
	<- 	inquireEnvironment(People, PoTs, WWidth, WHeight) [artifact_id(SimArtID)];
		!inquireNodes(People, NodesInformation);
		showSimulation(NodesInformation, PoTs, WWidth, WHeight) [artifact_id(SimArtID)];
		.wait(200);
		!start;
		.

+!inquireNodes([], []).

+!inquireNodes([[ID, MB, Pos] | Rest], [InfoH | InfoT])
	<- 	.send(MB, askOne, inquire(_, _, _), inquire(Range, Storage, OccStorage));
		InfoH = [ID, MB, Pos, Range, Storage, OccStorage];
		!inquireNodes(Rest, InfoT);
		.
		
		
		
		
		
		
		
		
		
		