// Agent person in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */

!init.
!joinWSP.

/* Plans */

+!init : true 
	<-	makeArtifact("body", "it.unibo.sisma.hi.mas.social.BodyArtifact", ArtID);
		+artifacts(body, ArtID);
		focus(body).
		
+!joinWSP : wsp(WspId) & art(EAid) & position(X, Y) &
			artifacts(body, ArtID) & ui_name(AUName)
	<- 	!joinEnv(WspId, EAid);
		!getDevice(AUName);
		?artifacts(envSocial, EAid);
		linkArtifact(ArtID, "out-1", EAid);
		+joined;
		.
		
-!joinWSP <- !joinWSP.

+!joinEnv(WspId, EAid) <- 
	cartago.set_current_wsp(WspId);
	-+artifacts(envSocial, EAid);
	.

+!getDevice(AUName)
	<-	lookupArtifact(AUName, AUId);
		-+artifacts(mobile, AUid);
		.
-!getDevice(AUName)
	<-	getDevice(AUName).

+!start : joined
	<- 		
		+started
		//  Turn on device, start moving
		!todo.

-!start : not started
			<- !start.
