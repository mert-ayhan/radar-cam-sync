# Radar Cam Sync Simulation

## Overview

This project aims to facilitate communication between radar and camera systems. It's designed to streamline the integration process, ensuring efficient data transfer and processing.

## Getting Started
### Prerequisites
- Docker
- Docker Compose

### Installation and Setup
- Clone the Repository
- Modify the relevant settings in the docker-compose.yml file as needed for your environment.

### Launch the Services

- Run **docker-compose up zookeeper kafka**
- Build and run radar_control, camera_control and world component.

_Note: The kafka module, used by both radar-control and camera-control, must be placed in a local Maven repository, such as Nexus or Artifactory, if you want to run it under Docker._


## Demo

![Demo Animation](https://i.imgur.com/r3aETYV.gif)
