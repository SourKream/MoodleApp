def index():
	return list()

def list():
	if auth.is_logged_in():
		return courses_list()
	else:
		return dict()
		return dict(courses=db(db.courses.id>0).select())

def course():
	if len(request.args)<1:
		raise HTTP(404)
	course_code = str(request.args[0]).lower()
	course = db(db.courses.code==course_code).select()
	if len(course)>0:
		course = course.first()
	else:
		raise HTTP(404)
	try:
		tab = str(request.args[1])
	except Exception, e:
		tab = "overview"
	try:
		year = int(request.args[2])
	except Exception, e:
		year = get_current_year()
	
	try:
		sem = str(request.args[3])
		sem = sems.index(sem)
	except Exception, e:
		sem = get_current_sem()

	previous = db((db.registered_courses.course_id==course.id)&(~((db.registered_courses.year_==year)&(db.registered_courses.semester==sem)))).select()
	registered = db(db.registered_courses.course_id==course.id)(db.registered_courses.year_==year)(db.registered_courses.semester==sem).select()
	assignments = []
	course_threads = []
	grades = []
	resources = []
	if len(registered)>0:
		registered = registered.first()
		# if db(db.student_registrations.registered_course_id==registered.id)(db.student_registrations.student_id==auth.user.id).count()<1:
		# 	redirect('/')
		reg_course = registeredForCourse(course_code)
		if tab=="assignments":
			assignments = db(db.events.registered_course_id==registered.id)(db.events.type_==0).select()
		elif tab=="threads":
			course_threads = db(db.threads.registered_course_id==reg_course).select()
		elif tab=="grades":
			grades = db(db.grades.registered_course_id==reg_course)(db.grades.user_id==auth.user.id).select()
		elif tab=="resources":
			resources = db(db.resources.registered_course_id==course.id).select()
	else:
		registered = None


	return dict(course=course, year=year, sem=sem, previous=previous, registered=registered, tab=tab, assignments=assignments, course_threads=course_threads, grades=grades, resources=resources)

def download(): 
	return response.download(request,db)

def link(): 
	return response.download(request,db,attachment=False)

def delete():
	db(db.submissions.file_==request.args[0]).delete()
	session.flash = "Submission deleted successfully!!"
	if request.env.http_referer:
		redirect(request.env.http_referer)
	else:
		redirect('/')

def delete_resource():
	db(db.resources.file_==request.args[0]).delete()
	session.flash = "Resource deleted successfully!!"
	if request.env.http_referer:
		redirect(request.env.http_referer)
	else:
		redirect('/')




def new_resource():
	if len(request.args)<1:
		raise HTTP(404)
	course_code = str(request.args[0]).lower()
	course = db(db.courses.code==course_code).select()
	if len(course)>0:
		course = course.first()
	else:
		raise HTTP(404)

	if auth.is_logged_in():
		sub_form = FORM(
			INPUT(_name="sub_name", _type="text"),
			INPUT(_name="sub_file", _type="file")
			)
		reg_course = registeredForCourse(course_code)
		if sub_form.accepts(request.vars, formname='sub_form') and sub_form.vars.sub_name.strip()!="":
			sub = db.resources.file_.store(sub_form.vars.sub_file.file, sub_form.vars.sub_file.filename)
			id = db.resources.insert(file_=sub,name=sub_form.vars.sub_name,is_public=auth.user.type_, user_id=auth.user_id, registered_course_id=reg_course )
			if id>0:
				session.flash = "Resource upload Successful!"
	if request.env.http_referer:
		redirect(request.env.http_referer)
	else:
		redirect('/')

def assignment():
	if len(request.args)<1:
		raise HTTP(404)
	try:
		aid = request.args[0]
	except Exception, e:
		raise HTTP(404)
	assignment = db(db.events.type_==0)(db.events.id==aid).select()
	if len(assignment)<1:
		raise HTTP(404)
	else:
		assignment = assignment.first()

	registered = db(db.registered_courses.id==assignment.registered_course_id).select().first()
	course = db(db.courses.id==registered.course_id).select().first()
	submissions = []
	
	if auth.is_logged_in():
		sub_form = FORM(
			INPUT(_name="sub_name", _type="text"),
			INPUT(_name="sub_file", _type="file")
			)
		if sub_form.accepts(request.vars, formname='sub_form') and sub_form.vars.sub_name.strip()!="":
			sub = db.submissions.file_.store(sub_form.vars.sub_file.file, sub_form.vars.sub_file.filename)
			id = db.submissions.insert(file_=sub,name=sub_form.vars.sub_name,event_id=aid, user_id=auth.user_id)
			if id>0:
				session.flash = "Submission Successful!"
		
		submissions = db(db.submissions.user_id==auth.user.id)(db.submissions.event_id==aid).select()

	return dict(course=course, registered=registered, assignment=assignment, submissions=submissions)