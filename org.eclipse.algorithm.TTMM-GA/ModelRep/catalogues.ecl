rule Catalogue2Catalogue
	match c1 : In!t_catalogue
	with c2 : Out!t_catalogue {
	
	compare : true
		
}

rule Product2Product
	match p1 : In!t_product
	with p2 : Out!t_product {
	
	compare : p1.a_id = p2.a_id
	
}