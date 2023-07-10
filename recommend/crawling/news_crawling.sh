#!/bin/bash

DIR_PATH=/home/ubuntu/S08P22A503/recommend/crawling

echo "$(/usr/bin/date "+%Y-%m-%d %H:%M:%S,%3N")|INFO|start|crawling"

#/usr/bin/python3 /home/sohee/crawling/naver_news_crawling.py
#/usr/bin/python3 /home/sohee/crawling/naver_enter_news_crawling.py
/usr/bin/python3 ${DIR_PATH}/start_all_crawling.py

echo "$(/usr/bin/date "+%Y-%m-%d %H:%M:%S,%3N")|INFO|finish|crawling"
