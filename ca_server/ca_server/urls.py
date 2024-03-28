from django.urls import path
from . import views

urlpatterns = [
    path('submit-certificate-request/', views.submit_certificate_request, name='submit_certificate_request'),
]
