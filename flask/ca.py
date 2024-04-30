from flask import Flask, request, jsonify, send_file
from OpenSSL import crypto

def generate_certificate():
    # Charger la clé privée de la CA
    with open("ca_private_key.pem", "rb") as f:
        ca_key = crypto.load_privatekey(crypto.FILETYPE_PEM, f.read())

    # Charger le certificat de la CA
    with open("ca_cert.pem", "rb") as f:
        ca_cert = crypto.load_certificate(crypto.FILETYPE_PEM, f.read())

    # Créer un nouveau certificat pour le user
    cert = crypto.X509()
    cert.set_serial_number(2)  # Choisir un numéro de série unique
    cert.gmtime_adj_notBefore(0)
    cert.gmtime_adj_notAfter(365*24*60*60)  # Valide pour 1 an

    # Ajouter les détails du sujet (user) au certificat
    cert.get_subject().C = "FR"
    cert.get_subject().ST = "Bordeaux"
    cert.get_subject().L = "Bordeaux"
    cert.get_subject().O = "Université de Bordeaux"
    cert.get_subject().OU = "Client"
    cert.get_subject().CN = "Client Certificate"  # Nom du user


    # Ajouter l'extension basicConstraints pour un certificat user
    cert.add_extensions([
        crypto.X509Extension(b"basicConstraints", True, b"CA:FALSE"),
        crypto.X509Extension(b"subjectKeyIdentifier", False, b"hash", subject=cert),
    ])

    # Signer le certificat avec la clé privée de la CA
    cert.sign(ca_key, 'sha256')

    # Exporter le certificat en format PEM
    cert_pem = crypto.dump_certificate(crypto.FILETYPE_PEM, cert)
    return cert_pem