#!/bin/sh
#
# FILE:    dkclean.sh
# PURPOSE: stops redis containers and removes redis-contacts images and related danglers
# OPTIONS: --force - force removal of exited redis container and redis images
#          --prune - force removal of all exited containers
# RUN:     . dkclean.sh  or . dkclean.sh --force or . dkclean.sh --force --prune or . dkclean.sh --prune
# AUTHOR: T.N.Silverman
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------
# -- stop running redis containers if any
if [[ $(docker ps -a -q --filter ancestor=redis) ]]; then
    echo "stopping redis container..."
    docker stop $(docker ps -a -q --filter ancestor=redis)
fi
# -- remove redis-contacts images if any
if [[ $(docker images | grep ^.*redis-contacts) ]]; then
    echo "removing redis-contacts images..."
	for i in $(docker images | grep ^.*redis-contacts | awk '{ print $3 }'); do
	    echo "removing image $i"
        docker rmi $i -f
	done
fi
# -- remove dangling images if any
if [[ $(docker images --filter 'dangling=true' -q --no-trunc) ]]; then
    echo "removing dangling images..."
    docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)
fi
# --force remove redis container if --force exists
if [ "$1" == "--force" ] || [ "$2" == "--force" ]; then
    if [[ $(docker ps -a -q --filter ancestor=redis) ]]; then
        echo "removing exited redis container"
        docker container rm $(docker ps -a -q --filter ancestor=redis) -f
		if [[ $(docker images | grep ^.*redis) ]]; then
		    echo "removing redis image..."
			for i in $(docker images | grep ^.*redis | awk '{ print $3 }'); do
			    echo "removing image $i"
		        docker rmi $i -f
			done
		fi
    fi
fi
# --force prune exited containers if --prune exists
if [ "$1" == "--prune" ] || [ "$2" == "--prune" ]; then
    echo "pruning containers..."
	docker container prune -f
fi