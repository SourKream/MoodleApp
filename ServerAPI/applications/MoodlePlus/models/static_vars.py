def get_current_year():
	return 2015 if db(db.static_vars.name=="current_year").count()<1 else db(db.static_vars.name=="current_year").select().first().int_value

def get_current_sem():
	return 2 if db(db.static_vars.name=="current_sem").count()<1 else db(db.static_vars.name=="current_sem").select().first().int_value
