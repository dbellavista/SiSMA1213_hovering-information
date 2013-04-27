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
		makeArtifact(PersonBody, "it.unibo.sisma.hi.mas.social.BodyArtifact", [], ArtBodyID);
		+artifacts(body, ArtBodyID);
	
		lookupArtifact(EAName, EnvArtID);
		-+artifacts(envSocial, EnvArtID);		
		linkArtifacts(ArtBodyID, "out-1", EnvArtID);
		!getDeviceUI;
		
		-~configured;
		+configured.	

-!init : ~inited
	<- 	!init.
	
+!getDeviceUI : ui_name(AUName)
	<-	lookupArtifact(AUName, UiArtd);
		-+artifacts(mobileUI, UiArtd);
		.
		
-!getDeviceUI : not artifacts(mobileUI, _)
	<- 	.wait(200);
		!getDeviceUI.

-!start : not configured
	<- .wait(200);
		!start.
		
+!start : configured
	<- 	println("===>Person STARTED!");
		.
		
// Running behavior definition

