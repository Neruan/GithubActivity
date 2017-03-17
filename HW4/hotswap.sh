#/bin/bash
docker ps -a > list.txt
if grep --quiet $1 list.txt
    then
    if grep --quiet web1 list.txt
        then
        docker run --network ecs189_default --name web2 -d $1
        docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
        echo "Swapping web1 with web2"
        #docker kill web1
        docker rm -f `docker ps -a | grep web1 | sed -e 's: .*$::'`
    fi

    else if grep --quiet web2 list.txt
        then
        docker run --network ecs189_default --name web1 -d $1
        docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
        echo "Swapping web2 with web1"
        #docker kill web2
        docker rm -f `docker ps -a | grep web2 | sed -e 's: .*$::'`
    fi
fi
rm -f list.txt