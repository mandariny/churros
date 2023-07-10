#! /bin/bash

MODEL_PATH=/home/ubuntu/recommend
DIR_PATH=${MODEL_PATH}/data
ARCHIVE_PATH=${DIR_PATH}/archive
CRS_PATH=/fast-api/data

TODAY=$(/usr/bin/date "+%y%m%d")

FILE1=corpus.pkl
FILE2=ldaindex.sim
FILE3=ldamodels.lda

CONTAINER_NAME=churros-crs

echo "$(/usr/bin/date "+%Y-%m-%d %H:%M:%S,%3N")|INFO|start|remodeling"

# archive 디렉토리가 존재하지 않는 경우 생성
if [ ! -e ${ARCHIVE_PATH} ]; then
	mkdir ${ARCHIVE_PATH}
fi

# 파일이 존재할 경우 이름 변경 후 archive 디렉토리로 이동
if [ -e ${DIR_PATH}/${FILE1} ]; then
	mv ${DIR_PATH}/${FILE1} ${ARCHIVE_PATH}/${TODAY}_${FILE1}
fi

if [ -e ${DIR_PATH}/${FILE2} ]; then
	mv ${DIR_PATH}/${FILE2} ${ARCHIVE_PATH}/${TODAY}_${FILE2}
fi

if [ -e ${DIR_PATH}/${FILE3} ]; then
	mv ${DIR_PATH}/${FILE3} ${ARCHIVE_PATH}/${TODAY}_${FILE3}
fi

# 모델 학습
/usr/bin/python3 ${MODEL_PATH}/lda_model.py

# container에 있는 파일 삭제
if [ -e ${CRS_PATH}/${FILE1} ]; then
	/usr/bin/docker exec ${CONTAINER_NAME} rm ${CRS_PATH}/${FILE1}
fi

if [ -e ${CRS_PATH}/${FILE2} ]; then
	/usr/bin/docker exec ${CONTAINER_NAME} rm ${CRS_PATH}/${FILE2}
fi

if [ -e ${CRS_PATH}/${FILE3} ]; then
	/usr/bin/docker exec ${CONTAINER_NAME} rm ${CRS_PATH}/${FILE3}
fi

# container에 파일 복사
/usr/bin/docker cp ${DIR_PATH}/${FILE1} ${CONTAINER_NAME}:${CRS_PATH}
/usr/bin/docker cp ${DIR_PATH}/${FILE2} ${CONTAINER_NAME}:${CRS_PATH}
/usr/bin/docker cp ${DIR_PATH}/${FILE3} ${CONTAINER_NAME}:${CRS_PATH}

# 파일 변경 요청

echo "$(/usr/bin/date "+%Y-%m-%d %H:%M:%S,%3N")|INFO|finish|remodeling"

