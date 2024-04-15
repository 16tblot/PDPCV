import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'pdpcv.settings')
django.setup()

from myapp.models import User

def add_example_users():
    example_users = [
        {'username': 'user1', 'password': 'password1', 'immatriculation': 'IMM001', 'phone': '123456789', 'address': '123 Street, City', 'verifiedUser': True},
        {'username': 'user2', 'password': 'password2', 'immatriculation': 'IMM002', 'phone': '987654321', 'address': '456 Avenue, Town', 'verifiedUser': False},
        # Ajoutez autant d'utilisateurs que nécessaire avec leurs données
    ]

    for user_data in example_users:
        user = User.objects.create(**user_data)
        print(f"User '{user.username}' added.")

if __name__ == "__main__":
    add_example_users()
