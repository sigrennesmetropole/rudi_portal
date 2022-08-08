#!/bin/bash

set -x

cp -r ../rudi-microservice/rudi-microservice-acl/rudi-microservice-acl-facade/target/rudi-microservice-acl-facade.jar acl/rudi-microservice-acl-facade.jar
cp -r ../rudi-microservice/rudi-microservice-gateway/rudi-microservice-gateway-facade/target/rudi-microservice-gateway-facade.jar gateway/rudi-microservice-gateway-facade.jar
cp -r ../rudi-microservice/rudi-microservice-kalim/rudi-microservice-kalim-facade/target/rudi-microservice-kalim-facade.jar kalim/rudi-microservice-kalim-facade.jar
cp -r ../rudi-microservice/rudi-microservice-konsult/rudi-microservice-konsult-facade/target/rudi-microservice-konsult-facade.jar konsult/rudi-microservice-konsult-facade.jar
cp -r ../rudi-microservice/rudi-microservice-kos/rudi-microservice-kos-facade/target/rudi-microservice-kos-facade.jar kos/rudi-microservice-kos-facade.jar
cp -r ../rudi-microservice/rudi-microservice-registry/rudi-microservice-registry-facade/target/rudi-microservice-registry-facade.jar registry/rudi-microservice-registry-facade.jar
cp -r ../rudi-microservice/rudi-microservice-projekt/rudi-microservice-projekt-facade/target/rudi-microservice-projekt-facade.jar projekt/rudi-microservice-projekt-facade.jar
cp -r ../rudi-microservice/rudi-microservice-strukture/rudi-microservice-strukture-facade/target/rudi-microservice-strukture-facade.jar strukture/rudi-microservice-strukture-facade.jar
