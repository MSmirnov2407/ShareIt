# ShareIt
Приложение шеринга вещей. 
##
REST API приложение c разделением на сервисы, запускаемые в отдельных Docker-контейнерах. (Sptring Boot, Maven, JPA, Hibernate ORM,
PostgreSQL, SQL, Docker)
##
В приложении реализованы CRUD-операции для пользователей, вещей, запросов на бронирование вещи, бронирование вещи. 
###
Приложение разделено на сервер приложения и на gateway, принимающий Http-Запросы и отправляющий их на сервер по Http.
