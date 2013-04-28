// Agent testingAgent in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */
!test_link.
+!test_link
	<- /*
	createWorkspace("World");
	joinWorkspace("TEST", TWId);
	cartago.set_current_wsp(TWId);
	*/
	F = 12 + 2;
	println("ASD ", F);
//	makeArtifact("Environment","it.unibo.sisma.hi.mas.social.EnvironmentArtifact",[10,21],Id2);
//	makeArtifact("PIPPO","it.unibo.sisma.hi.mas.social.BodyArtifact",[],Id1);
//	linkArtifacts(Id1,"out-1",Id2);
//	println("artifacts linked: going to test");
	/*test;
	test2(V);
	println("value ",V);
	test3
	*/
	.
/*
!init.
!dothem.

+count([[A|T1],[B|T2]]) : true <- println("ok: ", A, " ", B).

+!init : true
	<-  .print("H");
		.wait(1000);
		?current_wsp(WspId, WspName, WspBoh);
		println("=================>Telling: ", WspId, " - ", WspName, " - ", WspBoh);
		.send("testing", tell, [a1(WspId),a2(WspName),a3(WspBoh) ]);
		+done;
		.
		
+!dothem : true
	<- !doit;
		makeArtifact("A", "test.Counter", [], EAid);
		focus(EAid);
		.

+!doit : done & a1(A1) & a3(A3) & a2(A2)
	<- .print("D: ",A1, " - ", A2, " - ", A3);
	.
-!doit : true
	<-	!doit;
	.
*/