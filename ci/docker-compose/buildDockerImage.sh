#!/bin/bash

#set -x

CI_REGISTRY_IMAGE=rudi
#DOCKER_OPTS=--no-cache
DOCKER_OPTS=
PWD=`pwd`
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

CI_DIR=`dirname ${SCRIPT_DIR}`
ROOT_DIR=`dirname ${CI_DIR}`

function log(){
	echo "$*"
}

function build_docker_images() {
        local dir=$1
        local tag=${2:-${DOCKER_TAG_NAME}}

        local pwd=$(pwd)

        find $dir -type f -name "Dockerfile" | while read line; do
                cd ${ROOT_DIR}
                DOCKER_IMAGE_NAME=$(basename $(dirname $line))

                IMAGE_URL=${CI_REGISTRY_IMAGE}/${DOCKER_IMAGE_NAME}:$tag
                cd $(dirname ${line})

		log "Handle `pwd` => ${IMAGE_URL} $tag "

		if [[ ! -f "Dockerfile.ori" ]]; then 
			cp Dockerfile Dockerfile.ori
		fi
                sed "s/glregistry.boost.open.global\/rennes-metropole\/rudi\///" Dockerfile.ori > Dockerfile

                if [[ "x${DOCKER_DEBUG}" != "x" ]]; then
                        echo "Dockerfile found in `pwd`"
                        echo "docker command: docker build --pull ${DOCKER_OPTS} -t ${IMAGE_URL} ${DOCKER_CLI_OPTS} . --build-arg=\"BASE_IMAGE_TAG=$tag\""
                        echo "Content of `pwd`:"
                        find .
                fi
                docker build ${DOCKER_OPTS} -t "${IMAGE_URL}" ${DOCKER_CLI_OPTS} . --build-arg="BASE_IMAGE_TAG=$tag"
                echo ${IMAGE_URL} >> ${CI_DIR}/.ci/docker.build
        done

cd $pwd
}

log "Start docker image building..."
log "RootDir=${ROOT_DIR}"

cd ${ROOT_DIR}
mkdir -p ${CI_DIR}/.ci || true
rm -f ${CI_DIR}/.ci/docker.build || true

regex='^([0-9]*)[.]([0-9]*)[.]([0-9]*)(.*)'
RUDI_VERSION=$(xmlstarlet sel -N mvn='http://maven.apache.org/POM/4.0.0' -t -m '/mvn:project/mvn:version' -v . -n <pom.xml)

log "Rudi Version=${RUDI_VERSION}"
DOCKER_TAG_NAME=${RUDI_VERSION}

#'echo $DOCKER_REGISTRY_PASSWORD | docker login -u $DOCKER_REGISTRY_USER --password-stdin $DOCKER_REGISTRY'

# JAR pour WSO2
log "Copy wso2 jar"

mkdir -p ${CI_DIR}/docker/apim
mkdir -p ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/dropins
mkdir -p ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/lib
 
cp rudi-tools/rudi-tools-wso2/target/org.rudi.wso2.userstore.jar                       ${CI_DIR}/docker/apim/org.rudi.wso2.userstore.jar
cp rudi-tools/rudi-tools-wso2-handler/target/org.rudi.wso2.handler.jar ${CI_DIR}/docker/apim/org.rudi.wso2.handler.jar
cp rudi-facet/rudi-facet-crypto/target/rudi-facet-crypto.jar           ${CI_DIR}/docker/apim/rudi-facet-crypto.jar
cp rudi-common/rudi-common-core/target/rudi-common-core-${RUDI_VERSION}.jar            ${CI_DIR}/docker/apim/rudi-common.jar

cp rudi-tools/rudi-tools-wso2/target/org.rudi.wso2.userstore.jar                       ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/dropins/org.rudi.wso2.userstore.jar
cp rudi-tools/rudi-tools-wso2-handler/target/org.rudi.wso2.handler.jar ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/lib/org.rudi.wso2.handler.jar
cp rudi-facet/rudi-facet-crypto/target/rudi-facet-crypto.jar           ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/lib/rudi-facet-crypto.jar
cp rudi-common/rudi-common-core/target/rudi-common-core-${RUDI_VERSION}.jar            ${CI_DIR}/docker-compose/wso2/conf/apim/repository/components/lib/rudi-common-core.jar

# JAR Dataverse
#export SERVICE_BASIC=`printf "${SERVICE_LOGIN}:${SERVICE_PWD}" | base64`
#export AUTHORIZATION_HEADER_RAW=" Basic ${SERVICE_BASIC}"
#echo "wget --header \"Authorization:${AUTHORIZATION_HEADER_RAW}\" \"${RAW_GROUP}dataverse/v5.5/dataverse.war\" -O ci/docker/dataverse-engine/dvinstall"
#wget --header "Authorization:${AUTHORIZATION_HEADER_RAW}" "${RAW_GROUP}dataverse/v5.5/dataverse.war" -O "${CI_DIR}/docker/dataverse-engine/dvinstall/dataverse.war"

log "Create config directories..."

find "${ROOT_DIR}/docker" -type f -name "Dockerfile" | while read line; do
	MICROSERVICE_NAME=$(basename $(dirname $line))
	log "Handle ${MICROSERVICE_NAME}..."
	mkdir -p ${ROOT_DIR}/ci/docker-compose/portal/config/${MICROSERVICE_NAME}
	if [ -f "${ROOT_DIR}/rudi-microservice/rudi-microservice-${MICROSERVICE_NAME}/rudi-microservice-${MICROSERVICE_NAME}-facade/src/main/resources/${MICROSERVICE_NAME}-exemple.properties" ]; then
		cp ${ROOT_DIR}/rudi-microservice/rudi-microservice-${MICROSERVICE_NAME}/rudi-microservice-${MICROSERVICE_NAME}-facade/src/main/resources/${MICROSERVICE_NAME}-exemple.properties ${ROOT_DIR}/ci/docker-compose/portal/config/${MICROSERVICE_NAME}/${MICROSERVICE_NAME}.properties
        fi
	mkdir -p data/${MICROSERVICE_NAME} 
done

cd ${PWD}

# Dockerfile des images parents
log "Build parent images..."
find ci/docker-parents -type f -name "Dockerfile" | while read line; do
        cd ${ROOT_DIR}
        DOCKER_IMAGE_NAME=$(basename $(dirname $line))
        IMAGE_URL=${CI_REGISTRY_IMAGE}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG_NAME}
        cd $(dirname ${line})
        echo "`pwd`"
        docker build --pull ${DOCKER_OPTS} -t ${IMAGE_URL} ${DOCKER_CLI_OPTS} .
        echo ${IMAGE_URL} >> ${ROOT_DIR}/.ci/docker.build
done

cd ${ROOT_DIR}

log "Build services images..."
build_docker_images ci/docker/

log "Build wso2..."

GIT_SUBMODULE_STRATEGY=recursive
WSO2_DOCKERFILES_DIRECTORY=ci/docker-apim/docker-compose/apim-with-analytics/
WSO2_VERSION=3.2.0.1 
# On corrige les Dockerfile de WSO2 pour pouvoir les builder (source : https://github.com/wso2/docker-apim/issues/412#issuecomment-1146758176)
find "$WSO2_DOCKERFILES_DIRECTORY" -type f -name "Dockerfile" | while read line; do
	sed -i 's/FROM docker.wso2.com/FROM wso2/' $line
done
build_docker_images "$WSO2_DOCKERFILES_DIRECTORY" $WSO2_VERSION

cd ${PWD}

