from django.http import JsonResponse
from .models import Certificate
from OpenSSL import crypto
from django.http import HttpResponse

def generate_certificate(request):
    # Générer une nouvelle paire de clés pour le certificat
    key = crypto.PKey()
    key.generate_key(crypto.TYPE_RSA, 2048)

    # Créer un certificat X.509
    cert = crypto.X509()
    cert.get_subject().CN = "Client Certificate"
    cert.set_serial_number(1000)
    cert.gmtime_adj_notBefore(0)
    cert.gmtime_adj_notAfter(365 * 24 * 60 * 60)  # Valide pour un an
    cert.set_issuer(cert.get_subject())
    cert.set_pubkey(key)

    # Ajouter des extensions au certificat si nécessaire
    cert.add_extensions([
        crypto.X509Extension(b"basicConstraints", True, b"CA:FALSE"),
        crypto.X509Extension(b"subjectKeyIdentifier", False, b"hash", subject=cert),
    ])

    # Signer le certificat avec la clé privée du serveur
    ca_key = crypto.load_privatekey(crypto.FILETYPE_PEM, open("ca_private_key.pem").read())
    cert.sign(ca_key, 'sha256')

    # Exporter le certificat
    cert_pem = crypto.dump_certificate(crypto.FILETYPE_PEM, cert)

    # Envoyer le certificat en réponse à la requête du client
    response = HttpResponse(cert_pem, content_type="application/x-x509-ca-cert")
    response['Content-Disposition'] = 'attachment; filename="client_certificate.pem"'
    return response


def submit_certificate_request(request):
    if request.method == 'POST':
        user_id = request.POST.get('user_id')
        certificate_data = request.POST.get('certificate_data')

        certificate = Certificate.objects.create(
            user_id=user_id,
            certificate_data=certificate_data,
            status='pending'
        )

        return JsonResponse({'status': 'success', 'message': 'Certificate request submitted successfully.'})
    else:
        return JsonResponse({'status': 'error', 'message': 'Invalid request method.'})

