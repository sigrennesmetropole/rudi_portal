#!/bin/bash

set -x

cp -r ../rudi-microservice/rudi-microservice-registry/rudi-microservice-registry-facade/target/rudi-microservice-registry-facade.jar registry/rudi-microservice-registry-facade.jar
cp -r ../rudi-microservice/rudi-microservice-konsult/rudi-microservice-konsult-facade/target/rudi-microservice-konsult-facade.jar konsult/rudi-microservice-konsult-facade.jar
cp -r ../rudi-microservice/rudi-microservice-providers/rudi-microservice-providers-facade/target/rudi-microservice-providers-facade.jar providers/rudi-microservice-providers-facade.jar
cp -r ../rudi-microservice/rudi-microservice-gateway/rudi-microservice-gateway-facade/target/rudi-microservice-gateway-facade.jar gateway/rudi-microservice-gateway-facade.jar
cp -r ../rudi-microservice/rudi-microservice-kalim/rudi-microservice-kalim-facade/target/rudi-microservice-kalim-facade.jar kalim/rudi-microservice-kalim-facade.jar
cp -r ../rudi-microservice/rudi-microservice-acl/rudi-microservice-acl-facade/target/rudi-microservice-acl-facade.jar acl/rudi-microservice-acl-facade.jar
cp -r ../rudi-microservice/rudi-microservice-kos/rudi-microservice-kos-facade/target/rudi-microservice-kos-facade.jar kos/rudi-microservice-kos-facade.jar
