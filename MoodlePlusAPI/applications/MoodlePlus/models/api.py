def courses_list(user=None):
	user = auth.user if not user else db(db.users.id==user).select().first()
	if user.type_==1:
		courses = db(db.registered_courses.professor==user.id).select(db.registered_courses.course_id)
		courses = map(lambda x: x.course_id, courses)
	else:
		courses = db(db.student_registrations.student_id==user.id).select(db.student_registrations.registered_course_id)
		courses = map(lambda x: x.registered_course_id, courses)
	courses = db(db.courses.id.belongs(courses)).select()
	return dict(current_year=get_current_year(), current_sem=get_current_sem(), courses=courses, user=user)
