from django.db import models

# Create your models here.

class User(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=100, unique=True)
    password = models.CharField(max_length=100)
    immatriculation = models.CharField(max_length=100, unique=True)
    phone = models.CharField(max_length=100, unique=True)
    public_key = models.CharField(max_length=100, unique=True)
    token = models.CharField(max_length=100, unique=True, null=True)
    verifiedUser = models.BooleanField(default=False)


class FriendRequest(models.Model):
    ETAT_CHOICES = (
        ('en_attente', 'En attente'),
        ('acceptee', 'Acceptée'),
        ('refusee', 'Refusée'),
    )

    user_request = models.IntegerField()
    user_target = models.IntegerField()
    date = models.DateTimeField(auto_now_add=True)
    state = models.CharField(max_length=20, choices=ETAT_CHOICES)