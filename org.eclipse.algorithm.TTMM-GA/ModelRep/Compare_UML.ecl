
rule Pack
	match b : In!Package with l : Out!Package {
		compare : true	
}


rule Class
	match b : In!Class with l : Out!Class{

		compare : b.name = l.name
		
}