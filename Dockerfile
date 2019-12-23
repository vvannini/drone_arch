FROM ros:melodic

WORKDIR /home


# TODO: Pass only the needed folders in order to reduce image size
COPY ./ /home


# Intall requirements

# MAVROS
RUN apt-get update \
    && apt-get install -qq -y --no-install-recommends \
        ros-melodic-mavros \
        ros-melodic-mavros-extras \
        python-prettytable \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://raw.githubusercontent.com/mavlink/mavros/master/mavros/scripts/install_geographiclib_datasets.sh \
    && chmod +x install_geographiclib_datasets.sh \
    && ./install_geographiclib_datasets.sh

# && apt install python-prettytable


# PX4 FIRMWARE.
RUN apt-get update \
    && apt-get install -qq -y --no-install-recommends \
        wget \
    && rm -rf /var/lib/apt/lists/*
RUN git clone https://github.com/vvannini/Firmware.git
RUN sed -i -e 's/\r$//' ubuntu_sim.sh && \
    chmod +x ubuntu_sim.sh && \
    ./ubuntu_sim.sh
RUN apt install openjdk-8-jdk
RUN update-alternatives --config java # choose 8
RUN rm -rf Tools/jMAVSim/out
RUN sed -i -e '/^assistive_technologies=/s/^/#/' /etc/java-*-openjdk/accessibility.properties