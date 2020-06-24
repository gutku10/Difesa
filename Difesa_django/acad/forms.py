from django import forms
from .models import Resource

class ResourceForm(forms.ModelForm):
    class Meta:
        model=Resource
        fields = ['title','author','pdf','cover']