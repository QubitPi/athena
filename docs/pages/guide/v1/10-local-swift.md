---
layout: doc-guide
group: guide
subtopic: true
title: Standup A Local Swift Instance
description: A guide that gets people ramp up speed on playing with Swift
version: 1
---

In this quickstart, we will download a [OpenStack Swift Image](https://hub.docker.com/r/fnndsc/docker-swift-onlyone),
spinup a container on a single machine, upload a test file, and download that file.

Prerequisites
-------------

You will need:

* Docker command line tool
* Python 3

Getting started
---------------

To download the image, issue the following commands in your terminal:

```bash
docker pull fnndsc/docker-swift-onlyone
```

Setup Volume for Swift
----------------------

> [Volumes](https://docs.docker.com/storage/volumes/) are the preferred mechanism for persisting data generated by and
> used by Docker containers.

```bash
docker volume create swift_storage
```

Start up Container Instance
---------------------------

```bash
docker run -d --name swift-onlyone -p 12345:8080 -v swift_storage:/srv -t fnndsc/docker-swift-onlyone
```

Setup Swift Client
------------------

Querying Swift through command line requires [python-swiftclient](https://pypi.org/project/python-swiftclient/). We'll
need to install it:

```bash
pip3 pip install python-swiftclient
```

> If you are on Mac OS, which has Apple Swift overriding Openstack swift package, explicitly invoking swiftclient as
> /usr/bin/local/swift works.
>
> In case the absolute path is not `/usr/bin/local/swift`, try `sudo find / -name swift` which will give you a list
> that contains the right > executable path
>
> For example, `/usr/local/bin/swift -A http://127.0.0.1:12345/auth/v1.0 -U chris:chris1234 -K testing stat` should
> work. We will keep using `/usr/local/bin/swift` afterwards

Create, Upload, and Download a Test File
----------------------------------------

We've create an empty file called `test-file.txt` to get you started:

```bash
touch test-file.txt
```

To upload this file onto Swift:

```bash
/usr/local/bin/swift -A http://127.0.0.1:12345/auth/v1.0 -U chris:chris1234 -K testing upload --object-name
test-file.txt user_uploads ./test-file.txt
```

which will print the name of the file if the upload was successful:

```bash
test-file.txt
```

To download that file:

```bash
/usr/local/bin/swift -A http://127.0.0.1:12345/auth/v1.0 -U chris:chris1234 -K testing download user_uploads test-file.txt
```
