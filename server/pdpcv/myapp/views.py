from django.shortcuts import render
from django.http import JsonResponse
from .models import User
# Create your views here.
import subprocess
import uuid
from django.views.decorators.csrf import csrf_exempt

@csrf_exempt 
def login(request):
    if request.method == 'POST':
        data = request.POST
        username = data.get('username')
        password = data.get('password')

        user = User.objects.filter(username=username, password=password).first()
        if user and user.verifiedUser:
            # generate connection token
            token = uuid.uuid4()
            # add token to user
            user.token = token
            user.save()
            return JsonResponse({'message': 'Login successful', 'token': token}, status=200)
        elif user and not user.verifiedUser:
            # return ca certificate
            crt = user.certificate
            return JsonResponse({'message': 'You are not a verified user', 'crt': crt}, status=401)
    else:
        return JsonResponse({'message': 'Only POST requests are allowed'}, status=405)
    
def read_file(file):
    content = ""
    try:
        # Ouvre le fichier en mode lecture
        with open(file, 'r') as fichier:
            # Lit le contenu du fichier
            content = fichier.read()
    except FileNotFoundError:
        print(f"Le fichier {file} n'a pas été trouvé.")
    except IOError:
        print(f"Erreur lors de la lecture du fichier {file}.")
    
    return content

def create_certificates(user, immatriculation, public_key)-> str:
    # Create a certificate for the user
    # Définir les paramètres pour la demande de certificat
    common_name = user + immatriculation

    path = "../../Certificat/"
    ## met la clé publique dans un fichier
    with open(f"{path}client_public_key_{immatriculation}.pem", "w") as fichier:
        fichier.write(public_key)

    # Commande OpenSSL pour générer une demande de certificat signée par la CA
    #create_certificate = f"openssl req -new -key _{immatriculation}.pem -out {path}certificate_request_{immatriculation}.crt"
    #create_certificate = f"openssl rsa -in {path}client_public_key_{immatriculation}.pem-pubout -out client_public_key.pem"
    # Exécutez la commande OpenSSL
    subprocess.run(create_certificate, shell=True, check=True)
    # signer le certificat 
    sign_certificat = f"openssl x509 -req -in {path}certificate_request_{immatriculation}.crt -CA {path}ca_cert.pem -CAkey {path}ca_private_key.pem -CAcreateserial -out {path}/clients/client_certificate_{immatriculation}.pem"
    subprocess.run(sign_certificat, shell=True, check=True)
    # supprime la demande de certificat
    delete_certificate_request = f"rm {path}certificate_request_{immatriculation}.crt"
    subprocess.run(delete_certificate_request, shell=True, check=True)
    # supprime la clé publique
    delete_public_key = f"rm {path}client_public_key_{immatriculation}.pem"
    subprocess.run(delete_public_key, shell=True, check=True)

    return read_file(f"{path}certificate_request_{immatriculation}.crt")

   

@csrf_exempt 
def register(request):
    if request.method == 'POST':
        data = request.POST
        username = data.get('username')
        password = data.get('password')
        immatriculation = data.get('immatriculation')
        phone = data.get('phone')
        public_key = data.get('public_key') 
        #certificat = create_certificates(username, immatriculation, public_key)

        user = User.objects.create(username=username, password=password, immatriculation=immatriculation, phone=phone, public_key=public_key)

        return JsonResponse({'message': 'User created successfully'}, status=200)
    else:
        return JsonResponse({'message': 'Only POST requests are allowed'}, status=405)
    
def user_certificate_is_valid(certificat):
    path = "../../Certificat/"
    ## met le certificat dans un fichier
    with open(f"{path}tmp.pem", "w") as fichier:
        fichier.write(certificat)
    # Vérifiez si le certificat est valide
    verify_certificate = f"openssl verify -CAfile {path}ca_cert.pem {path}tmp.pem"
    # Exécutez la commande OpenSSL
    result = subprocess.run(verify_certificate, shell=True, check=True)
    # supprime le certificat
    delete_certificate = f"rm {path}tmp.pem"
    return result.returncode == 0

def request(request):
    if request.method == 'POST':
        data = request.POST
        token = data.get('token')
        immatriculation_target = data.get('target')

        user = User.objects.filter(token=token).first()
        certificat = user.certificate
        user_target = User.objects.filter(immatriculation=immatriculation_target).first()

        ## Verifier le certifcat
        is_valid = user_certificate_is_valid(certificat)
        ## Envoie de la demande d'ami si le certificat est valide
        friend_request = FriendRequest.objects.create(user_request=user.id, user_target=user_target.id, state='en_attente')
        return JsonResponse({'message': 'Request successful'})
    else:
        return JsonResponse({'message': 'Only POST requests are allowed'}, status=405)
