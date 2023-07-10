#! /bin/bash

# PATH=/var/jenkins/workspace/A503/recommend/crawling
DIR_PATH=/home/ubuntu/S08P22A503/recommend/crawling
FILE1=naver_news_crawling.py
FILE2=naver_enter_news_crawling.py
FILE3=news_crawling.sh
FILE4=tokenizer.py
FILE5=stopwords.txt
FILE6=lda_model.py
FILE7=start_all_crawling.py

# /usr/bin/cp ${PATH}/${FILE1} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE2} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE3} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE4} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE5} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE6} ${DIR_PATH}
# /usr/bin/cp ${PATH}/${FILE7} ${DIR_PATH}

/usr/bin/chmod 555 ${DIR_PATH}/${FILE1}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE2}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE3}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE4}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE5}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE6}
/usr/bin/chmod 555 ${DIR_PATH}/${FILE7}
