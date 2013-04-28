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
		makeArtifact("SimArtifact", "it.unibo.sisma.hi.mas.sim.SimulatorArtifact",[GW,GH,GR],SimArtID);
		+artifact(sim, "SimArtifact", SimArtID);
		focus(SimArtID);
		
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(envSocial, EnvArtID);		
		linkArtifacts(SimArtID, "out-1", EnvArtID);
		
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
	<- 	inquireEnvironment(People, PoTs, WWidth, WHeight);
		showSimulation(People, PoTs, WWidth, WHeight);
		.wait(100);
		!start;
		.