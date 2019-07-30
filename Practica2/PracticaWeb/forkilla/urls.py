from django.conf.urls import url

from . import views

listOfAddresses = ["sd2019-f2-forkilla.herokuapp.com", "sd2019-f4-forkilla.herokuapp.com", "sd2019-f8-forkilla.herokuapp.com",
                   "sd2019-forkillaf6.herokuapp.com","sd2019-f7-forkilla.herokuapp.com", "sd2019-forkillaf10.herokuapp.com"

                   ]
#listOfAddresses = ["localhost"]

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^restaurants/$', views.restaurants, name='restaurants'),
    url(r'^restaurants/(?P<city>.*)/$', views.restaurants, name='restaurants'),
    url(r'^restaurants/(?P<city>.*)/(?P<category>.*)$', views.restaurants, name='restaurants'),
    url(r'^restaurant/(?P<restaurant_number>.*)', views.details, name='details'), # Poner el /
    url(r'^reservation/$', views.reservation, name='reservation'),
    url(r'^checkout/$', views.checkout, name='checkout'),
    url(r'^base/$', views.checkout, name='base'),
    url(r'^reviews/$', views.reviews, name='reviews'),
    url(r'^register/$', views.register, name='register'),
    url(r'^user_space/$', views.user_space, name='user_space'),
    url(r'^delreservation/$', views.delreservation, name='delreservation'),

    # url('restaurants/$', views.RestaurantList.as_view()),
    # url('restaurants/<int:pk>/', views.RestaurantDetail.as_view()),
    url(r'^comparator/$', views.comparator, {'ips': listOfAddresses}, name='comparator'),
]

# from django.conf.urls import include

# urlpatterns += [
#    url('api-auth/', include('rest_framework.urls')),
# ]
