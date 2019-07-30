from django.contrib.auth import authenticate, login
from django.contrib.auth.decorators import login_required
from django.contrib.auth.forms import UserCreationForm
from django.db.models import Avg
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.urls import reverse
from django.contrib.messages import constants as messages

from .models import Restaurant, ViewedRestaurants, Reservation, ExistingReviews, Review, RestaurantInsertDate
from .forms import ReservationForm, ReviewForm


# global variable


# Create your views here.

def index(request):
    # return HttpResponse("Hello, world. You're at the forkilla home.")
    return restaurants(request)


def details(request, restaurant_number=""):
    try:
        viewedrestaurants = _check_session(request)

        restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
        _update_rate(restaurant)
        lastviewed = RestaurantInsertDate(viewedrestaurants=viewedrestaurants, restaurant=restaurant)
        lastviewed.save()

        # viewedrestaurants.restaurant.add(restaurant)

        reviews = Review.objects


    except (KeyError, Restaurant.DoesNotExist, AttributeError):
        return HttpResponse("The specified Restaurant does not exist!")

    context = {
        'restaurant': restaurant,
        'viewedrestaurants': viewedrestaurants,
        'reviews': reviews
    }
    return render(request, 'forkilla/details.html', context)


def restaurants(request, city="", category=""):
    promoted = False

    viewedrestaurants = _check_session(request)

    query = request.GET.get('q')
    if query:
        restaurants = Restaurant.objects.filter(city=query)
    else:

        if city:
            restaurants = Restaurant.objects.filter(city=city)
            if category:
                restaurants = restaurants.filter(category=category)
        else:
            restaurants = Restaurant.objects.filter(is_promot="True")
            promoted = True

    context = {
        'city': city,
        'category': category,
        'restaurants': restaurants,
        'promoted': promoted,
        'viewedrestaurants': viewedrestaurants,
        'search_result': query,
    }

    return render(request, 'forkilla/restaurants.html', context)


@login_required
def reservation(request):
    the_user = request.user
    if not the_user.is_authenticated():
        return HttpResponseRedirect(reverse('/accounts/login/?next=/forkilla/reviews'))

    else:
        try:
            if request.method == "POST":
                form = ReservationForm(request.POST)
                if form.is_valid():
                    resv = form.save(commit=False)
                    restaurant_number = request.session["reserved_restaurant"]
                    resv.restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
                    resv.user = the_user

                    check_d = _check_disponibility(resv)

                    if check_d:
                        return check_d

                    resv.save()

                    request.session["reservation"] = resv.id
                    request.session["result"] = "OK"

                else:
                    request.session["result"] = form.errors
                return HttpResponseRedirect(reverse('checkout'))


            elif request.method == "GET":
                restaurant_number = request.GET["reservation"]

                restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)

                request.session["reserved_restaurant"] = restaurant_number

                form = ReservationForm()
                viewedrestaurants = _check_session(request)
                # viewedrestaurants.restaurant.add(restaurant)

                context = {

                    'restaurant': restaurant,
                    'form': form,
                    'viewedrestaurants': viewedrestaurants

                }

        except Restaurant.DoesNotExist:
            return HttpResponse("Restaurant Does not exists")

        return render(request, 'forkilla/reservation.html', context)


def _check_session(request):
    try:
        if "viewedrestaurants" not in request.session:
            viewedrestaurants = ViewedRestaurants()
            viewedrestaurants.save()
            request.session["viewedrestaurants"] = viewedrestaurants.id_vr
        else:
            viewedrestaurants = ViewedRestaurants.objects.get(id_vr=request.session["viewedrestaurants"])

    except (KeyError, Reservation.DoesNotExist, AttributeError):
        return HttpResponse("The specified Viewed Restaurant does not exist!")

    return viewedrestaurants


def _check_session_reviews(request):
    try:
        if "existingreviews" not in request.session:
            existingreviews = ExistingReviews()
            existingreviews.save()
            request.session["existingreviews"] = existingreviews.id
        else:
            existingreviews = ExistingReviews.objects.get(id=request.session["existingreviews"])

    except (KeyError, Review.DoesNotExist, AttributeError):
        return HttpResponse("The specified Review does not exist!")

    return existingreviews


def checkout(request):
    try:
        reservation = Reservation.objects.get(id=request.session["reservation"])


    except (KeyError, Reservation.DoesNotExist, AttributeError):
        return HttpResponse("The specified Reservation does not exist!")

    viewedrestaurants = _check_session(request)

    context = {
        'reservation': reservation,
        'viewedrestaurants': viewedrestaurants
    }
    return render(request, 'forkilla/checkout.html', context)


def _check_disponibility(reservation):
    reservations_filtered = Reservation.objects.filter(restaurant=reservation.restaurant,
                                                       time_slot=reservation.time_slot)

    total_people_reserved = 0
    for r in reservations_filtered:
        total_people_reserved += r.num_people

    result = reservation.restaurant.restaurant_capacity - total_people_reserved

    if result < reservation.num_people:
        return HttpResponse("There is only  " + str(result) + " spaces available!")


def base(request):
    viewedrestaurants = ViewedRestaurants.objects

    context = {
        'viewedrestaurants': viewedrestaurants
    }

    return render(request, 'forkilla/base.html', context)


@login_required
def reviews(request):
    the_user = request.user
    if not the_user.is_authenticated():
        return HttpResponseRedirect(reverse('/accounts/login/?next=/forkilla/reviews'))

    else:
        try:
            if request.method == "POST":
                form = ReviewForm(request.POST)  # recuperamos informacion de la review (rating y comment)
                if form.is_valid():
                    review = form.save(commit=False)  # guardamos review pero sin hacer commit
                    restaurant_number = request.session[
                        "review_made"]  # recuperamos id del restaurante y lo guardamos en restaurant_number

                    review.restaurant = Restaurant.objects.get(
                        restaurant_number=restaurant_number)  # obtenemos el restaurante de la review a partir de restaurant_number
                    review.user = the_user
                    review.save()  # ahora si hacemos commit de la review

                    request.session["review"] = review.id  # guardamos en review el id de la review
                    request.session["reviewresult"] = "OK"


                else:
                    request.session["reviewresult"] = form.errors
                return HttpResponseRedirect(reverse('restaurants'))


            elif request.method == "GET":
                restaurant_number = request.GET[
                    "review"]  # GET del value del button con name=review qu es el id del restaurante

                restaurant = Restaurant.objects.get(
                    restaurant_number=restaurant_number)  # obtenemos restaurante a partir de su id

                request.session[
                    "review_made"] = restaurant_number  # guardamos id de restaurante para recuperarlo despues en el POST

                form = ReviewForm()  # recogemos informacion de la review (rating y comment)

                viewedrestaurants = _check_session(request)

                context = {

                    'restaurant': restaurant,
                    'form': form,
                    'viewedrestaurants': viewedrestaurants

                }

        except Restaurant.DoesNotExist:
            return HttpResponse("Restaurant Does not exists")

        return render(request, 'forkilla/reviews.html', context)


def _update_rate(restaurant):
    reviews_of_restaurant = Review.objects.filter(restaurant=restaurant)

    if len(reviews_of_restaurant) == 0:
        restaurant.rate = 0.0
    else:
        review_avg = reviews_of_restaurant.aggregate(Avg('review_rate'))
        restaurant.rate = round(review_avg['review_rate__avg'], 1)
    restaurant.save()


def register(request):
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            new_user = form.save()
            new_user = authenticate(username=form.cleaned_data['username'],
                                    password=form.cleaned_data['password1'],
                                    )
            login(request, new_user)
            return HttpResponseRedirect(reverse("index"))
    else:
        form = UserCreationForm()
    return render(request, "registration/register.html", {
        'form': form,
    })


def user_space(request):
    the_user = request.user
    if not the_user.is_authenticated():
        return HttpResponseRedirect(reverse('/accounts/login/?next=/forkilla/user_space'))

    else:
        try:
            user_reservations = Reservation.objects.filter(user_id=the_user.id)

            context = {
                'user_reservations': user_reservations
            }

        except Reservation.DoesNotExist:
            return HttpResponse("Reservation Does not exists")

        return render(request, 'forkilla/user_space.html', context)


def handler404(request, exception):
    return render(request, 'errors/404.html')


def handler500(request, exception):
    return render(request, 'errors/500.html')


def delreservation(request):
    the_user = request.user

    if not the_user.is_authenticated():
        return HttpResponseRedirect(reverse('/accounts/login/?next=/forkilla/user_space'))

    else:

        if request.method == "GET":
            reservation_id = request.GET["delete_reservation"]
            Reservation.objects.get(id=reservation_id).delete()  # obtenemos reservation a partir de su id

            try:
                user_reservations = Reservation.objects.filter(user_id=the_user.id)

                context = {
                    'user_reservations': user_reservations
                }

            except Reservation.DoesNotExist:
                return HttpResponse("Reservation Does not exists")

    return render(request, 'forkilla/user_space.html', context)


def comparator(request, ips):
    categorias = [x[0] for x in Restaurant.objects.model.CATEGORIES]
    categorias.insert(0, "")
    context = {
        'ips': ips,
        'categories': categorias
    }

    return render(request, "forkilla/comparator.html", context)


###########################################################################

from django.contrib.auth.models import User, Group
from rest_framework import viewsets, generics
from .serializers import RestaurantSerializer
from rest_framework import permissions
from forkilla.permissions import HasCustomPermission
from rest_framework.response import Response
from rest_framework import status


class RestaurantViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows Restaurants to be viewed or edited.
    """
    queryset = Restaurant.objects.all().order_by('category')
    serializer_class = RestaurantSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, HasCustomPermission)

    def get_queryset(self):

        queryset = Restaurant.objects.all().order_by('restaurant_number')
        category = self.request.query_params.get('category', None)
        if category:
            queryset = queryset.filter(category=category)
        city = self.request.query_params.get('city', None)
        if city:
            queryset = queryset.filter(city=city)

        price = self.request.query_params.get('price', None)
        if price:
            queryset = queryset.filter(price_average__lte=price)
        return queryset



"""
class RestaurantList(generics.ListAPIView):
    queryset = Restaurant.objects.all()
    serializer_class = RestaurantSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, HasCustomPermission)

    
    def get(self, request):
        print("HI1")
        restaurants = Restaurant.objects.all().order_by('category')
        # the many param informs the serializer that it will be serializing more than a single restaurant.
        serializer = RestaurantSerializer(restaurants, many=True)
        return Response({"Restaurants": serializer.data})


    def post(self, request):
        # Create an article from the above data
        serializer = RestaurantSerializer(data=request.data)
        if serializer.is_valid(raise_exception=True):
            restaurant_saved = serializer.save()

        return Response({"success": "Restaurant '{}' created successfully".format(restaurant_saved.name)})

    
    def delete(self, request, pk):
        print("enter")
        restaurant = Restaurant.objects.get(restaurant_number=pk)
        restaurant.delete()
        return Response(status.HTTP_204_NO_CONTENT)
   

class RestaurantDetail(generics.RetrieveAPIView):
    queryset = Restaurant.objects.all()
    serializer_class = RestaurantSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, HasCustomPermission)

 """
