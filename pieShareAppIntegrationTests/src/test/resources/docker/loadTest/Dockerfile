FROM vauvenal5/pieshare
RUN git clone https://github.com/vauvenal5/pieShare.git
RUN cd /pieShare && git checkout dockerExperimental && git pull origin dockerExperimental && mvn clean install -DskipTests
ENTRYPOINT ["/bin/bash", "/pieShare/pieShareAppIntegrationTests/src/test/resources/docker/internal.sh"]