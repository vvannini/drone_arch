# drone_arch 
            

## Prerequisites

### ROS

Install [ROS-Mellodic](http://wiki.ros.org/melodic/Installation/Ubuntu) on Ubuntu 18

```
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

```
sudo apt-get install ros-melodic-mavros ros-melodic-mavros-extras
wget https://raw.githubusercontent.com/mavlink/mavros/master/mavros/scripts/install_geographiclib_datasets.sh
chmod +x install_geographiclib_datasets.sh
sudo ./install_geographiclib_datasets.sh
sudo apt install python-prettytable
```
### PX4 Firmware

For the simulation install the PX4 firmware

```
git clone https://github.com/vvannini/Firmware.git
source ubuntu_sim.sh

sudo apt install openjdk-8-jdk
sudo update-alternatives --config java # choose 8
rm -rf Tools/jMAVSim/out
sudo sed -i -e '/^assistive_technologies=/s/^/#/' /etc/java-*-openjdk/accessibility.properties
```
