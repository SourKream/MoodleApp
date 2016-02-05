def isUserRegisteredForCourse(course_code):
	return registered_courses(course_code)>0

def registeredForCourse(course_code):
	if type(course_code)==str:
		course = db(db.courses.code==course_code).select()
		if len(course)<1:
			return -1
		reg_course = db(db.registered_courses.course_id==course.first().id)(db.registered_courses.year_==get_current_year())(db.registered_courses.semester==get_current_sem()).select()
		if len(reg_course)<1:
			return -1
        if auth.user.type_==0:
    		stu_course = db(db.student_registrations.student_id==auth.user.id)(db.student_registrations.registered_course_id==reg_course.first().id).select()
    		if len(stu_course)<1:
    			return -1
    		return reg_course.first().id
        else:
            return reg_course.first().id
	return -1

def getAllUsersForCourseCode(course_code):
    if type(course_code)==str:
        course = db(db.courses.code==course_code).select()
        if len(course)<1:
            return []
        reg_course = db(db.registered_courses.course_id==course.first().id)(db.registered_courses.year_==get_current_year())(db.registered_courses.semester==get_current_sem()).select()
        if len(reg_course)<1:
            return []    
        stu_course = db(db.student_registrations.registered_course_id==reg_course.first().id).select()
        all_users = map(lambda x: x.student_id, stu_course)
        all_users.append(reg_course.first().professor)
        return set(all_users)
    
    return []

def getValidThreadTitle(thread):
	return (thread.title.title() if thread.title != None else thread.description[:20]).title()


def getProfileLink(user_id):
    user = db(db.users.id==user_id).select().first()
    return XML('<a href="/users/user/%d">%s</a>'%(user_id, (user.first_name+" "+user.last_name).title()))


def timeHuman(date_time):
    """
    converts a python datetime object to the 
    format "X days, Y hours ago"

    @param date_time: Python datetime object

    @return:
        fancy datetime:: string
    """
    import datetime
    current_datetime = datetime.datetime.now()
    delta = str(current_datetime - date_time)
    if delta.find(',') > 0:
        days, hours = delta.split(',')
        days = int(days.split()[0].strip())
        hours, minutes = hours.split(':')[0:2]
    else:
        hours, minutes = delta.split(':')[0:2]
        days = 0
    days, hours, minutes = int(days), int(hours), int(minutes)
    datelets =[]
    years, months, xdays = None, None, None
    plural = lambda x: 's' if x!=1 else ''
    if days >= 365:
        years = int(days/365)
        datelets.append('%d year%s' % (years, plural(years)))
        days = days%365
    if days >= 30 and days < 365:
        months = int(days/30)
        datelets.append('%d month%s' % (months, plural(months)))        
        days = days%30
    if not years and days > 0 and days < 30:
        xdays =days
        datelets.append('%d day%s' % (xdays, plural(xdays)))        
    if not (months or years) and hours != 0:
        datelets.append('%d hour%s' % (hours, plural(hours)))        
    if not (xdays or months or years):
        datelets.append('%d minute%s' % (minutes, plural(minutes)))        
    return ', '.join(datelets) + ' ago.'