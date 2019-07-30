from rest_framework import permissions


class HasCustomPermission(permissions.BasePermission):

    def has_permission(self, request, view):

        if request.method in permissions.SAFE_METHODS:
            return True

        return request.user.groups.filter(name='Commercial').exists() or \
               request.user.groups.filter(name='Admin').exists()

    def has_object_permission(self, request, view, obj):
        return True
