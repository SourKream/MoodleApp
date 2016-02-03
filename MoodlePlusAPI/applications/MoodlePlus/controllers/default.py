# -*- coding: utf-8 -*-
# this file is released under public domain and you can use without limitations

#########################################################################
## This is a sample controller
## - index is the default action of any application
## - user is required for authentication and authorization
## - download is for downloading files uploaded in the db (does streaming)
#########################################################################

def index():
    """
    example action using the internationalization operator T and flash
    rendered by views/default/index.html or views/generic.html

    if you need a simple wiki simply replace the two lines below with:
    return auth.wiki()
    """
    response.flash = T("Welcome on Moodle+")
    return dict(noti_count=4)

def grades():
    grades = db(db.grades.user_id==auth.user.id).select()
    courses = []
    for grade in grades:
        courses.append(db(db.courses.id==grade.registered_course_id.course_id).select().first())
    return dict(grades=grades, courses=courses)

def notifications():
    noti = db(db.notifications.user_id==auth.user.id).select(orderby=~db.notifications.created_at)
    db(db.notifications.user_id==auth.user.id).update(is_seen=1)
    return dict(notifications=noti)


def logged_in():
    return dict(success=auth.is_logged_in(), user=auth.user)

def logout():
    return dict(success=True, loggedout=auth.logout())

def user():
    """
    exposes:
    http://..../[app]/default/user/login
    http://..../[app]/default/user/logout
    http://..../[app]/default/user/register
    http://..../[app]/default/user/profile
    http://..../[app]/default/user/retrieve_password
    http://..../[app]/default/user/change_password
    http://..../[app]/default/user/manage_users (requires membership in
    use @auth.requires_login()
        @auth.requires_membership('group name')
        @auth.requires_permission('read','table name',record_id)
    to decorate functions that need access control
    """
    return dict(form=auth())


@cache.action()
def download():
    """
    allows downloading of uploaded files
    http://..../[app]/default/download/[filename]
    """
    return response.download(request, db)


def call():
    """
    exposes services. for example:
    http://..../[app]/default/call/jsonrpc
    decorate with @services.jsonrpc the functions to expose
    supports xml, json, xmlrpc, jsonrpc, amfrpc, rss, csv
    """
    return service()


@request.restful()
def api():
    response.view = 'generic.'+request.extension
    def GET(*args,**vars):
        patterns = 'auto'
        parser = db.parse_as_rest(patterns,args,vars)
        if parser.status == 200:
            return dict(content=parser.response)
        else:
            raise HTTP(parser.status,parser.error)
    def POST(table_name,**vars):
        return db[table_name].validate_and_insert(**vars)
    def PUT(table_name,record_id,**vars):
        return db(db[table_name]._id==record_id).update(**vars)
    def DELETE(table_name,record_id):
        return db(db[table_name]._id==record_id).delete()
    return dict(GET=GET, POST=POST, PUT=PUT, DELETE=DELETE)

def login():
    userid = request.vars.userid
    password = request.vars.password
    user = auth.login_bare(userid,password)
    return dict(success=False if not user else True, user=user)


def api():
    return """
Moodle Plus API (ver 1.0)
-------------------------

Url: /default/login.json
Input params:
    userid: (string)
    password: (string)
Output params:
    success: (boolean) True if login success and False otherwise
    user: (json) User details if login is successful otherwise False


Url: /default/logout.json
Input params:
Output params:
    success: (boolean) True if logout successful and False otherwise


Url: /courses/list.json
Input params:
Output params:
    current_year: (int)
    current_sem: (int) 0 for summer, 1 break, 2 winter
    courses: (List) list of courses
    user: (dictionary) user details

Url: /threads/new.json
Input params:
    title: (string) can't be empty
    description: (string) can't be empty
    course_code: (string) must be a registered courses
Output params:
    success: (bool) True or False depending on whether thread was posted
    thread_id: (bool) id of new thread created

    """