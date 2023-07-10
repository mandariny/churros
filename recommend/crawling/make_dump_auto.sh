today=$(date +"%Y-%m-%d %H:%M:%S")

echo "[$today] make_dump start"

sudo /usr/bin/docker exec mongo-db ./make_dump.sh

echo "[$today] make_dump finished"
