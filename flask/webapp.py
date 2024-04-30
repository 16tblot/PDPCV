#!/usr/bin/python3
# Create your views here.
import sqlite3

import time
from flask import Flask, request, jsonify
import sys, requests, secrets
from models import db, User, Connection
from flask_sqlalchemy import SQLAlchemy
    
#NE PAS MODIFIER LA LIGNE SUIVANTE
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.sqlite3'
db.init_app(app)

@app.route('/register', methods=['POST'])
def register():
    with app.app_context():
        data = request.json
        username = data.get('username')
        password = data.get('password')

        if User.query.filter_by(username=username).first():
            return jsonify({'message': 'Username already exists'}), 400

        new_user = User(username=username)
        new_user.set_password(password)
        db.session.add(new_user)
        db.session.commit()

        return jsonify({'message': 'User registered successfully'}), 201

@app.route('/login', methods=['POST'])
def login():
    with app.app_context():
        data = request.json
        username = data.get('username')
        password = data.get('password')

        user = User.query.filter_by(username=username).first()

        if not user or not user.check_password(password):
            return jsonify({'message': 'Invalid username or password'}), 401

        token = secrets.token_urlsafe(16)
        user.token = token
        db.session.commit()

        if user.is_valid:
            return jsonify({'message': 'Login successful', 'token': token}), 200
        else:
            return jsonify({'message': 'You are not certified', 'token': token}), 200

@app.route('/update', methods=['POST'])
def update():
    with app.app_context():
        data = request.json
        token = data.get('token')

        user = User.query.filter_by(token=token).first()

        if user:
            # Vérifie si les champs existent dans la requête
            if 'immatriculation' in data:
                user.immatriculation = data['immatriculation']
            if 'phone' in data:
                user.phone = data['phone']

            # Met à jour la base de données
            db.session.commit()
            
            return jsonify({'message': 'User updated successfully'}), 200
        else:
            return jsonify({'message': 'User not found'}), 404
    
@app.route('/delete', methods=['DELETE'])
def delete_user():
    with app.app_context():
        data = request.json
        token = data.get('token')

        user = User.query.filter_by(token=token).first()

        if user:
            db.session.delete(user)
            db.session.commit()
            return jsonify({'message': 'User deleted successfully'}), 200
        else:
            return jsonify({'error': 'User not found'}), 404


@app.route('/request_certificate', methods=['GET'])
def request_certificate():
    user_id = request.args.get('user_id')
    certificate = Certificate.query.filter_by(user_id=user_id).first()
    if certificate:
        return send_file(certificate.certificate_data, as_attachment=True)
    else:
        certificate_data = generate_certificate()
        new_certificate = Certificate(user_id=user_id, certificate_data=certificate_data)
        db.session.add(new_certificate)
        db.session.commit()
        return send_file(certificate_data, as_attachment=True)

@app.route('/send_connection_request', methods=['POST'])
def send_connection_request():
    sender_token = request.json.get('sender_token')
    receiver_immat = request.json.get('receiver_immatriculation')

    sender = User.query.filter_by(token=sender_token).first()
    receiver = User.query.filter_by(immatriculation=receiver_immat).first()
    if not sender:
        return jsonify({'message': 'Utilisateur émetteur non trouvé'}), 404
    
    if not receiver:
        return jsonify({'message': 'Utilisateur destinataire non trouvé'}), 404

    existing_connection = Connection.query.filter_by(user_id_1=sender.id, user_id_2=receiver.id).first()
    reverse_connection = Connection.query.filter_by(user_id_1=receiver.id, user_id_2=sender.id).first()

    if existing_connection or reverse_connection:
        return jsonify({'message': 'Une demande de connexion existe déjà entre ces utilisateurs'}), 400
    
    if not sender.is_valid:
        return jsonify({'message': 'Utilisateur émetteur non certifié'}), 400
    
    if not receiver.is_valid:
        return jsonify({'message': 'Utilisateur destinataire non certifié'}), 400
    
    new_connection = Connection(user1=sender, user2=receiver, status='pending')  
    db.session.add(new_connection)
    db.session.commit()

    return jsonify({'message': 'Demande de connexion envoyée avec succès'}), 200

@app.route('/get_all_connections', methods=['POST'])
def get_all_connections():
    token = request.json.get('token')
    userMain = User.query.filter_by(token=token).first()

    # Récupérer toutes les connexions de l'utilisateur spécifié
    connections_receive = Connection.query.filter(Connection.user_id_2 == userMain.id).all()
    connections_send = Connection.query.filter(Connection.user_id_1 == userMain.id).all()
    # Formatage des données des connexions
    connection_list_s = []
    connection_list_r = []  

    for connection in connections_send:
        user = User.query.filter_by(id=connection.user_id_2).first()
        connection_data = {
            'username': user.username,
            'immatriculation': user.immatriculation,
            'status': connection.status
        }
        connection_list_r.append(connection_data)

    for connection in connections_receive:
        user = User.query.filter_by(id=connection.user_id_1).first()
        connection_data = {
            'username': user.username,
            'immatriculation': user.immatriculation,
            'status': connection.status
        }
        connection_list_s.append(connection_data)
    
    return jsonify({'receive': connection_list_s, 'send': connection_list_r}), 200

@app.route('/accept_connection', methods=['POST'])
def accept_connection():
    token = request.json.get('token')
    user = User.query.filter_by(token=token).first()
    target_immat = request.json.get('target_immatriculation')
    user_target = User.query.filter_by(immatriculation=target_immat).first()
    # Récupérer la connexion à accepter
    connection = Connection.query.filter(
        (Connection.user_id_1 == user_target.id) & (Connection.user_id_2 == user.id)
    ).first()
    
    if not connection:
        return jsonify({'message': 'Connexion non trouvée'}), 404

    # Mettre à jour le statut de la connexion à 'accepted'
    connection.status = 'accepted'
    db.session.commit()

    return jsonify({'message': 'Connexion acceptée avec succès'}), 200

@app.route('/reject_connection', methods=['POST'])
def reject_connection():
    token = request.json.get('token')
    user = User.query.filter_by(token=token).first()
    target_immat = request.json.get('target_immatriculation')
    user_target = User.query.filter_by(immatriculation=target_immat).first()
    
    # Récupérer la connexion à rejeter en vérifiant qu'elle est en état 'pending'
    connection = Connection.query.filter(
        (Connection.user_id_2 == user.id) & 
        (Connection.user_id_1 == user_target.id) &
        (Connection.status == 'pending')
    ).first()

    if not connection:
        return jsonify({'message': 'Connexion non trouvée ou déjà traitée'}), 404

    # Mettre à jour le statut de la connexion à 'rejected'
    connection.status = 'rejected'
    db.session.commit()

    return jsonify({'message': 'Connexion rejetée avec succès'}), 200

@app.route('/delete_connection', methods=['POST'])
def delete_connection():
    token = request.json.get('token')
    user = User.query.filter_by(token=token).first()
    target_immat = request.json.get('target_immatriculation')
    user_target = User.query.filter_by(immatriculation=target_immat).first()

    connection = Connection.query.filter(
        (Connection.user_id_2 == user_target.id) & 
        (Connection.user_id_1 == user.id) 
    ).first()

    if not connection:
        connection = Connection.query.filter(
        (Connection.user_id_1 == user_target.id) & 
        (Connection.user_id_2 == user.id) 
    ).first()
    
    if not connection:
        return jsonify({'message': 'Connexion non trouvée ou déjà traitée'}), 404

    # Supprimer la connexion de la base de données
    db.session.delete(connection)
    db.session.commit()

    return jsonify({'message': 'Connexion supprimée avec succès'}), 200

@app.route('/get_friendlist', methods=['POST'])
def get_friendlist():
    token = request.json.get('token')
    user = User.query.filter_by(token=token).first()

    connections = []  

    connections_receive = Connection.query.filter((Connection.user_id_2 == user.id) & (Connection.status == 'accepted')).all()
    connections_send = Connection.query.filter((Connection.user_id_1 == user.id) & (Connection.status == 'accepted')).all()
    
    for connection in connections_send:
        user = User.query.filter_by(id=connection.user_id_2).first()
        connection_data = {
            'immatriculation': user.immatriculation,
            'phone': user.phone
        }
        connections.append(connection_data)

    for connection in connections_receive:
        user = User.query.filter_by(id=connection.user_id_1).first()
        connection_data = {
            'immatriculation': user.immatriculation,
            'phone': user.phone
        }
        connections.append(connection_data)
    
    
    if not user:
        return jsonify({'message': 'Utilisateur non trouvée'}), 404

    return jsonify({'message': 'Liste des utilisateurs ajoutés dans votre liste de contact', 'friend': connections}), 200

#NE SURTOUT PAS MODIFIER     
if __name__ == "__main__":
   app.app_context().push()
   db.create_all()
   app.run(debug=True, host='0.0.0.0', port='5000')
