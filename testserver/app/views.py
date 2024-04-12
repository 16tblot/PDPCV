from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

# Create your views here.

def index(request):
    return HttpResponse("Hello, world. You're at the app index.")

@csrf_exempt
def login(request):
    return HttpResponse(request.body)

@csrf_exempt
def signup(request):
    return HttpResponse("Hello, world. You're at the polls index.")

