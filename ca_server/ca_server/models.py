from django.db import models

class Certificate(models.Model):
    user_id = models.IntegerField()
    certificate_data = models.TextField()
    status = models.CharField(max_length=20, default='pending')
