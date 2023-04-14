ARG BASE_IMAGE_TAG=latest
FROM glregistry.boost.open.global/rennes-metropole/rudi/rudi/front:$BASE_IMAGE_TAG

ADD rudi-application-front-office-angular-dist.zip /usr/local/apache2/htdocs/application.zip
ADD httpd.conf /usr/local/apache2/conf

RUN cd /usr/local/apache2/htdocs &&\
    unzip application.zip && \
    rm application.zip index.html

RUN mv /usr/local/apache2/htdocs/angular-project/* /usr/local/apache2/htdocs && \
    rm /usr/local/apache2/htdocs/angular-project -r
