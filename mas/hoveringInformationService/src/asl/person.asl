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
+!choose_destination(DX, DY): behaviour(random)
	<-  .random(X);
		.random(LX);
		DX = (X - 0.5) * (LX * 100);
		.random(Y);
		.random(LY);
		DY = (Y - 0.5) * (LY * 100);
		.

+!reach(DX, DY): behaviour(random)
	<- !reach(DX, DY, 40);
	.

+!reach(DX, DY, 0): behaviour(random).

+!reach(DX, DY, S): behaviour(random)
	<- 	move(DX, DY, 1);
		sense;
		.wait(100);
		!reach(DX,DY,S-1);
		.

// Running behavior definition

