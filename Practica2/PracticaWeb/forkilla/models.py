from django.db import models

from django.core.validators import MaxValueValidator
from django.core.validators import MinValueValidator
from django.contrib.auth.models import User

from datetime import datetime


# Create your models here.


class Restaurant(models.Model):
    CATEGORIES = (
        ("Rice", "Rice"),
        ("Fusi", "Fusion"),
        ("BBQ", "Barbecue"),
        ("Chin", "Chinese"),
        ("Medi", "Mediterranean"),
        ("Crep", "Creperie"),
        ("Hind", "Hindu"),
        ("Japa", "Japanese"),
        ("Ital", "Italian"),
        ("Mexi", "Mexican"),
        ("Peru", "Peruvian"),
        ("Russ", "Russian"),
        ("Turk", "Turkish"),
        ("Basq", "Basque"),
        ("Vegy", "Vegetarian"),
        ("Afri", "African"),
        ("Egyp", "Egyptian"),
        ("Grek", "Greek")
    )
    _d_categories = dict(CATEGORIES)

    restaurant_number = models.CharField(max_length=8, unique=True)
    name = models.CharField(max_length=50)
    menu_description = models.TextField()
    price_average = models.DecimalField(max_digits=5, decimal_places=2)
    is_promot = models.BooleanField()
    rate = models.DecimalField(default=0.0, max_digits=2, decimal_places=1,
                               validators=[MinValueValidator(0.0), MaxValueValidator(5.0)])
    address = models.CharField(max_length=50)
    city = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    featured_photo = models.ImageField(upload_to="images/")
    category = models.CharField(max_length=5, choices=CATEGORIES)
    restaurant_capacity = models.PositiveIntegerField(validators=[MaxValueValidator(999)])

    def get_human_category(self):
        return self._d_categories[self.category]

    def __str__(self):
        return ('[**Promoted**]' if self.is_promot else '') + "[" + self.category + "] " \
                                                                                    "[" + self.restaurant_number + "] " + self.name + " - " + self.menu_description + " (" + str(
            self.rate) + ")" \
                         ": " + str(self.price_average) + u" €"


class Reservation(models.Model):
    SLOTS = (
        ("morning_first", "12h00"),
        ("morning_second", "13h00"),
        ("morning_third", "14h00"),
        ("morning_fourth", "15h00"),
        ("evening_first", "20h00"),
        ("evening_second", "21h00"),
        ("evening_third", "22h00"),
    )
    _d_slots = dict(SLOTS)

    id = models.AutoField(primary_key=True)
    restaurant = models.ForeignKey(Restaurant)
    day = models.DateField(default=datetime.now)
    time_slot = models.CharField(max_length=15, choices=SLOTS)
    num_people = models.PositiveIntegerField(default=1, validators=[MinValueValidator(1)])
    user = models.ForeignKey(User, on_delete=models.DO_NOTHING, default=1)

    def get_human_slot(self):
        return self._d_slots[self.time_slot]

    def get_reservation_amount(self):
        return len(self._d_slots.get(0))


class ViewedRestaurants(models.Model):
    id_vr = models.AutoField(primary_key=True)
    restaurant = models.ManyToManyField(Restaurant, through='RestaurantInsertDate')


class RestaurantInsertDate(models.Model):
    viewedrestaurants = models.ForeignKey(ViewedRestaurants, on_delete=models.CASCADE)
    restaurant = models.ForeignKey(Restaurant, on_delete=models.CASCADE)
    date_added = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-date_added']


class Review(models.Model):
    id = models.AutoField(primary_key=True)
    restaurant = models.ForeignKey(Restaurant)
    review_title = models.CharField(max_length=50)
    review_comment = models.TextField(max_length=100)
    review_rate = models.DecimalField(default=1.0, max_digits=2, decimal_places=1,
                                      validators=[MinValueValidator(1.0), MaxValueValidator(5.0)])
    user = models.ForeignKey(User, on_delete=models.DO_NOTHING, default=1)


# todas las reviews de todos los restaurantes
class ExistingReviews(models.Model):
    id_review = models.AutoField(primary_key=True)
    reviews = models.ManyToManyField(Review)
