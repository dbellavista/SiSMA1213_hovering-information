// Agent simulator in project hoveringInformationService

// Il simulatore deve oltre che inizializzare e graficare, permettere
// il movimento delle persone.

/* Initial beliefs and rules */
disseminations("random", random).
behaviour("random", random).

~artifact(_).
~started.

/* Initial goals */

!initialize.

+started : setted
	<- -~started;
	!show_simulation.

/* Plans */

// Init plan: create the simulation artifact
+!initialize : ~artifact(_)
	<-	!buildArtifact;
		?artifact(ID);
		!initialize_simulation;
		?parameter("world");
		!setup_world;
		?parameter("simulation");
		!setup_simulation;
		?parameter("people", NP);
		!setup_people(NP);
		?parameter("hovering", NH);
		!setup_hovering(NH);
		?parameter("analysis");
		!setup_analysis;
		.

-!initialize [error_msg(Msg)]
	<-	println("Configuration error: ", Msg).

+!buildArtifact : ~artifact(_)
	<- 	makeArtifact("simartifact", "it.unibo.sisma.hi.mas.sim.SimulatorArtifact",[],ID);
		-~artifact(_);
		+artifact(ID).

// Input Data
+!initialize_simulation: artifact(ID)
	<- 	focus(ID);
		input_data [artifact_id(ID)];
		.

-!initialize_simulation [error_msg(Msg),env_failure_reason(fail(File,Exception))]
	<-	println(Msg);
		println("The Configuration file '", File, "' produced the error: '", Exception,"'").

// Parameters setup

+!setup_world : parameter("world")
	<-	?parameter("world", "width", W);
		?parameter("world", "height", H);
		createWorkspace("World");
		joinWorkspace("World", WspId);
		cartago.set_current_wsp(WspId);
		makeArtifact("Environment", "it.unibo.sisma.hi.mas.social.EnvironmentArtifact", [W, H], EAid);
		+environment(EAid);
		println("World configuration\n   * width=",W,"\n   * height=",H);
		.

+!setup_simulation : parameter("simulation")
	<-	?parameter("simulation", "gui_width", W);
		?parameter("simulation", "gui_height", H);
		?parameter("simulation", "gui_refresh_rate", R);
		?parameter("simulation", "dissemination", Dt);
		?disseminations(Dt, D);
		-+parameter("simulation", "dissemination", D);
		println("Simulation configuration",
				"\n   * gui_width=",W,
				"\n   * gui_height=",H,
				"\n   * gui_refresh_rate=",R,
				"\n   * dissemination=",D
		);
	.

+!setup_analysis : parameter("analysis")
	<-	?parameter("analysis", "analysis_rate", R);
		// TODO
		println("Analysis configuration",
				"\n   * analysis_rate=",R
		);
	.
+!setup_people(0).

+!setup_people(NP) : parameter("people", NTot) & NP <= NTot & NP > 0
	<-	?parameter("people", NP, "name", Name);
		?parameter("people", NP, "behaviour", Bt);
		?behaviour(Bt, B);
		-+parameter("people", NP, "behaviour", Bt);
		?parameter("people", NP, "xpos", X);
		?parameter("people", NP, "ypos", Y);
		?parameter("people", NP, "device_range", DR);
		?parameter("people", NP, "device_storage", DS);

		// Creating Person and Mobile device agents
		.concat(Name, "_", NameTmp);
		.concat(NameTmp, NP, PersonAgent);
		+person(NP, PersonAgent);
		.concat(Name, "_", NameTmp);
		.concat(NameTmp, NP, DeviceAgent);
		+device(NP, DeviceAgent);
		
		.create_agent(PersonAgent, "personAgent.asl");
		.create_agent(DeviceAgent, "mobileNode.asl");

		// Sending configuration and binding
		// person with his device
		?environment(EnvArtId);
		?current_wsp(WspId, _, _)
		.concat("NodeUI_", DeviceAgent, ArtUIName);
		.send(DeviceAgent, tell, [	worldwsp(WspId), ui_name(ArtUIName),
								range(DR), storage(DS)
							]);
		.send(PersonAgent, tell, [	wsp(WspId), art(EnvArtId), ui_name(ArtUIName),
								position(X, Y), behaviour(B),
								device(DeviceAgent)
							]);
		
		println("Person ",PersonAgent," and device, ", DeviceAgent ," configuration",
				"\n   * behaviour=",B,
				"\n   * position=(",X,", ",Y,")",
				"\n   * device_range=",DR,
				"\n   * device_storage=",DS
		);
		!setup_people(NP - 1);
	.
+!setup_hovering(0).

+!setup_hovering(NH) : parameter("hovering", NTot) & NH <= NTot & NH > 0
	<-	?parameter("hovering", NH, "name", Name);
		?parameter("hovering", NH, "xanchor", X);
		?parameter("hovering", NH, "yanchor", Y);
		?parameter("hovering", NH, "anchor_radius", AR);
		?parameter("hovering", NH, "data_size", DS);
		// Add point of interest
		?environment(EnvArtId);
		.concat(Name, "_", NameTmp);
		.concat(NameTmp, NH, IDHover);
		createPointInterest(IDHover, X, Y);
		println("Hovering ",Name," configuration",
				"\n   * anchor=(",X,", ",Y,")",
				"\n   * anchor_radius=",AR,
				"\n   * data_size=",DS,
				"\n   * Disseminations:"
		);
		
		!disseminate(IDHover, NH, X, Y, AR, DS);
		!setup_hovering(NH - 1);
	.
	
+!disseminate(ID, NH, X, Y, AR, DS) : parameter("simulation", "dissemination", random)
	<-	// Random: for each person, give a piece with probability p 
		?parameter("people", NP);	
		toss_coin(0.3, DissRes);
		!random_dissemination(ID, NH, X, Y, AR, DS, NP, 0.3, 1, DissRes);
		.

+!random_dissemination(_, _, _, _, _, _, NP, _, _, _, _) : 
		NP <= 0.

+!random_dissemination(ID, NH, X, Y, AR, DS, NP, Prob, Index, true) :
		person(NP, PersonAgent)
	<-	
		.concat(ID, "_", NameT);
		.concat(NameT, Index, HoveringAgent);
		
		.create_agent(HoveringAgent, "hovering.asl");
		.send(HoveringAgent, tell, [worldwsp(WspId), anchor(X, Y, AR),
								size(DS) ]);
								
		println("\n       - ", PersonAgent);
		
		// DRY trick
		!random_dissemination(ID, NH, NP, Prob, Index + 1, false);
		.
		
+!random_dissemination(ID, NH, NP, Prob, Index, false) : person(NP, PersonAgent)
	<-	toss_coin(Prob, DissRes);
		!random_dissemination(ID, NH, NP - 1, Prob, Index, DissRes).

// Start simulation
+!start_simulation :  artifact(ID) & parameters(Param) & setted
	<- start_simulation [artifact_id(ID)].

// Show simulation

+!show_simulation: wait(T) & artifact(ID) & started
	<- show_simulation [artifact_id(ID)].
	
-!show_simulation: wait(T) & artifact(ID) & started
	<- .wait(T); 
	!show_simulation.
