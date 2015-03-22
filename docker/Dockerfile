FROM ubuntu:14.04

RUN DEBIAN_FRONTEND=noninteractive apt-get -y install software-properties-common
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
RUN sudo apt-get -y install oracle-java8-installer

RUN sudo apt-get -y install git
RUN sudo apt-get -y install maven

RUN git clone https://github.com/vauvenal5/pieShare.git