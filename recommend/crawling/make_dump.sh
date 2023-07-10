#!/bin/bash

today=$(date +%y%m%d)
dump_path="/data/db/mongo-dump/"
host="127.0.0.1"
port="27017"
db_name="newsdb"

echo "$today dump start..."

mongodump --out $dump_path$today --host $host --port $port --db $db_name

echo "$today dump finished..."
