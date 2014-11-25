# PieShare

## What is PieShare?

PieShare is a free tool for syncing and collaborating. It can make your documents, photos and everything else available everywhere you want without giving it to anybody you don’t want to.

A normal cloud drive needs you to store your data on a remote server. You don't actually know who else has the possibility to view your documents. So you naturally won't store sensible information on such a solution.

There are also quite a few solutions out there which allow you to set up your private server at home. However not everybody has the resources or knowledge needed to set up such a server. And even worse: the ability to share your data with somebody else is quite limited when using a private server.

PieShare tackles exactly these downsides. Your data is not copied to an external server and PieShare is easy to use because you don't need anything else than the devices you already have.

So PieShare brings the benefits of both systems together: 
- It is easy to set up. 
- You are free to collaborate with others. 
- And last, but certainly most important, your data is safe.

## Build
- Master: [![Build Status](https://travis-ci.org/vauvenal5/pieShare.svg?branch=master)](https://travis-ci.org/vauvenal5/pieShare)
- Development: [![Build Status](https://travis-ci.org/vauvenal5/pieShare.svg?branch=development)](https://travis-ci.org/vauvenal5/pieShare)

## Some additional information.
At the moment PieShare is being developed as a Bachelor project at the Technical University of Vienna. Therefore we cannot allow anybody to collaborate directly. When the project reaches the next phase it will be open for collaboration. However feel free to submit suggestions, critiques and feature requests. Until 2nd quarter of 2015 we should be able to open the project for code collaboration.

## Current state.
Currently the LAN part of the project is almost ready to use. However this is only a unstable alpha. It uses basic encryption and no key derivation protocols at the moment. There are also no sharing features implemented for now. Encrypted synchronization between multiple devices should allready work properly.
For a first look you will need to build the project with at least JDK8u25 and Mave v3.2. The newest "stable" state is always on the development branch. The master branch gets all view weeks synchronized.

### Upcoming
The next features that will be worked on:
- further test automatization for more stability
- enhancment of encryption protocols
- synchronization of own files over WAN
