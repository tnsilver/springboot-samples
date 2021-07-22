#!usr/bin/env bash
#
# FILE:    dkadmin.sh
# PURPOSE: administer the mysql container
# OPTIONS: --password prompt for a mysql root password
#          --restart restart an existing mysql container
#          --stop stop an existing mysql container
#          --status displays the status of the MySql container
#          --clean cleans any jpa-contacts related containers and images
#          --purge uninstall the mysql container and images
#          --help displays usage message
# RUN:     . dkadmin.sh [OPTION]
# AUTHOR:  T.N.Silverman
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

main() {
  local image="mysql/mysql-server"
  local name="mysql"
  sanity_test # check if docker is running
  if [ "$#" -gt 1 ]; then
    usage
    kill -SIGINT $$
  elif [[ -z $1 ]]; then
    echo $(info "installing '$image' named '$name' with generated mysql root password")
    install $image $name
  elif [ "$1" == "--password" ]; then
    echo $(info "installing '$image' named '$name' with user selected mysql root password")
    install $image $name "password"
  elif [ "$1" == "--purge" ]; then
    purge $image $name
  elif [ "$1" == "--restart" ]; then
    restart $image $name
  elif [ "$1" == "--status" ]; then
    status $image $name
  elif [ "$1" == "--clean" ]; then
    clean $image $name
  elif [ "$1" == "--help" ]; then
    usage
  elif [ "$1" == "--stop" ]; then
    stop $image $name
  else
    usage
    kill -SIGINT $$
  fi
}

# utilities

sanity_test () {
  local error=$(docker ps 2>&1 | grep -i "is the docker daemon running?")
  if [[ $error ]]; then
    echo $(warn "cannot execute this script without a running docker daemon!")
    kill -SIGINT $$
  fi
}

function warn {
    local RED='\033[0;31m'
    local NC='\033[0m' # No Color
    printf "\n${RED}$@${NC}\n"
}

function info {
    local GREEN='\033[0;32m'
    local NC='\033[0m' # No Color
    printf "\n${GREEN}$@${NC}\n"
}

function debug {
    local YELLOW='\033[0;33m'
    local NC='\033[0m' # No Color
    printf "\n${YELLOW}$@${NC}\n"
}

usage () {
  IFS='~'
  echo $(debug "Purpose:  install / uninstall and administer the docker MySql server")
  echo $(debug "usage:    dkadmin.sh [OPTIONS] - defaults to generating a mysql root password")
  echo $(debug "OPTIONS:  --password: install and prompt for a mysql root password")
  echo $(debug "          --restart: restart the mysql container")
  echo $(debug "          --stop: stop the mysql container")
  echo $(debug "          --status: show the container status")
  echo $(debug "          --clean: cleans any application related containers and images")
  echo $(debug "          --purge: remove the mysql container")
  echo $(debug "          --help: show this menu")
  unset IFS
}

success () {
  echo "Args: $@"
  local image=$1
  local name=$2
  local userpwd=$3
  local rootpwd=$4
  local ip=$(docker inspect -f "{{ .NetworkSettings.IPAddress }}" $name)
  local localhost=$(docker network inspect bridge -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}')
  # single quote the password if it has a dollar sign in it
  if [[ $rootpwd =~ [$\&] ]]; then rootpwd=${rootpwd@Q}; fi
  IFS='~'
  echo ""
  echo $(warn "root access to the container's mysql shell is now possible using: ")
  echo $(warn "docker exec -it $name mysql -u root -p$rootpwd")
  echo ""
  echo $(info "application access to the container's mysql shell is now possible using: ")
  echo $(info "docker exec -it $name mysql -u user -p$userpwd")
  echo ""
  echo $(warn "root access from localhost to the container's mysql shell is now possible using: ")
  echo $(warn "mysql -h $ip -u root -p$rootpwd")
  echo ""
  echo $(info "application access from localhost to the container's mysql shell is now possible using: ")
  echo $(info "mysql -h $ip -u user -p$userpwd")
  echo ""
  echo $(debug "to change the mysql root password login to the docker image mysql shell and use: ")
  echo $(debug "ALTER USER 'root'@'localhost' IDENTIFIED BY '<choose-password>';")
  echo $(debug "ALTER USER 'root'@'$localhost' IDENTIFIED BY '<use-same-password>';")
  echo ""
  unset IFS
}

purge () {
  local image=$1
  local name=$2
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
    echo $(info "purging '$image'...")
    docker container rm $(docker ps -a -q --filter ancestor=$image) -f
    if [[ $(docker images | grep ^.*$image) ]]; then
      for i in $(docker images | grep ^.*$image | awk '{ print $3 }'); do
        echo $(debug "removing image $i")
        docker rmi $i -f
      done
    fi
    echo $(info "purged '$image' and container '$image'")
  else
    echo $(warn "'$image' does not exist!")
  fi
}

restart () {
  local image=$1
  local name=$2
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
    id=$(docker ps -qf "name=$name" --no-trunc)
    if [[ -z $id ]]; then
      echo $(info "restarting '$image' named '$name'...")
      docker container restart $name
      echo $(info "'$image' named '$name' started")
    else
      echo $(warn "'$image' named '$name' is already running!")
    fi
  else
    echo $(warn "'$image' named '$name' does not exist!")
  fi
}

stop () {
  local image=$1
  local name=$2
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
    local id=$(docker ps -qf "name=$name")
    if [[ ! -z $id ]]; then
      echo $(info "stopping '$image' named '$name' identified by '$id'...")
      docker container stop $id
      echo $(info "$id stopped")
    else
      echo $(warn "'$image' named '$name' is not running")
    fi
  else
    echo $(warn "'$image' named '$name' does not exist!")
  fi
}

status () {
  local image=$1
  local name=$2
  IFS='~'
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
    local format="table {{.ID}}\\t{{.Image}}\\t{{.Names}}\\t{{.State}}\\t{{.Status}}\\t{{.Ports}}"
    local out=$(docker ps -a --format "$format" --filter ancestor=$image)
    echo $(debug "$out")
  else
    echo $(warn "'$image' does not exist!")
  fi
  unset IFS
}

clean () {
  local image=$1
  local name=$2
  local artifact=jpa-contacts
  # -- stop running mysql containers if any
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
    echo $(info "stopping mysql container...")
    docker stop $(docker ps -a -q --filter ancestor=$image)
  fi
  # -- remove jpa-contacts containers if any
  if [[ $(docker ps -a -q --filter ancestor=$artifact) ]]; then
    echo $(info "removing $artifact container...")
    docker rm $(docker ps -a -q --filter ancestor=$artifact) -f
  fi
  # -- remove jpa-contacts images if any
  if [[ $(docker images | grep ^.*$artifact) ]]; then
    echo $(info "removing $artifact images...")
	for i in $(docker images | grep ^.*$artifact | awk '{ print $3 }'); do
	    echo $(debug "removing image $i")
        docker rmi $i -f
	done
  fi
  # -- remove dangling images if any
  if [[ $(docker images --filter 'dangling=true' -q --no-trunc) ]]; then
    echo $(info "removing dangling images...")
    docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)
  fi
}

install () {
  local image=$1
  local name=$2
  local userpwd="password"
  local dbname="contacts"
  local appuser="user"
  if [[ $(docker ps -a -q --filter ancestor=$image) ]]; then
   echo $(warn "'$image' named '$name' is already installed!")
   kill -SIGINT $$
  fi
  docker run --name=$name --restart on-failure -d $image
  # we're looking for the 2nd time the mysql server is started'
  echo $(info "waiting 10 seconds for '$image' initialization. (expect 1.5 minutes)")
  while [[ $(docker logs $name 2>&1 | grep -c "/usr/sbin/mysqld: ready for connections.") != 2 ]]; do
    sleep 10
    echo $(debug "waiting another 10 seconds...")
  done
  echo $(info "$image initialized")
  echo ""
  local out=$(docker logs $name 2>&1 | grep GENERATED | awk '{ print $5 }') # grep the log for the generated password
  local genpwd=$(echo $out)
  local localhost=$(docker network inspect bridge -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}')
  local rootpwd=$(date +%s | sha256sum | base64 | head -c 12 ; echo)
  local verify=""
  # accept passwords from stdin
  if [[ ! -z $3 ]]; then
    echo $(info "Note: password phrases must have at least 8 characters, contain digit/s and at least one of: @|#|&|$|%%|(|)|^|*|")
    echo ""
    while true; do
      unset rootpwd
      unset verify
      prompt="Enter mysql root password:  "
      while IFS= read -p "$prompt" -r -s -n 1 char; do
        if [[ $char == $'\0' ]]; then break; fi
        prompt='*'
        rootpwd+="$char"
      done
      echo ""
      prompt="Retype mysql root password: "
      while IFS= read -p "$prompt" -r -s -n 1 char; do
        if [[ $char == $'\0' ]]; then break; fi
        prompt='*'
        verify+="$char"
      done
      echo ""
      if [ "$rootpwd" != "$verify" ]; then
         echo $(warn "no match. try again...")
         continue 1
      fi
      if [[ ${#rootpwd} -ge 8 && $rootpwd =~ [0-9] && $rootpwd =~ [@#\&$%()^*!] ]]; then break; else echo $(warn "unsecured password. try again..."); fi
    done
  fi
  # adminiter mysql
  unset IFS
  echo $(debug "replacing generated password: $genpwd with "$rootpwd"")
  docker exec -it $name mysql --connect-expired-password -u root -p$genpwd -se "ALTER USER 'root'@'localhost' IDENTIFIED BY '$rootpwd'"
  echo $(debug "creating database $dbname...")
  docker exec -it $name mysql -u root -p$rootpwd -e "CREATE DATABASE IF NOT EXISTS $dbname"
  echo $(debug "creating user $appuser with default password...")
  docker exec -it $name mysql -u root -p$rootpwd -e "CREATE USER '$appuser'@'%' IDENTIFIED BY '$userpwd'"
  echo $(debug "granting user $appuser all priviliges on database $dbname...")
  docker exec -it $name mysql -u root -p$rootpwd -e "GRANT ALL ON $dbname.* TO '$appuser'@'%' WITH GRANT OPTION"
  echo $(debug "creating user 'root'@'$localhost' and granting privileges on *.*")
  docker exec -it $name mysql -u root -p$rootpwd -e "CREATE USER 'root'@'$localhost' IDENTIFIED BY '$rootpwd'"
  docker exec -it $name mysql -u root -p$rootpwd -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'$localhost' WITH GRANT OPTION"
  docker exec -it $name mysql -u root -p$rootpwd -e "FLUSH PRIVILEGES"
  success $image $name $userpwd "$rootpwd"
  unset rootpwd
  unset verify
}

main "$@";