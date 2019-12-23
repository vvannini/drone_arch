# drone_arch

## Prerequisites

### ROS

Install [ROS-Mellodic](http://wiki.ros.org/melodic/Installation/Ubuntu) on Ubuntu 18

```bash
sudo sh -c 'echo "deb http://packages.ros.org/ros/ubuntu $(lsb_release -sc) main" > /etc/apt/sources.list.d/ros-latest.list'
sudo apt-key adv --keyserver 'hkp://keyserver.ubuntu.com:80' --recv-key C1CF6E31E6BADE8868B172B4F42ED6FBAB17C654
sudo apt-get update
sudo apt install ros-melodic-desktop-full
sudo rosdep init
rosdep update
echo "source /opt/ros/melodic/setup.bash" >> ~/.bashrc
source ~/.bashrc
sudo apt install python-rosinstall python-rosinstall-generator python-wstool build-essential
sudo apt install rosbash
```

### MAVROS

```bash
sudo apt-get install ros-melodic-mavros ros-melodic-mavros-extras
wget https://raw.githubusercontent.com/mavlink/mavros/master/mavros/scripts/install_geographiclib_datasets.sh
chmod +x install_geographiclib_datasets.sh
sudo ./install_geographiclib_datasets.sh
sudo apt install python-prettytable
```

### PX4 Firmware

For the simulation install the PX4 firmware

```bash
git clone https://github.com/vvannini/Firmware.git
source ubuntu_sim.sh

sudo apt install openjdk-8-jdk
sudo update-alternatives --config java # choose 8
rm -rf Tools/jMAVSim/out
sudo sed -i -e '/^assistive_technologies=/s/^/#/' /etc/java-*-openjdk/accessibility.properties
```

## Docker

### Dependencies

- [Docker](https://docs.docker.com/install/linux/docker-ce/ubuntu/)

### Usage

The environment for this project can be accessed by using Docker.

First, you need to build or download a previous built image. To build one from source code, open the bash terminal on this folder (drone_arch) and run the following, it should take from 10 to 15 minutes to complete:

```bash
    make docker-build-image
```

Once you have the image, it is time to run the container, by invoking:

```bash
    make docker-run-bash
```

This command will open the Docker container and show you a bash terminal, in which you can run any codes or commands you would on your local machine. It is mapped to show this repository under the /home folder inside the container.

> If you get any error when trying to enter a container, try running ```make docker-rm``` as it will remove the previous running container
