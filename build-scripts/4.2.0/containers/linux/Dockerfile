FROM centos:7
RUN yum update -y
RUN echo "assumeyes=1" >> /etc/yum.conf
RUN yum install epel-release -y
RUN yum install -y\
       gcc \
       expat-devel \
       openssl-devel \
       autoconf \
       gcc-c++ \
       cups-devel \
       pam-devel \
       java-1.7.0-openjdk-devel \
       java-1.8.0-openjdk-devel \
       rpm-build \
       dpkg \
       fakeroot \
       gperf \
       freetype-devel \
       libX11-devel \
       libXt-devel \
       fontconfig-devel \
       libXrandr-devel \
       bison \
       flex \
       GConf2-devel \
       gnome-vfs2-devel \
       gtk2-devel \
       gstreamer-devel \
       gstreamer-plugins-base-devel \
       gstreamer1-devel \
       gstreamer1-plugins-base-devel \
       mesa-libGLU-devel \
       wget \
       subversion \
       ccache \
       glib2-devel \
       dbus-glib-devel \
       perl \
       ORBit2-devel
RUN ccache -M 2G
RUN wget https://dlcdn.apache.org/ant/binaries/apache-ant-1.9.16-bin.tar.bz2
RUN tar xvf apache-ant-1.9.16-bin.tar.bz2
RUN mv apache-ant-1.9.16 ant
ENV ANT_HOME=/ant
RUN yum install \
       cpan \
       perl-libwww-perl \
       perl-Archive-Zip \
       perl-Digest-SHA \
       perl-XML-Parser \
       perl-Crypt-SSLeay \
       perl-Env \
       perl-LWP-Protocol-https \
       perl-Test-Simple \
       perl-File-Path
RUN wget https://github.com/jimjag/dmake/archive/v4.13.1/dmake-4.13.1.tar.gz
RUN tar xvf dmake-4.13.1.tar.gz
WORKDIR /dmake-4.13.1
RUN ./configure --prefix=/usr/local; make install
WORKDIR /
RUN wget https://github.com/jimjag/epm/archive/v5.0.0/epm-5.0.0.tar.gz
RUN tar xvf epm-5.0.0.tar.gz
WORKDIR epm-5.0.0
RUN ./configure --prefix=/usr/local ; make -i install
RUN yum install -y git autoconf
