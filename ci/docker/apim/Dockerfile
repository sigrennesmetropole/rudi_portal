FROM glregistry.boost.open.global/rennes-metropole/rudi/rudi/apim:3.2.0.1

ADD org.rudi.wso2.userstore.jar /home/wso2carbon/wso2am-3.2.0/repository/components/dropins/org.rudi.wso2.userstore-0.0.1.jar
ADD org.rudi.wso2.handler.jar   /home/wso2carbon/wso2am-3.2.0/repository/components/lib/org.rudi.wso2.handler.jar
ADD rudi-facet-crypto.jar       /home/wso2carbon/wso2am-3.2.0/repository/components/lib/rudi-facet-crypto.jar
ADD postgresql-42.2.18.jar      /home/wso2carbon/wso2am-3.2.0/repository/components/dropins/postgresql-42.2.18.jar

ADD update-all-apis.sh    /home/wso2carbon/wso2am-3.2.0/scripts/
ADD re-deploy-all-apis.sh /home/wso2carbon/wso2am-3.2.0/scripts/

# Install jq (required to execute scripts like update-all-apis.sh)
USER root
RUN apt-get update && \
    apt-get install -y jq
