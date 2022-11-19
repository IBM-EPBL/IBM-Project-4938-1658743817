
#* Import Statements
from flask import Flask, request, session
import ibm_db
import re
from flask import Response
from flask_db2 import DB2

#* Object Creation for flask
app = Flask(__name__)
app.secret_key = 'qwvhuf'
db = DB2(app)

#! Important: Connecting to IBM DB2 Database
try:
    conn = ibm_db.connect("DATABASE=bludb;HOSTNAME=b1bc1829-6f45-4cd4-bef4-10cf081900bf.c1ogj3sd0tgtu0lqde00.databases.appdomain.cloud;PORT=32304;SECURITY=SSL;SSLServerCertificate=DigiCertGlobalRootCA.crt;UID=dxh81184;PWD=pVgWe2O3uTuTaLuK", "", "")
    print("Connected")
except:
    print("Failed to connect")

#* Route to add new User
@app.route('/signup')
def newUser():
    if request.method == 'POST':
        username = request.form['username']
        email = request.form['emailaddresss']
        phonenumber = request.form['phonenumber']
        password = request.form['password']
        sql = "SELECT * FROM USER WHERE Name = ?"
        stmt = ibm_db.prepare(conn, sql)
        ibm_db.bind_param(stmt, 1, username)
        ibm_db.execute(stmt)
        account = ibm_db.fetch_assoc(stmt)
        print(account)
        if account:
            msg = 'Account already exists!'
        elif not re.match(r'[^]', email):
            msg = 'Invalid email address'
        elif not re.match(r'[A-Za-z0-9]+', username):
            msg = 'Name must contain characters and numbers'
        else:
            insert_sql = "INSERT into USER values (?, ?, ?, ?)"
            prep_stmt = ibm_db.prepare(conn, insert_sql)
            ibm_db.bind_param(prep_stmt, 1, username)
            ibm_db.bind_param(prep_stmt, 2, email)
            ibm_db.bind_param(prep_stmt, 3, phonenumber)
            ibm_db.bind_param(prep_stmt, 4, password)
            ibm_db.execute(prep_stmt)
            msg = 'You have successfully registered'
            return Response("{}", status=201, mimetype='application/json')
        return Response("{}", status=404, mimetype='application/json')


#* Route to login
@app.route('/')
@app.route('/login')
def login():
    if request.method == 'POST':
        name = request.form['username']
        password = request.form['password']
        sql = "SELECT * FROM USER WHERE name = ? AND password = ?"
        stmt = ibm_db.prepare(conn, sql)
        ibm_db.bind_param(stmt, 1, name)
        ibm_db.bind_param(stmt, 2, password)
        ibm_db.execute(stmt)
        user = ibm_db.fetch_assoc(stmt)
        if user:
            return Response(user, status=201, mimetype='application/json')
    return Response(user, status=404, mimetype='application/json')

#* Route to home
@app.route('/home', methods=['GET', 'POST'])
def home():
    global userid
    msg = ''

    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        sql = "SELECT * FROM ADMIN WHERE Name = ? AND Password = ?"
        stmt = ibm_db.prepare(conn, sql)
        ibm_db.bind_param(stmt, 1, username)
        ibm_db.bind_param(stmt, 2, password)
        ibm_db.execute(stmt)
        account = ibm_db.fetch_assoc(stmt)
        print(account)
        if account:
            session['loggedin'] = True
            session['id'] = account['NAME']
            userid = account['NAME']
            session['UserName'] = account['NAME']
        return

#* Route to logout
@app.route('/logout')
def logout():
    session.pop('Loggedin', None)
    session.pop('id', None)
    session.pop('UserName', None)
    return Response(user, status=201, mimetype='application/json')


#* Route to user details manipulation
@app.route('/user')
def user():
    sql = "SELECT * FROM USER"
    stmt = ibm_db.prepare(conn, sql)
    ibm_db.execute(stmt)
    user = ibm_db.fetch_assoc(stmt)
    return Response(user, status=201, mimetype='application/json')


#* Adding new zones to DB2
@app.route('/zones/new', methods=['POST'])
def zoneAdd():
    if request.method == 'POST':
        zid = request.form['id']
        latitude = request.form['latitude']
        longitude = request.form['longitude']
        zname = request.form['zname']
        sql = "SELECT * FROM ZONES WHERE id = ?"
        stmt = ibm_db.prepare(conn, sql)
        ibm_db.bind_param(stmt, 1, zid)
        ibm_db.execute(stmt)
        zone = ibm_db.fetch_assoc(stmt)
        print(zone)
        if zone:
            msg = 'Zone already exists!'
        else:
            insert_sql = "INSERT INTO ZONES VALUES (?, ?, ?, ?)"
            prep_stmt = ibm_db.prepare(conn, insert_sql)
            ibm_db.bind_param(prep_stmt, 1, zid)
            ibm_db.bind_param(prep_stmt, 2, latitude)
            ibm_db.bind_param(prep_stmt, 3, longitude)
            ibm_db.bind_param(prep_stmt, 4, zname)
            ibm_db.execute(prep_stmt)
            msg = 'You have successfully added'
            return Response("{msg : " + msg + "}", status=204, mimetype='application/json')
        return Response("{msg : " + msg + "}", status=404, mimetype='application/json')
        #* Route to add new User

        
@app.route('/signup')
def newUser():
    if request.method == 'POST':
        username = request.form['username']
        email = request.form['emailaddresss']
        phonenumber = request.form['phonenumber']
        password = request.form['password']
        sql = "SELECT * FROM USER WHERE Name = ?"
        stmt = ibm_db.prepare(conn, sql)
        ibm_db.bind_param(stmt, 1, username)
        ibm_db.execute(stmt)
        account = ibm_db.fetch_assoc(stmt)
        print(account)
        if account:
            msg = 'Account already exists!'
        elif not re.match(r'[^]', email):
            msg = 'Invalid email address'
        elif not re.match(r'[A-Za-z0-9]+', username):
            msg = 'Name must contain characters and numbers'
        else:
            insert_sql = "INSERT into USER values (?, ?, ?, ?)"
            prep_stmt = ibm_db.prepare(conn, insert_sql)
            ibm_db.bind_param(prep_stmt, 1, username)
            ibm_db.bind_param(prep_stmt, 2, email)
            ibm_db.bind_param(prep_stmt, 3, phonenumber)
            ibm_db.bind_param(prep_stmt, 4, password)
            ibm_db.execute(prep_stmt)
            msg = 'You have successfully registered'
            return Response("{}", status=204, mimetype='application/json')
        return Response("{}", status=404, mimetype='application/json')


@app.route('/zones')
def zoneDisplay():
    cur = db.connection.cursor()
    sql = "SELECT * FROM ZONES"
    cur.execute(sql)
    zone = cur.fetch()
    return zone;



