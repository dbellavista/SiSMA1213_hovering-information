// Agent testingAgent in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */

!init.
!dothem.

+count([[A|T1],[B|T2]]) : true <- println("ok: ", A, " ", B).

+!init : true
	<-  .print("H");
		.wait(1000);
		+done;
		.
		
+!dothem : true
	<- !doit;
		makeArtifact("A", "test.Counter", [], EAid);
		focus(EAid);
		.

+!doit : done
	<- .print("D");
	.
-!doit : true
	<-	!doit;
	.