// Agent person in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

+!init : ~inited & worldWsp(WspName) & envArt(EAName) & position(X, Y) &
			ui_name(AUName) & behaviour(B) & device(DeviceAgent)
	<-	-~inited;
		joinWorkspace(WspName, WspId);
		+workspace(world, WspId);
		cartago.set_current_wsp(WspId);

		.my_name(Name);
		// Todo: behaviour
		.concat("PersonBody_", Name , PersonBody);
		makeArtifact(PersonBody, "it.unibo.sisma.hi.mas.social.BodyArtifact", [Name], ArtBodyID);
		+artifacts(body, ArtBodyID);
	
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(envSocial, EnvArtID);		
		linkArtifacts(ArtBodyID, "env-link", EnvArtID);
		!getDeviceUI;
		
		-~configured;
		+configured.	

-!init : ~inited
	<- 	!init.
	
+!getDeviceUI : ui_name(AUName)
	<-	lookupArtifact(AUName, UiArtd);
		focus(UiArtd);
		-+artifacts(mobileUI, UiArtd);
		.
		
-!getDeviceUI : not artifacts(mobileUI, _)
	<- 	.wait(200);
		!getDeviceUI.

-!start : ~configured
	<- .wait(200);
		!start.
		
+!start : configured
	<- !behave.
	
+!behave
	<- 	sense;
		!choose_destination(DX, DY);
		!reach(DX, DY);
		!behave;
		.
+!choose_destination(0, 0): behaviour(none).
+!reach(0, 0): behaviour(none)
	<- .wait(1000).

+!choose_destination(DX, DY): behaviour(random)
	<- 	randomInt(LX, 1, 100);
		randomInt(X, -LX, LX);
		DX = (X - 0.5) * LX;
	 	randomInt(LY, 1, 100);
		randomInt(Y, -LY, LY);
		DY = (Y - 0.5) * LY;
		.

+!reach(DX, DY): behaviour(random)
	<- 
	randomInt(Time, 10, 100);
	randomInt(Speed, 1, 3);
	!reach(DX, DY, Time, Speed);
	.

+!reach(_, _, 0, _): behaviour(random).

+!reach(DX, DY, Time, Speed): behaviour(random)
	<- 	move(DX, DY, Speed);
		sense;
		.wait(100);
		!reach(DX,DY,Time-1, Speed);
		.

// Running behavior definition

