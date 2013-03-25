// Agent hoveringAgent in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */

!init.

/* Plans */

+!init : worldwsp(WspId) & anchor(X, Y, Area) & size(S)
	<-	+initialized;
		cartago.set_current_wsp(WspId);
		.
		

-!init : not initialized
	<- !init.