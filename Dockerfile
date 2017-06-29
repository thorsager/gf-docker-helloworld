FROM thorsager/glassfish:glassfish4

COPY src/main/config/gf-resources.xml /tmp

RUN asadmin --user=admin start-domain ${DOMAIN_NAME} && \
    echo "AS_ADMIN_PASSWORD=${PASSWORD}" > /tmp/glassfishpwd && \
    asadmin --user=admin --passwordfile=/tmp/glassfishpwd add-resources /tmp/gf-resources.xml && \
    asadmin --user=admin stop-domain ${DOMAIN_NAME} && \
    rm /tmp/gf-resources.xml && \
    rm /tmp/glassfishpwd

COPY ./target/hello-world.war ${DEPLOYMENT_DIR}