FROM vauvenal5/pieshare
RUN git clone https://github.com/vauvenal5/pieShare.git
RUN cd /pieShare && git checkout docker && mvn clean install -DskipTests
COPY ./internal.sh /internal.sh
ENTRYPOINT ["/bin/bash", "/internal.sh"]