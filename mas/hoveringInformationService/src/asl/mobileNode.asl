// Agent mobileNode in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */

!init.

/* Plans */

+!init : worldwsp(WspId) & range(DR) & storage(DS) & ui_name(AUName)
	<-	+initialized;
		.my_name(Name);
		cartago.set_current_wsp(WspId);
		makeArtifact(AUName, "it.unibo.sisma.hi.mas.hs.MobileUIArtifact",[], UResID);
		+artifacts(ui, UResID);
		focus(UResID);
		.concat("NodeWorkspace_", Name, WNName);
		createWorkspace(WNName);
		joinWorkspace(WNName, WNid);
		+own_wsp(node, WNid);
		.concat("RangeWorkspace_", Name, WRName);
		createWorkspace(WRName);
		joinWorkspace(WRName, WRid);
		+own_wsp(range, WRid);
		cartago.set_current_wsp(WNid);
		makeArtifact("MobileResource", "it.unibo.sisma.hi.mas.hs.NodeResourceArtifact",[DR, DS],NResID);
		+artifacts(resource, NResID);
		focus(NResID);
		.
		

-!init : not initialized
	<- !init.