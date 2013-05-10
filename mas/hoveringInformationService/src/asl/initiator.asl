// Agent initiator in project hoveringInformationService

/* Initial beliefs and rules */
disseminations("random", random).
behaviour("random", random).

/* Initial goals */

!initialize.

/* Plans */

// Init plan: create the simulation artifact
+!initialize
	<-	?current_wsp(BaseWsp, BaseWspName, _);
		+wsp(default, BaseWsp);
		makeArtifact("initArtifact", "it.unibo.sisma.hi.mas.sim.InitiatorArtifact",[],ID);
		+artifact(init, "initArtifact", ID);
		focus(ID);
		input_data [artifact_id(ID)];
		// Setup the world properties, creating the EnvironmentArtifact
		?parameter("world");
		!setup_world;
		// Setup the simulator, creating the simulator agent
		?parameter("simulation");
		?parameter("analysis");
		!setup_simulation;
		// Setup people and devices, imposing the MobileUI Artifact name.
		?parameter("people", NP);
		!setup_people(NP);
		// Setup the hovering information, creating pieces using the given
		// dissemination algorithm
		?parameter("hovering", NH);
		!setup_hovering(NH);
		// Start the system
		!start_system;
		.
		
// Parameters setup

+!setup_world : parameter("world")
	<-	?parameter("world", "width", W);
		?parameter("world", "height", H);
		
		// World workspace
		createWorkspace("World");
		joinWorkspace("World", WspId);
		cartago.set_current_wsp(WspId);
		+wsp(world, "World", WspId);
		// Environment artifact
		makeArtifact("EnvArtifact", "it.unibo.sisma.hi.mas.environment.EnvironmentArtifact", [W, H], EAid);
		+artifact(env, "EnvArtifact", EAid);
		
		println("World configuration\n   * width=",W,"\n   * height=",H);
		?wsp(default, WspOrigId);
		cartago.set_current_wsp(WspOrigId);
		.

+!setup_simulation : parameter("simulation")
	<-	?parameter("simulation", "gui_width", W);
		?parameter("simulation", "gui_height", H);
		?parameter("simulation", "gui_refresh_rate", GR);
		?parameter("simulation", "dissemination", Dt);
		?disseminations(Dt, D);
		-+parameter("simulation", "dissemination", D);
		?parameter("analysis", "analysis_rate", AR);
		
		?artifact(env, EAName, _);
		?wsp(world, WspName, _);
		
		// Simulator agent creation, sending the world parameters.
		.create_agent("Simulator", "simulator.asl", [agentArchClass("c4jason.CAgentArch")]);
		.send("Simulator", tell, [	guiSize(W, H), guiRefresh(GR), worldWsp(WspName), envArt(EAName), analysisRate(AR) ]);
		println("Simulation configuration",
				"\n   * gui_width=",W,
				"\n   * gui_height=",H,
				"\n   * gui_refresh_rate=",GR,
				"\n   * dissemination=",D,
				"\n   * analysis_rate=",AR
		);
	.
	
+!setup_people(0).

+!setup_people(NP) : parameter("people", NTot) & NP <= NTot & NP > 0
	<-	?parameter("people", NP, "name", Name);
		?parameter("people", NP, "behaviour", Bt);
		?behaviour(Bt, B);
		-+parameter("people", NP, "behaviour", Bt);
		?parameter("people", NP, "xpos", TmpX);
		?parameter("people", NP, "ypos", TmpY);
		!analyzePos(TmpX, TmpY, X, Y);
		?parameter("people", NP, "device_range", DR);
		?parameter("people", NP, "device_storage", DS);

		// Creating Person and Mobile device agents
		.concat(Name, NP, PersonAgent);
		+person(NP, PersonAgent);
		.concat("device_", Name, NameTmp);
		.concat(NameTmp, NP, DeviceAgent);
		+device(NP, DeviceAgent);
		
		.create_agent(PersonAgent, "person.asl", [agentArchClass("c4jason.CAgentArch")]);
		.create_agent(DeviceAgent, "mobileNode.asl", [agentArchClass("c4jason.CAgentArch")]);

		// Sending configuration and binding person with his device, by imposition of MobileUI Artifact name
		?artifact(env, EAName, _);
		?wsp(world, WspName, WWspId);
		 
		.concat("NodeUI_", DeviceAgent, ArtUIName);
		.send(DeviceAgent, tell, [	worldWsp(WspName), ui_name(ArtUIName),
								range(DR), storage(DS), node_id(PersonAgent) ]);
		.send(PersonAgent, tell, [worldWsp(WspName), envArt(EAName), ui_name(ArtUIName),
								position(X, Y), behaviour(B),
								device(DeviceAgent) ]);
		
		cartago.set_current_wsp(WWspId);
		enter(PersonAgent, X, Y);
		?wsp(default, DWspId);
		cartago.set_current_wsp(DWspId);
		
		
		println("Person ",PersonAgent," and device, ", DeviceAgent ," configuration",
				"\n   * behaviour=",B,
				"\n   * position=(",X,", ",Y,")",
				"\n   * device_range=",DR,
				"\n   * device_storage=",DS
		);
		!setup_people(NP - 1);
	.
	
+!analyzePos("random", TmpY, X, Y) : parameter("world", "width", W)
	<-  .random(Ran);
		ValX = Ran * W;
		!analyzePos(ValX, TmpY, X, Y).
		 
+!analyzePos(TmpX, "random", X, Y) : parameter("world", "height", H)
	<-  .random(Ran);
		ValY = Ran * H;
		!analyzePos(TmpX, ValY, X, Y).

+!analyzePos(TmpX, TmpY, X, Y)
	<-  Y = TmpY;
		X = TmpX.

+!setup_hovering(0).

+!setup_hovering(NH) : parameter("hovering", NTot) & NH <= NTot & NH > 0
	<-	?parameter("hovering", NH, "name", Name);
		?parameter("hovering", NH, "xanchor", X);
		?parameter("hovering", NH, "yanchor", Y);
		?parameter("hovering", NH, "anchor_radius", AR);
		?parameter("hovering", NH, "data_size", DS);
		?parameter("simulation", "dissemination", DISS);

		// Add point of interest
		?artifact(env, EnvArt, EnvArtId);
		.concat(Name, "_", NameTmp);
		.concat(NameTmp, NH, IDHover);
		
		?wsp(world, _, WWspId);
		cartago.set_current_wsp(WWspId);
		createPointInterest(IDHover, X, Y);
		?wsp(default, DWspId);
		cartago.set_current_wsp(DWspId);
		
		println("Hovering ",Name," configuration",
				"\n   * anchor=(",X,", ",Y,")",
				"\n   * anchor_radius=",AR,
				"\n   * data_size=",DS,
				"\n   * Disseminations:"
		);
		// Disseminate hovering information among people
		!disseminate(IDHover, NH, anchor(X, Y, AR), size(DS), DISS);
		!setup_hovering(NH - 1);
	.
	
+!disseminate(ID, NH, Anchor, Size, random)
	<-	?artifact(init, _, InitArt); 
		?parameter("people", NP);
		// Random: for each person, give a piece with probability p
		toss_coin(0.8, DissRes);
		!random_dissemination(ID, NH, Anchor, Size, NP, 0.8, 1, DissRes);
		.

+!random_dissemination(I, NH, A, S, NP, P, IX, _) : 
		NP <= 0.

+!random_dissemination(ID, NH, Anchor, Size, NP, Prob, Index, true) :
		person(NP, PersonAgent)
	<-	.concat(ID, "_", NameT);
		.concat(NameT, Index, HoveringAgent);
		!create_hovering_agent(HoveringAgent, Anchor, Size);		
		// DRY trick
		!random_dissemination(ID, NH, Anchor, Size, NP, Prob, Index + 1, false);
		.

+!random_dissemination(ID, NH, Anchor, Size, NP, Prob, Index, false) : person(NP, PersonAgent)
	<-	toss_coin(Prob, DissRes);
		!random_dissemination(ID, NH, Anchor, Size, NP - 1, Prob, Index, DissRes).

+!create_hovering_agent(Name, Anchor, Size)
	<- 	.create_agent(Name, "hovering.asl", [agentArchClass("c4jason.CAgentArch")]);
		?wsp(world, WspName, _);
		.send(Name, tell, [worldWsp(WspName), Anchor, Size]);
		// TODO: insert the hovering information inside the mobile node
		.

// Start simulation
+!start_system
	<- 	.send("Simulator", achieve, start);
		!start_person(1);
		.
+!start_person(NP) : person(NP, Name)
	<- 	.send(Name, achieve, start);
		!start_person(NP + 1).
	
-!start_person(NP) : not person(NP, _).