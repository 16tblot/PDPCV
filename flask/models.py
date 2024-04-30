from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy()

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(100), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    immatriculation = db.Column(db.String(20))
    phone = db.Column(db.String(20))
    public_key = db.Column(db.String(1000))
    token = db.Column(db.String(100))
    is_valid = db.Column(db.Boolean, default=False) 
    registration = db.Column(db.LargeBinary)

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def __str__(self):
        return f"User(username='{self.username}', immatriculation='{self.immatriculation}')"

class Connection(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id_1 = db.Column(db.Integer, db.ForeignKey('user.id'))
    user_id_2 = db.Column(db.Integer, db.ForeignKey('user.id'))
    status = db.Column(db.String)  # Champ pour le statut de la connexion

    # Définir les relations avec la classe User
    user1 = db.relationship('User', foreign_keys=[user_id_1])
    user2 = db.relationship('User', foreign_keys=[user_id_2])