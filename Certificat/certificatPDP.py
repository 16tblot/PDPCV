from OpenSSL import crypto

# Création de la clé privée de la CA racine
ca_key = crypto.PKey()
ca_key.generate_key(crypto.TYPE_RSA, 2048)

# Création du certificat de la CA racine
ca_cert = crypto.X509()
ca_cert.set_serial_number(1)
ca_cert.gmtime_adj_notBefore(0)
ca_cert.gmtime_adj_notAfter(10*365*24*60*60)  # Valide pour 10 ans
ca_cert.set_issuer(ca_cert.get_subject())

# Ajout des détails du sujet
ca_cert.get_subject().C = "FR"
ca_cert.get_subject().ST = "Bordeaux"
ca_cert.get_subject().L = "Bordeaux"
ca_cert.get_subject().O = "Université de Bordeaux"
ca_cert.get_subject().OU = "Certification Authority"
ca_cert.get_subject().CN = "ChatCA"

ca_cert.set_pubkey(ca_key)

# Ajout de l'extension basicConstraints pour une CA
ca_cert.add_extensions([
  crypto.X509Extension(b"basicConstraints", True, b"CA:TRUE, pathlen:0"),
  crypto.X509Extension(b"keyUsage", True, b"keyCertSign, cRLSign"),
  crypto.X509Extension(b"subjectKeyIdentifier", False, b"hash", subject=ca_cert),
])

ca_cert.sign(ca_key, 'sha256')

# Exportation de la clé privée de la CA et du certificat
with open("ca_private_key.pem", "wb") as f:
    f.write(crypto.dump_privatekey(crypto.FILETYPE_PEM, ca_key))

with open("ca_cert.pem", "wb") as f:
    f.write(crypto.dump_certificate(crypto.FILETYPE_PEM, ca_cert))
