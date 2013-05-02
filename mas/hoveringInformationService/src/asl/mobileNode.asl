// Agent mobileNode in project hoveringInformationService

/* Initial beliefs and rules */
~inited.
~configured.
/* Initial goals */

!init.

/* Plans */

+!init : ~inited & worldWsp(WspName) &
		 range(DR) & storage(DS) & ui_name(AUName)
	<-	-~inited;
		.my_name(Name);
		joinWorkspace(WspName, WspId);
		cartago.set_current_wsp(WspId);
		+workspace(world, WspId);
		
		makeArtifact(AUName, "it.unibo.sisma.hi.mas.hs.MobileUIArtifact",[], UResID);
		+artifacts(ui, UResID);
		.concat(AUName, "_interface", AUIfName);
		makeArtifact(AUIfName, "it.unibo.sisma.hi.mas.hs.MobileUIInterfaceArtifact",[], UIfResID);
		+artifacts(ui_if, UIfResID);
		focus(UIfResID);
		linkArtifacts(UResID, "to-device", UIfResID);
		linkArtifacts(UIfResID, "to-ui", UResID);
		
		
		.concat("NodeWorkspace_", Name, WNName);
		createWorkspace(WNName);
		joinWorkspace(WNName, WNid);
		+workspace(node, WNid);
		.concat("RangeWorkspace_", Name, WRName);
		createWorkspace(WRName);
		joinWorkspace(WRName, WRid);
		+workspace(range, WRid);
		
		cartago.set_current_wsp(WNid);
		makeArtifact("MobileResource", "it.unibo.sisma.hi.mas.hs.NodeResourceArtifact",[DR, DS],NResID);
		+artifacts(resource, NResID);
		focus(NResID);
		-~configured;
		+configured;
		!start.
		

-!init : ~inited
	<- 	!init.
		
+!start : configured
	<- 	println("===>Device waiting for start...");
		.wait(10000);
		!start.