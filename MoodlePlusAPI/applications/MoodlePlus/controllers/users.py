def user():
	user = db(db.users.id==request.args[0]).select().first()
	return dict(user=user)