from django.shortcuts import render,redirect
from .models import Resource
from django.contrib.auth.models import User
from django.core.files.storage import FileSystemStorage
from .forms import ResourceForm 
from django.views.generic import (
    ListView,
    DetailView,
    CreateView,
    UpdateView,
    DeleteView
)



# def upload(request):
    
#     if request.method == 'POST':
#         uploaded_file=request.FILES('document')
#         fs = FileSystemStorage()
#         name = fs.save(uploaded_file.name,uploaded_file)
    
#     # context = {
#     #     'posts': Post.objects.all()                                               #using posts in models.py ehich we can add through python shell
#     # }
#     return render(request,'acad/home.html',context)

  
class ResourceListView(ListView):
    model = Resource
    template_name = 'acad/resource_list.html'
    context_object_name = 'resources'
    


# def resource_list(request):
#     resources = Resource.objects.all()
#     return render(request,'acad/resource_list.html',{'resources':resources})

def upload_resource(request):
    if request.method == 'POST':
        form = ResourceForm(request.POST,request.FILES)
        if form.is_valid():
            form.save()
            return redirect('resource_list')
    else:
        form = ResourceForm()

    return render(request,'acad/upload_resource.html',{'form':form})

# def model_form_upload(request):
#     if request.method == 'POST':
#         form = DocumentForm(request.POST, request.FILES)
#         if form.is_valid():
#             form.save()
#             return redirect('home')
#     else:
#         form = DocumentForm()
#     return render(request, 'core/model_form_upload.html', {
#         'form': form
#     })
  