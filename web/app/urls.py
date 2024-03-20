from django.conf.urls import url

from app import views

urlpatterns = [
  url(r'^$', views.home),
  url(r'^loginForm$', views.login_form),
  url(r'^login$', views.login),
  url(r'^data$', views.data_page),
  url(r'^monitor$', views.monitoring_page),
  url(r'^logout$', views.logout),
  url(r'^profile$', views.profile),
  url(r'^home$', views.home),
  url(r'^products$', views.products),
  url(r'^product$', views.product, name='views.product'),
  url(r'^cart$', views.cart, name='views.cart'),
  url(r'^createOrder$', views.create_order),
  url(r'^orders$', views.orders),
  url(r'^order$', views.order),
  url(r'^createTestData$', views.create_test_data),
  url(r'^deleteTestData$', views.delete_test_data),
  url(r'^status$', views.get_status),
  url(r'^systemStatus$', views.get_system_status),
  url(r'^registry$', views.get_registry),
  url(r'^tableData$', views.get_table_data),
]
