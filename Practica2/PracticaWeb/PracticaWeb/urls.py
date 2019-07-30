"""PracticaWeb URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf import settings
from django.conf.urls import url, include
from django.conf.urls.static import static
from django.contrib import admin
from django.contrib.auth.views import login, logout
from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from django.views.static import serve
from rest_framework import routers

from forkilla import views

router = routers.DefaultRouter()
router.register(r'restaurants', views.RestaurantViewSet)


urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^forkilla/', include('forkilla.urls')),
    url(r'', include('forkilla.urls')),

    url(r'^accounts/login/$', login, name='login'),
    url(r'^accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^forkilla/accounts/login/$', login, name='login'),
    url(r'^forkilla/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^reservations/accounts/login/$', login, name='login'),
    url(r'^reservations/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^reviews/accounts/login/$', login, name='login'),
    url(r'^reviews/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^details/accounts/login/$', login, name='login'),
    url(r'^details/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^restaurant/accounts/login/$', login, name='login'),
    url(r'^restaurant/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^comparator/accounts/login/$', login, name='login'),
    url(r'^comparator/accounts/logout/$', logout, {'next_page': '/'}, name='logout'),

    url(r'^media/(?P<path>.*)$', serve, {'document_root': settings.MEDIA_ROOT}),
    url(r'^static/(?P<path>.*)$', serve, {'document_root': settings.STATIC_ROOT, 'show_indexes': settings.DEBUG}),

    url(r'^api/', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework'))


] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

handler404 = views.handler404
handler500 = views.handler500


urlpatterns += staticfiles_urlpatterns()
urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)


