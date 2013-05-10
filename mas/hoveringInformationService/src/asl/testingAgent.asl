// Agent testingAgent in project hoveringInformationService

/* Initial beliefs and rules */

/* Initial goals */
!test_link.
+!test_link
	<-
	createWorkspace("W1");
	createWorkspace("W2");
	joinWorkspace("W1", W1);
	joinWorkspace("W2", W2);
	?current_wsp(_,Name1,_);
	println("Working in: ", Name1);
	//cartago.set_current_wsp(TWId);

	makeArtifact("Test1","test.TestArtifact",[],Id1);
	println("Focusing Test1...");
//	focus(Id1);
	
	cartago.set_current_wsp(W1);
	?current_wsp(_,Name2,_);
	println("Working in: ", Name2);
	
	makeArtifact("Test2","test.TestArtifact2",[],Id2);
	println("Focusing Test2...");
//	focus(Id2);
	println("Linking...");
	linkArtifacts(Id2,"to1-link",Id1);
	println("artifacts linked 1");
	cartago.set_current_wsp(W2);
	linkArtifacts(Id1,"to2-link",Id2);
	println("artifacts linked 2");
	/*test;
	test2(V);
	println("value ",V);
	test3
	*/
	.

+!notWk
	<-
		createWorkspace("W1");
	createWorkspace("W2");
	joinWorkspace("W1", W1);
	joinWorkspace("W2", W2);
	cartago.set_current_wsp(W2);

	makeArtifact("Test1","it.unibo.sisma.hi.mas.social.BodyArtifact",["A"],Test1);
//	focus(Test1);
	cartago.set_current_wsp(W1);
	
	makeArtifact("Test2","it.unibo.sisma.hi.mas.environment.EnvironmentArtifact",[12,12],Test2);
//	focus(Test2);
	println("Linking...");
	linkArtifacts(Test1,"env-link",Test2);
//	linkArtifacts(Test2,"to1-link",Test1);
	println("artifacts linked: going to test");
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