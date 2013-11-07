// Agent hoveringAgent in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

+!init : ~inited & worldWsp(WspName) & anchor(X, Y, Area) & size(S) & hover_name(HoverName)
	<-	-~inited;
		// TODO: Cogito ergo sum. Host must be defined!!! :)
		joinWorkspace(WspName, WspId);
		+workspace(world, WspId);
		cartago.set_current_wsp(WspId);
		.my_name(Name);
		.concat("HoveringArtifact_", Name, ArtName);
		makeArtifact(ArtName, "it.unibo.sisma.hi.mas.hs.HoveringArtifact",[Name, S],HArtID);
		+artifacts(artifact, HArtID);
		-~configured;
		+configured;
		.

-!init : ~inited
	<- !init.
	
+!start : configured
<- 	println("===>Hovering waiting for start...");
	.wait(10000);
	!start.
	
+?inquire(HoverName, Size) : size(S) & hover_name(HN)
<- HoverName = HN; Size = S.