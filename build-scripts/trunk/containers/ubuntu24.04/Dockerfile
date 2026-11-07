FROM ubuntu:24.04
RUN apt update && apt dist-upgrade -y
RUN apt update && apt install -y build-essential g++ gcc bison flex gawk \
       libarchive-zip-perl libcups2-dev libpam0g-dev \
       gperf libfreetype6-dev libxaw7-dev libfontconfig1-dev libxrandr-dev \
       patch ant libgtk2.0-dev libgdk-pixbuf-xlib-2.0-dev junit junit4 \
       libidl-dev libdbus-glib-1-dev libgstreamer-plugins-base1.0-dev \
       openjdk-8-jdk liblablgl-ocaml-dev git autoconf wget \
       liblwp-protocol-https-perl libxml-parser-perl rpm zip

RUN wget https://dlcdn.apache.org/ant/binaries/apache-ant-1.10.18-bin.tar.bz2
RUN tar xvf apache-ant-1.10.18-bin.tar.bz2
RUN mkdir -p /usr/local/share/java/
RUN mv apache-ant-1.10.18 /usr/local/share/java/apache-ant-1.10
ENV ANT_HOME=/usr/local/share/java/apache-ant-1.10

RUN wget https://github.com/jimjag/dmake/archive/v4.13.2/dmake-4.13.2.tar.gz
RUN tar xvf dmake-4.13.2.tar.gz
WORKDIR /dmake-4.13.2
RUN ./configure --prefix=/usr/local; make install

WORKDIR /
RUN wget https://github.com/jimjag/epm/archive/v5.1.0/epm-5.1.0.tar.gz
RUN tar xvf epm-5.1.0.tar.gz
WORKDIR epm-5.1.0
RUN ./configure --prefix=/usr/local ; make -i install
